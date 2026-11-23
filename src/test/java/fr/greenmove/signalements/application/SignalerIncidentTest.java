package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Gravite;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignalerIncidentTest {

    private static final Instant MAINTENANT = Instant.parse("2026-11-23T14:00:00Z");

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final SignalerIncident signalerIncident =
            new SignalerIncident(signalements, Clock.fixed(MAINTENANT, ZoneOffset.UTC));

    @Test
    void le_signalement_est_enregistre_avec_sa_date() {
        Signalement signalement = signalerIncident.signaler("V-0412", Categorie.FREINS, "Le frein avant ne répond plus");

        assertThat(signalements.parId(signalement.id())).contains(signalement);
        assertThat(signalement.signaleLe()).isEqualTo(MAINTENANT);
        assertThat(signalement.gravite()).isEqualTo(Gravite.BLOQUANT);
    }

    @Test
    void un_signalement_refuse_n_est_pas_enregistre() {
        assertThatThrownBy(() -> signalerIncident.signaler("V-12", Categorie.FREINS, ""))
                .isInstanceOf(SignalementInvalide.class);

        assertThat(signalements.tous()).isEmpty();
    }
}
