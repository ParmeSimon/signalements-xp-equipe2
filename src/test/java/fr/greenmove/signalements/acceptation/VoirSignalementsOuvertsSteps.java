package fr.greenmove.signalements.acceptation;

import fr.greenmove.signalements.application.SignalerIncident;
import fr.greenmove.signalements.application.VoirSignalementsOuverts;
import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Étapes de S3. Le scénario appelle l'application, pas l'API REST. */
public class VoirSignalementsOuvertsSteps {

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final VoirSignalementsOuverts voirSignalementsOuverts = new VoirSignalementsOuverts(signalements);

    private List<Signalement> signalementsAffiches;

    @Given("un signalement ouvert pour {string} sur le véhicule {string}")
    public void un_signalement_ouvert(String categorie, String vehicule) {
        un_signalement_ouvert_cree_a(categorie, vehicule, "2026-11-23T14:00:00Z");
    }

    @Given("un signalement ouvert pour {string} sur le véhicule {string} créé à {string}")
    public void un_signalement_ouvert_cree_a(String categorie, String vehicule, String dateIso) {
        Clock horloge = Clock.fixed(Instant.parse(dateIso), ZoneOffset.UTC);
        new SignalerIncident(signalements, horloge).signaler(vehicule, Categorie.de(categorie), null);
    }

    @When("le technicien consulte les signalements ouverts")
    public void le_technicien_consulte_les_signalements_ouverts() {
        signalementsAffiches = voirSignalementsOuverts.lister();
    }

    @Then("le technicien voit {int} signalement pour le véhicule {string}")
    public void le_technicien_voit_signalement(int nombre, String vehicule) {
        assertThat(signalementsAffiches).hasSize(nombre);
        assertThat(signalementsAffiches.get(0).vehicule()).isEqualTo(vehicule);
    }

    @Then("le signalement affiché concerne le véhicule {string}, la catégorie {string} créé à {string}")
    public void le_signalement_affiche_concerne(String vehicule, String categorie, String dateIso) {
        assertThat(signalementsAffiches).hasSize(1);
        Signalement s = signalementsAffiches.get(0);
        assertThat(s.vehicule()).isEqualTo(vehicule);
        assertThat(s.categorie().name()).isEqualTo(categorie);
        assertThat(s.signaleLe()).isEqualTo(Instant.parse(dateIso));
    }

    @Then("l'ordre des véhicules affichés est {string}")
    public void l_ordre_des_vehicules_affiches_est(String ordreAttendu) {
        List<String> vehiculesObtenus = signalementsAffiches.stream()
                .map(Signalement::vehicule)
                .toList();

        assertThat(vehiculesObtenus).containsExactly(ordreAttendu.split(", "));
    }
}