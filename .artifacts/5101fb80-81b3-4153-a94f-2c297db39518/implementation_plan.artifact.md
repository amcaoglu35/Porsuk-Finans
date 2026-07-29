# Fix failing MacroIntelligenceViewModelTest

The test `loadMacroData updates uiState with indicators and AI outlook` in `MacroIntelligenceViewModelTest` is failing because `uiState` is created using `stateIn(..., SharingStarted.WhileSubscribed(5000), MacroIntelligenceUiState())`.

In the test environment, `uiState.value` returns the initial value `MacroIntelligenceUiState()` because there are no active collectors to trigger the `combine` block. The default `activeTab` in `MacroIntelligenceUiState` is `GLOBAL_HEATMAP`, but the test expects `INFLATION` (which is the value set in `MacroIntelligenceViewModel`).

## Proposed Changes

### [Component: Macro Intelligence]

#### [MODIFY] [MacroIntelligenceViewModelTest.kt](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/app/src/test/java/com/nexus/porsuk/feature/macro/MacroIntelligenceViewModelTest.kt)
- Update the test to collect the `uiState` flow, ensuring the `combine` block executes and updates the state.

#### [MODIFY] [MacroIntelligenceViewModel.kt](file:///C:/Users/amcao/AndroidStudioProjects/Porsuk/app/src/main/java/com/nexus/porsuk/feature/macro/MacroIntelligenceViewModel.kt)
- Update the `stateIn` initial value to match the ViewModel's intended initial tab (`INFLATION`) for consistency, even if not strictly required for the test if collection is added.
- Add missing import for `MacroIndicatorCategory` to clean up the code.

## Verification Plan

### Automated Tests
- Run `app:testDebugUnitTest` to verify that all tests pass, including the fixed one.
