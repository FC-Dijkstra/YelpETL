import org.apache.spark
import org.apache.spark.sql.SparkSession

object Test {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("YelpETL")
      //.config("spark.config.option", "some-value")
      .getOrCreate();

    val path_business = "/etl_data/yelp_academic_dataset_business.json";
    val path_checkin = "/etl_data/yelp_academic_dataset_checkin.json";
    val path_tip = "/etl_data/yelp_academic_dataset_tip.csv";

    val df_business = spark.read.json(path_business);
    val df_checkin = spark.read.json(path_checkin)
    val df_tip = spark.read.csv(path_tip)

    df_business.show()
    df_business.printSchema()
    print(df_business.count())

    df_checkin.show()
    df_checkin.printSchema()
    print(df_checkin.count())

    df_tip.show()
    df_tip.printSchema()
    print(df_tip.count())
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
