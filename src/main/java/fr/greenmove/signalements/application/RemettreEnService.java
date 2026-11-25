package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Pool;
import fr.greenmove.signalements.domaine.Signalements;

/** S5 – Remettre en service un véhicule et clore tous ses signalements ouverts. */
public class RemettreEnService {

    private final Signalements signalements;
    private final Pool pool;

    public RemettreEnService(Signalements signalements, Pool pool) {
        this.signalements = signalements;
        this.pool = pool;
    }

    public void remettreEnService(String vehicule, String technicien, String commentaire) {
        // Pas encore implémenté (étape Rouge TDD)
    }
}