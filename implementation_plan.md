# Feature 2: Host Training Wizard

Build a 5-step wizard for host onboarding (`HostWizardScreen`). The steps include Basic Setup, Facilities, Media, Safety & Food, and Pricing. It tracks a readiness score based on 10 checklist items (2 per step) and stores this progress in Firestore under the host's UID.

## User Review Required

> [!IMPORTANT]
> Please review the architecture, particularly the data model and the fallback strategy for Firestore. Since we don't have real Firebase authentication or Firestore live yet, I plan to use a simulated "dummy host UID" and a local state fallback (just like we did for the Discover screen) to ensure the UI works perfectly right away. 

## Open Questions

> [!NOTE]
> - Do you want the user to be able to swipe between steps like a carousel, or use standard "Next/Previous" buttons at the bottom of the screen?
> - For the checklist items, do you want them to be simple clickable rows with checkboxes, or something more complex like expandable cards with descriptions?

## Proposed Changes

### Domain & Data Layers
#### [NEW] [domain/model/WizardProgress.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/domain/model/WizardProgress.kt)
Create data classes to represent the steps and checklist items:
- `WizardStep` (enum): `BASIC_SETUP`, `FACILITIES`, `MEDIA`, `SAFETY_FOOD`, `PRICING`.
- `ChecklistItem`: `id`, `text`, `isCompleted`.
- `WizardProgress`: `hostUid`, Map of `WizardStep` to List of `ChecklistItem`, and a helper property to calculate `readinessScore` (0-100%).

#### [NEW] [domain/repository/HostWizardRepository.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/domain/repository/HostWizardRepository.kt)
Interface with functions:
- `fun getProgress(hostUid: String): Flow<WizardProgress>`
- `suspend fun updateProgress(progress: WizardProgress)`

#### [NEW] [data/repository/HostWizardRepositoryImpl.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/data/repository/HostWizardRepositoryImpl.kt)
Implementation utilizing Firebase Firestore to store progress under the collection path `hosts/{uid}/wizard_progress/main`. I will include a local state dummy fallback if Firebase throws exceptions (so you can test it immediately).

#### [MODIFY] [di/RepositoryModule.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/di/RepositoryModule.kt)
Bind the `HostWizardRepository`.

### Presentation Layer
#### [NEW] [viewmodel/HostWizardViewModel.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/viewmodel/HostWizardViewModel.kt)
A ViewModel to manage the state. It will fetch the dummy user's progress, hold the currently selected step index, and expose functions to toggle checkboxes and save progress.

#### [NEW] [ui/screens/HostWizardScreen.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/screens/HostWizardScreen.kt)
Build the wizard UI containing:
- A linear progress indicator showing overall readiness (0-100%).
- A step indicator (e.g., clickable tabs or row for the 5 steps).
- A column of 2 checklist items for the currently active step.
- Checkboxes/rows that use `EarthyGreen` when completed and `Amber/Orange` when pending.

#### [MODIFY] [ui/navigation/Screen.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/navigation/Screen.kt)
Add `HostWizard` to the routing paths.

#### [MODIFY] [ui/navigation/NavGraph.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/navigation/NavGraph.kt)
Register the new route. As a bonus, I can temporarily make the starting destination the `HostWizard` screen so you can immediately see it upon launching the app.

## Verification Plan
### Manual Verification
- Compile the app successfully.
- Ensure tapping a checklist item immediately turns it green, updates the readiness score in the progress bar, and logs the mock "save to Firestore" action.
- Ensure pending items are displayed in Amber.
