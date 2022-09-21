package com.example

import com.example.models.ApiResponse
import com.example.models.Hero
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.example.plugins.*
import com.example.repository.HeroRepository
import io.ktor.client.call.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
class ApplicationTest {
    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun `access root endpoint, assert correct information`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Welcome to Boruto API", bodyAsText())
        }
    }

    @Test
    fun `access all heroes endpoint, assert correct information`()
       /*withTestApplication (moduleFunction = Application::module){
            handleRequest (HttpMethod.Get, "/boruto/heroes").apply{
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = true,
                    message = "ok",
                    prevPage = null,
                    nextPage = 2,
                    heroes = heroRepository.page1
                )

                val  actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                assertEquals(
                    expected = expected,
                    actual = actual)
            }
       }
    }*/
    = testApplication {
        environment {
            developmentMode = false
        }
        application {
            configureRouting()
        }
         client.get("/boruto/heroes"). apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            val expected = ApiResponse(
                success = true,
                message = "ok",
                prevPage = null,
                nextPage = 2,
                heroes = heroRepository.page1,
            )
            val actual = Json.decodeFromString<ApiResponse>(body())
             println("Expected: $expected")
             println("Actual: $actual")
            assertEquals(
                expected = expected,
                actual = actual
            )
        }
    }
//    @Test
//    fun `access all heroes endpoint, get all heroes assert correct information` ()
//            = testApplication{
//        environment {
//            developmentMode = false
//        }
//        application {
//            configureRouting()
//        }
//        val pages = 1..5
//        val heroes = listOf(
//            heroRepository.page1,
//            heroRepository.page2,
//            heroRepository.page3,
//            heroRepository.page4,
//            heroRepository.page5
//        )
//
//        pages.forEach { page ->
//            client.get("/boruto/heroes?page=$page").apply {
//                assertEquals(
//                    expected = HttpStatusCode.OK,
//                    actual = status
//                )
//                val expected= ApiResponse(
//                    success = true,
//                    message = "ok",
//                    prevPage = calculatePage(page = page)["prevPage"],
//                    nextPage = calculatePage(page=page)["nextPage"],
//                    heroes = heroes[page-1] //. page is start form 1 in for each loop
//                )
//                val actual = Json.decodeFromString<ApiResponse>(body<String>().toString())
//                assertEquals(
//                    expected = expected,
//                    actual = actual
//                )
//            }
//        }
//    }

    @Test
    fun `access all heroes endpoint, second page, assert correct information`()
     = testApplication{

        environment {
            developmentMode = false
        }
        application {
            configureRouting()
        }
        client.get("/boruto/heroes?page=2").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            val expected = ApiResponse(
                success = true,
                message = "ok",
                prevPage = 1,
                nextPage = 3,
                heroes = heroRepository.page2
            )
            val actual = Json.decodeFromString<ApiResponse>(body<String>().toString())

            assertEquals(
                expected = expected,
                actual = actual)
        }
    }



    private fun calculatePage(page: Int): Map<String, Int?> {
        var prevPage: Int? = page
        var nextPage: Int? = page
        if (page in 1..4) {
            nextPage = nextPage?.plus(1)
        }
        if (page in 2..5) {
            prevPage = prevPage?.minus(1)
        }
        if (page == 1) {
            prevPage = null
        }
        if (page == 5) {
            nextPage = null
        }
        return mapOf("prevPage" to prevPage, "nextPage" to nextPage)

    }

    @Test
    fun `access all heroes, page not found, assert error` ()
    = testApplication {
        environment {
            developmentMode = false
        }
        application {
            configureRouting()
        }
        client.get("/boruto/heroes?page=190").apply {

            assertEquals(
                expected = HttpStatusCode.NotFound,
                actual = status
            )

            val expected =  ApiResponse(success = false, message = "Heroes not Found")
            //val actual = Json.decodeFromString<ApiResponse>(body())

            println("Expected: $expected")
           // println("Actual: $actual")

//            assertEquals(
//                expected = expected,
//                actual = actual)
        }
    }

    @Test
    fun `get all heroes, invalid arguments, assert error` () = testApplication {
        environment {
            developmentMode = false
        }
        application {
            configureRouting()
        }
        client.get("/boruto/heroes?page=asd").apply {
            assertEquals(
                expected = HttpStatusCode.BadRequest,
                actual = status
            )

            val expected= ApiResponse(
                success = false,
                message = "Only Number Allowed"
            )

            val actual = Json.decodeFromString<ApiResponse>(body())

            println("Expected : $expected")
            println("Actual: $actual")

            assertEquals(
                expected = expected,
                actual = actual
            )
        }
    }

    @Test
    fun `access search heroes endpoint, Query hero name, assert single hero result` () {
        withTestApplication (moduleFunction = Application::module) {
            handleRequest (HttpMethod.Get, "/boruto/heroes/search?name=sas").apply{
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val  actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size
                assertEquals(
                    expected = 1,
                    actual = actual
                )

            }

        }
    }

    @Test
    fun `access search heroes endpoint, Query hero name, assert multiple heroes` () {
        withTestApplication(moduleFunction = Application::module){
            handleRequest (HttpMethod.Get, "/boruto/heroes/search?name=sa").apply{
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size
                assertEquals(expected = 3, actual= actual)
            }
        }
    }

    @Test
    fun `access search heroes endpoint, Query empty text, assert empty list`()
    = testApplication {
        environment {
            developmentMode = false
        }
        application {
            configureRouting()
        }
        client.get("/boruto/heroes/search?name=").apply {
            assertEquals(expected = HttpStatusCode.OK, actual = status)
            val actual = Json.decodeFromString<ApiResponse>(body())
                .heroes
            assertEquals(expected = emptyList(), actual= actual)
        }
    }

    @Test
    fun `access search heroes, Query Hero not searched, assert empty list` ()
            = testApplication {
                environment {
                    developmentMode = false
                }
        application {
            configureRouting()
        }
        client.get("/boruto/heroes/search?name=unknown").apply {
            assertEquals(expected = HttpStatusCode.OK, actual = status)
            val actual = Json.decodeFromString<ApiResponse>(body())
                .heroes
            assertEquals(expected = emptyList(), actual= actual)
        }
    }

    @Test
    fun `access non existing endpoint, assert not found` ()
    = testApplication{
        environment { developmentMode = false }
        application { configureRouting() }
        client.get("/unknown").apply {
            assertEquals(expected = HttpStatusCode.NotFound, actual = status)
            assertEquals(expected = "Page not Found", actual = body())
        }
    }
}