FROM ubuntu:12.04
MAINTAINER Hesham Massoud "heshamhamdymassoud@gmail.com"
COPY coeur-category-sync.jar /home/coeur-category-sync.jar
CMD ["java","-jar","/home/coeur-category-sync.jar"]