# QAIQuest 2019: Testing your API's like a hero

![logo](http://qaiquest.org/2019/wp-content/uploads/2019/04/QUEST-2019-n.jpg)

A simple REST server and Postman collection for demoing points in our talk.

# Simple Kotlin based Rest API

This is an example application that uses the [Spark framework](http://sparkjava.com/) and [Kotlin](http://kotlinlang.org/) to write a small, concise and fast web application.

# How to start

1) Import the `postman_collection.json` into Postman.

2) Open a terminal on Mac, or other command line utility.

3) Clone this repo, or download and unpack the Zip.

4) Navigate into the `source` directory from your terminal. 

5) Type `./gradlew build` and hit enter. (this built the code)

6) Type `java -jar build/libs/spark-webapp-fat-1.0.jar` and hit enter. (server is now running)

7) Open Postman and begin sending API requests!