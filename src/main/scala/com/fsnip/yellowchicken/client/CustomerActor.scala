package com.fsnip.yellowchicken.client

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.fsnip.yellowchicken.commom.{ClientMessage, ServerMessage}
import com.typesafe.config.{Config, ConfigFactory}

import scala.io.StdIn

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 11:05 2019/9/19
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
class CustomerActor(serverHost: String, serverPort: Int) extends Actor{
  // 定义一个YellowChickenServerRef
  var serverActorRef: ActorSelection = _

  // 在Actor中有一个prefStart方法，他会在Actor运行前执行
  // 在akka的开发中，通常将初始化的工作放在prefStart中执行
  override def preStart(): Unit = {
    serverActorRef = context.actorSelection(s"akka.tcp://Server@${serverHost}:${serverPort}/user/YellowChickenServer")

    println("serverActorRef = " + serverActorRef)
  }
  override def receive = {
    case "start" => println("客户端start，开始咨询问题")
    case message: String => {
      serverActorRef ! ClientMessage(message)
    }
    case ServerMessage(message) =>{
      println(s"收到小黄鸡客服(Server)：$message")
    }
  }
}

object CustomerActor extends App{
  val(clientHost, clientPort, serverHost, serverPort) = ("127.0.0.1", 9990, "127.0.0.1", 9999)
  val config: Config = ConfigFactory.parseString(
    s"""
      akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      akka.remote.netty.tcp.hostname = $clientHost
      akka.remote.netty.tcp.port = $clientPort
     """.stripMargin
  )

  val clientActorSystem = ActorSystem("client", config)
  val customerActorRef: ActorRef = clientActorSystem.actorOf(Props(new CustomerActor(serverHost, serverPort)), "CustomerActor")

  customerActorRef ! "start"

  // 客户端可以发送消息给服务器
  while(true){
    val message = StdIn.readLine()
    customerActorRef ! message
  }
}
