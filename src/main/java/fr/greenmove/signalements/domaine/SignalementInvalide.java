package fr.greenmove.signalements.domaine;

/** Un signalement que le domaine refuse : le message est destiné à l'usager. */
public class SignalementInvalide extends RuntimeException {

    public SignalementInvalide(String message) {
        super(message);
    }
}
