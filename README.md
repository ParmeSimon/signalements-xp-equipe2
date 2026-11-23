# signalements-xp – le service Signalements de GreenMove

Code de départ du TP1 du module eXtreme Programming (EADL, EKOD). S1 est faite de bout en bout : c'est l'exemple à suivre pour les stories suivantes.

## Lancer

Prérequis : JDK 17 ou plus récent, Git. Maven n'est pas nécessaire, le wrapper `mvnw` le télécharge.

```bash
./mvnw verify            # tests unitaires et scénarios Cucumber (mvnw.cmd verify sous Windows)
./mvnw spring-boot:run   # l'API sur http://localhost:8080
```

Essayer l'API :

```bash
curl -i -X POST localhost:8080/signalements -H 'Content-Type: application/json' \
     -d '{"vehicule": "V-0412", "categorie": "FREINS", "commentaire": "Le frein avant ne répond plus"}'
curl localhost:8080/signalements/<id>   # l'adresse renvoyée dans l'en-tête Location
curl localhost:8080/signalements
```

## Mettre le dépôt en place

```bash
git init -b main
git add .
git update-index --chmod=+x mvnw   # indispensable depuis Windows, sinon ./mvnw n'est plus exécutable après un clone
git commit -m "Code de départ : S1"
git remote add origin https://github.com/<compte>/signalements-xp-equipe1.git
git push -u origin main
```

## Ce qu'il y a dedans

```
src/main/java/fr/greenmove/signalements/
├── domaine/          Signalement, Categorie (et sa gravité), Gravite, port Signalements
├── application/      SignalerIncident : S1
└── infrastructure/   API REST, stockage en mémoire, configuration Spring
src/test/java/…       tests unitaires (domaine, application), test de l'API, étapes Cucumber
src/test/resources/features/s01-signaler-un-incident.feature
.github/workflows/ci.yml   le pipeline : ./mvnw -B verify à chaque push sur main
```

Le domaine ne dépend pas de Spring. La gravité d'une catégorie est écrite à un seul endroit : `Categorie`.

## Les règles du TP

- Une story livrée = un fichier `.feature` qui passe, écrit avec la cliente, et des tests unitaires écrits avant le code.
- Une seule branche, `main`. On intègre plusieurs fois par heure, le build ne reste jamais rouge.
- Les paires signent leurs commits :

```bash
git commit -m "S2 : retirer un véhicule signalé bloquant" \
           -m "Co-authored-by: Prénom Nom <prenom.nom@exemple.fr>"
```

- Jira reflète le travail réel : une story passe à « Terminé » quand la cliente l'accepte en démo.
