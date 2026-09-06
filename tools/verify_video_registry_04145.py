#!/usr/bin/env python3
import argparse, hashlib, json, subprocess, sys, tempfile, xml.etree.ElementTree as ET
from pathlib import Path

parser=argparse.ArgumentParser(); parser.add_argument('--root'); args=parser.parse_args()
root=Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
errors=[]
def need(cond,msg):
    if not cond: errors.append(msg)
def sha(path): return hashlib.sha256(path.read_bytes()).hexdigest()

gradle=root/'app/build.gradle.kts'; source=root/'app/src/main/cpp/vulkanscope.cpp'; header=root/'app/src/main/cpp/video_registry_generated.h'; lockp=root/'registry/video_registry_lock.json'; videop=root/'registry/upstream/video.xml'; vkp=root/'registry/upstream/vk.xml'; gen=root/'tools/generate_video_registry.py'
for p in [gradle,source,header,lockp,videop,vkp,gen]: need(p.is_file(),f'missing {p.relative_to(root) if p.is_relative_to(root) else p}')
if not all(p.is_file() for p in [gradle,source,header,lockp,videop,vkp,gen]):
    for e in errors: print('FAIL:',e)
    raise SystemExit(1)
g=gradle.read_text(encoding='utf-8'); cpp=source.read_text(encoding='utf-8'); h=header.read_text(encoding='utf-8'); lock=json.loads(lockp.read_text(encoding='utf-8'))
vm=__import__('re').search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"',g); vcod=__import__('re').search(r'versionCode\s*=\s*(\d+)',g)
need(bool(vm and vcod and (tuple(map(int, vm.groups())) >= (0, 80, 0) or tuple(map(int, vm.groups())) >= (0, 41, 45)) and int(vcod.group(1)) >= 455),'0.41.45+ compatible version metadata missing')
need(lock.get('schemaVersion')==1,'video registry lock schema mismatch')
need(lock.get('sourceRepository')=='KhronosGroup/Vulkan-Docs','video registry source repository mismatch')
need(lock.get('sourcePath')=='xml/video.xml','video registry source path mismatch')
need(lock.get('sourceRef')=='content-sha256:d018b914014c06605e367a3b929670511e6f6de2f225c405a8b5e2d912408b76','video registry source ref is not an exact content pin')
need(lock.get('vulkanBaseline')=='Vulkan 1.4.362','video registry Vulkan baseline mismatch')
need(lock.get('sha256')==sha(videop),'bundled video.xml SHA does not match lock')
need(lock.get('vulkanRegistrySha256')==sha(vkp),'bundled vk.xml SHA does not match video lock')
need(lock.get('vulkanRegistrySha256')=='cf31c965cf6e788697139601da0c7e02a75a9b6c7ac764e7641f5521ffd9da06','video lock drifted from canonical Vulkan 1.4.362 registry')
need('This file, video.xml, provides the machine readable definition of data' in videop.read_text(encoding='utf-8'),'bundled video.xml is not recognizable Khronos video registry data')

video_root=ET.parse(videop).getroot(); vk_root=ET.parse(vkp).getroot()
def enum_names(group):
    node=next((x for x in video_root.findall('enums') if x.get('name')==group),None)
    return {e.get('name'):int(e.get('value'),0) for e in node.findall('enum') if e.get('name') and e.get('value') and not e.get('name').endswith('_INVALID')} if node is not None else {}
std_groups={'H264':enum_names('StdVideoH264ProfileIdc'),'H265':enum_names('StdVideoH265ProfileIdc'),'VP9':enum_names('StdVideoVP9Profile'),'AV1':enum_names('StdVideoAV1Profile')}
vc=vk_root.find('videocodecs'); need(vc is not None,'vk.xml videocodecs missing')
codecs={}
if vc is not None:
    for c in vc.findall('videocodec'):
        pn=c.find('videoprofiles')
        if pn is None: continue
        members={m.get('name'):[(x.get('name') or '',x.get('value') or '') for x in m.findall('videoprofile')] for m in pn.findall('videoprofilemember')}
        codecs[c.get('name')]=(pn.get('struct'),members)
specs=[
('H264Decode','H.264 Decode','stdProfileIdc','H264'),('H265Decode','H.265 Decode','stdProfileIdc','H265'),('VP9Decode','VP9 Decode','stdProfile','VP9'),('AV1Decode','AV1 Decode','stdProfile','AV1'),('H264Encode','H.264 Encode','stdProfileIdc','H264'),('H265Encode','H.265 Encode','stdProfileIdc','H265'),('AV1Encode','AV1 Encode','stdProfile','AV1')]
for suffix,codec,member,key in specs:
    need(codec in codecs,f'{codec} missing from vk.xml videocodecs')
    if codec not in codecs: continue
    vals=codecs[codec][1].get(member,[]); need(bool(vals),f'{codec}.{member} is empty')
    for _,token in vals: need(token in std_groups[key],f'{codec} token {token} not defined by video.xml')
    need(f'kVideo{suffix}Profiles[]' in h,f'generated {codec} profile array missing')
    for display,token in vals:
        need(f'"{token}"' in h,f'generated header missing {codec} token {token}')
        need(f'static_assert(static_cast<int32_t>({token}) == {std_groups[key][token]});' in h,f'{token} numeric lock missing')

