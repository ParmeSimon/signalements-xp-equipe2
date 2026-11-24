package fr.greenmove.signalements.infrastructure;

import fr.greenmove.signalements.domaine.Notifications;
import fr.greenmove.signalements.domaine.Pool;
import fr.greenmove.signalements.domaine.Trajet;
import fr.greenmove.signalements.domaine.Usager;
import fr.greenmove.signalements.domaine.Vehicule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class RetraitDuPoolIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    Pool pool;

    @MockitoBean
    Notifications notifications;   // remplace le vrai bean par un mock qu'on peut vérifier

    @Test
    void un_signalement_bloquant_en_http_retire_le_vehicule_et_arrete_l_usager() throws Exception {
        // Préparer : un véhicule en cours d'utilisation dans le VRAI pool de l'appli
        Vehicule velo = Vehicule.creer("V-0777", Instant.now());
        velo.affecter(Trajet.demarre(new Usager("U-12"), "V-0777", Instant.now()), Instant.now());
        ((PoolEnMemoire) pool).ajouter(velo);

        // Agir : passer par l'API HTTP, comme le ferait l'appli mobile
        mockMvc.perform(post("/signalements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vehicule": "V-0777", "categorie": "freins", "commentaire": "Plus de frein"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gravite").value("BLOQUANT"));

        // Vérifier : tout le câblage Spring a fonctionné
        assertThat(pool.parNumero("V-0777")).isNull();
        verify(notifications).demanderArret(new Usager("U-12"), "V-0777");
    }
}