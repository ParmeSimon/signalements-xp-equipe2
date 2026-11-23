# language: fr
Fonctionnalité: S1 – Signaler un incident depuis l'application
  En tant qu'usager,
  je veux signaler un incident sur le véhicule que j'utilise (véhicule, catégorie, commentaire),
  pour que l'atelier le prenne en charge.

  Plan du scénario: La gravité du signalement dépend de sa catégorie
    Quand un usager signale "<catégorie>" sur le véhicule "<véhicule>" avec le commentaire "<commentaire>"
    Alors le signalement est enregistré
    Et sa gravité est "<gravité>"

    Exemples:
      | catégorie | véhicule | commentaire                     | gravité    |
      | FREINS    | T-1187   | Le frein arrière ne répond plus | BLOQUANT   |
      | PNEU      | V-0412   | Le pneu avant est crevé         | GENANT     |
      | PROPRETE  | V-0412   | Selle très sale                 | COSMETIQUE |

  Scénario: Le commentaire est facultatif
    Quand un usager signale "RAYURE" sur le véhicule "V-0412" sans commentaire
    Alors le signalement est enregistré

  Scénario: Un commentaire de plus de 500 caractères est refusé
    Quand un usager signale "AUTRE" sur le véhicule "V-0412" avec un commentaire de 501 caractères
    Alors le signalement est refusé avec le message "Le commentaire dépasse 500 caractères"
    Et aucun signalement n'est enregistré

  Scénario: Un numéro de véhicule mal formé est refusé
    Quand un usager signale "FREINS" sur le véhicule "X-12" avec le commentaire ""
    Alors le signalement est refusé avec le message "Numéro de véhicule invalide : X-12"
    Et aucun signalement n'est enregistré
