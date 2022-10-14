package tim.files

import org.junit.Assert._
import org.junit.{Test, BeforeClass, AfterClass}
import tim.{Conf, TestConf}

import Codecs.given
import org.junit.AfterClass
import tim.TestUtil
import tim.Date

class RecordTest {

  given Conf = TestConf
  val repo = new Repo()

  @BeforeClass def beforeAll(): Unit = {
    FileUtil.delete(TestConf.basePath)
  }
  @AfterClass def afterAll(): Unit = {
    FileUtil.delete(TestConf.basePath)
  }

  @Test def testLoadSaveEnded() = {
    val origin = TestUtil.endedRecord
    repo.saveRecord(origin)
    val record = repo.loadRecord(origin.date)
    assertEquals(Some(origin), record)
  }

  @Test def testLoadSaveNotEnded() = {
    val origin = TestUtil.notEndedRecord
    repo.saveRecord(origin)
    val record = repo.loadRecord(origin.date)
    assertEquals(Some(origin), record)
  }

  @Test def testInvalidDate() = {
    val record = repo.loadRecord(Date(2000,1,2))
    assertEquals(record, None)
  }

}
