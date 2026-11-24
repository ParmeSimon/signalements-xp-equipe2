package fr.greenmove.signalements.acceptation;

import fr.greenmove.signalements.application.SignalerIncident;
import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.Trajet;
import fr.greenmove.signalements.domaine.Usager;
import fr.greenmove.signalements.domaine.Vehicule;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/** Étapes de S2. Le scénario appelle l'application, pas l'API REST. */
public class RetireVehiculeSignalement {

    private static final Instant MAINTENANT = Instant.parse("2026-11-24T10:00:00Z");

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final FauxPool pool = new FauxPool();
    private final NotificationsEspion notifications = new NotificationsEspion();
    private final SignalerIncident signalerIncident = new SignalerIncident(
            signalements, pool, notifications, Clock.fixed(MAINTENANT, ZoneOffset.UTC));

    private Signalement signalement;

    @Given("un véhicule {string} disponible dans le pool")
    public void un_vehicule_disponible(String vehicule) {
        pool.ajouter(Vehicule.creer(vehicule, MAINTENANT));
    }

    @Given("un véhicule {string} actuellement utilisé par l'usager {string}")
    public void un_vehicule_utilise_par(String vehicule, String usager) {
        Vehicule v = Vehicule.creer(vehicule, MAINTENANT);
        v.affecter(Trajet.demarre(new Usager(usager), v.numero(), MAINTENANT), MAINTENANT);
        pool.ajouter(v);
    }

    @When("un utilisateur signale {string} sur le véhicule {string} avec le commentaire {string}")
    public void un_utilisateur_signale(String categorie, String vehicule, String commentaire) {
        signalement = signalerIncident.signaler(vehicule, Categorie.de(categorie), commentaire);
    }

    @Then("le signalement est enregistré avec la gravité {string}")
    public void le_signalement_est_enregistre_avec_la_gravite(String gravite) {
        assertThat(signalements.parId(signalement.id())).isPresent();
        assertThat(signalement.gravite().name()).isEqualTo(gravite);
    }

    @Then("le véhicule {string} est immédiatement retiré du pool")
    public void le_vehicule_est_immediatement_retire(String vehicule) {
        assertThat(pool.contient(vehicule)).isFalse();
    }

    @Then("le véhicule {string} est retiré du pool")
    public void le_vehicule_est_retire(String vehicule) {
        assertThat(pool.contient(vehicule)).isFalse();
    }

    @Then("le véhicule {string} n'est pas retiré du pool pour incident bloquant")
    public void le_vehicule_n_est_pas_retire(String vehicule) {
        assertThat(pool.contient(vehicule)).isTrue();
    }

    @Then("une alerte informe le déclarant que le véhicule {string} est en cours d'utilisation")
    public void une_alerte_informe_le_declarant(String vehicule) {
        assertThat(notifications.alertesAuDeclarant()).containsExactly(vehicule);
    }

    @Then("une notification critique est envoyée à l'usager {string} lui demandant de s'arrêter immédiatement")
    public void une_notification_critique_est_envoyee(String usager) {
        assertThat(notifications.demandesDArret()).containsExactly(new Usager(usager));
    }

    @Then("aucune notification d'arrêt n'est envoyée")
    public void aucune_notification_d_arret() {
        assertThat(notifications.demandesDArret()).isEmpty();
    }

    @Then("aucune notification d'arrêt n'est envoyée à l'usager {string}")
    public void aucune_notification_d_arret_a(String usager) {
        assertThat(notifications.demandesDArret()).doesNotContain(new Usager(usager));
    }
}
