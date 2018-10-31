![WoWS and WoT Community Assistant logo](https://i.imgur.com/yhy0ELe.png)

# community-assistant
Stats application for World of Warship & World of Tanks Players profiles to view changes over time.

## Summary

Player stats applications built using the [Wargaming players API](https://developers.wargaming.net/). Specifically the World of Tanks PC and WoWS PC APIs.


![Image of WoWS Community Assistant](https://i.imgur.com/Mgc8Ihh.png)

### Community Assistant for WoWS

Currently published on Google Play Store [here](https://play.google.com/store/apps/details?id=com.half.wowsca).

Stats application built using MVI with multiple activities/fragments and [GreenRobot.Eventbus](https://github.com/greenrobot/EventBus). Uses my old file storage process instead of a database for ease of creation at the time. Uses a third party api for average player data and has base files inside the app for default use. All backend communication code is in the `backend` package, business logic is in `managers` package and models tend to parse for themselves.

This project could use a lot of improvements. UI could get a better material design and have data shown in different manners. The backend code could be chopped up and have less code duplication along with the files being split for readability. Adding animiations for better UX would be nice along with better use of styles and images to give the app a more concise look. The view layout files could be split up into snippets and potentially made reusable.


![Image of WoT Community Assistant](https://i.imgur.com/kbYasjV.png)

### Community Assistant for WoT

Currently unpublished due to lack of development time needed to update and third party data sources no longer updating.

Build using a one activity to rule them all architecture with Otto Eventbus for communications within the app. Backend folder contains all third party APIs. The Factory class in the model package helps with parsing the massive data sets. No sqlite database was used due to the speed improvements of the time in using small json text files instead. The `tools` package contains the managers for grabbing in and out of files, along with much of the business logic in the app.

The app is currently not runnable but needs to have its API code updated with new elements, WN8 data updated, otto updated to [GreenRobot.Eventbus](https://github.com/greenrobot/EventBus) and general updates from not being touched for 2+ years.


### Utils

Android library used to keep common files between WoWS and WoT profiles. Contains early implementations of a Preferences handler called `Prefs`, data storage handlers in `utilities/storage` for small text file speed improvements, an old interface styling for creating backend processes with third party information and old libraries used to help make certain things in Android easier.

## Repo state

Ported over to Github from BitBucket. The project is building and runs when targeting WoWS. WoT project needs work to get going again due to its age.