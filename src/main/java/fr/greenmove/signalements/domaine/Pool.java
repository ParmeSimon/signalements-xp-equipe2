package fr.greenmove.signalements.domaine;

public interface Pool {

    Vehicule parNumero(String numero);

    void retirer(String numero);
}