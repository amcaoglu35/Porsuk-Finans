# Porsuk Finans - Production Architecture Guide

## Overview
Porsuk Finans is a professional-grade investment platform built with a modular, offline-first, and AI-driven architecture. The system is designed for high performance, reliability, and security.

## Core Pillars

### 1. Hybrid AI Orchestration
The `AiMasterOrchestrator` manages the switching between **Cloud AI** (Gemini/OpenAI) and **Local AI** (Rule-based Fallback). It handles:
- **Failover**: Switches to rule-based Local AI when offline to ensure critical analysis remains available without internet.
- **Consensus**: Coordinates multi-agent opinions for a unified decision.
- **Caching**: Local Room-based caching for frequent analysis requests.

> [!NOTE]
> Current Local AI uses a deterministic rule engine (Kotlin + ta4j). Full on-device LLM (TFLite) integration is currently on the roadmap.

### 2. Centralized Event Bus
All modules communicate through `PorsukEventBus`. This enables complete decoupling.
- `PriceUpdated`: Triggers Alarms and UI updates.
- `PortfolioChanged`: Triggers rebalancing AI logic.
- `InternetStatus`: Orchestrates sync queues.

### 3. Enterprise Data Layer
- **Unified DB (v47)**: 87 entities covering everything from basic holdings to deep institutional analytics.
- **Room Migration Stratejisi**: Veri kayıplarını önlemek için üretim ortamında **yükseltme (upgrade) sırasında yıkıcı göç (destructive migration) kapalıdır**. Bunun yerine `DatabaseMigrations` kullanılmaktadır. Sadece sürüm düşürme (downgrade) durumunda `.fallbackToDestructiveMigrationOnDowngrade()` ile veriler sıfırlanır.
  - **MIGRATION_1_40**: Erken versiyonlar için temel iskelet (TODO: Geçmişe dönük tam destek).
  - **MIGRATION_40_43**: Ekonomik takvim, bilanço takvimi ve portföy motoru tabloları.
  - **MIGRATION_43_44**: Bulut senkronizasyon kuyruğu (`cloud_sync_queue`) ve kullanıcı cihazları (`user_devices`) tabloları.
  - **MIGRATION_44_45**: Otomasyon kuralları (`automation_rules`), geçmiş, AI önerileri, bildirim merkezi ve ajan performans metrikleri.
  - **MIGRATION_45_46**: Aracı kurum hesapları (`broker_accounts`), abonelik yetkileri, AI çalışma alanları ve temettü zekası.
  - **MIGRATION_46_47**: Güvenlik ve gizlilik denetim günlükleri (`engine_security_audit_logs`) ve aktif oturumlar (`engine_security_sessions`).
- **Multi-Provider Repositories**: Transparently switches between different APIs (Yahoo, Finnhub, Google) based on reliability and rate limits.

### 4. Security & Privacy
- **Encrypted Storage**: Sensitive data (API keys) are stored using `EncryptedSharedPreferences`.
- **Local Priority**: All basic calculations (RSI, Portfolio Math) are done in Kotlin to avoid sending data to the cloud unnecessarily.

## Developer Guide

### Setup & API Keys Configuration
Before building the project, copy `local.properties.example` to `local.properties` and fill in the required API keys:
- `FINNHUB_API_KEY`
- `FMP_API_KEY`
- `NEWS_API_KEY`
- `EXCHANGE_RATE_API_KEY`
- `FRED_API_KEY`
- `YAHOO_RAPIDAPI_KEY`

If any key is missing or empty in `local.properties`, the Gradle build will intentionally fail to prevent unauthenticated or hardcoded key builds.

### Adding a New Module
1. Define Domain Models in `domain/model/`.
2. Define Repository Interface in `domain/repository/`.
3. Implement in `data/repository/` using the `PorsukLogger`.
4. Register in Hilt module (`DatabaseModule` or specialized EngineModule).
5. Create UI using Jetpack Compose and Material 3.

### Logging Standards
- `PorsukLogger.i()`: Key lifecycle events.
- `PorsukLogger.e()`: All exceptions and network failures.
- `PorsukLogger.d()`: Verbose state changes (debug only).

## Performance Optimization
- **Compose**: Use `rememberSaveable` and state hoisting.
- **Room**: Use `Flow` for real-time reactive updates.
- **Threads**: Always use `Dispatchers.IO` for heavy IO and `Dispatchers.Default` for math/AI logic.
