package fr.greenmove.signalements.application;

import fr.greenmove.signalements.acceptation.NotificationsEspion;
import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.Vehicule;
import fr.greenmove.signalements.infrastructure.PoolEnMemoire;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsulterHistoriqueVehiculeTest {

    private static final Instant MAINTENANT = Instant.parse("2026-11-23T14:00:00Z");
    private static final Instant HIER = Instant.parse("2026-11-22T14:00:00Z");
    private static final Instant AVANTHIER = Instant.parse("2026-11-21T14:00:00Z");

    private static final Instant CREATION = Instant.parse("2026-11-22T10:00:00Z");

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final PoolEnMemoire pool = new PoolEnMemoire();
    private final NotificationsEspion notifications = new NotificationsEspion();
    private final SignalerIncident signalerIncident = new SignalerIncident(
            signalements, pool, notifications, Clock.fixed(MAINTENANT, ZoneOffset.UTC));
    private final ConsulterHistoriqueVehicule consulterHistorique = new ConsulterHistoriqueVehicule(signalements);

    @BeforeEach
    void un_signalement_existe() {
        signalements.enregistrer(new Signalement("1", "V-0412", Categorie.FREINS, "", AVANTHIER));
        signalements.enregistrer(new Signalement("2", "V-0999", Categorie.RAYURE, "", HIER));
        signalements.enregistrer(new Signalement("3", "V-0412", Categorie.RAYURE, "", MAINTENANT));
        Vehicule vehicule1 = Vehicule.creer("V-0412", CREATION);
        Vehicule vehicule2 = Vehicule.creer("V-0999", CREATION);
        pool.ajouter(vehicule1);
        pool.ajouter(vehicule2);
    }

    @Test
    void un_vehicule_sans_incident_a_un_historique_vide(){
        assertTrue(signalements.parVehicule("V-0999").isEmpty());
    }

    @Test
    void l_historique_ne_contient_que_les_incidents_du_vehicule_demande() {
        assertEquals(signalements.parVehicule("V-0412").size(), 2);
    }

    @Test
    void l_historique_va_du_plus_recent_au_plus_ancien() {
        List<Instant> dates = consulterHistorique.de("V-0412").stream()
                .map(Signalement::signaleLe)
                .toList();

        assertEquals(List.of(MAINTENANT, AVANTHIER), dates);
    }

    @Test
    void l_historique_n_est_pas_trie_par_gravite_contrairement_aux_signalements_ouverts() {
        // V-0412 : FREINS (bloquant) avant-hier, RAYURE (cosmétique) maintenant.
        // Trié par gravité, FREINS sortirait en premier ; trié par date, c'est la RAYURE.
        List<Categorie> categories = consulterHistorique.de("V-0412").stream()
                .map(Signalement::categorie)
                .toList();

        assertEquals(List.of(Categorie.RAYURE, Categorie.FREINS), categories);
    }

    @Test
    void un_incident_pris_en_charge_reste_dans_l_historique() {
        // Karim prend en charge le signalement "1" (FREINS sur V-0412)
        Signalement pris = signalements.parId("1").orElseThrow().prendre("Karim");
        signalements.enregistrer(pris);

        List<String> idsDansLHistorique = consulterHistorique.de("V-0412").stream()
                .map(Signalement::id)
                .toList();

        assertEquals(List.of("3", "1"), idsDansLHistorique);
        assertEquals("Karim", consulterHistorique.de("V-0412").get(1).prisPar());
    }
}