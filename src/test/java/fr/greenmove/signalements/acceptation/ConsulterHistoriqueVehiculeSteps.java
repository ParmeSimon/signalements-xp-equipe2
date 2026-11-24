package fr.greenmove.signalements.acceptation;

import fr.greenmove.signalements.application.ConsulterHistoriqueVehicule;
import fr.greenmove.signalements.application.PrendreEnChargeSignalement;
import fr.greenmove.signalements.application.SignalerIncident;
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

/** Étapes de S12. Le scénario appelle l'application, pas l'API REST. */
public class ConsulterHistoriqueVehiculeSteps {

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final ConsulterHistoriqueVehicule consulterHistorique = new ConsulterHistoriqueVehicule(signalements);
    private final PrendreEnChargeSignalement prendreEnCharge = new PrendreEnChargeSignalement(signalements);

    private Signalement dernierSignalement;
    private List<Signalement> historique;

    @Given("un incident {string} signalé sur le véhicule {string} le {string}")
    public void un_incident_signale_le(String categorie, String vehicule, String dateIso) {
        Clock horloge = Clock.fixed(Instant.parse(dateIso), ZoneOffset.UTC);
        dernierSignalement = new SignalerIncident(signalements, horloge)
                .signaler(vehicule, Categorie.de(categorie), null);
    }

    @Given("le technicien {string} a pris en charge cet incident")
    public void le_technicien_a_pris_en_charge_cet_incident(String technicien) {
        prendreEnCharge.prendre(dernierSignalement.id(), technicien);
    }

    @When("le technicien consulte l'historique du véhicule {string}")
    public void le_technicien_consulte_l_historique_du_vehicule(String vehicule) {
        historique = consulterHistorique.de(vehicule);
    }

    @Then("l'historique affiche les catégories {string}")
    public void l_historique_affiche_les_categories(String categoriesAttendues) {
        assertThat(historique)
                .extracting(s -> s.categorie().name())
                .containsExactly(categoriesAttendues.split(", "));
    }

    @Then("l'incident {string} de l'historique est pris par {string}")
    public void l_incident_de_l_historique_est_pris_par(String categorie, String technicien) {
        assertThat(historique)
                .filteredOn(s -> s.categorie().name().equals(categorie))
                .extracting(Signalement::prisPar)
                .containsExactly(technicien);
    }

    @Then("l'historique est vide")
    public void l_historique_est_vide() {
        assertThat(historique).isEmpty();
    }
}