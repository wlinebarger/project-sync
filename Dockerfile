FROM java:openjdk-8-jre
MAINTAINER Hesham Massoud "hesham.massoud@commercetools.de"
COPY /build/libs/coeur-category-sync.jar /home/coeur-category-sync.jar
CMD ["java","-jar","/home/coeur-category-sync.jar"]