plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.efishell.vulkanscope"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.efishell.vulkanscope"
        minSdk = 24
        targetSdk = 36
        versionCode = 17
        versionName = "0.4"
        // Build all supported Android ABIs. Turnip/libadrenotools is compiled only
        // for arm64-v8a; the other ABIs use the system Vulkan loader only.
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64") }
    }

    // Build the same inspection engine for all requested Android ABIs. Turnip itself
    // remains arm64 + Adreno only; other ABIs use the system Vulkan loader.
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86_64")
            isUniversalApk = true
        }
    }

    externalNativeBuild {
        cmake { path = file("src/main/cpp/CMakeLists.txt") }
    }

    // Required by libadrenotools: its hook libraries must be extracted to
    // nativeLibraryDir so Android linker namespaces can load them.
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    buildTypes {
        release {
            // Release builds are optimized, non-debuggable and ready to be signed.
            // Signing is intentionally left to the developer-owned keystore.
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug { isDebuggable = true }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.ui:ui:1.11.4")
    implementation("androidx.compose.foundation:foundation:1.11.4")
    implementation("androidx.compose.animation:animation:1.11.4")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.3")
}
