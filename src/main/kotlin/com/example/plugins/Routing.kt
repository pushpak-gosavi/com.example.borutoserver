package com.example.plugins

import com.example.routes.getAllHeroes
import com.example.routes.root
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {

    /*routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }*/
    routing {
        root()
        getAllHeroes()
    }
}
