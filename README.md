# kardia_android_crm
A Kardia CRM app for Android

# 1. Read the CRM Mobile App PDF in the Clean Architecture branch.
This file contains the design specifications for the app as given by Greg.

# 2. Branches
Currently the repo has two branches. The first branch, master, contains all of the original work I did on the app. The code is messy, and the logic/control flow is confusing. That being said, it does effectively access the API and store that data into an SQLite database, and displays most of it.

The second branch is CleanArhchitecture. In this branch I have begun redesigning the app around the idea of using a Clean Architecture approach. This branch would be the best place to start (and the rest of the readme will refer to this design rather than the original spaghetti code in master branch.

# 3. Clean Architecture

The app is designed around the concept of "clean architecture". See:

http://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html

https://vimeo.com/43612849

The idea of clean architecture is to decouple the business logic and important parts of the app from the specific frameworks and implementation details.

Additionally, I am using the Repository pattern for data persistence, and intend to use the MVP pattern for the Android UI. A dependency injector such as Dagger would also be useful.
