import org.apache.log4j.{Level, Logger}
import org.apache.spark
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.catalyst.dsl.expressions.StringToAttributeConversionHelper
import org.apache.spark.sql.functions.{col, dayofmonth, explode, monotonically_increasing_id, month, split, to_date, weekofyear, year}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}
import org.postgresql.Driver

import java.util.Properties

object Main {
  def main(args: Array[String]): Unit = {
    val SAVE_COMMERCES = false
    val SAVE_LOCALISATIONS = false
    val SAVE_CHECKINS = true
    val SAVE_TEMPO = true
    val SAVE_USERS = false
    val SAVE_AVIS = true

    val spark = SparkSession
      .builder()
      .master("local")
      //.master("spark://etl-server:7077")
      .appName("YelpETL")
      //config pour utiliser la mémoire maximale
      .config("spark.sql.legacy.timeParserPolicy", "LEGACY")
      .config("spark.memory.offHeap.enabled", "true")
      .config("spark.memory.offHeap.size", "8g")
      .config("spark.driver.memory", "32g")
      .getOrCreate();

    import spark.implicits._

    //suppression des logs
    spark.sparkContext.setLogLevel("ERROR")
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.zookeeper").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.hadoop").setLevel(Level.ERROR)
    Logger.getLogger("org.spark-project").setLevel(Level.ERROR)

    //configuration de la connection au serveur postgresql
    val options = Map(
      "url" -> "jdbc:postgresql://localhost:5432/yelpdata",
      "driver" -> "org.postgresql.Driver",
      "user" -> "postgres",
      "password" -> "postgres"
    )

    //chemin d'accès aux fichiers
    val path_business = "/etl_data/yelp_academic_dataset_business.json";
    val path_checkin = "/etl_data/yelp_academic_dataset_checkin.json";

    //######################## LOAD
    var businessDF = spark.read.json(path_business).cache();
    var checkinsDF = spark.read.json(path_checkin).cache()
    //chargement des données d'origine, besoin de 32g de ram sinon OutOfMemoryException
    var usersDF = spark.read.format("jdbc").options(options).option("dbtable", "yelp.user").load().cache()
    var friendsDF = spark.read.format("jdbc").options(options).option("dbtable", "yelp.friend").load().cache()
    var eliteDF = spark.read.format("jdbc").options(options).option("dbtable", "yelp.elite").load().cache()
    var reviewsDF = spark.read.format("jdbc").options(options).option("dbtable", "yelp.review").load().cache()
    println("LOAD OK")

    //########################## Localisation
    //ajout de l'id de localisation pour jointure
    businessDF = businessDF.withColumn("idLocalisation", monotonically_increasing_id());
    //extraction localisation
    val localisationDF = businessDF.select(
      col("address").as("adresse"),
      col("postal_code").as("codePostal"),
      col("state").as("etat"),
      col("city").as("ville"),
      col("idLocalisation").as("id")
    );
    println("Localisation OK")

    //contrôle qualité
    //print_metadata(localisationDF)

    //########################## Attributs des commerces
    /*
    var attributesDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], attributesSchema)
    businessDF = businessDF.withColumn("idAttributs", monotonically_increasing_id());
    attributesDF = attributesDF.union(
      businessDF.select(
        col("idAttributs"),
        col("attributes.*")
      )
    );

    print_metadata(attributesDF);
    */
    /*
    for (columnName <- attributesDF.columns){
      println("---" + columnName + "---")
      if (columnName != "idAttributs" && columnName != "id"){
        val valeursColonne = attributesDF
          .select(columnName)
          .distinct()
          .map(f => f.getString(0))
          .collect()
          .toList
        println(valeursColonne)
        println()
      }
    }
     */

    //########################### Commerces
    //ajout clé primaire INT
    businessDF = businessDF.withColumn("id", monotonically_increasing_id())
    //séléction des colonnes
    val commercesDF = businessDF.select(
        //col("business_id").as("internal_id"),
        //col("idAttributs"),
        col("idLocalisation"),
        col("categories"),
        col("stars"),
        col("name"),
        col("id")
      )
    println("Commerces OK")

