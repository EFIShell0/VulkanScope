#pragma once
#include <cstddef>
#include <cstdint>

struct VideoRegistryValue { const char* displayName; const char* token; int32_t value; };

inline constexpr VideoRegistryValue kVideoH264DecodeProfiles[] = {
    {"Baseline", "STD_VIDEO_H264_PROFILE_IDC_BASELINE", STD_VIDEO_H264_PROFILE_IDC_BASELINE},
    {"Main", "STD_VIDEO_H264_PROFILE_IDC_MAIN", STD_VIDEO_H264_PROFILE_IDC_MAIN},
    {"High", "STD_VIDEO_H264_PROFILE_IDC_HIGH", STD_VIDEO_H264_PROFILE_IDC_HIGH},
    {"High 10", "STD_VIDEO_H264_PROFILE_IDC_HIGH_10", STD_VIDEO_H264_PROFILE_IDC_HIGH_10},
    {"High 4:2:2", "STD_VIDEO_H264_PROFILE_IDC_HIGH_422", STD_VIDEO_H264_PROFILE_IDC_HIGH_422},
    {"High 4:4:4 Predictive", "STD_VIDEO_H264_PROFILE_IDC_HIGH_444_PREDICTIVE", STD_VIDEO_H264_PROFILE_IDC_HIGH_444_PREDICTIVE},
};
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_BASELINE) == 66);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_MAIN) == 77);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_HIGH) == 100);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_HIGH_10) == 110);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_HIGH_422) == 122);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_HIGH_444_PREDICTIVE) == 244);

inline constexpr VideoRegistryValue kVideoH265DecodeProfiles[] = {
    {"Main", "STD_VIDEO_H265_PROFILE_IDC_MAIN", STD_VIDEO_H265_PROFILE_IDC_MAIN},
    {"Main 10", "STD_VIDEO_H265_PROFILE_IDC_MAIN_10", STD_VIDEO_H265_PROFILE_IDC_MAIN_10},
    {"Main Still Picture", "STD_VIDEO_H265_PROFILE_IDC_MAIN_STILL_PICTURE", STD_VIDEO_H265_PROFILE_IDC_MAIN_STILL_PICTURE},
    {"Format range extensions", "STD_VIDEO_H265_PROFILE_IDC_FORMAT_RANGE_EXTENSIONS", STD_VIDEO_H265_PROFILE_IDC_FORMAT_RANGE_EXTENSIONS},
    {"Screen content coding extensions", "STD_VIDEO_H265_PROFILE_IDC_SCC_EXTENSIONS", STD_VIDEO_H265_PROFILE_IDC_SCC_EXTENSIONS},
};
static_assert(static_cast<int32_t>(STD_VIDEO_H265_PROFILE_IDC_MAIN) == 1);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_PROFILE_IDC_MAIN_10) == 2);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_PROFILE_IDC_MAIN_STILL_PICTURE) == 3);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_PROFILE_IDC_FORMAT_RANGE_EXTENSIONS) == 4);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_PROFILE_IDC_SCC_EXTENSIONS) == 9);

inline constexpr VideoRegistryValue kVideoVP9DecodeProfiles[] = {
    {"Profile 0", "STD_VIDEO_VP9_PROFILE_0", STD_VIDEO_VP9_PROFILE_0},
    {"Profile 1", "STD_VIDEO_VP9_PROFILE_1", STD_VIDEO_VP9_PROFILE_1},
    {"Profile 2", "STD_VIDEO_VP9_PROFILE_2", STD_VIDEO_VP9_PROFILE_2},
    {"Profile 3", "STD_VIDEO_VP9_PROFILE_3", STD_VIDEO_VP9_PROFILE_3},
};
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_PROFILE_0) == 0);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_PROFILE_1) == 1);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_PROFILE_2) == 2);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_PROFILE_3) == 3);

inline constexpr VideoRegistryValue kVideoAV1DecodeProfiles[] = {
    {"Main", "STD_VIDEO_AV1_PROFILE_MAIN", STD_VIDEO_AV1_PROFILE_MAIN},
    {"High", "STD_VIDEO_AV1_PROFILE_HIGH", STD_VIDEO_AV1_PROFILE_HIGH},
    {"Professional", "STD_VIDEO_AV1_PROFILE_PROFESSIONAL", STD_VIDEO_AV1_PROFILE_PROFESSIONAL},
};
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_PROFILE_MAIN) == 0);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_PROFILE_HIGH) == 1);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_PROFILE_PROFESSIONAL) == 2);

