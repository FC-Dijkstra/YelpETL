# YelpETL

TODO:

- ETL en scala avec connecteurs Postgres, JSON et CSV
- Structure du datawarehouse
- Transformation vers la structure du data warehouse
- Appels SQL Front
- Dashboards de rendu


 les différents axes : 
 
  -Axe géographique avec : nombre de commerçant par localisation, meilleurs commerçants par localisation, nombre de commerçant possédent un parking, le nombre de catégorie de commerçant par localisation, nombre de personne aillant été chez un commerçant par localisation
  -Axe analytique avec 

Tables de faits :

- Table de faits "avis" : contient des informations sur les avis, comme la valeur de l'avis, le commentaire textuel et les réactions des autres utilisateurs.
 - Table de faits "tips" : contient des informations sur les tips, comme le texte du tip et la date de création.
 - Table de faits "checkins" : contient des informations sur les checkins, comme la date et l'heure du checkin et l'identifiant du commerce.

Tables de dimensions :

- Table de dimensions "commerces" : contient des informations sur les commerces, comme leur localisation, leurs catégories et leurs attributs complémentaires.
- Table de dimensions "utilisateurs" : contient des informations sur les utilisateurs, comme leur prénom, leur identifiant, leurs amis et la date de création de leur compte.
- Table de dimensions "années élites" : contient des informations sur les années où un utilisateur a été "élite", ainsi que des statistiques sur les comptes.

Chaque table de faits sera liée à une ou plusieurs tables de dimensions, en utilisant les clés étrangères appropriées. Par exemple, la table de faits "avis" sera liée à la table de dimensions "commerces" et à la table de dimensions "utilisateurs", en utilisant les identifiants de commerce et d'utilisateur comme clés étrangères.
