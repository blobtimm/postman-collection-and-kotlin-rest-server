package com.bajabob.qaiquest2019

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import spark.Spark.*
import java.sql.ResultSet

data class Error(val message: String)

data class Person(
        val id: Long,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("last_name")
        val lastName: String?,
        @SerializedName("username")
        val username: String?,
        @SerializedName("date_created")
        val dateCreated: LocalDate) {

    @SerializedName("date_created_pretty")
    val dateCreatedPretty: String
    init {
        dateCreatedPretty = "${dateCreated.year}-${dateCreated.monthOfYear}-${dateCreated.dayOfMonth}"
    }
}

val authToken = "some-secret"
val clients = listOf("android", "ios", "web")
val gson = Gson()

fun main() {
    val jdbc = Jdbc()
    createTablesAndData(jdbc)

    val personMapper = {
        rs: ResultSet -> Person(
            rs.getLong("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("username"),
            LocalDate(rs.getString("date_created"))
    )}

    staticFiles.location("/static")
    redirect.get("/", "/index.html")


    /**
     * Level 1
     */
    get("/person", { req, res ->
        res.type("application/json")
        if(req.queryParams("username") != null) {
            jdbc.queryForList("SELECT * FROM person WHERE username = ?", personMapper, req.queryParams("username"))
        } else if(req.queryParams("first_name") != null) {
            onError(500, "Internal server error!")
        } else {
            jdbc.queryForList("SELECT * FROM person", personMapper)
        }
    }, gson::toJson)

    /**
     * Level 2
     */
    get("/person-auth", { req, res ->
        res.type("application/json")

        val auth = req.headers("auth")
        if (auth == null || !auth.equals(authToken, ignoreCase = true)) {
            onError(400, "Missing/invalid auth!")
        }

        val client = req.headers("client-id")
        if (client == null || !clients.contains(client.toLowerCase())) {
            onError(400, "Missing/invalid client-id!")
        }

        jdbc.queryForList("SELECT * FROM person", personMapper)

    }, gson::toJson)

    /**
     * Level 3
     */
    post("/person") { req, res ->
        res.type("application/json")

        val person = gson.fromJson(req.body(), Person::class.java)

        if (person.firstName == null) {
            onError(400, "Please specify a first_name")
        }

        if (person.lastName == null) {
            onError(400, "Please specify a last_name")
        }

        if (person.username == null) {
            onError(400, "Please specify a username")
        }

        val dateCreated = LocalDate.now().toString(DateTimeFormat.forPattern("yyyy-MM-dd"))
        jdbc.execute("INSERT INTO person (first_name, last_name, username, date_created) VALUES (?, ?, ?, ?)", person.firstName, person.lastName, person.username, dateCreated)
        halt(201)
    }

}

private fun onError(status: Int, message: String) {
    halt(status, gson.toJson(Error(message)))
}

private fun createTablesAndData(jdbc: Jdbc) {
    val initialSql = jdbc.javaClass.classLoader.getResource("initialData.sql").readText()
    jdbc.execute(initialSql)
}