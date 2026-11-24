package fr.greenmove.signalements.application;

import fr.greenmove.signalements.acceptation.NotificationsEspion;
import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.Trajet;
import fr.greenmove.signalements.domaine.Usager;
import fr.greenmove.signalements.domaine.Vehicule;
import fr.greenmove.signalements.infrastructure.PoolEnMemoire;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/** S2 – Un incident bloquant retire le véhicule du pool et fait arrêter l'usager qui roule avec. */
class SignalerIncidentBloquantTest {

    private static final Instant MAINTENANT = Instant.parse("2026-11-24T10:00:00Z");
    private static final Usager USAGER = new Usager("U-89");

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final PoolEnMemoire pool = new PoolEnMemoire();
    private final NotificationsEspion notifications = new NotificationsEspion();
    private final SignalerIncident signalerIncident = new SignalerIncident(
            signalements, pool, notifications, Clock.fixed(MAINTENANT, ZoneOffset.UTC));

    @Test
    void un_incident_bloquant_retire_du_pool_un_vehicule_disponible_sans_notifier_personne() {
        disponible("T-1187");

        signalerIncident.signaler("T-1187", Categorie.FREINS, "Le frein arrière ne répond plus");

        assertThat(pool.parNumero("T-1187")).isNull();
        assertThat(notifications.alertesAuDeclarant()).isEmpty();
        assertThat(notifications.demandesDArret()).isEmpty();
    }

    @Test
    void un_incident_bloquant_sur_un_vehicule_en_cours_d_utilisation_alerte_le_declarant_et_arrete_l_usager() {
        enCoursDUtilisation("V-0412", USAGER);

        signalerIncident.signaler("V-0412", Categorie.DIRECTION, "Direction bloquée");

        assertThat(pool.parNumero("V-0412")).isNull();
        assertThat(notifications.alertesAuDeclarant()).containsExactly("V-0412");
        assertThat(notifications.demandesDArret()).containsExactly(USAGER);
    }

    @Test
    void un_incident_genant_laisse_le_vehicule_dans_le_pool() {
        enCoursDUtilisation("V-0412", USAGER);

        signalerIncident.signaler("V-0412", Categorie.PNEU, "Le pneu avant est crevé");

        assertThat(pool.parNumero("V-0412")).isNotNull();
        assertThat(notifications.demandesDArret()).isEmpty();
    }

    @Test
    void un_incident_cosmetique_laisse_le_vehicule_dans_le_pool() {
        enCoursDUtilisation("V-0412", USAGER);

        signalerIncident.signaler("V-0412", Categorie.PROPRETE, "Selle très sale");

        assertThat(pool.parNumero("V-0412")).isNotNull();
        assertThat(notifications.demandesDArret()).isEmpty();
    }

    @Test
    void un_trajet_termine_ne_declenche_pas_de_notification_d_arret() {
        Vehicule velo = Vehicule.creer("V-0412", MAINTENANT);
        velo.affecter(Trajet.demarre(USAGER, "V-0412", MAINTENANT).termine(3.2, MAINTENANT), MAINTENANT);
        pool.ajouter(velo);

        signalerIncident.signaler("V-0412", Categorie.FREINS, "");

        assertThat(pool.parNumero("V-0412")).isNull();
        assertThat(notifications.alertesAuDeclarant()).isEmpty();
        assertThat(notifications.demandesDArret()).isEmpty();
    }

    @Test
    void un_vehicule_absent_du_pool_est_quand_meme_signale() {
        Signalement signalement = signalerIncident.signaler("T-0001", Categorie.BATTERIE, "");

        assertThat(signalements.parId(signalement.id())).contains(signalement);
        assertThat(notifications.demandesDArret()).isEmpty();
    }

    private void disponible(String numero) {
        pool.ajouter(Vehicule.creer(numero, MAINTENANT));
    }

    private void enCoursDUtilisation(String numero, Usager usager) {
        Vehicule vehicule = Vehicule.creer(numero, MAINTENANT);
        vehicule.affecter(Trajet.demarre(usager, numero, MAINTENANT), MAINTENANT);
        pool.ajouter(vehicule);
    }
}
