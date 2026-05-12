# Feature 3: Farm Activities Screen

Build a grid-based Activities screen (`ActivitiesScreen`) displaying 8 core farm activities. Each card will show an icon, the activity name, and the count of available farmstays offering that activity. Tapping a card will navigate the user to the Discover screen, automatically filtering the results for the selected activity.

## User Review Required

> [!IMPORTANT]
> Please review the proposed navigation changes. To make the cards navigate to the filtered Discover results, I need to update the `Home` route in the navigation graph to accept a `filter` argument. 

## Open Questions

> [!NOTE]
> - Do you want the `ActivitiesScreen` to be the new `startDestination` of the app when it launches, replacing the temporary Host Wizard start destination?
> - For the icons, I will use standard Material Icons (e.g., a bird for Birdwatching, a restaurant icon for Local Cooking). Is that acceptable?

## Proposed Changes

### Domain & Data Layers
#### [NEW] [domain/model/ActivityItem.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/domain/model/ActivityItem.kt)
Create a simple data class to represent the UI state of an activity card: `name: String`, `count: Int`. (Icons will be mapped in the UI layer).

#### [MODIFY] [data/repository/FarmstayRepositoryImpl.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/data/repository/FarmstayRepositoryImpl.kt)
I will ensure our dummy data array contains at least one instance of "Coffee Picking" and "Organic Farming" so that none of the counts show as `0` when you test it.

### Presentation Layer
#### [NEW] [viewmodel/ActivitiesViewModel.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/viewmodel/ActivitiesViewModel.kt)
Create a ViewModel that fetches all farmstays from `FarmstayRepository`, then dynamically computes the count of farmstays offering each of the 8 activities. It will expose a `StateFlow<List<ActivityItem>>`.

#### [NEW] [ui/screens/ActivitiesScreen.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/screens/ActivitiesScreen.kt)
Build the screen using a `LazyVerticalGrid` (with 2 columns). 
Each card will be a rounded Material 3 surface displaying:
- An appropriate `ImageVector` (Material Icon).
- The activity name in bold.
- The computed count of available stays.
- An `onClick` lambda that triggers navigation.

#### [MODIFY] [viewmodel/HomeViewModel.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/viewmodel/HomeViewModel.kt)
Inject `SavedStateHandle` to retrieve the `filter` argument passed via navigation, setting it as the initial value for the Discover screen's selected filter instead of defaulting to `"All"`.

#### [MODIFY] [ui/navigation/Screen.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/navigation/Screen.kt)
- Add `object Activities : Screen("activities")`
- Modify the `Home` route constructor to create a route with arguments: `fun createRoute(filter: String) = "home?filter=$filter"`

#### [MODIFY] [ui/navigation/NavGraph.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/navigation/NavGraph.kt)
- Add the `Activities` composable route.
- Update the `Home` composable route to accept the `navArgument("filter")`.
- Change `startDestination` to the Activities Screen (or keep it as is based on your feedback).

## Verification Plan
### Manual Verification
- Compile the app successfully.
- Ensure the Activities grid renders correctly with 8 cards.
- Ensure tapping an activity card (e.g., "Local Cooking") correctly navigates to the Discover screen and pre-selects the "Local Cooking" filter chip, updating the farmstay list automatically.
