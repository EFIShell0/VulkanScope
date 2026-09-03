#!/usr/bin/env python3
VK_SUCCESS=0
VK_ERROR_OUT_OF_HOST_MEMORY=-1
VK_ERROR_OUT_OF_DEVICE_MEMORY=-2
VK_ERROR_UNKNOWN=-13
VK_ERROR_VALIDATION_FAILED=-1000011001
VK_ERROR_VIDEO_PICTURE_LAYOUT_NOT_SUPPORTED_KHR=-1000023001
VK_ERROR_VIDEO_PROFILE_OPERATION_NOT_SUPPORTED_KHR=-1000023002
VK_ERROR_VIDEO_PROFILE_FORMAT_NOT_SUPPORTED_KHR=-1000023003
VK_ERROR_VIDEO_PROFILE_CODEC_NOT_SUPPORTED_KHR=-1000023004
UNSUPPORTED={VK_ERROR_VIDEO_PICTURE_LAYOUT_NOT_SUPPORTED_KHR,VK_ERROR_VIDEO_PROFILE_OPERATION_NOT_SUPPORTED_KHR,VK_ERROR_VIDEO_PROFILE_FORMAT_NOT_SUPPORTED_KHR,VK_ERROR_VIDEO_PROFILE_CODEC_NOT_SUPPORTED_KHR}

def state(result):
    if result==VK_SUCCESS: return 'supported'
    if result in UNSUPPORTED: return 'unsupported'
    return 'unavailable'
def extension_state(enumeration_complete,present):
    if present: return 'query'
    return 'not_applicable' if enumeration_complete else 'unknown'

def require(actual,expected,label):
    if actual!=expected: raise SystemExit(f'FAIL {label}: expected {expected}, got {actual}')
require(state(VK_SUCCESS),'supported','success')
for r in UNSUPPORTED: require(state(r),'unsupported',f'definitive profile error {r}')
for r in [VK_ERROR_OUT_OF_HOST_MEMORY,VK_ERROR_OUT_OF_DEVICE_MEMORY,VK_ERROR_UNKNOWN,VK_ERROR_VALIDATION_FAILED,-1000023005,-999999]: require(state(r),'unavailable',f'non-profile failure {r}')
require(extension_state(True,False),'not_applicable','complete extension absence')
require(extension_state(False,False),'unknown','incomplete extension absence')
require(extension_state(False,True),'query','positive extension evidence survives incomplete enumeration')
counts={'h264DecodeProfiles':6,'h264Layouts':3,'h265DecodeProfiles':5,'vp9DecodeProfiles':4,'av1DecodeProfiles':3,'av1FilmGrainModes':2,'h264EncodeProfiles':6,'h265EncodeProfiles':5,'av1EncodeProfiles':3}
queries=counts['h264DecodeProfiles']*counts['h264Layouts']+counts['h265DecodeProfiles']+counts['vp9DecodeProfiles']+counts['av1DecodeProfiles']*counts['av1FilmGrainModes']+counts['h264EncodeProfiles']+counts['h265EncodeProfiles']+counts['av1EncodeProfiles']
require(queries,47,'bounded exact capability query count')
print('PASS Vulkan Video exact-profile census state machine: 47 bounded capability combinations')
