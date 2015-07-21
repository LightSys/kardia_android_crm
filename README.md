# kardia_android_crm
A Kardia CRM app for Android

# 1. Read the CRM Mobile App PDF in the Clean Architecture branch.
This file contains the design specifications for the app as given by Greg.

# 2. Clean Architecture

The app is designed around the concept of "clean architecture". See:

http://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html

https://vimeo.com/43612849

The idea of clean architecture is to decouple the business logic and important parts of the app from the specific frameworks and implementation details.

Additionally, I am using the Repository pattern for data persistence, and intend to use the MVP pattern for the Android UI. A dependency injector such as Dagger would also be useful.
