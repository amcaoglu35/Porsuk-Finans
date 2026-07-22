# Jsoup kütüphanesinin release derlemesinde kırpılmasını engeller
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# Room veritabanı sınıflarının yollarını korur
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Dao
-keep class * { @androidx.room.Entity *; }
-keep class * { @androidx.room.PrimaryKey *; }

# Suppress warnings for missing classes referenced by Tink and Ktor
-dontwarn com.google.api.client.http.GenericUrl
-dontwarn com.google.api.client.http.HttpHeaders
-dontwarn com.google.api.client.http.HttpRequest
-dontwarn com.google.api.client.http.HttpRequestFactory
-dontwarn com.google.api.client.http.HttpResponse
-dontwarn com.google.api.client.http.HttpTransport
-dontwarn com.google.api.client.http.javanet.NetHttpTransport$Builder
-dontwarn com.google.api.client.http.javanet.NetHttpTransport
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.CheckReturnValue
-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn com.google.errorprone.annotations.RestrictedApi
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
-dontwarn org.joda.time.Instant

