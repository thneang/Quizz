Installation
=============

Le programme nécessite Java 8.

1. Il faut aller dans le répertoire du programme avec un terminal.
2. Taper ant pour compiler, un jar nommé "Quizz.jar" est crée.
3. Lancer le jar avec la commande "java -jar Quizz.jar"

Fonctionnement
================

Losque le programme est lancée, un lien apparait. Il faut lancer le lien avec un navigateur ou en faisant alt+click.
La liste des fichiers txt est affichée, il faut choisir lequel devra être utilisé par le programme pour le quizz.
Il est bien sûr possible de faire plusieurs quizz en même temps.

Une fois le fichier choisi, un nouvel onglet se lance avec les informations pour le jeu à gauche et un formulaire pour saisir la réponse au centre.
Il suffit d'appuyer sur "Entrer" ou clicker sur submit pour envoyer le résultat.

Si on arrive à 0 point ou 20 point, le jeu redémarre automatiquement.

Technique
==========

Une interface AJAX/REST est utilisé.
Les échanges se font donc grâce au format Json.

Le programme charge un fichier en mémoire qu'une seule fois et seulement si il est utilisé par un client. Ceci permet d'éviter de lire un fichier à
chaque fois qu'on doit tiré un mot pour le quizz, ce qui est beaucoup plus rapide ! Une fois que tous les clients ont fermé leur onglet, le fichier(la liste de mot) est déchargé.

J'ai utilisé un executor pour gérer la concurrence et permettre au serveur de traiter autant de client possible. J'ai donnée 4 threads au programme.
La concurrence étant une partie très délicate, des synchronisations ont été faite pour éviter des problèmes d'accès aux données par plusieurs threads.
De ce fait, des ConcurrentsHashMap sont utilisées pour stocker les exercices.

Le programme surveille en temps réel le répertoire pour détecter la création/modification/suppression de fichier txt. La classe WatchService est utilisé pour cela.
Cette partie nécessite d'utiliser des eventbus pour faire communiquer client et serveur pour obtenir les mis à jours.

De cette manière même si le programme est lancé, il se met à jour automatiquement et on peut donc rajouter d'autre fichier de mot pendant qu'on joue.
Si un fichier utilisé est supprimé, les clients ferme automatiquement l'onglet de jeu car il est déchargé de la mémoire.
J'ai essayé de détecter ce qu'un utilisateur rajoute quand il modifie un fichier mais je n'ai pas réussi, à moins de tout comparer et c'est plus coûteux que de tout recharger.

Il existe des bugs lors de l'utilisation de WatchService car les notifications peuvent être éronnée, j'ai donc du corrigé le résultat donnée.

Le build.xml a été fait à la main et pas automatiquement par eclipse.

J'ai utilisé l'API vertx pour faire le serveur en java.
Pour le traducteur il s'agit de Microsoft Translator car Google Translate est payant.

Les packages ont été fait pour séparer les fonctionnalitées et respecter au mieux les principes de Java.
Le projet utilise d'ailleurs le code d'un autre projet que j'ai fais, son design m'a fais gagné du temps pour faire ce Quizz.

Amélioration possible
======================

Choisir la langue du jeu.
Mettre à jours les indices au lieu de donner que la première lettre.(Comme le jeu du pendu)
Tirer un autre mot quand le traducteur ne sait pas traduire le mot. Il redonne le même mot quand il ne sait pas traduire mais il existe des mots identiques dans plusieurs langues...

Temps
======

Le projet en lui même a pris 2-3heures à coder mais j'ai pris du temps à trouver un traducteur gratuit et assez efficace pour remplacer google translate.
Les tests ont durée 1 h.
