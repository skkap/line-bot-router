package com.skkap.linebotrouter.server.configurations

import com.linecorp.armeria.spring.AnnotatedServiceRegistrationBean
import com.skkap.linebotrouter.server.controllers.WebhookController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ArmeriaConfiguration {
    @Bean
    fun termsServiceBean(webhookController: WebhookController): AnnotatedServiceRegistrationBean {
        return AnnotatedServiceRegistrationBean()
                .setService(webhookController)
                .setServiceName("WebhookController")
                .setPathPrefix("/webhook")
    }
}
