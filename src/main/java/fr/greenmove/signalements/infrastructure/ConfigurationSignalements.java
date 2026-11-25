package fr.greenmove.signalements.infrastructure;

import fr.greenmove.signalements.application.*;
import fr.greenmove.signalements.domaine.Notifications;
import fr.greenmove.signalements.domaine.Pool;
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
    Pool pool() {
        return new PoolEnMemoire();
    }

    @Bean
    Notifications notifications() {
        return new NotificationsJournalisees();
    }

    @Bean
    SignalerIncident signalerIncident(Signalements signalements, Pool pool, Notifications notifications, Clock horloge) {
        return new SignalerIncident(signalements, pool, notifications, horloge);
    }

    @Bean
    VoirSignalementsOuverts voirSignalementsOuverts(Signalements signalements) {
        return new VoirSignalementsOuverts(signalements);
    }

    @Bean
    PrendreEnChargeSignalement prendreEnChargeSignalement(Signalements signalements) {
        return new PrendreEnChargeSignalement(signalements);
    }

    @Bean
    RemettreEnService remettreEnService(Signalements signalements, Pool pool, Clock horloge) {
        return new RemettreEnService(signalements, pool, horloge);
    }

    @Bean
    ConsulterHistoriqueVehicule consulterHistoriqueVehicule(Signalements signalements) {
        return new ConsulterHistoriqueVehicule(signalements);
    }

    @Bean
    EnvoyerEnRevisionComplete envoyerEnRevisionComplete(Signalements signalements, Pool pool, Clock horloge) {
        return new EnvoyerEnRevisionComplete(signalements, pool, horloge);
    }
}