inline constexpr VideoRegistryValue kVideoH264EncodeProfiles[] = {
    {"Baseline", "STD_VIDEO_H264_PROFILE_IDC_BASELINE", STD_VIDEO_H264_PROFILE_IDC_BASELINE},
    {"Main", "STD_VIDEO_H264_PROFILE_IDC_MAIN", STD_VIDEO_H264_PROFILE_IDC_MAIN},
    {"High", "STD_VIDEO_H264_PROFILE_IDC_HIGH", STD_VIDEO_H264_PROFILE_IDC_HIGH},
    {"High 10", "STD_VIDEO_H264_PROFILE_IDC_HIGH_10", STD_VIDEO_H264_PROFILE_IDC_HIGH_10},
    {"High 4:2:2", "STD_VIDEO_H264_PROFILE_IDC_HIGH_422", STD_VIDEO_H264_PROFILE_IDC_HIGH_422},
    {"High 4:4:4 Predictive", "STD_VIDEO_H264_PROFILE_IDC_HIGH_444_PREDICTIVE", STD_VIDEO_H264_PROFILE_IDC_HIGH_444_PREDICTIVE},
};
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_BASELINE) == 66);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_MAIN) == 77);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_HIGH) == 100);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_HIGH_10) == 110);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_HIGH_422) == 122);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_PROFILE_IDC_HIGH_444_PREDICTIVE) == 244);

inline constexpr VideoRegistryValue kVideoH265EncodeProfiles[] = {
    {"Main", "STD_VIDEO_H265_PROFILE_IDC_MAIN", STD_VIDEO_H265_PROFILE_IDC_MAIN},
    {"Main 10", "STD_VIDEO_H265_PROFILE_IDC_MAIN_10", STD_VIDEO_H265_PROFILE_IDC_MAIN_10},
    {"Main Still Picture", "STD_VIDEO_H265_PROFILE_IDC_MAIN_STILL_PICTURE", STD_VIDEO_H265_PROFILE_IDC_MAIN_STILL_PICTURE},
    {"Format range extensions", "STD_VIDEO_H265_PROFILE_IDC_FORMAT_RANGE_EXTENSIONS", STD_VIDEO_H265_PROFILE_IDC_FORMAT_RANGE_EXTENSIONS},
    {"Screen content coding extensions", "STD_VIDEO_H265_PROFILE_IDC_SCC_EXTENSIONS", STD_VIDEO_H265_PROFILE_IDC_SCC_EXTENSIONS},
};
static_assert(static_cast<int32_t>(STD_VIDEO_H265_PROFILE_IDC_MAIN) == 1);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_PROFILE_IDC_MAIN_10) == 2);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_PROFILE_IDC_MAIN_STILL_PICTURE) == 3);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_PROFILE_IDC_FORMAT_RANGE_EXTENSIONS) == 4);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_PROFILE_IDC_SCC_EXTENSIONS) == 9);

inline constexpr VideoRegistryValue kVideoAV1EncodeProfiles[] = {
    {"Main", "STD_VIDEO_AV1_PROFILE_MAIN", STD_VIDEO_AV1_PROFILE_MAIN},
    {"High", "STD_VIDEO_AV1_PROFILE_HIGH", STD_VIDEO_AV1_PROFILE_HIGH},
    {"Professional", "STD_VIDEO_AV1_PROFILE_PROFESSIONAL", STD_VIDEO_AV1_PROFILE_PROFESSIONAL},
};
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_PROFILE_MAIN) == 0);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_PROFILE_HIGH) == 1);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_PROFILE_PROFESSIONAL) == 2);

static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_1_0) == 0);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_1_1) == 1);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_1_2) == 2);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_1_3) == 3);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_2_0) == 4);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_2_1) == 5);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_2_2) == 6);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_3_0) == 7);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_3_1) == 8);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_3_2) == 9);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_4_0) == 10);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_4_1) == 11);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_4_2) == 12);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_5_0) == 13);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_5_1) == 14);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_5_2) == 15);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_6_0) == 16);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_6_1) == 17);
static_assert(static_cast<int32_t>(STD_VIDEO_H264_LEVEL_IDC_6_2) == 18);