h264_layouts=codecs.get('H.264 Decode',(None,{}))[1].get('pictureLayout',[])
av1_grain=codecs.get('AV1 Decode',(None,{}))[1].get('filmGrainSupport',[])
need(len(h264_layouts)==3,'H.264 decode pictureLayout census is not three canonical values')
need(len(av1_grain)==2,'AV1 decode filmGrainSupport census is not two canonical values')
for _,token in h264_layouts: need(f'"{token}"' in h,f'H.264 picture layout missing: {token}')
for _,token in av1_grain: need(f'"{token}"' in h,f'AV1 film grain value missing: {token}')
expected_calls=(len(codecs['H.264 Decode'][1]['stdProfileIdc'])*len(h264_layouts)+len(codecs['H.265 Decode'][1]['stdProfileIdc'])+len(codecs['VP9 Decode'][1]['stdProfile'])+len(codecs['AV1 Decode'][1]['stdProfile'])*len(av1_grain)+len(codecs['H.264 Encode'][1]['stdProfileIdc'])+len(codecs['H.265 Encode'][1]['stdProfileIdc'])+len(codecs['AV1 Encode'][1]['stdProfile']))
need(expected_calls==47,f'canonical bounded capability census changed unexpectedly: {expected_calls}')

with tempfile.TemporaryDirectory() as td:
    out=Path(td)/'video_registry_generated.h'
    r=subprocess.run([sys.executable,str(gen),'--out',str(out)],cwd=root,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    need(r.returncode==0,f'video registry regeneration failed: {r.stdout.strip()}')
    if r.returncode==0: need(out.read_bytes()==header.read_bytes(),'checked-in generated video registry header is stale')

for token in ['#include "video_registry_generated.h"','kVideoH264DecodeProfiles','kVideoH265DecodeProfiles','kVideoVP9DecodeProfiles','kVideoAV1DecodeProfiles','kVideoH264EncodeProfiles','kVideoH265EncodeProfiles','kVideoAV1EncodeProfiles','kVideoH264PictureLayouts','kVideoAV1FilmGrainModes']:
    need(token in cpp,f'runtime census binding missing {token}')
need('VK_VIDEO_CHROMA_SUBSAMPLING_420_BIT_KHR' in cpp and cpp.count('VK_VIDEO_COMPONENT_BIT_DEPTH_8_BIT_KHR')>=2,'exact base 4:2:0 8-bit profile recipe missing')
need('not codec-wide or bit-depth-wide claims' in cpp,'exact-profile anti-overclaim disclosure missing')
need('Video format enumeration remains separately labelled sampled-profile evidence.' in cpp,'sampled video-format limitation disclosure missing')
need('for (const auto& profileEntry : kVideoH264DecodeProfiles) {' in cpp and 'for (const auto& layoutEntry : kVideoH264PictureLayouts)' in cpp,'H.264 profile/layout product census missing')
need('for (const auto& profileEntry : kVideoAV1DecodeProfiles) {' in cpp and 'for (const auto& filmGrainEntry : kVideoAV1FilmGrainModes)' in cpp,'AV1 profile/film-grain product census missing')
for old in ['emitDecode("H.264 decode", "VK_KHR_video_decode_h264"','emitDecode("H.265 decode", "VK_KHR_video_decode_h265"','emitDecode("VP9 decode", "VK_KHR_video_decode_vp9"','emitDecode("AV1 decode", "VK_KHR_video_decode_av1"']:
    need(old not in cpp,'legacy one-profile capability sample remains')
status=cpp[cpp.find('auto exactProfileStatus'):cpp.find('addProperty("videoRegistry"')]
need('if (result == VK_SUCCESS) return std::string("Supported for exact 4:2:0 8-bit profile");' in status,'capability status success branch was broadened or inverted')
need('if (result != VK_SUCCESS)' not in status,'generic capability failure is fabricated as Unsupported')
for token in ['VK_ERROR_VIDEO_PICTURE_LAYOUT_NOT_SUPPORTED_KHR','VK_ERROR_VIDEO_PROFILE_OPERATION_NOT_SUPPORTED_KHR','VK_ERROR_VIDEO_PROFILE_FORMAT_NOT_SUPPORTED_KHR','VK_ERROR_VIDEO_PROFILE_CODEC_NOT_SUPPORTED_KHR','Unavailable (VkResult=']:
    need(token in status,f'exact capability status mapping missing {token}')
need('VK_ERROR_VIDEO_STD_VERSION_NOT_SUPPORTED_KHR' not in status,'capability status claims an error not returned by vkGetPhysicalDeviceVideoCapabilitiesKHR')
for fn in ['videoH264LevelName','videoH265LevelName','videoVP9LevelName','videoAV1LevelName']:
    need(fn in cpp and fn in h,f'canonical level mapping missing {fn}')
need(cpp.count('(raw=')>=7,'canonical level values do not retain raw numeric evidence')
need('deviceExtensionsComplete ? "Not applicable:' in cpp and 'Unknown: device-extension enumeration is incomplete or unavailable' in cpp,'extension absence certainty semantics missing')
need('sampled profile/usage' in cpp,'video format evidence is no longer visibly sampled')

if errors:
    for e in errors: print('FAIL:',e)
    raise SystemExit(1)
print(f'PASS VulkanScope 0.41.45 locked video.xml/vk.xml registry census contract: exactCapabilityQueries={expected_calls}')
