import org.apache.spark.sql.SparkSession

object Test {
  def main(args: Array[String]): Unit = {
    val logFile = "D:\\Yann\\__DEV\\spark-3.3.1-bin-hadoop3\\README.md"
    val spark = SparkSession.builder.appName("Test app").getOrCreate()
    val logData = spark.read.textFile(logFile).cache()
    val numAs = logData.filter(line => line.contains('a')).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println(s"Lines with a: $numAs, Lines with b: $numBs")
    spark.stop()
  }
}