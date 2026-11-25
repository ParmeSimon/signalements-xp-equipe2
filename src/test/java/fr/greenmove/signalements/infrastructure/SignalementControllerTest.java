package fr.greenmove.signalements.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SignalementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void signaler_un_incident_renvoie_201_et_la_gravite() throws Exception {
        signaler("""
                {"vehicule": "T-1187", "categorie": "eclairage", "commentaire": "Phare arrière cassé"}
                """)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.vehicule").value("T-1187"))
                .andExpect(jsonPath("$.categorie").value("ECLAIRAGE"))
                .andExpect(jsonPath("$.gravite").value("GENANT"));
    }

    @Test
    void le_signalement_cree_se_consulte_a_l_adresse_renvoyee() throws Exception {
        String adresse = signaler("""
                {"vehicule": "V-0412", "categorie": "freins", "commentaire": "Le frein avant ne répond plus"}
                """)
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(adresse))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicule").value("V-0412"))
                .andExpect(jsonPath("$.gravite").value("BLOQUANT"));
    }

    @Test
    void un_signalement_inconnu_renvoie_404() throws Exception {
        mockMvc.perform(get("/signalements/inconnu"))
                .andExpect(status().isNotFound());
    }

    @Test
    void la_liste_contient_les_signalements_crees() throws Exception {
        signaler("""
                {"vehicule": "T-0042", "categorie": "rayure", "commentaire": ""}
                """);

        mockMvc.perform(get("/signalements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].vehicule", hasItem("T-0042")));
    }

    @Test
    void une_categorie_inconnue_renvoie_400() throws Exception {
        signaler("""
                {"vehicule": "V-0412", "categorie": "klaxon", "commentaire": ""}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Catégorie inconnue : klaxon"));
    }

    private ResultActions signaler(String json) throws Exception {
        return mockMvc.perform(post("/signalements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    @Test
    void remettre_en_service_un_vehicule_clot_ses_signalements_ouverts() throws Exception {
        String adresse = signaler("""
                {"vehicule": "V-0991", "categorie": "freins", "commentaire": "Frein avant HS"}
                """)
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(post("/signalements/vehicules/V-0991/remise-en-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"technicien": "Bob", "commentaire": "Plaquettes remplacées"}
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(adresse))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clos").value(true))
                .andExpect(jsonPath("$.prisPar").value("Bob"))
                .andExpect(jsonPath("$.commentaireCloture").value("Plaquettes remplacées"));

        mockMvc.perform(get("/signalements/ouverts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.vehicule == 'V-0991')]").isEmpty());
    }

    @Test
    void remettre_en_service_sans_commentaire_renvoie_400() throws Exception {
        mockMvc.perform(post("/signalements/vehicules/V-0992/remise-en-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"technicien": "Bob", "commentaire": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Le commentaire de réparation est obligatoire"));
    }
}
