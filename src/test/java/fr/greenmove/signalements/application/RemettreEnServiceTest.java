package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.infrastructure.PoolEnMemoire;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemettreEnServiceTest {

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final PoolEnMemoire pool = new PoolEnMemoire();
    private final RemettreEnService useCase = new RemettreEnService(signalements, pool);

    @Test
    void ne_clot_que_les_signalements_du_vehicule_concerne() {
        var s1 = new Signalement("S-1", "V-0412", Categorie.FREINS, "Frein HS", Instant.parse("2026-11-23T10:00:00Z"));
        var s2 = new Signalement("S-2", "T-1187", Categorie.PNEU, "Pneu plat", Instant.parse("2026-11-23T10:05:00Z"));
        signalements.enregistrer(s1);
        signalements.enregistrer(s2);

        useCase.remettreEnService("V-0412", "Alice", "Frein réparé");

        assertThat(signalements.parId("S-1").orElseThrow().clos()).isTrue();
        assertThat(signalements.parId("S-2").orElseThrow().clos()).isFalse();
    }

    @Test
    void refuse_un_commentaire_compose_uniquement_d_espaces() {
        var s1 = new Signalement("S-1", "V-0412", Categorie.FREINS, "Frein HS", Instant.parse("2026-11-23T10:00:00Z"));
        signalements.enregistrer(s1);

        assertThatThrownBy(() -> useCase.remettreEnService("V-0412", "Alice", "   "))
                .isInstanceOf(SignalementInvalide.class);

        assertThat(signalements.parId("S-1").orElseThrow().clos()).isFalse();
        assertThat(pool.parNumero("V-0412")).isNull();
    }

    @Test
    void rend_le_vehicule_actif_dans_le_pool() {
        pool.retirer("V-0412");

        useCase.remettreEnService("V-0412", "Bob", "Contrôle OK");

        var vehicule = pool.parNumero("V-0412");
        assertThat(vehicule).isNotNull();
        assertThat(vehicule.isActive()).isTrue();
    }
}