import org.apache.log4j.{Level, Logger}
import org.apache.spark
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.catalyst.dsl.expressions.StringToAttributeConversionHelper
import org.apache.spark.sql.functions.{monotonicallyIncreasingId, monotonically_increasing_id}
import org.apache.spark.sql.types.{BooleanType, IntegerType, StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}


object FieldMappings {
  def Business(business: Row): Array[Row] = {
    if (business.anyNull) {
      throw new Exception("Null values present in row");
    }
    else {
      val stars = business.getAs[Double]("stars");
      val adresse = business.getAs[String]("address");
      val ville = business.getAs[String]("city");
      val CP = business.getAs[String]("postal_code");
      val etat = business.getAs[String]("state");
      val categorie = business.getAs[String]("categorie");

      val localisationRow = Row(adresse, ville, CP, etat);
      val commerceRow = Row(stars, categorie)

      val attributesIndex = business.fieldIndex("attributes");
      val attributesRow = business.getStruct(attributesIndex);

      return Array(localisationRow, attributesRow, commerceRow);
    }
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("YelpETL")
      //.config("spark.config.option", "some-value")
      .getOrCreate();
    spark.sparkContext.setLogLevel("ERROR")
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.zookeeper").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.hadoop").setLevel(Level.ERROR)
    Logger.getLogger("org.spark-project").setLevel(Level.ERROR)

    //val config = ConfigFactory.load();
    //FIXME: charger les chemins d'accès et URL DB depuis la conf

    val path_business = "/etl_data/yelp_academic_dataset_business.json";
    //val path_checkin = "/etl_data/yelp_academic_dataset_checkin.json";
    //val path_tip = "/etl_data/yelp_academic_dataset_tip.csv";

    var df_business = spark.read.json(path_business);
    df_business = df_business.limit(15);
    //val df_checkin = spark.read.json(path_checkin)
    //val df_tip = spark.read.csv(path_tip)

    //print_metadata(df_business);
    //print_metadata(df_checkin);
    //print_metadata(df_tip);

    // ! Output schemas
    val attributesSchema = StructType(Array(
      StructField("id", IntegerType, false),
      StructField("AcceptsInsurance", BooleanType, false),
      StructField("AgesAllowed", BooleanType, false),
      StructField("Alcohol", BooleanType, false),
      StructField("Ambience", BooleanType, false),
      StructField("BYOB", BooleanType, false),
      StructField("BYOBCorkage", BooleanType, false),
      StructField("BestNights", BooleanType, false),
      StructField("BikeParking", BooleanType, false),
      StructField("BusinessAcceptsBitcoin", BooleanType, false),
      StructField("BusinessAcceptsCreditCards", BooleanType, false),
      StructField("BusinessParking", BooleanType, false),
      StructField("ByAppointmentOnly", BooleanType, false),
      StructField("Caters", BooleanType, false),
      StructField("CoatCheck", BooleanType, false),
      StructField("Corkage", BooleanType, false),
      StructField("DietaryRestrictions", BooleanType, false),
      StructField("DogsAllowed", BooleanType, false),
      StructField("DriveThru", BooleanType, false),
      StructField("GoodForDancing", BooleanType, false),
      StructField("GoodForKids", BooleanType, false),
      StructField("GoodForMeal", BooleanType, false),
      StructField("HairSpecializesIn", BooleanType, false),
      StructField("HappyHour", BooleanType, false),
      StructField("HasTV", BooleanType, false),
      StructField("Music", BooleanType, false),
      StructField("NoiseLevel", BooleanType, false),
      StructField("Open24Hours", BooleanType, false),
      StructField("OutdoorSeating", BooleanType, false),
      StructField("RestaurantsAttire", BooleanType, false),
      StructField("RestaurantsCounterService", BooleanType, false),
      StructField("RestaurantsDelivery", BooleanType, false),
      StructField("RestaurantsGoodForGroups", BooleanType, false),
      StructField("RestaurantsPriceRange2", BooleanType, false),
      StructField("RestaurantsReservations", BooleanType, false),
      StructField("RestaurantsTableService", BooleanType, false),
      StructField("RestaurantsTakeOut", BooleanType, false),
      StructField("Smoking", BooleanType, false),
      StructField("WheelchairAccessible", BooleanType, false),
      StructField("WiFi", BooleanType, false),
    ));

    val localisationSchema = StructType(Array(
      StructField("adresse", StringType, false),
      StructField("codePostal", StringType, false),
      StructField("etat", StringType, false),
      StructField("ville", StringType, false),
      StructField("id", IntegerType, false),
    ));



    // ! Output dataframes
    val attributesDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], attributesSchema);
    var localisationDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], localisationSchema);
    localisationDF = extractLocalisations(df_business, localisationDF);
    print_metadata(localisationDF)
    localisationDF.write.format("jdbc")
      .option("url", "jdbc:postgresql://localhost:5432/yelpdata")
      .option("driver", "org.postgresql.Driver").option("dbtable", "dw.localisation")
      .option("user", "postgres").option("password", "postgres")
      .mode("append")
      .save();

    // ! Save output dataframes
    /*
    attributesDF.write.format("jdbc")
      .option("url", "jdbc:postgresql://localhost:5432/yelp_data")
      .option("driver", "org.postgresql.Driver").option("dbtable", "DW")
      .option("user", "postgres").option("password", "postgres")
      .save();
     */
  }

  def extractLocalisations(businessDF : DataFrame, localisationDF : DataFrame) : DataFrame = {
    //TODO regarder pour utiliser les Futures et faire du traitement parallèle.

    //extraction localisation
    val localisationsOut  = localisationDF.union(businessDF.select(
      businessDF("address").as("adresse"),
      businessDF("postal_code").as("codePostal"),
      businessDF("state").as("etat"),
      businessDF("city").as("ville")
    ).withColumn("id", monotonically_increasing_id()))

    return localisationsOut
  }

  def print_metadata (dataframe : DataFrame): Unit = {
    dataframe.show();
    dataframe.printSchema();
    print(dataframe.count);
  }

  def test(): Unit = {
    val logFile = "D:\\Yann\\__DEV\\spark-3.3.1-bin-hadoop3\\README.md"
    val spark = SparkSession.builder.appName("Test app").getOrCreate()
    //spark.sparkContext.setLogLevel("WARN"); // réduire la verbose au strict minimum
    val logData = spark.read.textFile(logFile).cache()
    val numAs = logData.filter(line => line.contains('a')).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println(s"Lines with a: $numAs, Lines with b: $numBs")
    spark.stop()
  }
}
