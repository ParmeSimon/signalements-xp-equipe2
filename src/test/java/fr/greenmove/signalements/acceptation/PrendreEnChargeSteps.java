package fr.greenmove.signalements.acceptation;

import fr.greenmove.signalements.application.PrendreEnChargeSignalement;
import fr.greenmove.signalements.application.SignalerIncident;
import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/** Étapes de S4. Comme pour S1, le scénario appelle l'application, pas l'API REST. */
public class PrendreEnChargeSteps {

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final SignalerIncident signalerIncident = new SignalerIncident(
            signalements, Clock.fixed(Instant.parse("2026-11-24T09:00:00Z"), ZoneOffset.UTC));
    private final PrendreEnChargeSignalement prendreEnCharge = new PrendreEnChargeSignalement(signalements);

    private String idDuSignalement;
    private SignalementInvalide refus;

    @Given("un signalement {string} sur le véhicule {string}")
    public void un_signalement_sur_le_vehicule(String categorie, String vehicule) {
        idDuSignalement = signalerIncident.signaler(vehicule, Categorie.de(categorie), "").id();
    }

    @Given("le technicien {string} a pris le signalement")
    public void le_technicien_a_pris_le_signalement(String technicien) {
        prendreEnCharge.prendre(idDuSignalement, technicien);
    }

    @When("le technicien {string} prend le signalement")
    public void le_technicien_prend_le_signalement(String technicien) {
        try {
            prendreEnCharge.prendre(idDuSignalement, technicien);
        } catch (SignalementInvalide e) {
            refus = e;
        }
    }

    @When("le technicien {string} relâche le signalement")
    public void le_technicien_relache_le_signalement(String technicien) {
        prendreEnCharge.relacher(idDuSignalement);
    }

    @Then("le signalement est pris par {string}")
    public void le_signalement_est_pris_par(String technicien) {
        assertThat(leSignalement().prisPar()).isEqualTo(technicien);
    }

    @Then("la prise est refusée avec le message {string}")
    public void la_prise_est_refusee(String message) {
        assertThat(refus).isNotNull();
        assertThat(refus.getMessage()).isEqualTo(message);
    }

    @Then("le signalement est libre")
    public void le_signalement_est_libre() {
        assertThat(leSignalement().estLibre()).isTrue();
    }

    /** Relit le signalement dans le stockage : on vérifie ce qui a vraiment été enregistré. */
    private Signalement leSignalement() {
        return signalements.parId(idDuSignalement).orElseThrow();
    }
}