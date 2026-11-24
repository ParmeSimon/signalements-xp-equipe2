package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Gravite;
import fr.greenmove.signalements.domaine.Vehicule;
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
        if (signalement.gravite() == Gravite.BLOQUANT) {
            retirerDuPool(vehicule);
        }
        return signalement;
    }

    /** S2 – Un incident bloquant retire le véhicule du pool et fait arrêter l'usager qui roule avec. */
    private void retirerDuPool(String numero) {
        if (pool == null) {
            return; // ancien constructeur (S1) : pas de pool branché
        }
        Vehicule vehicule = pool.parNumero(numero);
        pool.retirer(numero);
        if (vehicule != null && vehicule.estEnCoursDUtilisation()) {
            notifications.alerterDeclarant(numero);
            notifications.demanderArret(vehicule.trajet().usager(), numero);
        }
    }
}
