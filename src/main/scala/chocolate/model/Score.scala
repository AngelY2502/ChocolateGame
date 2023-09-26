package chocolate.model
import scalikejdbc._

import scala.util.Try
import chocolate.util.Database
import scalafx.beans.property.{ObjectProperty, StringProperty}

class Score(val playerNameS: String, val playerScoreS: Int) extends Database {
  def this() = this(null, 0)

  var playerName = new StringProperty(playerNameS)
  var playerScore = new ObjectProperty[Int](this, "playerScore", playerScoreS)

  def isExist: Boolean = {
    DB readOnly { implicit session =>
      sql"""
        select * from Score where playerName = ${playerName.value} and playerScore = ${playerScore.value}
      """.map(rs => rs.string("playerName")).single.apply()
    } match {
      case Some(x) => true
      case None => false
    }
  }

  def save(): Try[Int] = {
    if (!isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
          insert into score (playerName, playerScore) values
            (${playerName.value}, ${playerScore.value})
        """.update.apply()
      })
    } else {
      Try(DB autoCommit { implicit session =>
        sql"""
        update score
        set
        playerName = ${playerName.value},
        playerScore = ${playerScore.value}
         where playerName = ${playerName.value} and playerScore = ${playerScore.value}
        """.update.apply()
      })
    }

  }

  def delete(): Try[Int] = {
    if (isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
        delete from score
         where playerName = ${playerName.value} and playerScore = ${playerScore.value}
        """.update.apply()
      })
    } else {
      throw new Exception("Data not found")
    }
  }

}

object Score extends Database {
  def apply(playerNameS: String, playerScoreS: Int): Score = {
    new Score(playerNameS, playerScoreS) {
      playerName.value = playerNameS
      playerScore.value = playerScoreS
    }
  }

  def initializeTable() = {
    DB autoCommit { implicit session =>
      sql"""
      create table score (
        playerName varchar(64) not null primary key,
        playerScore int not null
      )
      """.execute.apply()
    }
  }

  def getAllScore(): List[Score] = {
    DB readOnly { implicit session =>
      sql"""
        select * from Score
      """.map(rs => Score(rs.string("playerName"), rs.int("playerScore"))).list.apply()
    }
  }

}
