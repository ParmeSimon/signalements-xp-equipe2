package fr.greenmove.signalements.domaine;

/**
 * Gravité d'un signalement. Elle dit ce qui arrive au véhicule.
 * BLOQUANT : retiré de la location dès le signalement.
 * GENANT : retiré à la fin de la location en cours.
 * COSMETIQUE : reste en service, traité à la prochaine tournée de l'atelier.
 */
public enum Gravite {
    BLOQUANT,
    GENANT,
    COSMETIQUE
}
