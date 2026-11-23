package fr.greenmove.signalements.domaine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategorieTest {

    @Test
    void les_freins_sont_bloquants() {
        assertThat(Categorie.FREINS.gravite()).isEqualTo(Gravite.BLOQUANT);
    }

    @Test
    void un_pneu_est_genant() {
        assertThat(Categorie.PNEU.gravite()).isEqualTo(Gravite.GENANT);
    }

    @Test
    void un_qr_code_abime_est_genant() {
        assertThat(Categorie.de("qr_code").gravite()).isEqualTo(Gravite.GENANT);
    }

    @Test
    void la_proprete_est_cosmetique() {
        assertThat(Categorie.PROPRETE.gravite()).isEqualTo(Gravite.COSMETIQUE);
    }

    @Test
    void le_nom_saisi_par_l_usager_ignore_la_casse_et_les_espaces() {
        assertThat(Categorie.de(" freins ")).isEqualTo(Categorie.FREINS);
    }

    @Test
    void une_categorie_inconnue_est_refusee() {
        assertThatThrownBy(() -> Categorie.de("klaxon"))
                .isInstanceOf(SignalementInvalide.class)
                .hasMessage("Catégorie inconnue : klaxon");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "  ")
    void le_nom_de_la_categorie_est_obligatoire(String nom) {
        assertThatThrownBy(() -> Categorie.de(nom))
                .isInstanceOf(SignalementInvalide.class)
                .hasMessage("La catégorie est obligatoire");
    }
}
