package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Pool;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.domaine.Signalements;
import fr.greenmove.signalements.domaine.Vehicule;

import java.time.Instant;
import java.util.List;

/** S5 – Remettre en service un véhicule et clore tous ses signalements ouverts. */
public class RemettreEnService {

    private final Signalements signalements;
    private final Pool pool;

    public RemettreEnService(Signalements signalements, Pool pool) {
        this.signalements = signalements;
        this.pool = pool;
    }

    public void remettreEnService(String vehicule, String technicien, String commentaire) {
        if (commentaire == null || commentaire.isBlank()) {
            throw new SignalementInvalide("Le commentaire de réparation est obligatoire");
        }

        List<Signalement> tous = signalements.tous();
        for (int i = 0; i < tous.size(); i++) {
            Signalement s = tous.get(i);
            if (s.vehicule().equals(vehicule) && !s.clos()) {
                Signalement clos = new Signalement(
                        s.id(),
                        s.vehicule(),
                        s.categorie(),
                        s.commentaire(),
                        s.signaleLe(),
                        technicien,
                        true,
                        commentaire.strip()
                );
                signalements.enregistrer(clos);
            }
        }

        if (pool != null) {
            Vehicule v = pool.parNumero(vehicule);
            if (v == null) {
                v = Vehicule.creer(vehicule, Instant.now());
            } else {
                v.reactiver(Instant.now());
            }
            pool.ajouter(v);
        }
    }
}