# project-coeur-sync
<!-- TODO [![Build Status]()
[![codecov]()-->

Java application which synchronises staging commercetools project data of coeur to production 
existing commercetools project.


<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Usage](#usage)
  - [Prerequisites](#prerequisites)
  - [Run the application](#run-the-application)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Usage
### Prerequisites
 
 - install Java 8
 - a target CTP project to which your source of data would be synced to.
 - set the following environment variables before running the application
   ```bash
   export COEUR_SOURCE_PROJECT_KEY = xxxxxxxxxxxxx
   export COEUR_SOURCE_CLIENT_ID = xxxxxxxxxxxxxxx
   export COEUR_SOURCE_CLIENT_SECRET = xxxxxxxxxxx
   export COEUR_TARGET_PROJECT_KEY = xxxxxxxxxxxxx
   export COEUR_TARGET_CLIENT_ID = xxxxxxxxxxxxxxx
   export COEUR_TARGET_CLIENT_SECRET = xxxxxxxxxxx
   ```
   
### Run the application   
 - First, package the JAR
   ```bash
   ./gradlew clean jar
   ```
 - Then run the JAR
   ```bash
   java -jar build/libs/category-sync.jar
   ```   
