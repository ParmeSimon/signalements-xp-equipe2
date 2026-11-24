# language: fr
Fonctionnalité: S2 – Retirer du pool un véhicule ayant un incident bloquant
  En tant qu'utilisateur de l'application,
  je veux pouvoir signaler un véhicule pour un problème bloquant, qu'il soit retiré du pool et être alerté s'il est en cours d'utilisation,
  afin de réduire au maximum les risques pour les utilisateurs.

  Contexte:
    Soit un véhicule "T-1187" disponible dans le pool
    Et un véhicule "V-0412" actuellement utilisé par l'usager "U-89"

  Scénario: Retrait immédiat du pool lors d'un signalement bloquant sur un véhicule disponible
    Quand un utilisateur signale "FREINS" sur le véhicule "T-1187" avec le commentaire "Le frein arrière ne répond plus"
    Alors le signalement est enregistré avec la gravité "BLOQUANT"
    Et le véhicule "T-1187" est immédiatement retiré du pool
    Et aucune notification d'arrêt n'est envoyée

  Scénario: Alerte et notification d'arrêt lors d'un signalement bloquant sur un véhicule en cours d'utilisation
    Quand un utilisateur signale "FREINS" sur le véhicule "V-0412" avec le commentaire "Direction bloquée"
    Alors le signalement est enregistré avec la gravité "BLOQUANT"
    Et le véhicule "V-0412" est retiré du pool
    Et une alerte informe le déclarant que le véhicule "V-0412" est en cours d'utilisation
    Et une notification critique est envoyée à l'usager "U-89" lui demandant de s'arrêter immédiatement

  Plan du scénario: Seuls les incidents bloquants entraînent le retrait du pool et une notification d'arrêt
    Quand un utilisateur signale "<catégorie>" sur le véhicule "V-0412" avec le commentaire "<commentaire>"
    Alors le signalement est enregistré avec la gravité "<gravité>"
    Et le véhicule "V-0412" n'est pas retiré du pool pour incident bloquant
    Et aucune notification d'arrêt n'est envoyée à l'usager "U-89"

    Exemples:
      | catégorie | commentaire             | gravité    |
      | PNEU      | Le pneu avant est crevé | GENANT     |
      | PROPRETE  | Selle très sale         | COSMETIQUE |