package dev.yaansz.MessageRegister.config;

import ch.qos.logback.classic.LoggerContext;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {

    private final UnleashService unleashService;

    public LoggingConfiguration(UnleashService unleashService) {
        this.unleashService = unleashService;
    }

    @PostConstruct
    public void configureLogging() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        UnleashLoggingFilter unleashFilter = new UnleashLoggingFilter();
        unleashFilter.setUnleashService(unleashService);
        unleashFilter.setContext(loggerContext);
        unleashFilter.start();
        
        loggerContext.addTurboFilter(unleashFilter);
    }
}
