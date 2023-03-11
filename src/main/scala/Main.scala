import org.apache.log4j.{Level, Logger}
import org.apache.spark
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.types.{BooleanType, IntegerType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, Future};


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
    //Logger.getLogger("org").setLevel(Level.OFF);
    val spark = SparkSession
      .builder()
      .appName("YelpETL")
      //.config("spark.config.option", "some-value")
      .getOrCreate();

    //val config = ConfigFactory.load();
    //FIXME: charger les chemins d'accès et URL DB depuis la conf

    val path_business = "/etl_data/yelp_academic_dataset_business.json";
    //val path_checkin = "/etl_data/yelp_academic_dataset_checkin.json";
    //val path_tip = "/etl_data/yelp_academic_dataset_tip.csv";

    val df_business = spark.read.json(path_business);
    //val df_checkin = spark.read.json(path_checkin)
    //val df_tip = spark.read.csv(path_tip)

    print_metadata(df_business);
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

    ))



    // ! Output dataframes
    val attributesDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], attributesSchema);

    map_business(df_business)
  }

  def map_business(businessDF : DataFrame) : Unit = {
    val futures = Vector();
    businessDF.foreach((business:Row) => {
      val mappedRows = FieldMappings.Business(business)

    })
  }

  def print_metadata (dataframe : DataFrame): Unit = {
    dataframe.show();
    dataframe.printSchema();
    print(dataframe.count);
  }

  def createConnection() = {
     val url = "jdbc:postgresql://127.0.0.1:5432/postgres";
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
