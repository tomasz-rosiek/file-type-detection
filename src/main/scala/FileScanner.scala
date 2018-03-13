import java.io.{BufferedInputStream, File, FileInputStream}

import org.apache.tika.metadata.{Metadata, TikaMetadataKeys}
import sys.process._
object FileScanner extends App {

  import org.apache.tika.config.TikaConfig

  val config = TikaConfig.getDefaultConfig
  val detector = config.getDetector

  def detect(f : File): String = {
    val metadata = new Metadata()
    metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, f.getName)
    val detectionResult = detector.detect(new BufferedInputStream(new FileInputStream(f)), metadata)

    s"${detectionResult.getType}/${detectionResult.getSubtype}"
  }

  def detectWithFile(f : File): String = {
    (s"file ${f.getAbsolutePath} -b --mime-type" !!).trim
  }

  val detectedFiles = new File("Downloads")
    .listFiles()
    .toList
    .filter(_.isFile)
    .map(file => (file.getName, detect(file), detectWithFile(file)))

  detectedFiles
    .filter(v => v._2 != v._3)
    .filter(v => !v._3.contains("No such file"))
    .sortBy(_._1)
    .foreach(println)

}
