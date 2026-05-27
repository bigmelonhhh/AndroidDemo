# ProGuard rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep class com.zencare.model.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
