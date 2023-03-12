import org.apache.log4j.{Level, Logger}
import org.apache.spark
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.catalyst.dsl.expressions.StringToAttributeConversionHelper
import org.apache.spark.sql.functions.{monotonically_increasing_id}
import org.apache.spark.sql.types.{BooleanType, IntegerType, StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      //.master("local")
      .master("spark://etl-server:7077")
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

    var businessDF = spark.read.json(path_business);
    businessDF = businessDF.limit(15);

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

    var localisationDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], localisationSchema);

    //ajout de l'id de localisation pour jointure
    businessDF = businessDF.withColumn("idLocalisation", monotonically_increasing_id());
    //extraction localisation
    localisationDF = localisationDF.union(
      businessDF.select(
        businessDF("address").as("adresse"),
        businessDF("postal_code").as("codePostal"),
        businessDF("state").as("etat"),
        businessDF("city").as("ville"),
        businessDF("idLocalisation").as("id")
      )
    );

    print_metadata(localisationDF)

    /*
    localisationDF.write.format("jdbc")
      .option("url", "jdbc:postgresql://localhost:5432/yelpdata")
      .option("driver", "org.postgresql.Driver").option("dbtable", "dw.localisation")
      .option("user", "postgres").option("password", "postgres")
      .mode("append")
      .save();
    */

    var attributesDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], attributesSchema)
    businessDF = businessDF.withColumn("idAttributs", monotonically_increasing_id());
    attributesDF = attributesDF.union(
      businessDF.select(
        businessDF("attributes.*"),
        businessDF("idAttributs")
      )
    );
    print_metadata(attributesDF);
  }

  def print_metadata (dataframe : DataFrame): Unit = {
    dataframe.show();
    dataframe.printSchema();
    print(dataframe.count);
  }
}
