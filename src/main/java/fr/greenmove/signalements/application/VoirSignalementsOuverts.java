package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.Signalements;

import java.util.List;

/** S3 – Voir les signalements ouverts dans l'application. */
public class VoirSignalementsOuverts {

    private final Signalements signalements;

    public VoirSignalementsOuverts(Signalements signalements) {
        this.signalements = signalements;
    }

    public List<Signalement> lister() {
        return List.of();
    }
}