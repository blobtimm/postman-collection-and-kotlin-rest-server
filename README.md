# QAIQuest 2019: Testing your API's like a hero

![logo](http://qaiquest.org/2019/wp-content/uploads/2019/04/QUEST-2019-n.jpg)

A simple REST server and Postman collection for demoing points in our talk. At the very core of this project is the concept of increasing levels of complexity. We do this to establish a foundation with our audience. As we progress to Level 4 we will begin to see how we can use these techniques to test a dense graph of endpoints in an API environment. 

# Simple Kotlin based Rest API

This is an example application that uses the [Spark framework](http://sparkjava.com/) and [Kotlin](http://kotlinlang.org/) to write a small, concise and fast web application.

# How to start

1) Import the `postman_collection.json` into Postman.

2) Open a terminal on Mac, or other command line utility.

3) Clone this repo, or download and unpack the Zip.

4) Navigate into the `source` directory from your terminal. 

5) Type `./gradlew build` and hit enter. (this builds the code)

6) Type `java -jar build/libs/spark-webapp-fat-1.0.jar` and hit enter. (server is now running)

7) Open Postman, select the "cog" at the top right of the window.

8) Select "Add" (for creating a new environment)

9) Copy the following information into Postman. 
* "Environment Name" => "qaiquest_local"
* In the table, put a variable "base_url" with the initial value "http://localhost:4567/". This tells Postman where to point requests. 
* Click "Update"

10) Select "qaiquest_local" from the dropdown at the top left of the screen. 

11) Expand Level 1 folder, select the first API call, and select "Send"