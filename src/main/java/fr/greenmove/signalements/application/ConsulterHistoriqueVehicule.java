package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.Signalements;

import java.util.Comparator;
import java.util.List;

/** S12 – Consulter l'historique des incidents d'un véhicule. */
public class ConsulterHistoriqueVehicule {

    private final Signalements signalements;

    public ConsulterHistoriqueVehicule(Signalements signalements) {
        this.signalements = signalements;
    }

    public List<Signalement> de(String vehicule) {
        return signalements.tous().stream()                                   // tous les signalements
                .filter(s -> s.vehicule().equals(vehicule))
                //.sorted(Comparator.comparing(Signalement::signaleLe).reversed())
                .toList();
    }
}
