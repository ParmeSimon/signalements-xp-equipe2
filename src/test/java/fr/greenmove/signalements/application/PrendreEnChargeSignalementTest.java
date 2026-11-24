package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.infrastructure.SignalementsEnMemoire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PrendreEnChargeSignalementTest {

    private static final Instant MAINTENANT = Instant.parse("2026-11-24T09:00:00Z");

    private final SignalementsEnMemoire signalements = new SignalementsEnMemoire();
    private final PrendreEnChargeSignalement prendreEnCharge = new PrendreEnChargeSignalement(signalements);

    @BeforeEach
    void un_signalement_existe() {
        signalements.enregistrer(new Signalement("1", "V-0412", Categorie.FREINS, "", MAINTENANT));
    }

    @Test
    void prendre_un_signalement_l_enregistre_comme_pris() {
        prendreEnCharge.prendre("1", "Karim");

        assertThat(signalements.parId("1").orElseThrow().prisPar()).isEqualTo("Karim");
    }
}