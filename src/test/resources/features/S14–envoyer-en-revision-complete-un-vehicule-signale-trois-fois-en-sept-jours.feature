# language: fr
Fonctionnalité: S14 – Envoyer en révision complète un véhicule signalé trois fois en sept jours
  En tant que responsable d'atelier,
  je veux que lorsqu'il y a trois signalements sur le même véhicule sur 7 jours glissants quelle que soit leur gravité, le véhicule soit retiré du pool et envoyé en révision complète,
  afin de réparer les véhicules défectueux.

  Scénario: Trois signalements en moins de 7 jours déclenchent le retrait et l'envoi en révision complète
    Étant donné un véhicule "V-0412" en service dans le pool
    Et un signalement antérieur "PROPRETE" sur le véhicule "V-0412" le "2026-11-20T10:00:00Z"
    Et un signalement antérieur "RAYURE" sur le véhicule "V-0412" le "2026-11-22T14:00:00Z"
    Quand un usager signale "PNEU" sur le véhicule "V-0412" le "2026-11-25T09:00:00Z"
    Alors le véhicule "V-0412" est retiré du pool et pars en révision complète

  Scénario: Deux signalements sur 7 jours glissants ne déclenchent pas la révision complète
    Étant donné un véhicule "V-0412" en service dans le pool
    Et un signalement antérieur "PROPRETE" sur le véhicule "V-0412" le "2026-11-20T10:00:00Z"
    Quand un usager signale "RAYURE" sur le véhicule "V-0412" le "2026-11-25T09:00:00Z"
    Alors le véhicule "V-0412" reste disponible dans le pool
    Et le véhicule "V-0412" n'est pas envoyé en révision complète

  Scénario: Un signalement datant de plus de 7 jours n'est pas comptabilisé dans la fenêtre glissante
    Étant donné un véhicule "V-0412" en service dans le pool
    Et un signalement antérieur "PROPRETE" sur le véhicule "V-0412" le "2026-11-17T08:59:59Z"
    Et un signalement antérieur "RAYURE" sur le véhicule "V-0412" le "2026-11-21T10:00:00Z"
    Quand un usager signale "PNEU" sur le véhicule "V-0412" le "2026-11-25T09:00:00Z"
    Alors le véhicule "V-0412" reste disponible dans le pool
    Et le véhicule "V-0412" n'est pas envoyé en révision complète

  Scénario: Le seuil de trois signalements est calculé indépendamment par véhicule
    Étant donné un véhicule "V-0412" en service dans le pool
    Et un véhicule "T-1187" en service dans le pool
    Et un signalement antérieur "PROPRETE" sur le véhicule "V-0412" le "2026-11-22T10:00:00Z"
    Et un signalement antérieur "RAYURE" sur le véhicule "T-1187" le "2026-11-23T10:00:00Z"
    Quand un usager signale "PNEU" sur le véhicule "V-0412" le "2026-11-25T09:00:00Z"
    Alors le véhicule "V-0412" reste disponible dans le pool
    Et le véhicule "T-1187" reste disponible dans le pool

  Scénario: Il y a trois signalements dont un qui a déjà été réparé et le véhicule part en révision complète
    Étant donné un véhicule "V-0412" en service dans le pool
    Et un signalement antérieur "PNEU" sur le véhicule "V-0412" le "2026-11-20T10:00:00Z"
    Et l'incident "PNEU" sur le véhicule "V-0412" est réparé par le technicien "Karim" le "2026-11-21T10:00:00Z"
    Et un signalement antérieur "PROPRETE" sur le véhicule "V-0412" le "2026-11-22T10:00:00Z"
    Quand un usager signale "RAYURE" sur le véhicule "V-0412" le "2026-11-24T09:00:00Z"
    Alors le véhicule "V-0412" est retiré du pool et pars en révision complète