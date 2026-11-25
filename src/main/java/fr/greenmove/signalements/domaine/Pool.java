package fr.greenmove.signalements.domaine;

import java.util.List;

public interface Pool {

    void ajouter(Vehicule vehicule);

    Vehicule parNumero(String numero);

    void retirer(String numero);

    /* S21 : une copie des véhicules du pool, qu'on peut parcourir en retirant des véhicules. */
    List<Vehicule> tous();

    void envoyerEnRevisionComplete(String vehicule);

    boolean estEnRevisionComplete(String vehicule);
}