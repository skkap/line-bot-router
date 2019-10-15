package com.skkap.linebotrouter.server.services

import com.linecorp.bot.client.LineSignatureValidator
import com.skkap.linebotrouter.server.clients.DownstreamClientManager
import com.skkap.linebotrouter.server.exceptions.InvalidSignatureException
import java.util.concurrent.CompletableFuture
import javax.inject.Named

@Named
class WebhookService(
    private val lineSignatureValidator: LineSignatureValidator,
    private val downstreamClientManager: DownstreamClientManager
) {
    fun processWebhook(signature: String, requestContents: ByteArray): CompletableFuture<Map<String, Boolean>> {
        val isRequestValid = lineSignatureValidator
            .validateSignature(
                requestContents,
                signature
            )
        if (!isRequestValid) throw InvalidSignatureException()

        return downstreamClientManager.sendToAll(requestContents, signature)
    }
}
