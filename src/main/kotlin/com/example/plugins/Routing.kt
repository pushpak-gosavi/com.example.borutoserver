package com.example.plugins

import com.example.routes.getAllHeroes
import com.example.routes.root
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*

fun Application.configureRouting() {

    /*routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }*/
    routing {
        root()
        getAllHeroes()
        static ("/images"){
            resources("images")
        }
    }
}
