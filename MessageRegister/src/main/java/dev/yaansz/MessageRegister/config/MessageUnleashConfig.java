package dev.yaansz.MessageRegister.config;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.util.UnleashConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageUnleashConfig {

    @Value("${unleash.api.url:http://localhost:4242/api}")
    private String unleashApiUrl;

    @Value("${unleash.api.token:*:development.unleash-insecure-api-token}")
    private String unleashApiToken;

    @Value("${unleash.app.name:message-register}")
    private String appName;

    @Value("${unleash.instance.id:message-register-instance}")
    private String instanceId;

    @Bean
    public Unleash unleash() {
        UnleashConfig config = UnleashConfig.builder()
                .appName(appName)
                .instanceId(instanceId)
                .unleashAPI(unleashApiUrl)
                .apiKey(unleashApiToken)
                .build();

        return new DefaultUnleash(config);
    }
}
