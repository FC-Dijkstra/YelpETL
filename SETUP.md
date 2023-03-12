# Setup windows

- Installer Spark 3.3.1 (avec Hadoop)
- Installer Hadoop 3.3.4
- Télécharger https://github.com/steveloughran/winutils/tree/master/hadoop-3.0.0/bin
- Décompresser et placer dans le dossier de Hadoop
- Définir une variable d'env HADOOP_HOME qui pointe vers hadoop
- Dans le dossier Spark/conf, dupliquer log4j2.properties.template.
- Renommer le fichier log4j2.properties
- Ouvrir le fichier et ajouter les lignes:
```
log4j.logger.org.apache.spark.util.ShutdownHookManager=OFF
log4j.logger.org.apache.spark.SparkEnv=ERROR
```
- Changer la ligne
```rootLogger.level = error```

# Setup Linux

- JAVA 11 UNIQUEMENT (`openjdk-11-jdk`)
- SCALA 2.13.10 UNIQUEMENT (`cs install scala:2.13.10 && cs install scalac:2.13.10`)
- utiliser SBT pour compilation / package / run
- le shell SBT est disponible dans intelliJ
- utiliser `~run` pour avoir la recompilation automatique.
- gérer les dépendances via le fichier build.sbt
- SPARK 3.3.1 UNIQUEMENT
- ne pas oublier de lancer `start-master.sh` pour démarer le master server spark.