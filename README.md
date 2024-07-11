# Music Player Android App Using Jetpack Media3

This is a Music Player Android app that utilizes Jetpack Media3 libraries and other relevant AndroidX dependencies to play a list of songs and display an interactive playlist.

Screen recordings of working app:

[Music Player Main Demo Video](https://drive.google.com/file/d/1o3b9z_gMcxlgnniVv6G_ce8x3oVx5FoG/view?usp=drive_link)

[Demo with Dark Mode enabled](https://drive.google.com/file/d/1WLsGIGGS3SlL-EgklWMtLACk5rU0smL4/view?usp=drive_link)


## Building the App

1. Clone the repository in Android Studio using the URL: [MusicPlayer](https://github.com/nazlishahi/MusicPlayer.git).
2. Once all dependencies are downloaded and files are indexed, "Sync project with gradle files" and "Build".
3. select "Run 'app'" from the "Run" menu to run the app on an emulator or device of your choice.

## App Sections

The app consists of the following sections:

1. **Music Player Screen:**
    - Play/pause songs and navigate to the next or previous song using the buttons at the bottom of the screen.
    - View the current song name and artist information.

2. **Music Library Screen:**
    - Accessible via the top menu on the Music Player screen.
    - Highlights the current song.
    - Users can click on a song to play it.

## Highlighted Android Technologies

1. **Jetpack Media3**: For player functionality with a custom UI.
2. **Jetpack Navigation Component**: For robust navigation between activity and fragments.
3. **MVVM Pattern**: Using Activity and Fragments for views, ViewModel with Kotlin Coroutines, sealed classes, and LiveData.
4. **Hilt**: For dependency injection.
5. **JUnit and Mockito**: For unit testing.

## Implementation Thought Process

- The implementation focuses on a music player with basic controls (play/pause, forward, backward) and an interactive music library.
    - Each song in the library can be clicked to play immediately, with the player updating to the next song automatically.
- The music player is the default screen with navigation to and from the music library screen.
- Custom layout for the music player was created to align with Media3 ExoPlayer and required functionalities.
- The music library screen shows song information and highlights the current song without interaction initially.
- The music library is accessed from a dropdown menu on the player screen.
- Designed to support both light and dark modes, and portrait/landscape orientations.
- Handles edge cases, such as no files available to play, with an appropriate error message.

## Trade-offs, Technical Decisions, and Assumptions

### Trade-offs
1. **Custom View for Music Player Screen**:
    - Allows for custom UI components and app-specific functionality.
    - Requires additional customization for future features (e.g., song progress bar, artwork, shuffle).
    - The benefits outweigh the drawbacks for current and future flexibility.
2. **Music Library Access**:
    - Accessed through the menu item, hiding the ActionBar on the library screen to avoid redundancy.
    - Future versions may include a direct button for library access on the player screen.
3. **Unit Testing**:
    - An earlier version of Mockito is used for compatibility with Java 8.

### Assumptions
1. Music library access via menu item provides a cleaner UI for song information and playback controls.
2. ExoPlayer is decoupled from the player view for flexibility in playing selected songs from the library screen.

### Technical Decisions
- **Navigation Component**:
    - Base activity with default fragment container for navigation.
    - Facilitates static and dynamic flows and future functionality expansion.
- **Jetpack Media3**:
    - Chosen for its robust architecture, simplicity, and customization capabilities.
- **Dependency Injection**:
    - Single instance of ExoPlayer accessed via helper class.
    - Player interactions in `MainActivity` for non-UI updates and in `MusicPlayerFragment` for UI updates.
- **LiveData and Sealed Classes**:
    - Observed in `MainActivity` and `MusicLibraryFragment` for updates.

### Implementation Details

- **MVVM Separation of Concerns**:
    - Obtaining audio files from the assets folder is done in the ViewModel.
    - UseCase and Repository patterns were deemed unnecessary for this specific implementation.
    - File filtering uses `MimeType.isAudio()` which may need future adjustments.
- **Navigation Component**:
    - Activity initiates UI and navigates to fragments for player and library.
    - Helper class manages ExoPlayer with Activity scope.
- **Potential Future Implementations**:
    - Additional playback controls (shuffle, queue, song information).
    - Swipe gestures for song navigation.
    - Fetching and playing songs from remote sources.
    - Mini player for minimized music player screen.
    - Implement MediaService to display and interact with current song on notifications bar.
    - Espresso UI tests.
