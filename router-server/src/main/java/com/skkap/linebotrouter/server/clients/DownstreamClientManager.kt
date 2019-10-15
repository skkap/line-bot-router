package com.skkap.linebotrouter.server.clients

import com.linecorp.armeria.client.HttpClient
import com.linecorp.armeria.common.HttpMethod
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.common.RequestHeaders
import com.skkap.linebotrouter.server.LineBotHeaders.LINE_BOT_SIGNATURE_HEADER
import com.skkap.linebotrouter.server.properties.BotRouterProperties
import mu.KLogging
import java.util.concurrent.CompletableFuture
import javax.inject.Named

@Named
class DownstreamClientManager(botRouterProperties: BotRouterProperties) {
    var downstreamClients: List<DownstreamHttpClient> =
        botRouterProperties.downstreams.map { DownstreamHttpClient.of(it) }

    fun sendToAll(requestContents: ByteArray, signature: String): CompletableFuture<Map<String, Boolean>> {
        val responses = downstreamClients.map { downstreamClient ->
            val headers = RequestHeaders.of(
                HttpMethod.POST,
                "",
                LINE_BOT_SIGNATURE_HEADER,
                signature
            )
            logger.info(headers.toString())
            downstreamClient.httpClient
                .execute(headers, requestContents)
                .aggregate()
                .thenApply { aggregatedHttpResponse ->
                    logger.warn { "Request returned status: `${aggregatedHttpResponse.status()}`." }
                    downstreamClient.name to (aggregatedHttpResponse.status() == HttpStatus.OK)
                }
                .exceptionally { throwable ->
                    logger.warn(throwable) { "Request failed with exception: `${throwable.message}`." }
                    downstreamClient.name to false
                }
        }
        return CompletableFuture
            .allOf(*responses.toTypedArray())
            .thenApply { responses.map { it.join() }.toMap() }
    }

    class DownstreamHttpClient(
        val name: String,
        val httpClient: HttpClient
    ) {
        companion object {
            fun of(downstreamProperties: BotRouterProperties.DownstreamProperties): DownstreamHttpClient {
                return DownstreamHttpClient(
                    downstreamProperties.name,
                    HttpClient.of(downstreamProperties.uri)
                )
            }
        }
    }

    companion object : KLogging()
}
