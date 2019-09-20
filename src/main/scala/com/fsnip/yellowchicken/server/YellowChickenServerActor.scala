package com.fsnip.yellowchicken.server

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.fsnip.yellowchicken.commom.{ClientMessage, ServerMessage}
import com.typesafe.config.{Config, ConfigFactory}

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 10:51 2019/9/19
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
class YellowChickenServerActor extends Actor{
  override def receive: Receive = {
    case "start" => println("start 小黄鸡开始工作了~~~")
    case ClientMessage(message) => {
      message match {
        case "学费" => sender() ! ServerMessage("15000RMB")
        case "地址" => sender() ! ServerMessage("成都市武侯区楚峰国际")
        case "可以学什么技术" => sender() ! ServerMessage("大数据，python，人工智能")
        case "食安科技怎么样" => sender() ! ServerMessage("傻逼公司，工资都发不起了！")
        case "你确定" => sender() ! ServerMessage("不信你来试试就晓得了！")
        case _ => sender() ! ServerMessage("你说啥？")
      }
    }
  }
}

object YellowChickenServer extends App{
  val host = "127.0.0.1"
  val port = 9999
  val config: Config = ConfigFactory.parseString(
    s"""
      akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      akka.remote.netty.tcp.hostname = $host
      akka.remote.netty.tcp.port = $port
     """.stripMargin
  )
  val serverActorSystem = ActorSystem("Server", config)
  val yellowChickenServerRef: ActorRef = serverActorSystem.
    actorOf(Props[YellowChickenServerActor], "YellowChickenServer")

  yellowChickenServerRef ! "start"
}
