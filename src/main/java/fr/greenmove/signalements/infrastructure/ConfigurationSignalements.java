package fr.greenmove.signalements.infrastructure;

import fr.greenmove.signalements.application.SignalerIncident;
import fr.greenmove.signalements.domaine.Signalements;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** Assemble le domaine et l'infrastructure. Le domaine ne connaît pas Spring. */
@Configuration
public class ConfigurationSignalements {

    @Bean
    Clock horloge() {
        return Clock.systemDefaultZone();
    }

    @Bean
    Signalements signalements() {
        return new SignalementsEnMemoire();
    }

    @Bean
    SignalerIncident signalerIncident(Signalements signalements, Clock horloge) {
        return new SignalerIncident(signalements, horloge);
    }
}
