package fr.greenmove.signalements.acceptation;

import fr.greenmove.signalements.application.PrendreEnChargeSignalement;
import fr.greenmove.signalements.application.RemettreEnService;
import fr.greenmove.signalements.application.SignalerIncident;
import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.domaine.Vehicule;
import fr.greenmove.signalements.infrastructure.PoolEnMemoire;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Étapes de S5. Le scénario appelle l'application, pas l'API REST. */
public class RemettreEnServiceSteps {

    private Exception erreur;

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final PoolEnMemoire pool = new PoolEnMemoire();
    private final Clock horloge = Clock.fixed(Instant.parse("2026-11-23T12:00:00Z"), ZoneOffset.UTC);
    private final RemettreEnService remettreEnService = new RemettreEnService(signalements, pool, horloge);
    private final PrendreEnChargeSignalement prendreEnCharge = new PrendreEnChargeSignalement(signalements);

    @Given("un véhicule {string} indisponible à la location")
    public void un_vehicule_indisponible_a_la_location(String vehicule) {
        pool.retirer(vehicule);
    }

    @Given("un signalement enregistré pour {string} sur le véhicule {string}")
    public void un_signalement_enregistre_pour_sur_le_vehicule(String categorie, String vehicule) {
        Clock horloge = Clock.fixed(Instant.parse("2026-11-23T14:00:00Z"), ZoneOffset.UTC);
        new SignalerIncident(signalements, horloge).signaler(vehicule, Categorie.de(categorie), null);
    }

    @Given("le technicien {string} a pris en charge un signalement pour {string} sur le véhicule {string}")
    public void le_technicien_a_pris_en_charge(String technicien, String categorie, String vehicule) {
        pool.retirer(vehicule);
        Clock horloge = Clock.fixed(Instant.parse("2026-11-23T14:00:00Z"), ZoneOffset.UTC);
        var s = new SignalerIncident(signalements, horloge).signaler(vehicule, Categorie.de(categorie), null);
        prendreEnCharge.prendre(s.id(), technicien);
    }

    @When("le technicien remet en service le véhicule {string} avec le commentaire {string}")
    public void le_technicien_remet_en_service_sans_nom(String vehicule, String commentaire) {
        executerRemiseEnService(vehicule, "Tech-Defaut", commentaire);
    }

    @When("le technicien {string} remet en service le véhicule {string} avec le commentaire {string}")
    public void le_technicien_remet_en_service(String technicien, String vehicule, String commentaire) {
        executerRemiseEnService(vehicule, technicien, commentaire);
    }

    @When("le technicien tente de remettre en service le véhicule {string} sans commentaire")
    public void le_technicien_tente_de_remettre_en_service_sans_commentaire(String vehicule) {
        executerRemiseEnService(vehicule, "Tech-Defaut", "");
    }

    private void executerRemiseEnService(String vehicule, String technicien, String commentaire) {
        try {
            remettreEnService.remettreEnService(vehicule, technicien, commentaire);
        } catch (Exception e) {
            this.erreur = e;
        }
    }

    @Then("le véhicule {string} est à nouveau louable")
    public void le_vehicule_est_a_nouveau_louable(String vehicule) {
        Vehicule v = pool.parNumero(vehicule);
        assertThat(v).isNotNull();
        assertThat(v.isActive()).isTrue();
    }

    @Then("le véhicule {string} reste indisponible à la location")
    public void le_vehicule_reste_indisponible_a_la_location(String vehicule) {
        Vehicule v = pool.parNumero(vehicule);
        assertThat(v == null || !v.isActive()).isTrue();
    }

    @Then("le signalement pour {string} sur le véhicule {string} est clos avec le commentaire {string}")
    public void le_signalement_pour_sur_le_vehicule_est_clos_avec_le_commentaire(
            String categorie, String vehicule, String commentaire) {
        Signalement s = signalements.tous().stream()
                .filter(sig -> sig.vehicule().equals(vehicule) && sig.categorie() == Categorie.de(categorie))
                .findFirst()
                .orElseThrow();

        assertThat(s.clos()).isTrue();
        assertThat(s.commentaireCloture()).isEqualTo(commentaire);
    }

    @Then("le signalement sur le véhicule {string} est clos par {string} avec le commentaire {string}")
    public void le_signalement_sur_le_vehicule_est_clos_par_avec_le_commentaire(
            String vehicule, String technicien, String commentaire) {
        Signalement s = signalements.tous().stream()
                .filter(sig -> sig.vehicule().equals(vehicule))
                .findFirst()
                .orElseThrow();

        assertThat(s.clos()).isTrue();
        assertThat(s.prisPar()).isEqualTo(technicien);
        assertThat(s.commentaireCloture()).isEqualTo(commentaire);
    }

    @Then("tous les signalements du véhicule {string} sont clos avec le commentaire {string}")
    public void tous_les_signalements_du_vehicule_sont_clos_avec_le_commentaire(String vehicule, String commentaire) {
        List<Signalement> signalementsDuVehicule = signalements.tous().stream()
                .filter(s -> s.vehicule().equals(vehicule))
                .toList();

        assertThat(signalementsDuVehicule).hasSizeGreaterThanOrEqualTo(2);
        assertThat(signalementsDuVehicule).allSatisfy(s -> {
            assertThat(s.clos()).isTrue();
            assertThat(s.commentaireCloture()).isEqualTo(commentaire);
        });
    }

    @Then("il n'y a plus aucun signalement ouvert pour le véhicule {string}")
    public void il_n_y_a_plus_aucun_signalement_ouvert_pour_le_vehicule(String vehicule) {
        List<Signalement> ouverts = signalements.tous().stream()
                .filter(s -> s.vehicule().equals(vehicule))
                .filter(s -> !s.clos())
                .toList();

        assertThat(ouverts).isEmpty();
    }

    @Then("la remise en service est refusée avec une erreur indiquant qu'un commentaire est obligatoire")
    public void la_remise_en_service_est_refusee() {
        assertThat(erreur).isInstanceOf(SignalementInvalide.class);
        assertThat(erreur.getMessage()).containsIgnoringCase("commentaire");
    }

    @Then("le signalement sur le véhicule {string} reste ouvert")
    public void le_signalement_sur_le_vehicule_reste_ouvert(String vehicule) {
        List<Signalement> signalementsDuVehicule = signalements.tous().stream()
                .filter(s -> s.vehicule().equals(vehicule))
                .toList();

        assertThat(signalementsDuVehicule).isNotEmpty();
        assertThat(signalementsDuVehicule).allMatch(s -> !s.clos());
    }
}