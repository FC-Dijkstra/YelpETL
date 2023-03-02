# Setup 

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

UPDATE:
Tout se fait sur un serveur dédié.
Il faut juste récupérer les jars Hadoop pour pouvoir compiler.