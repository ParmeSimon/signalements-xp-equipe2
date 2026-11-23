package fr.greenmove.signalements.domaine;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Un incident signalé sur un véhicule.
 * Véhicules notés V-0412 pour un vélo, T-1187 pour une trottinette.
 */
public record Signalement(String id, String vehicule, Categorie categorie, String commentaire, Instant signaleLe) {

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

    public Gravite gravite() {
        return categorie.gravite();
    }
}
