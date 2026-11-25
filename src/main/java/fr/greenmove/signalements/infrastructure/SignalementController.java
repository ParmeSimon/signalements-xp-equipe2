package fr.greenmove.signalements.infrastructure;

import fr.greenmove.signalements.application.PrendreEnChargeSignalement;
import fr.greenmove.signalements.application.RemettreEnService;
import fr.greenmove.signalements.application.SignalerIncident;
import fr.greenmove.signalements.application.VoirSignalementsOuverts;
import fr.greenmove.signalements.domaine.Categorie;
import fr.greenmove.signalements.domaine.Signalement;
import fr.greenmove.signalements.domaine.SignalementInvalide;
import fr.greenmove.signalements.domaine.Signalements;
import fr.greenmove.signalements.application.ConsulterHistoriqueVehicule;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final VoirSignalementsOuverts voirSignalementsOuverts;
    private final PrendreEnChargeSignalement prendreEnChargeSignalement;
    private final RemettreEnService remettreEnService;
    private final Signalements signalements;
    private final ConsulterHistoriqueVehicule consulterHistoriqueVehicule;

    @Autowired
    public SignalementController(SignalerIncident signalerIncident,
                                 VoirSignalementsOuverts voirSignalementsOuverts,
                                 PrendreEnChargeSignalement prendreEnChargeSignalement,
                                 RemettreEnService remettreEnService,
                                 ConsulterHistoriqueVehicule consulterHistoriqueVehicule,
                                 Signalements signalements) {
        this.signalerIncident = signalerIncident;
        this.voirSignalementsOuverts = voirSignalementsOuverts;
        this.prendreEnChargeSignalement = prendreEnChargeSignalement;
        this.remettreEnService = remettreEnService;
        this.consulterHistoriqueVehicule = consulterHistoriqueVehicule;
        this.signalements = signalements;
    }

    public record NouveauSignalement(String vehicule, String categorie, String commentaire) {
    }

    /** S4 : le corps JSON d'une prise en charge, {"technicien": "Karim"}. */
    public record PriseEnCharge(String technicien) {
    }

    /** S5 : le corps JSON d'une remise en service, {"technicien": "Bob", "commentaire": "..."}. */
    public record RemiseEnServiceDemande(String technicien, String commentaire) {
    }

    public record SignalementVue(String id, String vehicule, String categorie, String gravite,
                                 String commentaire, String signaleLe, String prisPar,
                                 boolean clos, String commentaireCloture) {

        static SignalementVue de(Signalement s) {
            return new SignalementVue(s.id(), s.vehicule(), s.categorie().name(), s.gravite().name(),
                    s.commentaire(), s.signaleLe().toString(), s.prisPar(),
                    s.clos(), s.commentaireCloture());
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

    /** S3 – Les signalements ouverts : bloquants, puis gênants, puis cosmétiques ; le plus ancien d'abord. */
    @GetMapping("/ouverts")
    public List<SignalementVue> ouverts() {
        return voirSignalementsOuverts.lister().stream().map(SignalementVue::de).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignalementVue> parId(@PathVariable String id) {
        return ResponseEntity.of(signalements.parId(id).map(SignalementVue::de));
    }

    /** S12 – L'historique des incidents d'un véhicule, du plus récent au plus ancien. */
    @GetMapping("/vehicules/{vehicule}/historique")
    public List<SignalementVue> historique(@PathVariable String vehicule) {
        return consulterHistoriqueVehicule.de(vehicule).stream().map(SignalementVue::de).toList();
    }

    /** S4 : un technicien prend en charge le signalement. */
    @PostMapping("/{id}/prise-en-charge")
    public SignalementVue prendreEnCharge(@PathVariable String id, @RequestBody PriseEnCharge demande) {
        prendreEnChargeSignalement.prendre(id, demande.technicien());
        return SignalementVue.de(signalements.parId(id).orElseThrow());
    }

    /** S5 : remet en service un véhicule et clôt tous ses signalements ouverts. */
    @PostMapping("/vehicules/{vehicule}/remise-en-service")
    public ResponseEntity<Void> remettreEnService(@PathVariable String vehicule,
                                                  @RequestBody RemiseEnServiceDemande demande) {
        remettreEnService.remettreEnService(vehicule, demande.technicien(), demande.commentaire());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(SignalementInvalide.class)
    ResponseEntity<?> refuser(SignalementInvalide e) {
        return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
    }
}
