# VulkanScope release rules
# MainActivity contains the JNI entry point and is kept explicitly.
-keep class com.efishell.vulkanscope.MainActivity { *; }
# RestartActivity is launched from the manifest and must remain intact.
-keep class com.efishell.vulkanscope.RestartActivity { *; }
# Keep the native JNI method signature used by libvulkanscope.so.
-keepclassmembers class com.efishell.vulkanscope.MainActivity {
    native <methods>;
}
