package fr.greenmove.signalements.domaine;

/** Port : prévenir les personnes concernées. L'infrastructure l'implémente. */
public interface Notifications {

    /** Prévient le déclarant que le véhicule signalé est en cours d'utilisation. */
    void alerterDeclarant(String numeroVehicule);

    /** Demande à l'usager de s'arrêter immédiatement (notification critique). */
    void demanderArret(Usager usager, String numeroVehicule);
}
