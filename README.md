### Music player Android app using Jetpack media3

This is a Music Player android app that uses Jetpack media3 libraries and other relevant androidx dependencies to play a list of songs and show a read-only playlist with the current song highlighted.

### Building the app

On Android Studio, clone the repo using this web url: https://github.com/nazlishahi/MusicPlayer.git
Once all dependencies are downloaded and files are indexed, from the "Run" menu, click on "Run 'app'" on an emulator or device of your choice.

### App sections

The app consists of the following sections:

1. A music player screen, where users can
   * Play/pause a song and move to the next or previous song, using the corresponding buttons on the bottom of the screen.
   * View current song name and artist information.
2. A music library screen, which can be accessed via the top menu on the music player screen.
   * This is a read-only screen, as per project requirements that highlights the current song.
  
### Highlighted Android technologies

1. Jetpack Media3 for player functionality, with a custom ui
2. Jetpack Navigation Component for robust navigation between activity and two fragments
3. MVVM pattern with a combination of Activity and Fragments for views, ViewModel with kotlin coroutines, sealed classes and LiveData
4. Hilt for dependency injection
5. JUnit and Mockito for unit testing
   
