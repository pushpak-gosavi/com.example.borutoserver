package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.EnginePipeline.Companion.Call
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*


fun Application.configStatusPages(){
    install(StatusPages){
        status(HttpStatusCode.NotFound){
           call,status ->
            call.respond(message = "Page not Found", status= status)
        }
    }
}