package com.skkap.linebotrouter.server.configurations

import com.linecorp.bot.client.LineSignatureValidator
import com.skkap.linebotrouter.server.properties.BotRouterProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(BotRouterProperties::class)
class LineBotConfiguration(val botRouterProperties: BotRouterProperties) {
    @Bean
    fun lineSignatureValidator(): LineSignatureValidator {
        return LineSignatureValidator(
                botRouterProperties.channelSecret.toByteArray(StandardCharsets.US_ASCII)
        )
    }
}
