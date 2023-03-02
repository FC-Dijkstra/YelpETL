import org.apache.log4j.{Level, Logger}
import org.apache.spark
import org.apache.spark.sql.{DataFrame, SparkSession}
import com.typesafe.config.ConfigFactory;


object Test {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF);
    val spark = SparkSession
      .builder()
      .appName("YelpETL")
      //.config("spark.config.option", "some-value")
      .getOrCreate();

    val config = ConfigFactory.load();
    //FIXME: charger les chemins d'accès et URL DB depuis la conf

    val path_business = "/etl_data/yelp_academic_dataset_business.json";
    val path_checkin = "/etl_data/yelp_academic_dataset_checkin.json";
    val path_tip = "/etl_data/yelp_academic_dataset_tip.csv";

    val df_business = spark.read.json(path_business);
    val df_checkin = spark.read.json(path_checkin)
    val df_tip = spark.read.csv(path_tip)

    print_metadata(df_business);
    print_metadata(df_checkin);
    print_metadata(df_tip);
  }

  def print_metadata (dataframe : DataFrame): Unit = {
    dataframe.show();
    dataframe.printSchema();
    print(dataframe.count);
  }

  def createConnection() = {
    val url = "jdbc:postgresql://92.141.40.32:5432/postgres"
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
