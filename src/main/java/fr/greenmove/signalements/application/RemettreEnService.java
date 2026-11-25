package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Pool;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.domaine.Signalements;
import fr.greenmove.signalements.domaine.Vehicule;

import java.time.Instant;

/** S5 – Remettre en service un véhicule et clore tous ses signalements ouverts. */
public class RemettreEnService {

    private final Signalements signalements;
    private final Pool pool;

    public RemettreEnService(Signalements signalements, Pool pool) {
        this.signalements = signalements;
        this.pool = pool;
    }

}