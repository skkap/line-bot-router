package com.skkap.linebotrouter.server.controllers

import com.linecorp.armeria.common.HttpRequest
import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.annotation.Post
import com.skkap.linebotrouter.server.exceptions.InvalidSignatureException
import com.skkap.linebotrouter.server.LineBotHeaders.LINE_BOT_SIGNATURE_HEADER
import com.skkap.linebotrouter.server.services.WebhookService
import mu.KLogging
import java.util.concurrent.CompletableFuture
import javax.inject.Named


@Named
class WebhookController(val webhookService: WebhookService) {
    @Post("/bot")
    fun botWebhook(ctx: ServiceRequestContext, req: HttpRequest): CompletableFuture<HttpResponse> {
        return req
            .aggregate()
            .thenCompose { fullRequest ->
                logger.info { "context: `$ctx`; request: `$req`; content: `${fullRequest.contentUtf8()}`." }
                val signature = req.headers()[LINE_BOT_SIGNATURE_HEADER] ?: throw InvalidSignatureException()
                webhookService.processWebhook(signature, fullRequest.content().array())
            }
            .thenApply { clientStatuses ->
                if (!clientStatuses.all { it.value }) {
                    val failedClients = clientStatuses.filter { !it.value }.map { it.key }
                    logger.error { "Some of downstream clients did not process requests correctly: `$failedClients`." }
                }
                HttpResponse.of(HttpStatus.OK)
            }
            .exceptionally { t ->
                logger.error(t) { "Error while handling web-hook request." }
                when (t) {
                    is InvalidSignatureException -> HttpResponse.of(HttpStatus.BAD_REQUEST)
                    else -> HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
    }

    companion object : KLogging()
}
