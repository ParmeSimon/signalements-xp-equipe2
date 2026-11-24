package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VoirSignalementsOuvertsTest {

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final VoirSignalementsOuverts voirSignalementsOuverts = new VoirSignalementsOuverts(signalements);

    @Test
    void renvoie_une_liste_vide_quand_il_n_y_a_aucun_signalement() {
        List<Signalement> resultat = voirSignalementsOuverts.lister();

        assertThat(resultat).isEmpty();
    }

    @Test
    void trie_les_signalements_par_gravite_bloquants_puis_genants_puis_cosmetiques() {
        Instant memeDate = Instant.parse("2026-11-23T10:00:00Z");

        signalements.enregistrer(new Signalement("1", "V-0001", Categorie.RAYURE, "", memeDate));
        signalements.enregistrer(new Signalement("2", "V-0002", Categorie.PNEU, "", memeDate));
        signalements.enregistrer(new Signalement("3", "V-0003", Categorie.FREINS, "", memeDate));

        List<Signalement> resultat = voirSignalementsOuverts.lister();

        assertThat(resultat)
                .extracting(Signalement::vehicule)
                .containsExactly("V-0003", "V-0002", "V-0001");
    }

    @Test
    void trie_par_anciennete_a_gravite_egale() {
        signalements.enregistrer(new Signalement("1", "V-0001", Categorie.FREINS, "", Instant.parse("2026-11-23T12:00:00Z")));
        signalements.enregistrer(new Signalement("2", "V-0002", Categorie.FREINS, "", Instant.parse("2026-11-23T09:00:00Z")));
        signalements.enregistrer(new Signalement("3", "V-0003", Categorie.FREINS, "", Instant.parse("2026-11-23T10:30:00Z")));

        List<Signalement> resultat = voirSignalementsOuverts.lister();

        assertThat(resultat)
                .extracting(Signalement::vehicule)
                .containsExactly("V-0002", "V-0003", "V-0001");
    }
}