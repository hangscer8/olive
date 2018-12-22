package olive

import java.awt.Toolkit
import java.io.StringReader
import java.nio.file.Paths

import akka.actor.{ActorSystem, Props}
import com.jfoenix.controls.{JFXButton, JFXListView, JFXTreeView}
import javafx.application.{Application, Platform}
import javafx.concurrent.Worker.State
import javafx.scene.Scene
import javafx.scene.layout.{FlowPane, StackPane}
import javafx.stage.Stage
import netscape.javascript.JSObject
import olive.service.{ClipBoardMonitor, HahaService}
import olive.util.UIUtil
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document._
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.{DirectoryReader, IndexWriter, IndexWriterConfig, Term}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{IndexSearcher, TermQuery}
import org.apache.lucene.store.{FSDirectory, NIOFSDirectory}
import org.lionsoul.jcseg.analyzer.JcsegAnalyzer
import org.lionsoul.jcseg.tokenizer.core.{DictionaryFactory, IWord, JcsegTaskConfig, SegmentFactory}

import scala.io.{Codec, Source}
import scala.util.Try

object Main {

  class MainApplication extends Application {
    override def start(stage: Stage): Unit = {
      val root = new StackPane()
      val scene = new Scene(root, Toolkit.getDefaultToolkit.getScreenSize.width * 0.3, Toolkit.getDefaultToolkit.getScreenSize.height * 0.8)
      val listView = new JFXListView[String]()
      listView.setDepth(1)
      listView.setExpanded(true)
      root.getChildren.addAll(listView)
      listView.getItems().addAll((1 to 20).map(_.toString): _*)
      stage.setTitle("haha")
      stage.setScene(scene)
      stage.show()
    }
  }

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[MainApplication])
    Platform.runLater { () =>
      val system = ActorSystem("olive")
      system.actorOf(Props[ClipBoardMonitor])
    }
  }

}

object Main1 {
  var directory = FSDirectory.open(Paths.get("/Users/jianghang/Coding/luceneTemp"))
  val analyzer = new JcsegAnalyzer(JcsegTaskConfig.COMPLEX_MODE, new JcsegTaskConfig(true))
  val indexWriterConfig = new IndexWriterConfig(analyzer).setOpenMode(OpenMode.CREATE)
  val indexWriter = new IndexWriter(directory, indexWriterConfig)
  val sourceFiles = Paths.get("/Users/jianghang/Desktop/blog/source/_posts").toFile.listFiles().toList
  sourceFiles.foreach { file =>
    Try {
      val document = new Document()
      document.add(new TextField("fileName", file.getName, Store.YES)) //文件名
      document.add(new StoredField("fileSize", file.length()))
      document.add(new StoredField("filePath", file.getPath))
      document.add(new TextField("fileContent", Source.fromFile(file)(Codec.UTF8).getLines().toList.mkString, Store.YES))
      println(file.getName)
      document
    }.map(indexWriter.addDocument(_))
  }
  indexWriter.close()
  directory.close()

  println("1##################################################")

  val indexReader = DirectoryReader.open(new NIOFSDirectory(Paths.get("/Users/jianghang/Coding/luceneTemp")))
  val indexSearcher = new IndexSearcher(indexReader)
  val query = new TermQuery(new Term("fileName", "ensime")) //包含 搜索
  val top10Docs = indexSearcher.search(query, 10)
  top10Docs.scoreDocs.toList.foreach { item =>
    val doc = indexSearcher.doc(item.doc)
    //    println("fileName:" + doc.get("fileName"))
    //    println(s"fileSize:${doc.get("fileSize").toLong}")
    //    println(s"filePath:${doc.get("filePath")}")
  }

  val queryParser = new QueryParser("fileContent", new JcsegAnalyzer(JcsegTaskConfig.COMPLEX_MODE, new JcsegTaskConfig(true)))
  indexSearcher.search(queryParser.parse("fileContent:安装 完毕 sbt"), 10).scoreDocs.toList.foreach { scoreDoc =>
    val doc = indexSearcher.doc(scoreDoc.doc)
    println("fileName:" + doc.get("fileName"))
    println(s"fileSize:${doc.get("fileSize").toLong}")
    println(s"filePath:${doc.get("filePath")}")
  }

  indexReader.close()

  println("2##################################################")

  val jcsegTaskConfig = new JcsegTaskConfig(true)
  val seg = SegmentFactory.createJcseg(JcsegTaskConfig.COMPLEX_MODE, jcsegTaskConfig, DictionaryFactory.createSingletonDictionary(jcsegTaskConfig))
  seg.reset(new StringReader("我爱你施周莹，亲爱哒!! sbt world!"))

  var word: Option[IWord] = None
  do {
    word = Option(seg.next())
    println(word.map(_.getValue))
  } while (word.isDefined)

  println("3##################################################")
}
