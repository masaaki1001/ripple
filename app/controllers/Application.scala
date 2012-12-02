package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import play.api.libs.json._
import play.api.libs.iteratee._

import models._

import akka.actor._
import akka.util.duration._

import org.squeryl.PrimitiveTypeMode._
import jp.t2v.lab.play20.auth.Auth
import user.LoggedInUser

object Application extends Controller with Auth with AuthConfigImpl {

  def index = authorizedAction(LoggedInUser){ user => implicit request =>
    Ok(views.html.index())
  }

  /**
   * Display the chat room page.
   */
  def chatRoom = authorizedAction(LoggedInUser){ user => implicit request =>
    val username = Some("hoge")
    username.filterNot(_.isEmpty).map { username =>
      Ok(views.html.chatRoom(username))
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Please choose a valid username."
      )
    }
  }
  
  /**
   * Handles the chat websocket.
   */
  def chat(username: String) = WebSocket.async[JsValue] { request  =>
    ChatRoom.join(username)
  }
  
}