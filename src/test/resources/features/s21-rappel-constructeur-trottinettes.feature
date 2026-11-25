# language: fr
Fonctionnalité: S21 – Retirer d'un coup de la location les trottinettes rappelées par le constructeur
  En tant que responsable de l'atelier,
  je veux retirer d'un coup de la location toutes les trottinettes T-11xx, chacune avec un signalement bloquant « rappel constructeur »,
  pour qu'aucune trottinette rappelée ne reste louable.

  Contexte:
    Étant donné un véhicule "T-1100" louable
    Et un véhicule "T-1187" louable
    Et un véhicule "T-1199" louable
    Et un véhicule "T-1200" louable
    Et un véhicule "V-1150" louable

  Scénario: Toutes les trottinettes T-11xx sont retirées de la location d'un coup
    Quand l'atelier lance le rappel constructeur de la série "T-11"
    Alors le véhicule "T-1100" n'est plus louable
    Et le véhicule "T-1187" n'est plus louable
    Et le véhicule "T-1199" n'est plus louable

  Scénario: Chaque trottinette retirée reçoit un signalement bloquant « rappel constructeur »
    Quand l'atelier lance le rappel constructeur de la série "T-11"
    Alors le véhicule "T-1100" a un signalement bloquant "RAPPEL_CONSTRUCTEUR" avec le commentaire "Rappel constructeur"
    Et le véhicule "T-1187" a un signalement bloquant "RAPPEL_CONSTRUCTEUR" avec le commentaire "Rappel constructeur"
    Et le véhicule "T-1199" a un signalement bloquant "RAPPEL_CONSTRUCTEUR" avec le commentaire "Rappel constructeur"

  Scénario: Les autres véhicules restent louables
    Quand l'atelier lance le rappel constructeur de la série "T-11"
    Alors le véhicule "T-1200" reste louable
    Et le véhicule "V-1150" reste louable

  Scénario: L'usager qui roule sur une trottinette rappelée doit s'arrêter
    Étant donné un véhicule "T-1150" loué par l'usager "U-89"
    Quand l'atelier lance le rappel constructeur de la série "T-11"
    Alors le véhicule "T-1150" n'est plus louable
    Et l'usager "U-89" doit s'arrêter immédiatement