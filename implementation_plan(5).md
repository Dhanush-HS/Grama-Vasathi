# Feature 6: Search & Profile Screens

Build two new screens: a Search screen for finding farmstays by text and district, and a Profile screen showing guest history and a gateway to the Host Wizard. 

## User Review Required

> [!IMPORTANT]
> Since we now have multiple primary screens (Activities, Discover, Search, Profile), relying solely on buttons inside screens to navigate is becoming cumbersome. I propose adding a **Bottom Navigation Bar** to the app to easily switch between these 4 main tabs. 

## Open Questions

> [!NOTE]
> - Do you approve of adding a standard Material 3 **Bottom Navigation Bar** (`Activities`, `Discover`, `Search`, `Profile`) to the app's main layout?
> - For the Profile screen's "past stays", I will simulate some dummy past bookings data for the default user. Is this acceptable?

## Proposed Changes

### Domain & Data Layers
#### [NEW] [domain/model/UserProfile.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/domain/model/UserProfile.kt)
Create a `UserProfile` data class: `name: String`, `aggregateRating: Double`, `pastStays: List<Farmstay>`.

#### [NEW] [domain/repository/ProfileRepository.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/domain/repository/ProfileRepository.kt) & [data/repository/ProfileRepositoryImpl.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/data/repository/ProfileRepositoryImpl.kt)
Create a repository that returns simulated dummy data for the current user, including 1 or 2 past farmstays and an aggregate guest rating. Update `RepositoryModule` to bind this.

#### [MODIFY] [domain/repository/FarmstayRepository.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/domain/repository/FarmstayRepository.kt) & [data/repository/FarmstayRepositoryImpl.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/data/repository/FarmstayRepositoryImpl.kt)
Add a `searchFarmstays(query: String, district: String?)` function. The dummy implementation will filter the hardcoded list by checking if the query string matches the village name, location (district), or any activity tags, and optionally filter by the selected district chip.

### Presentation Layer
#### [NEW] [viewmodel/SearchViewModel.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/viewmodel/SearchViewModel.kt)
Manage the search query state, selected district state, and expose a list of filtered `Farmstay` results.

#### [NEW] [ui/screens/SearchScreen.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/screens/SearchScreen.kt)
- A text `OutlinedTextField` with a search icon for querying.
- A `LazyRow` of filter chips for specific districts (e.g., Mysore, Coorg, Ooty).
- A `LazyColumn` rendering `FarmstayCard`s based on the search results.

#### [NEW] [viewmodel/ProfileViewModel.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/viewmodel/ProfileViewModel.kt)
Fetch and expose the `UserProfile` data.

#### [NEW] [ui/screens/ProfileScreen.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/screens/ProfileScreen.kt)
- Display an avatar, name, and star rating.
- Display a list of past stays.
- A prominent "Become a Host" button that triggers navigation.

#### [MODIFY] [ui/navigation/NavGraph.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/navigation/NavGraph.kt) & [Screen.kt]
- Add `Screen.Search` and `Screen.Profile` routes.
- If Bottom Navigation is approved, restructure the `Scaffold` in the main app layout to include a `NavigationBar`. Otherwise, add navigation icons to the Top App Bars of existing screens to link to Search and Profile.

## Verification Plan
- Compile and run the app.
- Type in the search box (e.g., "Pottery" or "Ooty") and verify the results filter correctly.
- Select a district chip and verify results are narrowed down.
- Go to the Profile screen, verify past stays are displayed.
- Tap "Become a Host" and verify it successfully routes to the 5-step Host Training Wizard.
