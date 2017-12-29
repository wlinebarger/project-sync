[![Build Status](https://travis-ci.com/commercetools/project-coeur-sync.svg?token=g8WsNzbMTq7LVae4BoPF&branch=master)](https://travis-ci.com/commercetools/project-coeur-sync)
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
  - [Build and Deployment](#build-and-deployment)
    - [Build](#build)
    - [Deployment](#deployment)
- [License](#license)

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
   ./gradlew clean shadowJar
   ```
 - To run the product sync
   ```bash
   java -jar build/libs/coeur-sync.jar -s products
   ```  
    
- To run the category sync
   ```bash
   java -jar build/libs/coeur-sync.jar -s categories
   ```  
   
- Usage
  ```bash
  usage: project-coeur-sync
   -h,--help         Print help information to System.out.
   -s,--sync <arg>   Choose which sync module to run. "products" runs
                     product sync. "categories" runs category sync.
   -v,--version      Print the version of the application.
  ```
    
### Build and Deployment

#### Build 
 On each push to the remote github repository, a Docker image is built. Every image has the following tags:
 - `latest` (for master branch) or branch name (any other branch)
 - short git commit SHA (first 8 chars), e.g. `11be0178`
 - tag containing the travis build number, e.g. `travis-17`
 
#### Deployment
 This job is currently being deployed as an iron.io worker. In order to setup the worker, it is
 required to have the following:
 - Access to the [coeur-production iron.io project](https://hud-e.iron.io/worker/projects/57baae114efcd50007b84e66/tasks).
 - [Iron.io CLI](https://github.com/iron-io/ironcli) installed
 - Docker installed and running
 - A Docker Hub account with access to the `ctpcoeur` organization
 
 Then to deploy:
 - If you haven't already, set up Iron.io to work with your Dockerhub account so that it can access private repositories:
   ```bash
   iron docker login -e <DockerHub-email> -u <DockerHub-username> -p <DockerHub-password>
   ```
 - Then set up a worker as follows:
   ```bash
    IRON_PROJECT_ID=<iron-project-id> IRON_TOKEN=<iron-project-token> \
    iron worker upload \
    -e COEUR_SOURCE_PROJECT_KEY='<ctp-source-project-key>' \
    -e COEUR_SOURCE_CLIENT_ID='<ctp-source-client-id>' \
    -e COEUR_SOURCE_CLIENT_SECRET='<ctp-source-client-secret>' \
    -e COEUR_TARGET_PROJECT_KEY='<ctp-target-project-key>' \
    -e COEUR_TARGET_CLIENT_ID='<ctp-target-client-id>' \
    -e COEUR_TARGET_CLIENT_SECRET='<ctp-target-client-secret>' \
    -name <worker-name>  ctpcoeur/category-sync:<current-version>
   ```
 - For staging deployment, use the docker image with the tag `latest`, which is automatically built on travis after merging to master
 branch.
 - For production deployment, use the docker image with the tag `production`. 
    - In order to create it, you need to create a new git commit tag. This will trigger a new Travis build as described
  before but will create an additional Docker tag `production`. 
    - The tag can be created via command line
         ```bash
         git tag -a v1.0.1 -m "Minor text adjustments."
         ```
        or github UI "Draft new Release":
        https://github.com/commercetools/project-coeur-sync/releases
        
  - Post deployment on iron.io, it is important to
    - Setup a new task for the newly uploaded code (via the iron.io web UI). The old tasks remain scheduled for workers 
    uploaded in the past.
    - Setup the _max retries_ to `2` for the worker code, via the web UI. 

## License
Copyright (c) 2018 commercetools
