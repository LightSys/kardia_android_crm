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

This is a useful resource and reference for Dagger, Clean Architecture, etc: https://github.com/android10/Android-CleanArchitecture

# 3. RxJava

I'm planning on using RxJava to handle moving blocking events off the UI thread. The above example also uses RxJava. See also:

https://www.youtube.com/watch?v=k3D0cWyNno4

https://t.co/GYG99BzW77

# 4. Read and use the wiki

I've put important information on the Wiki pages for this repository. Most of the planning behind the models and architecture of the application is recorded there.
