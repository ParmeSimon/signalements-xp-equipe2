package fr.greenmove.signalements.domaine;
import java.time.Instant;

public record Trajet(Usager usager, String numeroVehicule, double distanceKm, Instant debut, Instant fin) {

    public static Trajet demarre(Usager usager, String numeroVehicule, Instant debut) {
        return new Trajet(usager, numeroVehicule, 0, debut, null);
    }

    public Trajet termine(double distanceKm, Instant fin) {
        return new Trajet(usager, numeroVehicule, distanceKm, debut, fin);
    }

    public boolean estEnCours() {
        return fin == null;
    }
}