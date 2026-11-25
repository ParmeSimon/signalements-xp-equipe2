package fr.greenmove.signalements.application;

import fr.greenmove.signalements.domaine.Pool;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.domaine.Signalements;
import fr.greenmove.signalements.domaine.Vehicule;

import java.time.Clock;
import java.time.Instant;

/** S5 – Remettre en service un véhicule et clore tous ses signalements ouverts. */
public class RemettreEnService {

    private final Signalements signalements;
    private final Pool pool;

    public RemettreEnService(Signalements signalements, Pool pool, Clock horloge) {
        this.signalements = signalements;
        this.pool = pool;
    }

    public void remettreEnService(String vehicule, String technicien, String commentaire) {
        if (commentaire == null || commentaire.isBlank()) {
            throw new SignalementInvalide("Le commentaire de réparation est obligatoire");
        }

        signalements.tous().stream()
                .filter(s -> s.vehicule().equals(vehicule))
                .filter(Signalement::estOuvert)
                .map(s -> s.clore(technicien, commentaire))
                .forEach(signalements::enregistrer);

        reintegrerDansLePool(vehicule);
    }

    private void reintegrerDansLePool(String vehicule) {
        if (pool == null) {
            return;
        }
        Vehicule v = pool.parNumero(vehicule);
        if (v == null) {
            v = Vehicule.creer(vehicule, Instant.now());
        } else {
            v.reactiver(Instant.now());
        }
        pool.ajouter(v);
    }

}