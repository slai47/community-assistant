# Community Assistant

![WoWS and WoT Community Assistant logo](https://i.imgur.com/yhy0ELe.png)

Statistic applications for World of Warship & World of Tanks Players profiles to view changes over time, other players, individual vehicle information and other provided information.

## Summary

Applications built using the [Wargaming players API](https://developers.wargaming.net/). Specifically the World of Tanks PC and WoWS PC APIs. Using multiple player owned third party apis like Warships-today, tanks.gg, wot-numbers and more to compare player data too. Used community or similar methods to develop scores for players to evaluate a players record and ability into one number. Used threading and other optimizations to fix for slowdowns with the app's amount of data it needed to show. Used [Picasso](https://square.github.io/picasso/) for image loading, [Otto](https://square.github.io/otto/) and eventually [GreenRobot.Eventbus](https://github.com/greenrobot/EventBus) for communication needs from backend to frontend and in between UI elements of the code bases. Both projects were built using MVI patterns using an eventbus.


## Community Assistant for WoWS

![Image of WoWS Community Assistant](https://i.imgur.com/Mgc8Ihh.png)

Currently published on Google Play Store [here](https://play.google.com/store/apps/details?id=com.half.wowsca).

Stats application built using MVI with multiple activities/fragments and [GreenRobot.Eventbus](https://github.com/greenrobot/EventBus). Uses my old file storage process instead of a database for ease of creation at the time. Uses a third party api for average player data and has base files inside the app for default use. All backend communication code is in the `backend` package, business logic is in `managers` package and models tend to parse for themselves.

This project could use a lot of improvements. UI could get a better material design and have data shown in different manners. The backend code could be chopped up and have less code duplication along with the files being split for readability. Adding animations for better UX would be nice along with better use of styles and images to give the app a more concise look. The view layout files could be split up into snippets and potentially made reusable.


## Community Assistant for WoT

![Image of WoT Community Assistant](https://i.imgur.com/kbYasjV.png)

Currently unpublished due to lack of development time needed to update and third party data sources no longer updating.

Build using a one activity to rule them all architecture with Otto Eventbus for communications within the app. Backend folder contains all third party APIs. The Factory class in the model package helps with parsing the massive data sets. No sqlite database was used due to the speed improvements of the time in using small json text files instead. The `tools` package contains the managers for grabbing in and out of files, along with much of the business logic in the app.

The app is currently not runnable but needs to have its API code updated with new elements, WN8 data updated, otto updated to [GreenRobot.Eventbus](https://github.com/greenrobot/EventBus) and general updates from not being touched for 2+ years.


## Utils

Android library used to keep common files between WoWS and WoT profiles. Contains early implementations of a Preferences handler called `Prefs`, data storage handlers in `utilities/storage` for small text file speed improvements, an old interface styling for creating backend processes with third party information and old libraries used to help make certain things in Android easier.

### Repo state

Ported over to Github from BitBucket. The project is building and runs when targeting WoWS. WoT project needs work to get going again due to its age.

### General improvements and lessons learned

The apps could start using a database to better store the immense amount of data needed in these apps. To give you a quick idea, the largest datasets I had was a clan with 100 players, each with general profile data, up to 330 vehicles and 200+ badges on each of their profiles. An optimization I did was my saved profiles had stripped variable names that removed about 30% off the original saved files. While at the time, time to develop JSON file system saving processes was quicker than a sqlite database, easier to access and adjust with changes, sqlite and phones have gotten fast enough to just not care about those minor improvements anymore. A move to a database would greatly decrease code in this app as well.

While MVI is good overall, these projects show how bloated it can get. Especially with the amount of data getting set in the views, MVVM with live data/observable data could with help that. This would let the backend classes return whole objects and a simple set of the current profile for a view model would update the rest of the views without individual setters.

The layouts could be improved, split apart and reused so much. Looking back its bad code duplication that could be replicated with a listview in many places or even custom views that could help strip out lots of boilerplate or copy pastas. This would help lots of the activity and fragment code to be reduced.

The backend code is generally fine but could be updated as Asynctask has grown out of style. WoT's backend code base uses an old interface boilerplate code I used in my first job and created there to help reduce code duplication. Looking back, the code was solid. Looking now, over engineered and could be made a lot simpler. Using more modern libraries and parsing styles, the backend code could be heavily reduced. WoWS used more of a fire and forget methodology and this could be refined by using new techniques and better storage manners. Some code splitting would also help due to the immense amount of calculations and more.

This was home code in the end. Not always the best but it works and did what it needed to do in the time I had. Hindsight is always 20/20 so you can only learn from the past to not repeat it in the future. I enjoyed working on these and found many friends and learned new things. Moving on to better and newer things.

slai47/HALF



