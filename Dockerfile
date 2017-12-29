FROM java:openjdk-8-jre
MAINTAINER Hesham Massoud "hesham.massoud@commercetools.de"
COPY /build/libs/coeur-sync.jar /home/coeur-sync.jar
CMD ["java","-jar","/home/coeur-sync.jar"]