package fr.greenmove.signalements.acceptation;

import fr.greenmove.signalements.application.EnvoyerEnRevisionComplete;
import fr.greenmove.signalements.application.RemettreEnService;
import fr.greenmove.signalements.application.SignalerIncident;
import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Vehicule;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvoyerEnRevisionCompleteSteps {

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final FauxPool pool = new FauxPool();

    @Given("un véhicule {string} en service dans le pool")
    public void un_vehicule_en_service_dans_le_pool(String vehicule) {
        pool.ajouter(Vehicule.creer(vehicule, Instant.parse("2026-11-01T00:00:00Z")));
    }

    @Given("un signalement antérieur {string} sur le véhicule {string} le {string}")
    public void un_signalement_anterieur_sur_le_vehicule_le(String categorie, String vehicule, String dateIso) {
        Clock horloge = Clock.fixed(Instant.parse(dateIso), ZoneOffset.UTC);
        new SignalerIncident(signalements, horloge)
                .signaler(vehicule, Categorie.de(categorie), null);
    }

    @Given("l'incident {string} sur le véhicule {string} est réparé par le technicien {string} le {string}")
    public void l_incident_est_repare_par_le_technicien_le(String categorie, String vehicule, String technicien, String dateIso) {
        Clock horloge = Clock.fixed(Instant.parse(dateIso), ZoneOffset.UTC);
        new RemettreEnService(signalements, pool, horloge)
                .remettreEnService(vehicule, technicien, "Réparation effectuée");
    }

    @When("un usager signale {string} sur le véhicule {string} le {string}")
    public void un_usager_signale_sur_le_vehicule_le(String categorie, String vehicule, String dateIso) {
        Clock horloge = Clock.fixed(Instant.parse(dateIso), ZoneOffset.UTC);
        new EnvoyerEnRevisionComplete(signalements, pool, horloge)
                .signaler(vehicule, Categorie.de(categorie), null);
    }

    @Then("le véhicule {string} est retiré du pool et pars en révision complète")
    public void le_vehicule_est_retire_du_pool_et_pars_en_revision_complete(String vehicule) {
        assertThat(pool.contient(vehicule)).isFalse();
        assertThat(pool.estEnRevisionComplete(vehicule)).isTrue();
    }

    @Then("le véhicule {string} reste disponible dans le pool")
    public void le_vehicule_reste_disponible_dans_le_pool(String vehicule) {
        assertThat(pool.contient(vehicule)).isTrue();
        assertThat(pool.estEnRevisionComplete(vehicule)).isFalse();
    }

    @Then("le véhicule {string} n'est pas envoyé en révision complète")
    public void le_vehicule_n_est_pas_envoye_en_revision_complete(String vehicule) {
        assertThat(pool.estEnRevisionComplete(vehicule)).isFalse();
    }
}