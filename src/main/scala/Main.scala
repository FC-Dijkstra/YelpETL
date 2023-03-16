import org.apache.log4j.{Level, Logger}
import org.apache.spark
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.catalyst.dsl.expressions.StringToAttributeConversionHelper
import org.apache.spark.sql.functions.{col, monotonically_increasing_id}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}

import java.util.Properties

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      //.master("local")
      .master("spark://etl-server:7077")
      .appName("YelpETL")
      //.config("spark.config.option", "some-value")
      .getOrCreate();

    import spark.implicits._

    spark.sparkContext.setLogLevel("ERROR")
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.zookeeper").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.hadoop").setLevel(Level.ERROR)
    Logger.getLogger("org.spark-project").setLevel(Level.ERROR)

    val dbUrl = "jdbc:postgresql://etl-server:5432/yelp_data"
    val connection = new Properties()
    connection.setProperty("driver", "org.postgresql.Driver")
    connection.setProperty("user", "postgres")
    connection.setProperty("password", "postgres")

    val path_business = "/etl_data/yelp_academic_dataset_business.json";

    var businessDF = spark.read.json(path_business).cache();
    //businessDF = businessDF.limit(15);

    // ! Output schemas
    val attributesSchema = StructType(Array(
      StructField("id", IntegerType, false),
      StructField("AcceptsInsurance", StringType, false),
      StructField("AgesAllowed", StringType, false),
      StructField("Alcohol", StringType, false),
      StructField("Ambience", StringType, false),
      StructField("BYOB", StringType, false),
      StructField("BYOBCorkage", StringType, false),
      StructField("BestNights", StringType, false),
      StructField("BikeParking", StringType, false),
      StructField("BusinessAcceptsBitcoin", StringType, false),
      StructField("BusinessAcceptsCreditCards", StringType, false),
      StructField("BusinessParking", StringType, false),
      StructField("ByAppointmentOnly", StringType, false),
      StructField("Caters", StringType, false),
      StructField("CoatCheck", StringType, false),
      StructField("Corkage", StringType, false),
      StructField("DietaryRestrictions", StringType, false),
      StructField("DogsAllowed", StringType, false),
      StructField("DriveThru", StringType, false),
      StructField("GoodForDancing", StringType, false),
      StructField("GoodForKids", StringType, false),
      StructField("GoodForMeal", StringType, false),
      StructField("HairSpecializesIn", StringType, false),
      StructField("HappyHour", StringType, false),
      StructField("HasTV", StringType, false),
      StructField("Music", StringType, false),
      StructField("NoiseLevel", StringType, false),
      StructField("Open24Hours", StringType, false),
      StructField("OutdoorSeating", StringType, false),
      StructField("RestaurantsAttire", StringType, false),
      StructField("RestaurantsCounterService", StringType, false),
      StructField("RestaurantsDelivery", StringType, false),
      StructField("RestaurantsGoodForGroups", StringType, false),
      StructField("RestaurantsPriceRange2", StringType, false),
      StructField("RestaurantsReservations", StringType, false),
      StructField("RestaurantsTableService", StringType, false),
      StructField("RestaurantsTakeOut", StringType, false),
      StructField("Smoking", StringType, false),
      StructField("WheelchairAccessible", StringType, false),
      StructField("WiFi", StringType, false),
    ));

    // ############################## Localisation
    val localisationSchema = StructType(Array(
      StructField("adresse", StringType, false),
      StructField("codePostal", StringType, false),
      StructField("etat", StringType, false),
      StructField("ville", StringType, false),
      StructField("id", IntegerType, false),
    ));

    var localisationDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], localisationSchema);

    //ajout de l'id de localisation pour jointure
    businessDF = businessDF.withColumn("idLocalisation", monotonically_increasing_id());
    //extraction localisation
    localisationDF = localisationDF.union(
      businessDF.select(
        col("address").as("adresse"),
        col("postal_code").as("codePostal"),
        col("state").as("etat"),
        col("city").as("ville"),
        col("idLocalisation").as("id")
      )
    );

    //print_metadata(localisationDF)

    //localisationDF.write.mode("append").jdbc(dbUrl, "dw.localisation", connection);

    //########################## Attributs des commerces
    var attributesDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], attributesSchema)
    businessDF = businessDF.withColumn("idAttributs", monotonically_increasing_id());
    attributesDF = attributesDF.union(
      businessDF.select(
        col("idAttributs"),
        col("attributes.*")
      )
    );

    print_metadata(attributesDF);

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

    val commercesSchema = StructType(Array(
      StructField("internal_id", StringType, false),
      StructField("idAttributs", IntegerType, false),
      StructField("idLocalisation", IntegerType, false),
      StructField("categories", StringType, true),
      StructField("stars", DoubleType, false),
      StructField("id", IntegerType, false),
    ))
    var commercesDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], commercesSchema)
    commercesDF = commercesDF.union(
      businessDF.select(
        col("business_id").as("internal_id"),
        col("idAttributs"),
        col("idLocalisation"),
        col("categories"),
        col("stars"),
      ).withColumn("id", monotonically_increasing_id())
    )
    print_metadata(commercesDF)




  }

  def print_metadata (dataframe : DataFrame): Unit = {
    dataframe.show();
    dataframe.printSchema();
    print(dataframe.count);
  }
}
