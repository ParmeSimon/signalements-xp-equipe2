package fr.greenmove.signalements.domaine;

import java.util.List;
import java.util.Optional;

/** Port : là où les signalements sont gardés. L'infrastructure l'implémente. */
public interface Signalements {

    void enregistrer(Signalement signalement);

    Optional<Signalement> parId(String id);

    List<Signalement> tous();
}
