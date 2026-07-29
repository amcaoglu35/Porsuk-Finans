# News & Macro Economics Module Production Upgrade - Task List

- [ ] **Phase 1: Data & Domain Layer Enhancements**
    - [x] Add `MacroDataEntity` to `Entities.kt`.
    - [x] Update `AssetDao.kt` for macro data and news interaction.
    - [ ] Implement NewsAPI, FRED, and Finnhub fetch logic in `FinanceRepository.kt`.
    - [ ] Coordinate categorical news fetching and macro indicator syncing.

- [ ] **Phase 2: ViewModel Logic Implementation**
    - [ ] Update `NewsViewModel.kt` for category management, search, and AI-driven sentiment/summaries.
    - [ ] Update `MacroIntelligenceViewModel.kt` to transform FRED series into UI metrics and chart points.
    - [ ] Update `CalendarViewModel.kt` with filter and notification logic.

- [ ] **Phase 3: UI Module Upgrades**
    - [ ] `NewsScreen.kt`: Integrate `TabRow`, `SearchBar`, and zenginleştirilmiş haber kartları.
    - [ ] `MacroIntelligenceScreen.kt`: Build the macro dashboard with real-time grids and line charts.
    - [ ] `CalendarScreen.kt`: Enhance the economic calendar UI with country filters and event details.

- [ ] **Phase 4: Validation & Walkthrough**
    - [ ] Ensure offline cache functionality works as expected.
    - [ ] Create `walkthrough.artifact.md`.