    //####################### Utilisateurs
    //renommage de la colonne pour éviter les conflits dans les jointures
    eliteDF = eliteDF.withColumnRenamed("user_id", "elite_user_id")
    friendsDF = friendsDF.withColumnRenamed("user_id", "friends_user_id")

    //traitement des données, on cherche a avoir nbAmis et nbAnneesElite donc GROUP BY + COUNT et renommage pour éviter les conflits
    friendsDF = friendsDF.groupBy(col("friends_user_id")).count().withColumnRenamed("count", "nbAmis")
    eliteDF = eliteDF.groupBy(col("elite_user_id")).count().withColumnRenamed("count", "nbAnneesElite")
    println("utilisateurs 1")
    //jointure des tables, en left outer pour conserver tous les utilisateurs
    usersDF = usersDF.join(friendsDF, friendsDF("friends_user_id") === usersDF("user_id"), "leftouter")
    usersDF = usersDF.join(eliteDF, eliteDF("elite_user_id") === usersDF("user_id"), "leftouter")
    println("utilisateurs 2")

    //la jointure génère des null, on les remplace par 0
    usersDF = usersDF.na.fill(0, Array("nbAmis", "nbAnneesElite"))
    println("utilisateurs 3")

    //fusion de tous les compliments en une seule colonne, pareil pour les réactions
    val nbCompliments = usersDF("compliment_cool") + usersDF("compliment_cute") + usersDF("compliment_funny") + usersDF("compliment_hot") + usersDF("compliment_list") + usersDF("compliment_more") + usersDF("compliment_note") + usersDF("compliment_photos") + usersDF("compliment_plain") + usersDF("compliment_profile") + usersDF("compliment_writer");
    val nbReactions = usersDF("cool") + usersDF("funny") + usersDF("useful")
    println("utilisateurs 4")

    //ajout des colonnes dans la dataframe principale
    usersDF = usersDF.withColumn("nbCompliments", nbCompliments).withColumn("nbReactions", nbReactions).withColumn("id", monotonically_increasing_id())
    println("utilisateurs 5")

    //renommage des colonnes pour coller au DW
    usersDF = usersDF
      .withColumnRenamed("average_stars", "etoilesMoyennes")
      .withColumnRenamed("yelping_since", "dateCreation")
      .withColumnRenamed("fans", "nbFollowers")
      .withColumnRenamed("review_count", "nbAvis")
      .withColumnRenamed("name", "prenom")
    println("utilisateurs 6")

    //####################### Checkins
    //jointure avec les business
    checkinsDF = checkinsDF.join(businessDF, businessDF("business_id") === checkinsDF("business_id"), "leftouter");
    println("checkins 1")
    //récupération de l'ID des business (integer, pas string)
    checkinsDF = checkinsDF.select(col("id").as("idCommerce"), col("date"))
    println("checkins 2")
    //explosion des dates (1 checkin = N dates) => (1 checkin = 1 date)
    checkinsDF = checkinsDF.withColumn("date", explode(split(checkinsDF("date"), ",")))
    println("checkins 3")
    //suppression des heures
    checkinsDF = checkinsDF.withColumn("date", to_date(col("date"), "yyyy-MM-dd"))
    println("checkins 4")
    //comptage du nombre de checkins par commerces et par jour
    checkinsDF = checkinsDF.groupBy("idCommerce", "date").count()
    println("checkins 5")
    //ajout id et renommage de colonne
    checkinsDF = checkinsDF.withColumn("id", monotonically_increasing_id()).withColumnRenamed("count", "nbVisites")
    println("checkins 6")


