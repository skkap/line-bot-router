package com.skkap.linebotrouter.server.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("bot-router")
class BotRouterProperties {
    lateinit var channelSecret: String

    lateinit var channelAccessToken: String

    lateinit var downstreams: List<DownstreamProperties>

    class DownstreamProperties {
        /**
         * Arbitrary name of the downstream bot.
         */
        lateinit var name: String

        /**
         * Full qualified uri to the bot webhook.
         */
        lateinit var uri: String
    }
}
