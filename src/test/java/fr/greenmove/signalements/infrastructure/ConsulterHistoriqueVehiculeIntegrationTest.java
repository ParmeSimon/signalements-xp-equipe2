package fr.greenmove.signalements.infrastructure;

import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.Signalements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * S12 – Historique des incidents d'un véhicule, de bout en bout : HTTP, contrôleur, Spring, cas d'usage.
 * Le contexte Spring est partagé : chaque test utilise ses propres numéros de véhicule (V-05xx).
 */
@SpringBootTest
@AutoConfigureMockMvc
class ConsulterHistoriqueVehiculeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Signalements signalements;

    @Test
    void l_historique_d_un_vehicule_renvoie_ses_incidents_du_plus_recent_au_plus_ancien() throws Exception {
        incident("V-0501", Categorie.PNEU, "2026-11-20T10:00:00Z");
        incident("V-0501", Categorie.FREINS, "2026-11-23T09:00:00Z");
        incident("V-0501", Categorie.PROPRETE, "2026-11-21T15:00:00Z");

        consulterHistorique("V-0501")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].categorie", contains("FREINS", "PROPRETE", "PNEU")));
    }

    @Test
    void l_historique_ne_contient_pas_les_incidents_des_autres_vehicules() throws Exception {
        incident("V-0502", Categorie.FREINS, "2026-11-23T09:00:00Z");
        incident("V-0503", Categorie.RAYURE, "2026-11-22T08:00:00Z");

        consulterHistorique("V-0502")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].vehicule", contains("V-0502")));
    }

    @Test
    void l_historique_d_un_vehicule_sans_incident_est_une_liste_vide() throws Exception {
        consulterHistorique("V-0599")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private void incident(String vehicule, Categorie categorie, String dateIso) {
        signalements.enregistrer(new Signalement(
                UUID.randomUUID().toString(), vehicule, categorie, "", Instant.parse(dateIso)));
    }

    private ResultActions consulterHistorique(String vehicule) throws Exception {
        return mockMvc.perform(get("/signalements/vehicules/{vehicule}/historique", vehicule));
    }
}