    //################################# Avis
    //suppression des heures dans les dates
    reviewsDF = reviewsDF.withColumn("date", to_date(col("date"), "yyyy-MM-dd"))
    println("avis 1")
    //suppression du texte
    reviewsDF = reviewsDF.drop("text")
    println("avis 2")
    //jointure avec les commerces pour réccupérer leur id
    reviewsDF = reviewsDF.join(businessDF.withColumnRenamed("id", "idCommerce"), "business_id")
    println("avis 3")
    //jointure avec les utilisateurs pour récupérer leur id
    reviewsDF = reviewsDF.join(usersDF.withColumnRenamed("id", "idUtilisateur"), "user_id")
    println("avis 4")
    //ajout d'un id
    reviewsDF = reviewsDF.withColumn("id", monotonically_increasing_id())
    println("avis 5")

    //###################### Dates
    //récupération de toutes les dates uniques
    var datesDF = checkinsDF.select("date").distinct()
    val datesReviews = reviewsDF.select("date").distinct()
    println("dates 1")
    //on doit rajouter dans une même table les dates des reviews et des checkins
    datesDF = datesDF.union(datesReviews).withColumn("id", monotonically_increasing_id())
    println("dates 2")
    //changement de nom pour faciliter la suppression après
    val datesJointure = datesDF.withColumnRenamed("id", "idDate").withColumnRenamed("date", "ddate")
    println("dates 3")
    //jointure entre les dates et les checkins pour récupérer les id des dates
    checkinsDF = checkinsDF.join(datesJointure, datesJointure("ddate") === checkinsDF("date"), "inner")
    println("dates 4")
    //jointure entre les dates et les reviews pour récupérer les id des dates
    reviewsDF = reviewsDF.join(datesJointure, datesJointure("ddate") === reviewsDF("date"), "inner")
    println("dates 5")

    //découpage des dates pour faciliter le traitement
    datesDF = datesDF
      .withColumn("annee", year(col("date")))
      .withColumn("mois", month(col("date")))
      .withColumn("jour", dayofmonth(col("date")))
      .withColumn("semaine", weekofyear(col("date")))
    println("dates 6")

    //#################### DROP
    usersDF = usersDF.drop("compliment_cool", "compliment_cute", "compliment_funny", "compliment_hot", "compliment_list", "compliment_more", "compliment_note", "compliment_photos", "compliment_plain", "compliment_profile", "compliment_writer", "cool", "funny", "useful", "elite_user_id", "friends_user_id", "user_id")
    //suppression de la colonne inutile
    datesDF = datesDF.drop("date")
    //suppression des colonnes inutiles
    checkinsDF = checkinsDF.drop("ddate", "date")
    reviewsDF = reviewsDF.drop("ddate", "date", "user_id", "business_id")
    println("DROP OK")

    // ################### SAVE
    //sauvegarde
    if (SAVE_LOCALISATIONS == true) {
      localisationDF.write
        .format("jdbc")
        .options(options)
        .option("dbtable", "dw.localisation")
        .mode("append")
        .save()
      println("LOCALISATIONS SAVED")
    }

    if (SAVE_COMMERCES == true){
      commercesDF.write.format("jdbc")
        .options(options)
        .option("dbtable", "dw.commerces")
        .mode("append")
        .save()
      println("COMMERCES SAVED")
    }

    //sauvegarde
    if (SAVE_TEMPO == true){
      datesDF.write.format("jdbc")
        .options(options)
        .option("dbtable", "dw.temporalite")
        .mode("append")
        .save()
      println("DATES SAVED")
    }

    if (SAVE_CHECKINS == true) {
      checkinsDF.write.format("jdbc")
        .options(options)
        .option("dbtable", "dw.checkins")
        .mode("append")
        .save()
      println("CHECKINS SAVED")
    }

    if (SAVE_USERS == true) {
      usersDF.write
        .format("jdbc")
        .options(options)
        .option("dbtable", "dw.utilisateurs")
        .mode("append")
        .save()
      println("USERS SAVED")
    }

    if (SAVE_AVIS == true) {
      reviewsDF.write
        .format("jdbc")
        .options(options)
        .option("dbtable", "dw.avis")
        .mode("append")
        .save()
      println("AVIS SAVED")
    }
  }

  def print_metadata (dataframe : DataFrame): Unit = {
    dataframe.show();
    dataframe.printSchema();
    print(dataframe.count);
  }
}
