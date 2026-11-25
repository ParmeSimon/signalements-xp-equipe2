package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Pool;

/** S21 – Retirer d'un coup de la location les trottinettes d'une série rappelée par le constructeur. */
public class RappelConstructeur {

    static final String COMMENTAIRE = "Rappel constructeur";

    private final Pool pool;
    private final SignalerIncident signalerIncident;

    public RappelConstructeur(Pool pool, SignalerIncident signalerIncident) {
        this.pool = pool;
        this.signalerIncident = signalerIncident;
    }

    /** @param serie début du numéro des trottinettes rappelées, par exemple "T-11" pour les T-11xx */
    public void rappeler(String serie) {
        pool.tous().stream()
                .filter(vehicule -> vehicule.numero().startsWith(serie))
                .forEach(vehicule -> signalerIncident.signaler(
                        vehicule.numero(), Categorie.RAPPEL_CONSTRUCTEUR, COMMENTAIRE));
    }
}