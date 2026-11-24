package fr.greenmove.signalements.domaine;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class VehiculeTest {

    private static final Instant CREATION = Instant.parse("2026-11-24T10:00:00Z");
    private static final Instant PLUS_TARD = Instant.parse("2026-11-24T10:30:00Z");
    private static final Usager USAGER = new Usager("U-89");

    @Test
    void un_numero_en_V_est_un_velo() {
        assertThat(Vehicule.creer("V-0412", CREATION)).isInstanceOf(Velo.class);
    }

    @Test
    void un_numero_en_T_est_une_trottinette() {
        assertThat(Vehicule.creer("T-1187", CREATION)).isInstanceOf(Trottinette.class);
    }

    @Test
    void un_vehicule_neuf_est_actif_libre_et_date_de_sa_creation() {
        Vehicule vehicule = Vehicule.creer("V-0412", CREATION);

        assertThat(vehicule.numero()).isEqualTo("V-0412");
        assertThat(vehicule.isActive()).isTrue();
        assertThat(vehicule.estEnCoursDUtilisation()).isFalse();
        assertThat(vehicule.trajet()).isNull();
        assertThat(vehicule.createdAt()).isEqualTo(CREATION);
        assertThat(vehicule.updatedAt()).isEqualTo(CREATION);
    }

    @Test
    void affecter_un_trajet_en_cours_le_met_en_cours_d_utilisation() {
        Vehicule vehicule = Vehicule.creer("V-0412", CREATION);
        Trajet trajet = Trajet.demarre(USAGER, "V-0412", PLUS_TARD);

        vehicule.affecter(trajet, PLUS_TARD);

        assertThat(vehicule.estEnCoursDUtilisation()).isTrue();
        assertThat(vehicule.trajet()).isEqualTo(trajet);
        assertThat(vehicule.updatedAt()).isEqualTo(PLUS_TARD);
        assertThat(vehicule.createdAt()).isEqualTo(CREATION);
    }

    @Test
    void un_vehicule_dont_le_trajet_est_termine_n_est_plus_en_cours_d_utilisation() {
        Vehicule vehicule = Vehicule.creer("V-0412", CREATION);

        vehicule.affecter(Trajet.demarre(USAGER, "V-0412", CREATION).termine(2.5, PLUS_TARD), PLUS_TARD);

        assertThat(vehicule.estEnCoursDUtilisation()).isFalse();
    }

    @Test
    void desactiver_un_vehicule_le_rend_inactif_et_met_a_jour_sa_date() {
        Vehicule vehicule = Vehicule.creer("T-1187", CREATION);

        vehicule.desactiver(PLUS_TARD);

        assertThat(vehicule.isActive()).isFalse();
        assertThat(vehicule.updatedAt()).isEqualTo(PLUS_TARD);
    }
}
