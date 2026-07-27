# Porsuk Finans - Production Architecture Guide

## Overview
Porsuk Finans is a professional-grade investment platform built with a modular, offline-first, and AI-driven architecture. The system is designed for high performance, reliability, and security.

## Core Pillars

### 1. Hybrid AI Orchestration
The `AiMasterOrchestrator` manages the switching between **Cloud AI** (Gemini/OpenAI) and **Local AI** (TFLite/On-device). It handles:
- **Failover**: Switches to Local AI when offline.
- **Consensus**: Coordinates multi-agent opinions for a unified decision.
- **Caching**: Local Room-based caching for frequent analysis requests.

### 2. Centralized Event Bus
All modules communicate through `PorsukEventBus`. This enables complete decoupling.
- `PriceUpdated`: Triggers Alarms and UI updates.
- `PortfolioChanged`: Triggers rebalancing AI logic.
- `InternetStatus`: Orchestrates sync queues.

### 3. Enterprise Data Layer
- **Unified DB (v45)**: Over 60 entities covering everything from basic holdings to deep institutional analytics.
- **Multi-Provider Repositories**: Transparently switches between different APIs (Yahoo, Finnhub, Google) based on reliability and rate limits.

### 4. Security & Privacy
- **Encrypted Storage**: Sensitive data (API keys) are stored using `EncryptedSharedPreferences`.
- **Local Priority**: All basic calculations (RSI, Portfolio Math) are done in Kotlin to avoid sending data to the cloud unnecessarily.

## Developer Guide

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
