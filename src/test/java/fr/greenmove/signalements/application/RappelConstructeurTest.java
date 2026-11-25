package fr.greenmove.signalements.application;

import fr.greenmove.signalements.acceptation.NotificationsEspion;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.domaine.Vehicule;
import fr.greenmove.signalements.infrastructure.PoolEnMemoire;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** S21 – Un rappel constructeur retire d'un coup toute une série de trottinettes de la location. */
class RappelConstructeurTest {

    private static final Instant MAINTENANT = Instant.parse("2026-11-25T10:00:00Z");

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final PoolEnMemoire pool = new PoolEnMemoire();
    private final NotificationsEspion notifications = new NotificationsEspion();
    private final RappelConstructeur rappel = new RappelConstructeur(pool,
            new SignalerIncident(signalements, pool, notifications, Clock.fixed(MAINTENANT, ZoneOffset.UTC)));

    @Test
    void refuse_une_serie_nulle() {
        refuseLaSerie(null);
    }

    @Test
    void refuse_une_serie_vide() {
        refuseLaSerie("");
    }

    @Test
    void refuse_une_serie_composee_d_espaces() {
        refuseLaSerie("  ");
    }

    private void refuseLaSerie(String serie) {
        disponible("T-1100");

        assertThatThrownBy(() -> rappel.rappeler(serie))
                .isInstanceOf(SignalementInvalide.class)
                .hasMessage("La série est obligatoire");

        assertThat(pool.parNumero("T-1100")).isNotNull();
    }

    private void disponible(String numero) {
        pool.ajouter(Vehicule.creer(numero, MAINTENANT));
    }
}