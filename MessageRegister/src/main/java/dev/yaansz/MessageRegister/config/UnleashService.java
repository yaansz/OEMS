package dev.yaansz.MessageRegister.config;

import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UnleashService {

    private final Unleash unleash;

    public UnleashService(Unleash unleash) {
        this.unleash = unleash;
    }

    public boolean isEmailSendEnabled(UUID userId, String email) {
        UnleashContext context = UnleashContext.builder()
                .userId(userId.toString())
                .addProperty("email", email)
                .build();

        return unleash.isEnabled("email-send", context, false);
    }

    public boolean isDebugLoggingEnabled(String module) {
        UnleashContext context = UnleashContext.builder()
                .addProperty("module", module)
                .build();

        return unleash.isEnabled("debug-logging", context, false);
    }

    public boolean isDebugLoggingEnabledGlobally() {
        return unleash.isEnabled("debug-logging-global", false);
    }
}
