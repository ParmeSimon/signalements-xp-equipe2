package fr.greenmove.signalements.infrastructure;

import fr.greenmove.signalements.domaine.Pool;
import fr.greenmove.signalements.domaine.Vehicule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Pool en mémoire : suffisant pour le TP, perdu à l'arrêt de l'application. */
public class PoolEnMemoire implements Pool {

    private final Map<String, Vehicule> parNumero = new HashMap<>();

    public synchronized void ajouter(Vehicule vehicule) {
        parNumero.put(vehicule.numero(), vehicule);
    }

    @Override
    public synchronized Vehicule parNumero(String numero) {
        return parNumero.get(numero);
    }

    @Override
    public synchronized void retirer(String numero) {
        parNumero.remove(numero);
    }

    @Override
    public synchronized List<Vehicule> tous() {
        return new ArrayList<>(parNumero.values());
    }

    private final Set<String> enRevisionComplete = new HashSet<>();

    @Override
    public void envoyerEnRevisionComplete(String vehicule) {
        retirer(vehicule);
        enRevisionComplete.add(vehicule);
    }

    @Override
    public boolean estEnRevisionComplete(String vehicule) {
        return enRevisionComplete.contains(vehicule);
    }
}
