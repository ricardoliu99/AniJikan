# AniJikan
This is an mobile application for Android devices. The application allows users to look at the anime (Japanese cartoons) airing or that have aired in the current year. It provides a weekly calendar, which includes a notification system, with the schedule for all anime from the current season. The user is also able to search for detailed information about each anime. You can download the APK of the app here: [download link](https://docs.google.com/uc?export=download&id=1O3VqU-fAW5S6ThAH9SZd6gn8AUf7xN43)

## Application process
The backend of the application was done in Kotlin and the frontend was done using Jetpack Compose. 
The application consists of two main screens and two other supporting screens showing states of loading or error when dealing with network and database requests. It retrieves information about anime from the [AniList GraphQL API](https://anilist.gitbook.io/anilist-apiv2-docs/overview/graphql/getting-started). [Apollo Kotlin](https://www.apollographql.com/docs/kotlin/) was used to fetch the network data from the API and generate Java models from my personal GraphQL queries. To store data locally on the mobile device, Room library was used. In addition, Coil library was used to load the images from the network efficiently and to integrate some gif images to the application.


## Demo
The Loading state, which refers to when the application is fetching data from the network or the local storage, looks as follows in the application:

<p align="center">
 <img src="https://raw.githubusercontent.com/ricardoliu99/AniJikan/master/demo_images/loading.jpg" height="450">
</p>

Another state is the Error state, which occurs when the data cannot be fetched from the network or the local storage. The follow image shows this state:

<p align="center">
 <img src="https://raw.githubusercontent.com/ricardoliu99/AniJikan/master/demo_images/error.jpg" height="450">
</p>

This error state provides a "Refresh" button that will try to fetch data from the network when pressed if the user regains access to WiFi on their mobile device.

The third and most complex state is the Success state. This state consists of two main screens. The "Home" screen consists of multiple sections. At the very top of this screen, there is a row of four tabs, each representing a different anime season and each one is indicated with an icon of winter, spring, summer or fall. Pressing a different tab will change the list of media displayed. For example, the following image shows the list of media when the winter icon is pressed:
<p align="center">
 <img src="https://raw.githubusercontent.com/ricardoliu99/AniJikan/master/demo_images/home.jpg" height="450">
</p>
The shown list of media consists of multiple cards displaying the title of the anime, the airing time of the next episode (if any or if the information is currently available) based on the local timezone of the user, a cover image of the anime and "down arrow" icon. When the down arrow icon is pressed, the card will expand and show a short summary of the anime as shown in the previous image.

The tab belonging to the current season will contain a calendar icon, which switches the view from the list of media view to a schedule view. In this case, the calendar icon only shows for the winter season because it is the current season at the time of this writing. The following image shows what happens when the calendar icon is pressed.

<p align="center">
 <img src="https://raw.githubusercontent.com/ricardoliu99/AniJikan/master/demo_images/schedule.jpg" height="450">
</p>

The schedule view shows the title and airing time of the next episode in a timeframe from the current day until next week, where each day is labeled as shown above. Additionally, each item of the list has a bell icon. When the user presses a bell icon, a notification reminder will be scheduled to trigger when the episode airs for the selected anime.

Back in the list of media view, there is a search bar that helps filtering the media from the current season as shown in the image below.

<p align="center">
 <img src="https://raw.githubusercontent.com/ricardoliu99/AniJikan/master/demo_images/filteredMedia.jpg" height="450">
</p>

Lastly, the user can press on any anime title from the list of media view and they will be redirected to another screen, the "Detailed Media" media screen. For instance, if the user presses on the title for "Pocket Monsters..." (from the third image), the following screen will be displayed:

<p align="center">
 <img src="https://raw.githubusercontent.com/ricardoliu99/AniJikan/master/demo_images/detailedMedia.jpg" height="450">
</p>

This deilted media screen shows the title of the selected anime at the top. Then, basic information such as alternate name, genres, source, episode count, start date, end date and animation studio is shown below the cover image for the anime. After the basic information, the synopsis of the anime is shown in a separate card. The bottom of this screen contains a scrollable horizontal list that displays an image of each character from the media and their Japanese voice actor/actress. The role of the character in the story (main, supporting, background, etc.) is shown at the top right corner of each character image and the name of the character is shown at the bottom of each character image. Each voice actor/actress name is also displayed at the bottom of each voice actor/actress image.
