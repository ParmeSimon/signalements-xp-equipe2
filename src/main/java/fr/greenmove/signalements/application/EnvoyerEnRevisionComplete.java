package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Pool;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.Signalements;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class EnvoyerEnRevisionComplete {

    private final Signalements signalements;
    private final Pool pool;
    private final Clock horloge;

    public EnvoyerEnRevisionComplete(Signalements signalements, Pool pool, Clock horloge) {
        this.signalements = signalements;
        this.pool = pool;
        this.horloge = horloge;
    }

    public void signaler(String vehicule, Categorie categorie, String commentaire) {
        new SignalerIncident(signalements, horloge).signaler(vehicule, categorie, commentaire);

        Instant ilYaSeptJours = Instant.now(horloge).minus(7, ChronoUnit.DAYS);
        int compteur = 0;
        for (Signalement s : signalements.tous()) {
            if (s.vehicule().equals(vehicule) && !s.signaleLe().isBefore(ilYaSeptJours)) {
                compteur++;
            }
        }

        if (compteur >= 3) {
            pool.envoyerEnRevisionComplete(vehicule);
        }
    }
}