package fr.greenmove.signalements.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** S4 en HTTP : tout le câblage Spring, du contrôleur au stockage. */
@SpringBootTest
@AutoConfigureMockMvc
class PriseEnChargeIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void prendre_en_charge_en_http_affiche_le_nom_du_technicien() throws Exception {
        String adresse = signalerUnIncident();

        mockMvc.perform(post(adresse + "/prise-en-charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"technicien": "Karim"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prisPar").value("Karim"));

        mockMvc.perform(get(adresse))
                .andExpect(jsonPath("$.prisPar").value("Karim"));
    }

    /** Crée un signalement par l'API de S1 et renvoie son adresse (en-tête Location). */
    private String signalerUnIncident() throws Exception {
        return mockMvc.perform(post("/signalements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vehicule": "V-0412", "categorie": "rayure", "commentaire": ""}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }
}