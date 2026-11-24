package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Notifications;
import fr.greenmove.signalements.domaine.Pool;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.Signalements;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/** S1 – Signaler un incident depuis l'application : véhicule, catégorie, commentaire. */
public class SignalerIncident {

    private final Signalements signalements;
    private final Pool pool;
    private final Notifications notifications;
    private final Clock horloge;

    public SignalerIncident(Signalements signalements, Clock horloge) {
        this(signalements, null, null, horloge);
    }

    public SignalerIncident(Signalements signalements, Pool pool, Notifications notifications, Clock horloge) {
        this.signalements = signalements;
        this.pool = pool;
        this.notifications = notifications;
        this.horloge = horloge;
    }

    public Signalement signaler(String vehicule, Categorie categorie, String commentaire) {
        Signalement signalement = new Signalement(
                UUID.randomUUID().toString(), vehicule, categorie, commentaire, Instant.now(horloge));
        signalements.enregistrer(signalement);
        return signalement;
    }
}
