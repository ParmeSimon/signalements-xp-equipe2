package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Gravite;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.Signalements;

import java.util.Comparator;
import java.util.List;

/** S3 – Voir les signalements ouverts dans l'application. */
public class VoirSignalementsOuverts {

    private final Signalements signalements;

    public VoirSignalementsOuverts(Signalements signalements) {
        this.signalements = signalements;
    }

    public List<Signalement> lister() {
        return signalements.tous().stream()
                .sorted(Comparator
                        .comparingInt((Signalement s) -> priorite(s.gravite()))
                        .thenComparing(Signalement::signaleLe))
                .toList();
    }

    private int priorite(Gravite gravite) {
        return switch (gravite) {
            case BLOQUANT -> 1;
            case GENANT -> 2;
            case COSMETIQUE -> 3;
        };
    }
}