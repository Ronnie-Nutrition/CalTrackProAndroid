# CalTrackPro ProGuard Rules
# ===========================

# Keep source file and line number information for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===========================
# Retrofit & OkHttp
# ===========================
-keepattributes Signature
-keepattributes Exceptions

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ===========================
# Moshi
# ===========================
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class com.squareup.moshi.** { *; }

# Keep Moshi-generated adapters
-keep class **JsonAdapter { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

# Keep model classes used with Moshi
-keep class com.easyaiflows.caltrackpro.data.remote.** { *; }
-keepclassmembers class com.easyaiflows.caltrackpro.data.remote.** { *; }

# ===========================
# Room Database
# ===========================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Room entities and DAOs
-keep class com.easyaiflows.caltrackpro.data.local.** { *; }
-keepclassmembers class com.easyaiflows.caltrackpro.data.local.** { *; }

# ===========================
# Hilt / Dagger
# ===========================
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keepclasseswithmembers class * {
    @dagger.* <methods>;
}
-keepclasseswithmembers class * {
    @javax.inject.* <fields>;
}
-keepclasseswithmembers class * {
    @javax.inject.* <methods>;
}

# ===========================
# Domain Models
# ===========================
-keep class com.easyaiflows.caltrackpro.domain.model.** { *; }
-keepclassmembers class com.easyaiflows.caltrackpro.domain.model.** { *; }

# ===========================
# Kotlin
# ===========================
-dontwarn kotlin.**
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}

# ===========================
# Jetpack Compose
# ===========================
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ===========================
# CameraX
# ===========================
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ===========================
# ML Kit
# ===========================
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# ===========================
# DataStore
# ===========================
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# ===========================
# Firebase Crashlytics
# ===========================
-keepattributes *Annotation*
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# ===========================
# Enums
# ===========================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===========================
# Serializable
# ===========================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===========================
# Parcelable
# ===========================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ===========================
# AndroidX
# ===========================
-keep class androidx.** { *; }
-dontwarn androidx.**

# ===========================
# Remove logging in release builds
# ===========================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
