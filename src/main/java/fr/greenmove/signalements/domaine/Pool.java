package fr.greenmove.signalements.domaine;

public interface Pool {

    void ajouter(Vehicule vehicule);

    Vehicule parNumero(String numero);

    void retirer(String numero);
}