package fr.greenmove.signalements.domaine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignalementTest {

    private static final Instant MAINTENANT = Instant.parse("2026-11-23T14:00:00Z");

    @Test
    void un_velo_et_une_trottinette_ont_un_numero_valide() {
        assertThat(new Signalement("1", "V-0412", Categorie.PNEU, "", MAINTENANT).vehicule()).isEqualTo("V-0412");
        assertThat(new Signalement("2", "T-1187", Categorie.PNEU, "", MAINTENANT).vehicule()).isEqualTo("T-1187");
    }

    @Test
    void un_numero_de_vehicule_mal_forme_est_refuse() {
        assertThatThrownBy(() -> new Signalement("1", "X-12", Categorie.FREINS, "", MAINTENANT))
                .isInstanceOf(SignalementInvalide.class)
                .hasMessage("Numéro de véhicule invalide : X-12");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "  ")
    void le_numero_de_vehicule_est_obligatoire(String vehicule) {
        assertThatThrownBy(() -> new Signalement("1", vehicule, Categorie.FREINS, "", MAINTENANT))
                .isInstanceOf(SignalementInvalide.class)
                .hasMessage("Le numéro de véhicule est obligatoire");
    }

    @Test
    void la_categorie_est_obligatoire() {
        assertThatThrownBy(() -> new Signalement("1", "V-0412", null, "", MAINTENANT))
                .isInstanceOf(SignalementInvalide.class)
                .hasMessage("La catégorie est obligatoire");
    }

    @Test
    void le_commentaire_est_facultatif() {
        assertThat(new Signalement("1", "V-0412", Categorie.RAYURE, null, MAINTENANT).commentaire()).isEmpty();
    }

    @Test
    void un_commentaire_de_la_longueur_maximale_est_accepte() {
        String commentaire = "a".repeat(Signalement.LONGUEUR_MAX_DU_COMMENTAIRE);
        assertThat(new Signalement("1", "V-0412", Categorie.AUTRE, commentaire, MAINTENANT).commentaire())
                .isEqualTo(commentaire);
    }

    @Test
    void un_commentaire_trop_long_est_refuse() {
        String commentaire = "a".repeat(Signalement.LONGUEUR_MAX_DU_COMMENTAIRE + 1);
        assertThatThrownBy(() -> new Signalement("1", "V-0412", Categorie.AUTRE, commentaire, MAINTENANT))
                .isInstanceOf(SignalementInvalide.class)
                .hasMessage("Le commentaire dépasse 500 caractères");
    }

    @Test
    void un_nouveau_signalement_est_libre() {
        Signalement signalement = new Signalement("1", "V-0412", Categorie.FREINS, "", MAINTENANT);

        assertThat(signalement.estLibre()).isTrue();
    }

    @Test
    void le_technicien_qui_prend_un_signalement_voit_son_nom_dessus() {
        Signalement signalement = new Signalement("1", "V-0412", Categorie.FREINS, "", MAINTENANT);

        Signalement pris = signalement.prendre("Karim");

        assertThat(pris.prisPar()).isEqualTo("Karim");
        assertThat(pris.estLibre()).isFalse();
    }

    @Test
    void un_signalement_pris_ne_peut_pas_etre_pris_par_un_autre_technicien() {
        Signalement prisParKarim = new Signalement("1", "V-0412", Categorie.FREINS, "", MAINTENANT).prendre("Karim");

        assertThatThrownBy(() -> prisParKarim.prendre("Julie"))
                .isInstanceOf(SignalementInvalide.class)
                .hasMessage("Signalement déjà pris par Karim");
    }

    @Test
    void un_signalement_relache_redevient_libre() {
        Signalement prisParKarim = new Signalement("1", "V-0412", Categorie.FREINS, "", MAINTENANT).prendre("Karim");

        Signalement relache = prisParKarim.relacher();

        assertThat(relache.estLibre()).isTrue();
        assertThat(relache.prisPar()).isNull();
    }

    @Test
    void un_nouveau_signalement_est_ouvert() {
        Signalement signalement = new Signalement("1", "V-0412", Categorie.FREINS, "", MAINTENANT);

        assertThat(signalement.clos()).isFalse();
        assertThat(signalement.estOuvert()).isTrue();
        assertThat(signalement.commentaireCloture()).isNull();
    }

    @Test
    void clore_un_signalement_le_ferme_avec_le_commentaire_et_le_technicien() {
        Signalement signalement = new Signalement("1", "V-0412", Categorie.FREINS, "", MAINTENANT);

        Signalement clos = signalement.clore("Bob", "  Plaquettes changées  ");

        assertThat(clos.clos()).isTrue();
        assertThat(clos.estOuvert()).isFalse();
        assertThat(clos.prisPar()).isEqualTo("Bob");
        assertThat(clos.commentaireCloture()).isEqualTo("Plaquettes changées");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void le_commentaire_de_cloture_est_obligatoire(String commentaireInvalide) {
        Signalement signalement = new Signalement("1", "V-0412", Categorie.FREINS, "", MAINTENANT);

        assertThatThrownBy(() -> signalement.clore("Bob", commentaireInvalide))
                .isInstanceOf(SignalementInvalide.class)
                .hasMessage("Le commentaire de réparation est obligatoire");
    }
}