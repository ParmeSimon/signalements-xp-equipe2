package fr.greenmove.signalements.domaine;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TrajetTest {

    private static final Instant DEBUT = Instant.parse("2026-11-24T10:00:00Z");
    private static final Instant FIN = Instant.parse("2026-11-24T10:25:00Z");
    private static final Usager USAGER = new Usager("U-89");

    @Test
    void un_trajet_demarre_est_en_cours_sans_distance_ni_fin() {
        Trajet trajet = Trajet.demarre(USAGER, "V-0412", DEBUT);

        assertThat(trajet.estEnCours()).isTrue();
        assertThat(trajet.usager()).isEqualTo(USAGER);
        assertThat(trajet.numeroVehicule()).isEqualTo("V-0412");
        assertThat(trajet.debut()).isEqualTo(DEBUT);
        assertThat(trajet.distanceKm()).isZero();
        assertThat(trajet.fin()).isNull();
    }

    @Test
    void terminer_un_trajet_garde_l_usager_le_vehicule_et_le_debut() {
        Trajet trajet = Trajet.demarre(USAGER, "V-0412", DEBUT).termine(4.7, FIN);

        assertThat(trajet.estEnCours()).isFalse();
        assertThat(trajet.distanceKm()).isEqualTo(4.7);
        assertThat(trajet.fin()).isEqualTo(FIN);
        assertThat(trajet.usager()).isEqualTo(USAGER);
        assertThat(trajet.numeroVehicule()).isEqualTo("V-0412");
        assertThat(trajet.debut()).isEqualTo(DEBUT);
    }

    @Test
    void terminer_ne_modifie_pas_le_trajet_d_origine() {
        Trajet enCours = Trajet.demarre(USAGER, "V-0412", DEBUT);

        enCours.termine(4.7, FIN);

        assertThat(enCours.estEnCours()).isTrue();
    }
}
