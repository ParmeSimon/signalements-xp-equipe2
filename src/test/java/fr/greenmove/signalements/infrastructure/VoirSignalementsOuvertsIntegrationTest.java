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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * S3 – Voir les signalements ouverts, de bout en bout : HTTP, contrôleur, Spring, cas d'usage.
 * Le contexte Spring est partagé entre les tests : chaque test utilise ses propres numéros de véhicule
 * et ne regarde que ceux-là.
 */
@SpringBootTest
@AutoConfigureMockMvc
class VoirSignalementsOuvertsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Signalements signalements;

    @Test
    void la_liste_des_signalements_ouverts_contient_un_signalement_enregistre() throws Exception {
        ouvert("V-0301", Categorie.FREINS, "2026-11-23T14:00:00Z");

        consulterLesOuverts()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.vehicule == 'V-0301')].vehicule", contains("V-0301")));
    }

    @Test
    void un_signalement_ouvert_affiche_le_vehicule_la_categorie_et_sa_date() throws Exception {
        ouvert("V-0302", Categorie.BATTERIE, "2026-11-23T12:00:00Z");

        consulterLesOuverts()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.vehicule == 'V-0302')].categorie", contains("BATTERIE")))
                .andExpect(jsonPath("$[?(@.vehicule == 'V-0302')].gravite", contains("BLOQUANT")))
                .andExpect(jsonPath("$[?(@.vehicule == 'V-0302')].signaleLe", contains("2026-11-23T12:00:00Z")));
    }

    @Test
    void les_signalements_ouverts_sont_tries_par_gravite_puis_par_anciennete() throws Exception {
        ouvert("V-0311", Categorie.RAYURE, "2026-11-23T10:00:00Z");
        ouvert("V-0312", Categorie.FREINS, "2026-11-23T12:00:00Z");
        ouvert("V-0313", Categorie.PNEU, "2026-11-23T11:00:00Z");
        ouvert("V-0314", Categorie.FREINS, "2026-11-23T09:00:00Z");

        consulterLesOuverts()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.vehicule in ['V-0311', 'V-0312', 'V-0313', 'V-0314'])].vehicule",
                        contains("V-0314", "V-0312", "V-0313", "V-0311")));
    }

    private void ouvert(String vehicule, Categorie categorie, String dateIso) {
        signalements.enregistrer(new Signalement(
                UUID.randomUUID().toString(), vehicule, categorie, "", Instant.parse(dateIso)));
    }

    private ResultActions consulterLesOuverts() throws Exception {
        return mockMvc.perform(get("/signalements/ouverts"))
                .andDo(print());   // affiche la requête et la réponse (avec la liste en JSON) dans la console
    }
}