# language: fr
Fonctionnalité: S3 – Voir les signalements ouverts dans l'application
  En tant que technicien,
  je veux pouvoir voir les signalements ouverts, les bloquants d’abord puis les gênants puis les cosmétiques. Dans chaque gravité, le plus ancien en premier.
  pour que les techniciens repèrent au mieux les véhicules endommagés.

  Scénario: Le technicien voit un signalement ouvert sur un véhicule
    Étant donné un signalement ouvert pour "FREINS" sur le véhicule "V-0412"
    Quand le technicien consulte les signalements ouverts
    Alors le technicien voit 1 signalement pour le véhicule "V-0412"

  Scénario: Un signalement possède un véhicule, la catégorie et sa date de création
    Étant donné un signalement ouvert pour "BATTERIE" sur le véhicule "V-0200" créé à "2026-11-23T12:00:00Z"
    Quand le technicien consulte les signalements ouverts
    Alors le signalement affiché concerne le véhicule "V-0200", la catégorie "BATTERIE" créé à "2026-11-23T12:00:00Z"

  Scénario: Les signalements sont triés par gravité puis par ancienneté
    Étant donné un signalement ouvert pour "RAYURE" sur le véhicule "V-0001" créé à "2026-11-23T10:00:00Z"
    Et un signalement ouvert pour "FREINS" sur le véhicule "V-0002" créé à "2026-11-23T12:00:00Z"
    Et un signalement ouvert pour "PNEU" sur le véhicule "V-0003" créé à "2026-11-23T11:00:00Z"
    Et un signalement ouvert pour "FREINS" sur le véhicule "V-0004" créé à "2026-11-23T09:00:00Z"
    Quand le technicien consulte les signalements ouverts
    Alors l'ordre des véhicules affichés est "V-0004, V-0002, V-0003, V-0001"