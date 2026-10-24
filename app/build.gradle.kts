plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val releaseVersionName = "4.0.15"

android {
    namespace = "com.efishell.vulkanscope"
    compileSdk { version = release(37) { minorApiLevel = 2 } }

    defaultConfig {
        applicationId = "com.efishell.vulkanscope"
        minSdk = 31
        targetSdk = 37
        versionCode = 4015
        versionName = releaseVersionName
        
        
        ndkVersion = "29.0.14206865"
        ndk { abiFilters += listOf("arm64-v8a", "x86_64") }
    }

    
    
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "x86_64")
            isUniversalApk = true
        }
    }

    externalNativeBuild {
        cmake { path = file("src/main/cpp/CMakeLists.txt") }
    }

    
    
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    buildTypes {
        release {
            
            
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug { isDebuggable = true }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui:1.12.0")
    implementation("androidx.compose.foundation:foundation:1.12.0")
    implementation("androidx.compose.animation:animation:1.12.0")
    implementation("androidx.compose.material3:material3:1.5.0-alpha27")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:5.5.0")
    implementation("com.google.zxing:core:3.5.4")
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        variant.outputs.forEach { output ->
            val abiName = output.filters.firstOrNull {
                it.filterType == com.android.build.api.variant.FilterConfiguration.FilterType.ABI
            }?.identifier ?: "universal"
            require(abiName in setOf("universal", "arm64-v8a", "x86_64"))
            output.outputFileName.set("vulkanscope-$releaseVersionName-$abiName.apk")
        }
    }
}
