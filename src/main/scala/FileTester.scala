import org.apache.spark.sql.SparkSession

class FileTester(implicit spark: SparkSession) {

  def testLoadFile(): Unit = {
    println("--- Starte Datei-Test ---")

    // 1. Datei einlesen
    // Wir sagen Spark: Es gibt eine Kopfzeile (header=true) und das Format ist CSV
    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true") // Versucht automatisch zu erraten, ob es Zahlen oder Strings sind
      .csv("posts.csv") // Pfad zur Datei (relativ zum Projektordner)

    // 2. Schema anzeigen (Damit sehen Sie, ob Spark die Spalten erkannt hat)
    println("Gefundenes Schema:")
    df.printSchema()

    // 3. Die ersten 20 Zeilen in der Konsole anzeigen
    println("Daten in der Datei:")
    df.show(truncate = false) // truncate=false zeigt den ganzen Text an

    println("--- Test beendet ---")
  }
}