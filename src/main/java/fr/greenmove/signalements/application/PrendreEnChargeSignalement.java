package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.domaine.Signalements;

/** S4 – Prendre en charge un signalement : le prendre, le relâcher. */
public class PrendreEnChargeSignalement {

    private final Signalements signalements;

    public PrendreEnChargeSignalement(Signalements signalements) {
        this.signalements = signalements;
    }

    public void prendre(String idDuSignalement, String technicien) {
        signalements.enregistrer(trouver(idDuSignalement).prendre(technicien));
    }

    public void relacher(String idDuSignalement) {
        signalements.enregistrer(trouver(idDuSignalement).relacher());
    }

    private Signalement trouver(String idDuSignalement) {
        return signalements.parId(idDuSignalement)
                .orElseThrow(() -> new SignalementInvalide("Signalement introuvable : " + idDuSignalement));
    }
}
