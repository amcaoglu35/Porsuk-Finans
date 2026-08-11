# Porsuk Finans — ProGuard / R8 Rules

# 1. Gson & Domain Models: Reflection ile JSON mapping ve Room TypeConverter için alan isimlerinin korunması gerekir.
-keep class com.nexus.porsuk.data.remote.dto.** { *; }
-keep class com.nexus.porsuk.data.model.** { *; }
-keep class com.nexus.porsuk.domain.model.** { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# 2. Retrofit Interface: Interface metotları ve parametreleri reflection ile okunduğu için korunmalıdır.
-keep interface com.nexus.porsuk.data.remote.api.** { *; }

# 3. Serialization & Generic Signatures: Generic type token ve anotasyon metadata korunmalıdır.
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
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

# 6. R8 Missing Class Warnings (Opsiyonel / JVM-spesifik bağımlılık uyarılarını bastır)
-dontwarn com.google.api.client.http.**
-dontwarn com.google.crypto.tink.util.KeysDownloader
-dontwarn java.lang.management.**
-dontwarn org.joda.time.**
-dontwarn org.jspecify.annotations.**

