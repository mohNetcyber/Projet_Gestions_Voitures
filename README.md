# Gestion de Voitures

## Description

Ce projet est une application de bureau développée en JavaFX pour la gestion d'un parc de véhicules. Elle permet de gérer les voitures, les clients, les locations et les utilisateurs de l'application.

## Fonctionnalités

*   **Gestion des utilisateurs** : Ajout, modification, suppression et consultation des utilisateurs avec gestion des rôles (administrateur, utilisateur).
*   **Gestion des véhicules** : Ajout, modification, suppression et consultation des véhicules disponibles à la location.
*   **Gestion des clients** : Ajout, modification, suppression et consultation des clients.
*   **Gestion des locations** : Enregistrement des nouvelles locations, suivi des locations en cours et archivage des locations terminées.
*   **Facturation** : Génération automatique des factures en format PDF pour chaque location.
*   **Tableau de bord** : Visualisation rapide des informations clés (nombre de voitures, clients, locations).

## Technologies utilisées

*   **Langage** : Java 8
*   **Interface graphique** : JavaFX
*   **Base de données** : Microsoft Access (.accdb)
*   **Connexion à la base de données** : JDBC avec le pilote UCanAccess
*   **Génération PDF** : iText

## Prérequis

*   JDK 8 ou une version ultérieure
*   JavaFX SDK
*   Les librairies suivantes (à ajouter au build path) :
    *   `ucanaccess-x.x.x.jar`
    *   `itextpdf-x.x.x.jar`
    *   Les dépendances de UCanAccess (commons-lang, commons-logging, hsqldb, jackcess)

## Installation et Lancement

1.  **Cloner le projet** :
    ```bash
    git clone <url-du-repository>
    ```
2.  **Ouvrir dans un IDE** :
    *   Ouvrez le projet dans votre IDE Java préféré (Eclipse, IntelliJ IDEA, etc.).
    *   Assurez-vous que le projet est configuré comme un projet JavaFX.

3.  **Configurer le Build Path** :
    *   Ajoutez les librairies JavaFX et les autres dépendances (`.jar`) au build path de votre projet.

4.  **Base de données** :
    *   Le fichier de base de données `GestionsVehicules.accdb` est inclus dans le projet.
    *   Assurez-vous que le chemin vers la base de données dans le code (dans la classe de connexion) est correct.

5.  **Lancer l'application** :
    *   Exécutez la classe principale qui contient la méthode `main`.

## Structure du projet

```
Projet_Gestions_Voitures/
├── src/
│   ├── main/           # Code source de l'application
│   ├── dao/            # Data Access Objects pour l'interaction avec la BDD
│   ├── model/          # Classes du modèle (Utilisateur, Voiture, etc.)
│   ├── utils/          # Classes utilitaires (connexion BDD, etc.)
│   └── css/            # Feuilles de style pour l'interface
├── lib/                # Librairies et dépendances (.jar)
├── GestionsVehicules.accdb # Fichier de la base de données
└── README.md           # Ce fichier
```
