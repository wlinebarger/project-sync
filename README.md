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
- [Contributing](#contributing)
  - [Build](#build)
      - [Run unit tests](#run-unit-tests)
      - [Package JARs](#package-jars)
      - [Package JARs and run tests](#package-jars-and-run-tests)
      - [Full build with tests, but without install to maven local repo (Recommended)](#full-build-with-tests-but-without-install-to-maven-local-repo-recommended)
      - [Install to local maven repo](#install-to-local-maven-repo)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Usage
### Prerequisites
 
 - install Java 8
 - a target CTP project to which your source of data would be synced to.
 - Set the following environment variables before running the application
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
   java -jar build/libs/coeur-category-sync.jar
   ```   
   
## Contributing

- Every PR should address an issue on the repository or JIRA of COEUR. If the issue doesn't exist, please create it first.
- Pull requests should always follow the following naming convention: 
`[issue-number]-[pr-name]`. For example,
to address issue CDL-2055 which refers to a style bug, the PR addressing it should have a name that looks something like
 `CDL-2055-fix-style-bug`.
- Commit messages should always be prefixed with the number of the issue that they address. 
For example, `CDL-2055: Remove redundant space.`
- After your PR is merged to master:
    - Delete the branch.
    - Mark the issue it addresses with the `merged-to-master` label.
    - Close the issue **only** if the change was released.

### Build
##### Run unit tests
````bash
./gradlew test
````

##### Package JARs
````bash
./gradlew clean jar
````

##### Package JARs and run tests
````bash
./gradlew clean check
````

##### Full build with tests, but without install to maven local repo (Recommended)
````bash
./gradlew clean build
````

##### Install to local maven repo
````bash
./gradlew clean install
````
<!-- TODO
##### Publish to Bintray
````bash
./gradlew clean -Dbuild.version={version} bintrayUpload
````-->
