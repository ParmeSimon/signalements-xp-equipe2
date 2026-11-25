package fr.greenmove.signalements.acceptation;

import fr.greenmove.signalements.domaine.Pool;
import fr.greenmove.signalements.domaine.Vehicule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FauxPool implements Pool {

    private final Map<String, Vehicule> vehicules = new HashMap<>();   // numéro → véhicule

    public void ajouter(Vehicule vehicule) {
        vehicules.put(vehicule.numero(), vehicule);
    }

    public boolean contient(String numero) {
        return vehicules.containsKey(numero);
    }

    @Override
    public Vehicule parNumero(String numero) {
        return vehicules.get(numero);
    }

    @Override
    public void retirer(String numero) {
        vehicules.remove(numero);
    }

    @Override
    public List<Vehicule> tous() {
        return new ArrayList<>(vehicules.values());
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