inline const char* videoH264LevelName(int32_t value) {
    switch (value) {
        case 0: return "STD_VIDEO_H264_LEVEL_IDC_1_0";
        case 1: return "STD_VIDEO_H264_LEVEL_IDC_1_1";
        case 2: return "STD_VIDEO_H264_LEVEL_IDC_1_2";
        case 3: return "STD_VIDEO_H264_LEVEL_IDC_1_3";
        case 4: return "STD_VIDEO_H264_LEVEL_IDC_2_0";
        case 5: return "STD_VIDEO_H264_LEVEL_IDC_2_1";
        case 6: return "STD_VIDEO_H264_LEVEL_IDC_2_2";
        case 7: return "STD_VIDEO_H264_LEVEL_IDC_3_0";
        case 8: return "STD_VIDEO_H264_LEVEL_IDC_3_1";
        case 9: return "STD_VIDEO_H264_LEVEL_IDC_3_2";
        case 10: return "STD_VIDEO_H264_LEVEL_IDC_4_0";
        case 11: return "STD_VIDEO_H264_LEVEL_IDC_4_1";
        case 12: return "STD_VIDEO_H264_LEVEL_IDC_4_2";
        case 13: return "STD_VIDEO_H264_LEVEL_IDC_5_0";
        case 14: return "STD_VIDEO_H264_LEVEL_IDC_5_1";
        case 15: return "STD_VIDEO_H264_LEVEL_IDC_5_2";
        case 16: return "STD_VIDEO_H264_LEVEL_IDC_6_0";
        case 17: return "STD_VIDEO_H264_LEVEL_IDC_6_1";
        case 18: return "STD_VIDEO_H264_LEVEL_IDC_6_2";
        default: return "UNKNOWN_STD_VIDEO_LEVEL";
    }
}

static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_1_0) == 0);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_2_0) == 1);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_2_1) == 2);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_3_0) == 3);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_3_1) == 4);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_4_0) == 5);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_4_1) == 6);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_5_0) == 7);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_5_1) == 8);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_5_2) == 9);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_6_0) == 10);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_6_1) == 11);
static_assert(static_cast<int32_t>(STD_VIDEO_H265_LEVEL_IDC_6_2) == 12);

inline const char* videoH265LevelName(int32_t value) {
    switch (value) {
        case 0: return "STD_VIDEO_H265_LEVEL_IDC_1_0";
        case 1: return "STD_VIDEO_H265_LEVEL_IDC_2_0";
        case 2: return "STD_VIDEO_H265_LEVEL_IDC_2_1";
        case 3: return "STD_VIDEO_H265_LEVEL_IDC_3_0";
        case 4: return "STD_VIDEO_H265_LEVEL_IDC_3_1";
        case 5: return "STD_VIDEO_H265_LEVEL_IDC_4_0";
        case 6: return "STD_VIDEO_H265_LEVEL_IDC_4_1";
        case 7: return "STD_VIDEO_H265_LEVEL_IDC_5_0";
        case 8: return "STD_VIDEO_H265_LEVEL_IDC_5_1";
        case 9: return "STD_VIDEO_H265_LEVEL_IDC_5_2";
        case 10: return "STD_VIDEO_H265_LEVEL_IDC_6_0";
        case 11: return "STD_VIDEO_H265_LEVEL_IDC_6_1";
        case 12: return "STD_VIDEO_H265_LEVEL_IDC_6_2";
        default: return "UNKNOWN_STD_VIDEO_LEVEL";
    }
}

static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_1_0) == 0);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_1_1) == 1);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_2_0) == 2);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_2_1) == 3);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_3_0) == 4);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_3_1) == 5);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_4_0) == 6);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_4_1) == 7);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_5_0) == 8);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_5_1) == 9);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_5_2) == 10);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_6_0) == 11);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_6_1) == 12);
static_assert(static_cast<int32_t>(STD_VIDEO_VP9_LEVEL_6_2) == 13);

inline const char* videoVP9LevelName(int32_t value) {
    switch (value) {
        case 0: return "STD_VIDEO_VP9_LEVEL_1_0";
        case 1: return "STD_VIDEO_VP9_LEVEL_1_1";
        case 2: return "STD_VIDEO_VP9_LEVEL_2_0";
        case 3: return "STD_VIDEO_VP9_LEVEL_2_1";
        case 4: return "STD_VIDEO_VP9_LEVEL_3_0";
        case 5: return "STD_VIDEO_VP9_LEVEL_3_1";
        case 6: return "STD_VIDEO_VP9_LEVEL_4_0";
        case 7: return "STD_VIDEO_VP9_LEVEL_4_1";
        case 8: return "STD_VIDEO_VP9_LEVEL_5_0";
        case 9: return "STD_VIDEO_VP9_LEVEL_5_1";
        case 10: return "STD_VIDEO_VP9_LEVEL_5_2";
        case 11: return "STD_VIDEO_VP9_LEVEL_6_0";
        case 12: return "STD_VIDEO_VP9_LEVEL_6_1";
        case 13: return "STD_VIDEO_VP9_LEVEL_6_2";
        default: return "UNKNOWN_STD_VIDEO_LEVEL";
    }
}

