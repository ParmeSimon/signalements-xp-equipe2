package fr.greenmove.signalements.infrastructure;

import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.Signalements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Stockage en mémoire : suffisant pour le TP, perdu à l'arrêt de l'application. */
public class SignalementsEnMemoire implements Signalements {

    private final Map<String, Signalement> parId = new LinkedHashMap<>();
    private final Map<String, Signalement> parVehicule = new LinkedHashMap<>();

    @Override
    public synchronized void enregistrer(Signalement signalement) {
        parId.put(signalement.id(), signalement);
    }

    @Override
    public synchronized Optional<Signalement> parId(String id) {
        return Optional.ofNullable(parId.get(id));
    }

    @Override
    public synchronized List<Signalement> parVehicule(String vehicule) {
        return parId.values().stream()
                .filter(s -> s.vehicule().equals(vehicule))
                .toList();
    }

    @Override
    public synchronized List<Signalement> tous() {
        return new ArrayList<>(parId.values());
    }
}
