# PhotoGallery
PhotoGallery is an app that fetches and displays the most interesting public photos of the day from [Flickr](https://www.flickr.com).
 
 ## Screenshots
 ![Photo Tab - Light](https://user-images.githubusercontent.com/89112108/152607858-c0175a16-fd4e-4213-a25a-024829d3548f.png)
 ![Photo Tab - Dark](https://user-images.githubusercontent.com/89112108/152608035-32adc95c-6d2c-44a4-aa70-fcb6a2ee6404.png)
 ![Web page - Light](https://user-images.githubusercontent.com/89112108/152608089-76f22a96-18c2-4a5c-a934-5e7c4811eca5.png)
 ![Web page - Dark](https://user-images.githubusercontent.com/89112108/152608132-7e9b8c22-71c6-4cc4-8e01-f7fbe34df487.png)
 ![PhotoGallery notification](https://user-images.githubusercontent.com/89112108/152608197-0ecbc375-2844-42e6-a59b-b3791323b622.png)

 ## Features
 * User can search for specific photos like "dog" or "sky" and get desired results.
 * With a click on any photo, a web view will pop up, showing more details of the clicked photo on [the Flickr webpage](https://www.flickr.com) while still on the app.
 * Supports dark mode.
 * In the app settings, user can toggle dark mode on or off.
 * It is also possible to choose web view from three available options in the settings.
 * Notification pops up when there are new pictures but the app is not currently in use.
 * With one of the web views comes a nice functionality - sharing photo link through any other app.

 ## Libraries
  * [Retrofit](https://github.com/square/retrofit)
  * [Gson](https://github.com/google/gson)
  * Android Architectural Components ([ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel), [LiveData](https://developer.android.com/topic/libraries/architecture/livedata))
  * [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
