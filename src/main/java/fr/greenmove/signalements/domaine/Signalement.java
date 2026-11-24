package fr.greenmove.signalements.domaine;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Un incident signalé sur un véhicule.
 * Véhicules notés V-0412 pour un vélo, T-1187 pour une trottinette.
 */
public record Signalement(String id, String vehicule, Categorie categorie, String commentaire, Instant signaleLe,
                          String prisPar) {

    private static final Pattern NUMERO_DE_VEHICULE = Pattern.compile("[VT]-\\d{4}");
    public static final int LONGUEUR_MAX_DU_COMMENTAIRE = 500;

    public Signalement {
        if (vehicule == null || vehicule.isBlank()) {
            throw new SignalementInvalide("Le numéro de véhicule est obligatoire");
        }
        if (!NUMERO_DE_VEHICULE.matcher(vehicule).matches()) {
            throw new SignalementInvalide("Numéro de véhicule invalide : " + vehicule);
        }
        if (categorie == null) {
            throw new SignalementInvalide("La catégorie est obligatoire");
        }
        commentaire = commentaire == null ? "" : commentaire.strip();
        if (commentaire.length() > LONGUEUR_MAX_DU_COMMENTAIRE) {
            throw new SignalementInvalide("Le commentaire dépasse " + LONGUEUR_MAX_DU_COMMENTAIRE + " caractères");
        }
    }

    /** Un signalement neuf n'est pris par personne : garde les appels à 5 paramètres de S1. */
    public Signalement(String id, String vehicule, Categorie categorie, String commentaire, Instant signaleLe) {
        this(id, vehicule, categorie, commentaire, signaleLe, null);
    }

    public Gravite gravite() {
        return categorie.gravite();
    }

    /** S4 : un signalement est libre tant qu'aucun technicien ne l'a pris. */
    public boolean estLibre() {
        return prisPar != null;
    }

    /** S4 : un signalement pris ne peut pas être pris par un autre technicien. */
    public Signalement prendre(String technicien) {
        if (!estLibre()) {
            throw new SignalementInvalide("Signalement déjà pris par " + prisPar);
        }
        return new Signalement(id, vehicule, categorie, commentaire, signaleLe, technicien);
    }

    /** S4 : le technicien relâche le signalement qu'il ne peut pas traiter, qui redevient libre. */
    public Signalement relacher() {
        return new Signalement(id, vehicule, categorie, commentaire, signaleLe, null);
    }
}

