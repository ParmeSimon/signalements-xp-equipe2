package fr.greenmove.signalements.infrastructure;

import fr.greenmove.signalements.application.PrendreEnChargeSignalement;
import fr.greenmove.signalements.application.SignalerIncident;
import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.domaine.Signalements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/signalements")
public class SignalementController {

    private final SignalerIncident signalerIncident;
    private final PrendreEnChargeSignalement prendreEnChargeSignalement;
    private final Signalements signalements;

    public SignalementController(SignalerIncident signalerIncident,
                                 PrendreEnChargeSignalement prendreEnChargeSignalement,
                                 Signalements signalements) {
        this.signalerIncident = signalerIncident;
        this.prendreEnChargeSignalement = prendreEnChargeSignalement;
        this.signalements = signalements;
    }

    public record NouveauSignalement(String vehicule, String categorie, String commentaire) {
    }

    /** S4 : le corps JSON d'une prise en charge, {"technicien": "Karim"}. */
    public record PriseEnCharge(String technicien) {
    }

    public record SignalementVue(String id, String vehicule, String categorie, String gravite,
                                 String commentaire, String signaleLe, String prisPar) {

        static SignalementVue de(Signalement s) {
            return new SignalementVue(s.id(), s.vehicule(), s.categorie().name(), s.gravite().name(),
                    s.commentaire(), s.signaleLe().toString(), s.prisPar());
        }
    }

    @PostMapping
    public ResponseEntity<SignalementVue> signaler(@RequestBody NouveauSignalement demande) {
        Signalement signalement = signalerIncident.signaler(
                demande.vehicule(), Categorie.de(demande.categorie()), demande.commentaire());
        return ResponseEntity.created(URI.create("/signalements/" + signalement.id()))
                .body(SignalementVue.de(signalement));
    }

    @GetMapping
    public List<SignalementVue> tous() {
        return signalements.tous().stream().map(SignalementVue::de).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignalementVue> parId(@PathVariable String id) {
        return ResponseEntity.of(signalements.parId(id).map(SignalementVue::de));
    }

    /** S4 : un technicien prend en charge le signalement. */
    @PostMapping("/{id}/prise-en-charge")
    public SignalementVue prendreEnCharge(@PathVariable String id, @RequestBody PriseEnCharge demande) {
        prendreEnChargeSignalement.prendre(id, demande.technicien());
        return SignalementVue.de(signalements.parId(id).orElseThrow());
    }

    @ExceptionHandler(SignalementInvalide.class)
    ResponseEntity<Map<String, String>> refuser(SignalementInvalide e) {
        return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
    }
}