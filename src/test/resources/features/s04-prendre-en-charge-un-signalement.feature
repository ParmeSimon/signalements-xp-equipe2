# language: fr
Fonctionnalité: S4 – Prendre en charge un signalement
  En tant que technicien de l'atelier,
  je veux prendre en charge un signalement,
  pour que les autres techniciens sachent que je m'en occupe.

  Contexte:
    Étant donné un signalement "FREINS" sur le véhicule "T-1187"

  Scénario: Le technicien qui prend un signalement voit son nom apparaître dessus
    Quand le technicien "Karim" prend le signalement
    Alors le signalement est pris par "Karim"

  Scénario: Un signalement pris ne peut pas être pris par un autre technicien
    Étant donné le technicien "Karim" a pris le signalement
    Quand le technicien "Julie" prend le signalement
    Alors la prise est refusée avec le message "Signalement déjà pris par Karim"
    Et le signalement est pris par "Karim"

  Scénario: Le technicien relâche un signalement qu'il ne peut pas traiter
    Étant donné le technicien "Karim" a pris le signalement
    Quand le technicien "Karim" relâche le signalement
    Alors le signalement est libre