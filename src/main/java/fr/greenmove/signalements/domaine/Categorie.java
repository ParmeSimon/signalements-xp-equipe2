package fr.greenmove.signalements.domaine;

import java.util.Locale;

/**
 * Catégorie choisie par l'usager. Chaque catégorie porte sa gravité :
 * c'est le seul endroit du code où cette règle est écrite.
 */
public enum Categorie {
    FREINS(Gravite.BLOQUANT),
    DIRECTION(Gravite.BLOQUANT),
    BATTERIE(Gravite.BLOQUANT),
    ECLAIRAGE(Gravite.GENANT),
    PNEU(Gravite.GENANT),
    BEQUILLE(Gravite.GENANT),
    CADENAS(Gravite.GENANT),
    QR_CODE(Gravite.GENANT),
    PROPRETE(Gravite.COSMETIQUE),
    RAYURE(Gravite.COSMETIQUE),
    AUTRE(Gravite.COSMETIQUE);

    private final Gravite gravite;

    Categorie(Gravite gravite) {
        this.gravite = gravite;
    }

    public static Categorie de(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new SignalementInvalide("La catégorie est obligatoire");
        }
        try {
            return valueOf(nom.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new SignalementInvalide("Catégorie inconnue : " + nom);
        }
    }

    public Gravite gravite() {
        return gravite;
    }
}