static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_2_0) == 0);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_2_1) == 1);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_2_2) == 2);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_2_3) == 3);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_3_0) == 4);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_3_1) == 5);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_3_2) == 6);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_3_3) == 7);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_4_0) == 8);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_4_1) == 9);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_4_2) == 10);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_4_3) == 11);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_5_0) == 12);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_5_1) == 13);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_5_2) == 14);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_5_3) == 15);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_6_0) == 16);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_6_1) == 17);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_6_2) == 18);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_6_3) == 19);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_7_0) == 20);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_7_1) == 21);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_7_2) == 22);
static_assert(static_cast<int32_t>(STD_VIDEO_AV1_LEVEL_7_3) == 23);

inline const char* videoAV1LevelName(int32_t value) {
    switch (value) {
        case 0: return "STD_VIDEO_AV1_LEVEL_2_0";
        case 1: return "STD_VIDEO_AV1_LEVEL_2_1";
        case 2: return "STD_VIDEO_AV1_LEVEL_2_2";
        case 3: return "STD_VIDEO_AV1_LEVEL_2_3";
        case 4: return "STD_VIDEO_AV1_LEVEL_3_0";
        case 5: return "STD_VIDEO_AV1_LEVEL_3_1";
        case 6: return "STD_VIDEO_AV1_LEVEL_3_2";
        case 7: return "STD_VIDEO_AV1_LEVEL_3_3";
        case 8: return "STD_VIDEO_AV1_LEVEL_4_0";
        case 9: return "STD_VIDEO_AV1_LEVEL_4_1";
        case 10: return "STD_VIDEO_AV1_LEVEL_4_2";
        case 11: return "STD_VIDEO_AV1_LEVEL_4_3";
        case 12: return "STD_VIDEO_AV1_LEVEL_5_0";
        case 13: return "STD_VIDEO_AV1_LEVEL_5_1";
        case 14: return "STD_VIDEO_AV1_LEVEL_5_2";
        case 15: return "STD_VIDEO_AV1_LEVEL_5_3";
        case 16: return "STD_VIDEO_AV1_LEVEL_6_0";
        case 17: return "STD_VIDEO_AV1_LEVEL_6_1";
        case 18: return "STD_VIDEO_AV1_LEVEL_6_2";
        case 19: return "STD_VIDEO_AV1_LEVEL_6_3";
        case 20: return "STD_VIDEO_AV1_LEVEL_7_0";
        case 21: return "STD_VIDEO_AV1_LEVEL_7_1";
        case 22: return "STD_VIDEO_AV1_LEVEL_7_2";
        case 23: return "STD_VIDEO_AV1_LEVEL_7_3";
        default: return "UNKNOWN_STD_VIDEO_LEVEL";
    }
}

inline constexpr VideoRegistryValue kVideoH264PictureLayouts[] = {
    {"progressive", "VK_VIDEO_DECODE_H264_PICTURE_LAYOUT_PROGRESSIVE_KHR", static_cast<int32_t>(VK_VIDEO_DECODE_H264_PICTURE_LAYOUT_PROGRESSIVE_KHR)},
    {"interlaced (interleaved lines)", "VK_VIDEO_DECODE_H264_PICTURE_LAYOUT_INTERLACED_INTERLEAVED_LINES_BIT_KHR", static_cast<int32_t>(VK_VIDEO_DECODE_H264_PICTURE_LAYOUT_INTERLACED_INTERLEAVED_LINES_BIT_KHR)},
    {"interlaced (separate planes)", "VK_VIDEO_DECODE_H264_PICTURE_LAYOUT_INTERLACED_SEPARATE_PLANES_BIT_KHR", static_cast<int32_t>(VK_VIDEO_DECODE_H264_PICTURE_LAYOUT_INTERLACED_SEPARATE_PLANES_BIT_KHR)},
};

inline constexpr VideoRegistryValue kVideoAV1FilmGrainModes[] = {
    {"with film grain support", "VK_TRUE", static_cast<int32_t>(VK_TRUE)},
    {"without film grain support", "VK_FALSE", static_cast<int32_t>(VK_FALSE)},
};

inline constexpr const char* kVideoRegistrySha256 = "d018b914014c06605e367a3b929670511e6f6de2f225c405a8b5e2d912408b76";
inline constexpr const char* kVideoRegistryVulkanSha256 = "3ff4984b841932e04eebeb4ce2a6613ebd37c00ffb2e96549785b2c5d7da9e1d";
