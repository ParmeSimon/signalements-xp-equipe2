package fr.greenmove.signalements.acceptation;

import fr.greenmove.signalements.application.SignalerIncident;
import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/** Étapes de S1. Le scénario appelle l'application, pas l'API REST. */
public class SignalerIncidentSteps {

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final SignalerIncident signalerIncident = new SignalerIncident(
            signalements, Clock.fixed(Instant.parse("2026-11-23T14:00:00Z"), ZoneOffset.UTC));

    private Signalement signalement;
    private SignalementInvalide refus;

    @When("un usager signale {string} sur le véhicule {string} avec le commentaire {string}")
    public void un_usager_signale(String categorie, String vehicule, String commentaire) {
        tenterDeSignaler(categorie, vehicule, commentaire);
    }

    @When("un usager signale {string} sur le véhicule {string} sans commentaire")
    public void un_usager_signale_sans_commentaire(String categorie, String vehicule) {
        tenterDeSignaler(categorie, vehicule, null);
    }

    @When("un usager signale {string} sur le véhicule {string} avec un commentaire de {int} caractères")
    public void un_usager_signale_avec_un_commentaire_de(String categorie, String vehicule, int longueur) {
        tenterDeSignaler(categorie, vehicule, "a".repeat(longueur));
    }

    @Then("le signalement est enregistré")
    public void le_signalement_est_enregistre() {
        assertThat(refus).isNull();
        assertThat(signalements.parId(signalement.id())).isPresent();
    }

    @Then("sa gravité est {string}")
    public void sa_gravite_est(String gravite) {
        assertThat(signalement.gravite().name()).isEqualTo(gravite);
    }

    @Then("le signalement est refusé avec le message {string}")
    public void le_signalement_est_refuse(String message) {
        assertThat(refus).isNotNull();
        assertThat(refus.getMessage()).isEqualTo(message);
    }

    @Then("aucun signalement n'est enregistré")
    public void aucun_signalement_n_est_enregistre() {
        assertThat(signalements.tous()).isEmpty();
    }

    private void tenterDeSignaler(String categorie, String vehicule, String commentaire) {
        try {
            signalement = signalerIncident.signaler(vehicule, Categorie.de(categorie), commentaire);
        } catch (SignalementInvalide e) {
            refus = e;
        }
    }
}
