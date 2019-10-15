package com.skkap.linebotrouter.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LineBotRouterServerApplication

fun main(args: Array<String>) {
    runApplication<LineBotRouterServerApplication>(*args)
}
