package fr.greenmove.signalements.domaine;

import java.time.Instant;

public abstract sealed class Vehicule permits Velo, Trottinette {

    private final String numero;
    private final Instant createdAt;
    private Trajet trajet;
    private boolean active;
    private Instant updatedAt;

    protected Vehicule(String numero, Instant maintenant) {
        this.numero = numero;
        this.createdAt = maintenant;
        this.updatedAt = maintenant;
        this.active = true;
        this.trajet = null;
    }

    /** V-xxxx = vélo, T-xxxx = trottinette. */
    public static Vehicule creer(String numero, Instant maintenant) {
        return numero.startsWith("V") ? new Velo(numero, maintenant) : new Trottinette(numero, maintenant);
    }

    public void affecter(Trajet trajet, Instant maintenant) {
        this.trajet = trajet;
        this.updatedAt = maintenant;
    }

    public void desactiver(Instant maintenant) {
        this.active = false;
        this.updatedAt = maintenant;
    }

    public boolean estEnCoursDUtilisation() { return trajet != null && trajet.estEnCours(); }

    public String numero() { return numero; }
    public Trajet trajet() { return trajet; }
    public boolean isActive() { return active; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}