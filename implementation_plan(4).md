# Feature 4: Booking Calendar Screen

Build a Booking screen (`BookingScreen`) that allows users to select a check-in and check-out date for a specific farmstay. It will calculate the total cost based on the number of nights plus a ₹500 activities fee, display a summary card, and simulate a Firestore write operation to create a pending booking.

## User Review Required

> [!IMPORTANT]
> Please review the architecture, specifically the use of the Compose Material 3 `DateRangePicker`. Currently, Jetpack Compose offers a built-in `DateRangePicker` component that provides a great out-of-the-box calendar experience.
> I will also add a "Book Now" button to the Farmstay cards on the Discover screen so you can actually navigate to this new booking flow.

## Open Questions

> [!NOTE]
> - Are you okay with using the standard Material 3 `DateRangePicker` UI, or do you require a highly customized custom-built calendar layout? (The Material 3 one looks great and handles date math effectively).
> - Since we are using dummy data, is it acceptable to simulate the "create booking" function with a standard Kotlin `delay` and a Toast message instead of attempting a real Firestore write that would crash?

## Proposed Changes

### Domain & Data Layers
#### [NEW] [domain/model/Booking.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/domain/model/Booking.kt)
Create a `Booking` data class: `id`, `farmstayId`, `checkInDate`, `checkOutDate`, `totalPrice`, `status` (defaults to "pending").

#### [NEW] [domain/repository/BookingRepository.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/domain/repository/BookingRepository.kt)
Interface to handle fetching unavailable dates and submitting bookings.
- `fun getUnavailableDates(farmstayId: String): Flow<List<Long>>` (Long as epoch milliseconds).
- `suspend fun createBooking(booking: Booking): Boolean`

#### [NEW] [data/repository/BookingRepositoryImpl.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/data/repository/BookingRepositoryImpl.kt)
Implementation that simulates fetching blocked dates (e.g., blocking out "tomorrow" just to show the feature works) and simulates writing to the Firestore `bookings` collection.

#### [MODIFY] [di/RepositoryModule.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/di/RepositoryModule.kt)
Bind the new `BookingRepository`.

### Presentation Layer
#### [NEW] [viewmodel/BookingViewModel.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/viewmodel/BookingViewModel.kt)
- Uses `SavedStateHandle` to retrieve the incoming `farmstayId`.
- Fetches the farmstay details (to get the nightly price) from `FarmstayRepository`.
- Computes `numberOfNights`, `totalCost` (nights * price + ₹500 fee).
- Handles the "Confirm Booking" logic.

#### [NEW] [ui/screens/BookingScreen.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/screens/BookingScreen.kt)
- Utilizes `DateRangePicker` for date selection.
- Shows a "Booking Summary" Card dynamically updating as dates are picked.
- "Confirm Booking" button that triggers the ViewModel save function.

#### [MODIFY] [ui/screens/HomeScreen.kt](file:///Users/dhanushhs/Desktop/grama-vasthi/app/src/main/java/com/example/gramavasathi/ui/screens/HomeScreen.kt)
- Update `FarmstayCard` to include a clickable "Book Now" button or make the whole card navigate to the Booking screen.

#### [MODIFY] [ui/navigation/Screen.kt] & [NavGraph.kt]
- Add `Screen.Booking("booking/{farmstayId}")`.
- Wire up the new screen passing the `farmstayId` nav argument.

## Verification Plan
### Manual Verification
- Compile and run the app.
- Go to Discover, tap "Book" on a farmstay.
- Ensure the DateRangePicker disables simulated "unavailable" dates.
- Select a range of 3 nights; verify the price calculation is (3 * price) + 500.
- Tap Confirm and verify the "pending" state logic runs successfully.
