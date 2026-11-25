package fr.greenmove.signalements.acceptation;

import fr.greenmove.signalements.application.RappelConstructeur;
import fr.greenmove.signalements.application.SignalerIncident;
import fr.greenmove.signalements.domaine.Gravite;
import fr.greenmove.signalements.domaine.Trajet;
import fr.greenmove.signalements.domaine.Usager;
import fr.greenmove.signalements.domaine.Vehicule;
import fr.greenmove.signalements.infrastructure.PoolEnMemoire;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/** Étapes de S21. Le scénario appelle l'application, pas l'API REST. */
public class RappelConstructeurSteps {

    private static final Instant MAINTENANT = Instant.parse("2026-11-25T10:00:00Z");

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final PoolEnMemoire pool = new PoolEnMemoire();
    private final NotificationsEspion notifications = new NotificationsEspion();
    private final RappelConstructeur rappelConstructeur = new RappelConstructeur(pool,
            new SignalerIncident(signalements, pool, notifications, Clock.fixed(MAINTENANT, ZoneOffset.UTC)));

    @Given("un véhicule {string} louable")
    public void un_vehicule_louable(String numero) {
        pool.ajouter(Vehicule.creer(numero, MAINTENANT));
    }

    @Given("un véhicule {string} loué par l'usager {string}")
    public void un_vehicule_loue_par(String numero, String usager) {
        Vehicule vehicule = Vehicule.creer(numero, MAINTENANT);
        vehicule.affecter(Trajet.demarre(new Usager(usager), numero, MAINTENANT), MAINTENANT);
        pool.ajouter(vehicule);
    }

    @When("l'atelier lance le rappel constructeur de la série {string}")
    public void l_atelier_lance_le_rappel(String serie) {
        rappelConstructeur.rappeler(serie);
    }

    @Then("le véhicule {string} n'est plus louable")
    public void le_vehicule_n_est_plus_louable(String numero) {
        assertThat(pool.parNumero(numero)).isNull();
    }

    @Then("le véhicule {string} reste louable")
    public void le_vehicule_reste_louable(String numero) {
        assertThat(pool.parNumero(numero)).isNotNull();
    }

    @Then("le véhicule {string} a un signalement bloquant {string} avec le commentaire {string}")
    public void le_vehicule_a_un_signalement_bloquant(String numero, String categorie, String commentaire) {
        assertThat(signalements.parVehicule(numero)).singleElement().satisfies(s -> {
            assertThat(s.categorie().name()).isEqualTo(categorie);
            assertThat(s.gravite()).isEqualTo(Gravite.BLOQUANT);
            assertThat(s.commentaire()).isEqualTo(commentaire);
        });
    }

    @Then("l'usager {string} doit s'arrêter immédiatement")
    public void l_usager_doit_s_arreter(String usager) {
        assertThat(notifications.demandesDArret()).containsExactly(new Usager(usager));
    }
}