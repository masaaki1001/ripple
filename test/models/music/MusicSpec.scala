package models.music

import models.{WithTestUser, ModelSpecBase}
import java.io.{FileInputStream, InputStream, File}
import models.chat.ChatRoom
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._

class MusicSpec extends ModelSpecBase {
  val mp3TestDataName = "test-data.mp3"
  val mp3TestDataPath = s"test/data/$mp3TestDataName"

  val m4aTestDataName = "test-data.m4a"
  val m4aTestDataPath = s"test/data/$m4aTestDataName"
  val m4aAlbumName = "One In A Million"
  val m4aArtistName = "Aaliyah"
  val m4aSongTitle  = "Came To Give Love (Outro)"

  val nonMusicTestDataPath = "test/data/hoge"

  "Music" should {
    ".apply(File)" >> {
      "ファイル名が取得できる" >> {
        Music(new File(mp3TestDataPath)).name must_== mp3TestDataName
      }
      "ファイルのデータが配列で取得できる" >> {
        val file = new File(mp3TestDataPath)
        Music(file).rawData sameElements read(file) must_== true
      }
      "曲名が取得できる" >> {
        val file = new File(m4aTestDataPath)
        Music(file).songTitle must_== m4aSongTitle
      }
      "アルバム名が取得できる" >> {
        val file = new File(m4aTestDataPath)
        Music(file).albumName must_== m4aAlbumName
      }
      "アーティスト名が取得できる" >> {
        val file = new File(m4aTestDataPath)
        Music(file).artistName must_== m4aArtistName
      }
      "音楽ファイル以外の場合は空データになる" >> {
        Music(new File(nonMusicTestDataPath)).rawData must_== Array.emptyByteArray
      }
    }
    "#validate" >> {
      "name" >> {
        "名前が空白のみの場合は作成できない" >> {
          verifyRequiredText(new Music(_ , Array.emptyByteArray))
        }
      }
      "rawData" >> {
        "空データでは登録できない" >> new ValidationTest[Music] {
          val target: Music = new Music("hoge", Array.emptyByteArray)
          expectFailed()
        }
      }
    }
    "relations" >> {
      "chatroomに紐づけて登録することができる" >> new WithData {
        chat.musics.associate(music).chatRoomId must_== chat.id
        music.isPersisted must beTrue
      }
    }
  }

  def read(data: File): Array[Byte] = {
    val buffer = new Array[Byte](data.length.toInt)
    using(new FileInputStream(data)){ _.read(buffer) }
    buffer
  }

  def using(in: InputStream)(f: InputStream => Unit) = {
    try {
      f(in)
    } finally {
      in.close
    }
  }

  trait WithData extends WithTransaction with WithTestUser {
    lazy val chat = ChatRoom("hoge", user)
    lazy val music = Music(new File(mp3TestDataPath))

    override def before = {
      saveUser
      chat.save
    }

    override def after = {
      musics.deleteWhere(m => m.id <> 0)
      chatRooms.deleteWhere(c => c.id <> 0)
      cleanUser
    }
  }

}
