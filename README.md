# Porsuk Finans (com.nexus.porsuk)

Porsuk Finans is a professional-grade Android investment platform built with Jetpack Compose, Clean Architecture, and Hilt.

## Local Setup & Configuration

To build and run the application locally:

1. **Copy Configuration Template**:
   Copy `local.properties.example` to `local.properties` in the project root directory:
   ```bash
   cp local.properties.example local.properties
   ```

2. **Set Your API Keys**:
   Open `local.properties` and replace the placeholder values with your valid API keys:
   ```properties
   FINNHUB_API_KEY=your_finnhub_key
   FMP_API_KEY=your_fmp_key
   NEWS_API_KEY=your_newsapi_key
   EXCHANGE_RATE_API_KEY=your_exchangerate_key
   FRED_API_KEY=your_fred_key
   YAHOO_RAPIDAPI_KEY=your_rapidapi_key
   ```

3. **Build the Project**:
   Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

> **Security Note:** If any required API key is missing or blank in `local.properties`, the build will intentionally fail with a descriptive error message (e.g. `Missing FINNHUB_API_KEY in local.properties`). No hardcoded fallback keys exist in the codebase.
