package fr.greenmove.signalements.infrastructure;

import fr.greenmove.signalements.domaine.Notifications;
import fr.greenmove.signalements.domaine.Usager;

/** Notifications écrites dans le journal : en attendant un vrai envoi (push, SMS…). */
public class NotificationsJournalisees implements Notifications {

    private static final System.Logger JOURNAL = System.getLogger(NotificationsJournalisees.class.getName());

    @Override
    public void alerterDeclarant(String numeroVehicule) {
        JOURNAL.log(System.Logger.Level.WARNING,
                "Alerte déclarant : le véhicule {0} est en cours d''utilisation", numeroVehicule);
    }

    @Override
    public void demanderArret(Usager usager, String numeroVehicule) {
        JOURNAL.log(System.Logger.Level.ERROR,
                "Notification critique à {0} : arrêtez immédiatement le véhicule {1}",
                usager.identifiant(), numeroVehicule);
    }
}
