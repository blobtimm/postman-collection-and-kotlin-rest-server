package com.blobtimm.qaiquest2019

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import spark.Spark.*
import java.sql.ResultSet

data class Error(val message: String)

data class UserAction (
        @SerializedName("auth_token")
        val authToken: String?
)

data class UserActionResponse(
        val message: String
)

data class Login(
        val username: String?,
        val password: String?
)

data class LoginResponse(
        @SerializedName("auth_token")
        val authToken: String
)

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

val userSecret = "secret55"
val authToken = "some-secret"
val clients = listOf("android", "ios", "web")
val gson = Gson()

fun main() {
    val jdbc = Jdbc()
    createTablesAndData(jdbc)

    val personMapper = {
        rs: ResultSet ->
        Person(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                LocalDate(rs.getString("date_created"))
        )
    }

    staticFiles.location("/static")
    redirect.get("/", "/index.html")


    /**
     * Level 1
     * Structuring simple GET request
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
     * Decomposing error responses from the server so that we can brute force a solution
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


    /**
     * Level 4
     * 1) Provides an interface to log in and obtain a secret token
     * 2) Provides an interface to use that secret token in order to do some task
     */
    post("/login") { req, res ->
        res.type("application/json")

        val login = gson.fromJson(req.body(), Login::class.java)

        if (login.username == null) {
            onError(400, "Please specify a username")
        }

        if (login.password == null) {
            onError(400, "Please specify a last_name")
        }

        /**
         * This is just and example, in a more robust solution we would query a db for a hashed password once we
         * validated that the user actually existed. Possibly track login attempts too :-)
         */
        if (login.username.equals("bob", ignoreCase = true)) {
            if (login.password.equals("Test1234", ignoreCase = false)) {
                // we have a successful login!
                halt(200, gson.toJson(LoginResponse(userSecret)))
            } else {
                onError(404, "Invalid username / password")
            }
        } else {
            onError(404, "Invalid username / password")
        }
    }

    /**
     * We made this a "put" because it is changing the state of the user's account
     */
    put("/update-account") { req, res ->
        res.type("application/json")

        val userAction = gson.fromJson(req.body(), UserAction::class.java)

        if (userAction.authToken == null) {
            onError(400, "Please specify an auth token")
        }

        /**
         * here we validate the auth token we got in the previous login response
         * so that the user can do some authorized action!
         */
        if (!userAction.authToken.equals(userSecret)) {
            onError(400, "Invalid secret supplied")
        }

        /**
         * Here is where we know we can do something authorized with the user account!
         */
        halt(200, gson.toJson(UserActionResponse("You made 'bob' and admin!")))
    }
}

private fun onError(status: Int, message: String) {
    halt(status, gson.toJson(Error(message)))
}

private fun createTablesAndData(jdbc: Jdbc) {
    val initialSql = jdbc.javaClass.classLoader.getResource("initialData.sql").readText()
    jdbc.execute(initialSql)
}