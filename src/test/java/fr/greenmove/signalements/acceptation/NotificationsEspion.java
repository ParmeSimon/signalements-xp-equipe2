package fr.greenmove.signalements.acceptation;

import fr.greenmove.signalements.domaine.Notifications;
import fr.greenmove.signalements.domaine.Usager;

import java.util.ArrayList;
import java.util.List;

/** Doublure de test : retient les notifications envoyées pour que les étapes Alors les vérifient. */
public class NotificationsEspion implements Notifications {

    private final List<String> alertesAuDeclarant = new ArrayList<>();
    private final List<Usager> demandesDArret = new ArrayList<>();

    @Override
    public void alerterDeclarant(String numeroVehicule) {
        alertesAuDeclarant.add(numeroVehicule);
    }

    @Override
    public void demanderArret(Usager usager, String numeroVehicule) {
        demandesDArret.add(usager);
    }

    public List<String> alertesAuDeclarant() {
        return alertesAuDeclarant;
    }

    public List<Usager> demandesDArret() {
        return demandesDArret;
    }
}
