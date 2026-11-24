# language: fr
Fonctionnalité: S12 – Consulter l'historique des incidents d'un véhicule
  En tant que technicien,
  je veux voir tous les incidents signalés sur un véhicule, du plus récent au plus ancien,
  afin de repérer les véhicules qui tombent souvent en panne.

  Scénario: L'historique contient tous les incidents du véhicule, du plus récent au plus ancien
    Étant donné un incident "PNEU" signalé sur le véhicule "V-0412" le "2026-11-20T10:00:00Z"
    Et un incident "FREINS" signalé sur le véhicule "V-0412" le "2026-11-23T09:00:00Z"
    Et un incident "PROPRETE" signalé sur le véhicule "V-0412" le "2026-11-21T15:00:00Z"
    Quand le technicien consulte l'historique du véhicule "V-0412"
    Alors l'historique affiche les catégories "FREINS, PROPRETE, PNEU"

  Scénario: L'historique ne montre que les incidents du véhicule demandé
    Étant donné un incident "FREINS" signalé sur le véhicule "V-0412" le "2026-11-23T09:00:00Z"
    Et un incident "RAYURE" signalé sur le véhicule "T-1187" le "2026-11-22T08:00:00Z"
    Quand le technicien consulte l'historique du véhicule "T-1187"
    Alors l'historique affiche les catégories "RAYURE"

  Scénario: Un incident pris en charge reste dans l'historique
    Étant donné un incident "BATTERIE" signalé sur le véhicule "V-0412" le "2026-11-23T09:00:00Z"
    Et le technicien "Karim" a pris en charge cet incident
    Quand le technicien consulte l'historique du véhicule "V-0412"
    Alors l'historique affiche les catégories "BATTERIE"
    Et l'incident "BATTERIE" de l'historique est pris par "Karim"

  Scénario: Un véhicule sans incident a un historique vide
    Étant donné un incident "FREINS" signalé sur le véhicule "V-0412" le "2026-11-23T09:00:00Z"
    Quand le technicien consulte l'historique du véhicule "V-0999"
    Alors l'historique est vide
