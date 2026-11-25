# language: fr
Fonctionnalité: S5 – Remettre en service
  En tant que technicien,
  je veux pouvoir remettre en service un véhicule, même si ce n'est pas moi qui ai fait ou pris en charge le signalement,
  pour que les véhicules redeviennent louables le plus rapidement possible.

  Scénario: Un technicien remet en service un véhicule avec un signalement ouvert
    Étant donné un véhicule "V-0412" indisponible à la location
    Et un signalement enregistré pour "FREINS" sur le véhicule "V-0412"
    Quand le technicien remet en service le véhicule "V-0412" avec le commentaire "Remplacement des plaquettes de frein"
    Alors le véhicule "V-0412" est à nouveau louable
    Et le signalement pour "FREINS" sur le véhicule "V-0412" est clos avec le commentaire "Remplacement des plaquettes de frein"

  Scénario: N'importe quel technicien peut remettre en service le véhicule
    Étant donné le technicien "Alice" a pris en charge un signalement pour "BATTERIE" sur le véhicule "V-0200"
    Quand le technicien "Bob" remet en service le véhicule "V-0200" avec le commentaire "Batterie remplacée et testée"
    Alors le véhicule "V-0200" est à nouveau louable
    Et le signalement sur le véhicule "V-0200" est clos par "Bob" avec le commentaire "Batterie remplacée et testée"

  Scénario: La remise en service clôture tous les signalements ouverts du véhicule
    Étant donné un véhicule "V-0105" indisponible à la location
    Et un signalement enregistré pour "PNEU" sur le véhicule "V-0105"
    Et un signalement enregistré pour "RAYURE" sur le véhicule "V-0105"
    Quand le technicien remet en service le véhicule "V-0105" avec le commentaire "Pneu changé et retouche carrosserie effectuée"
    Alors le véhicule "V-0105" est à nouveau louable
    Et tous les signalements du véhicule "V-0105" sont clos avec le commentaire "Pneu changé et retouche carrosserie effectuée"
    Et il n'y a plus aucun signalement ouvert pour le véhicule "V-0105"

  Scénario: Un commentaire de réparation est obligatoire pour remettre un véhicule en service
    Étant donné un véhicule "V-0300" indisponible à la location
    Et un signalement enregistré pour "FREINS" sur le véhicule "V-0300"
    Quand le technicien tente de remettre en service le véhicule "V-0300" sans commentaire
    Alors la remise en service est refusée avec une erreur indiquant qu'un commentaire est obligatoire
    Et le véhicule "V-0300" reste indisponible à la location
    Et le signalement sur le véhicule "V-0300" reste ouvert