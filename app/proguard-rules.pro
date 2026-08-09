# Porsuk Finans — ProGuard / R8 Rules

# 1. Gson Models: Reflection ile JSON mapping yapıldığı için alan isimlerinin korunması gerekir.
-keep class com.nexus.porsuk.data.remote.dto.** { *; }
-keep class com.nexus.porsuk.data.model.** { *; }

# 2. Retrofit Interface: Interface metotları ve parametreleri reflection ile okunduğu için korunmalıdır.
-keep interface com.nexus.porsuk.data.remote.api.** { *; }

# 3. Kotlinx Serialization: @Serializable anatasyonu ile işaretlenmiş modellerin metadata ve serializerları korunmalıdır.
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keep class * {
    @kotlinx.serialization.Serializable *;
}

# 4. Room Entity: Veritabanı tabloları ve alanları Room tarafından reflection ile maplenir.
-keep class com.nexus.porsuk.data.local.entity.** { *; }

# 5. BuildConfig: API anahtarları gibi statik alanların minifikasyon sırasında silinmemesi için.
-keep class com.nexus.porsuk.BuildConfig { *; }
