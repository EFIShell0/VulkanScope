#!/usr/bin/env python3
import shutil, subprocess, sys, tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]
ver=root/'tools/verify_video_registry_04145.py'
needed=['app/build.gradle.kts','app/src/main/cpp/vulkanscope.cpp','app/src/main/cpp/video_registry_generated.h','registry/video_registry_lock.json','registry/upstream/video.xml','registry/upstream/vk.xml','tools/generate_video_registry.py']

def run_mutation(name,mutator,expect_fail=True):
    with tempfile.TemporaryDirectory() as td:
        dst=Path(td)
        for rel in needed:
            p=dst/rel; p.parent.mkdir(parents=True,exist_ok=True); shutil.copy2(root/rel,p)
        mutator(dst)
        r=subprocess.run([sys.executable,str(ver),'--root',str(dst)],text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
        ok=(r.returncode!=0) if expect_fail else (r.returncode==0)
        if not ok:
            print(r.stdout)
            raise SystemExit(f'FAIL negative mutation control: {name}')
        print(f'PASS {name}: '+('rejected' if expect_fail else 'false-positive control accepted'))

def replace(rel,a,b):
    def f(dst):
        p=dst/rel; s=p.read_text(encoding='utf-8');
        if a not in s: raise SystemExit(f'fixture token missing for {rel}: {a}')
        p.write_text(s.replace(a,b,1),encoding='utf-8')
    return f
run_mutation('video.xml lock SHA drift',replace('registry/video_registry_lock.json','d018b914014c06605e367a3b929670511e6f6de2f225c405a8b5e2d912408b76','0'*64))
run_mutation('floating video.xml source ref',replace('registry/video_registry_lock.json','content-sha256:d018b914014c06605e367a3b929670511e6f6de2f225c405a8b5e2d912408b76','main'))
run_mutation('VP9 Profile 3 census removal',replace('app/src/main/cpp/video_registry_generated.h','    {"Profile 3", "STD_VIDEO_VP9_PROFILE_3", STD_VIDEO_VP9_PROFILE_3},\n',''))
run_mutation('H264 Baseline numeric drift',replace('app/src/main/cpp/video_registry_generated.h','static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_BASELINE) == 66);','static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_BASELINE) == 67);'))
run_mutation('H264 layout product removal',replace('app/src/main/cpp/vulkanscope.cpp','for (const auto& layoutEntry : kVideoH264PictureLayouts)','for (const auto& layoutEntry : kVideoAV1FilmGrainModes)'))
run_mutation('AV1 film-grain product removal',replace('app/src/main/cpp/vulkanscope.cpp','for (const auto& filmGrainEntry : kVideoAV1FilmGrainModes)','for (const auto& filmGrainEntry : kVideoH264PictureLayouts)'))
run_mutation('generic capability failure fabricated unsupported',replace('app/src/main/cpp/vulkanscope.cpp','if (result == VK_SUCCESS) return std::string("Supported for exact 4:2:0 8-bit profile");','if (result != VK_SUCCESS) return std::string("Unsupported for exact 4:2:0 8-bit profile");'))
run_mutation('unrelated audit text false-positive',lambda dst:(dst/'app/src/main/cpp/vulkanscope.cpp').write_text((dst/'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8').replace('Video capability query','Video capability query ',1),encoding='utf-8'),expect_fail=False)
print('PASS VulkanScope 0.41.45 video registry negative mutations')
