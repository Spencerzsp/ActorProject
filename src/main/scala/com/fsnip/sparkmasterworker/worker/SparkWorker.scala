package com.fsnip.sparkmasterworker.worker

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.fsnip.sparkmasterworker.common.RegisterWorkerInfo
import com.typesafe.config.ConfigFactory

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 15:37 2019/9/19
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
class SparkWorker(masterHost: String, masterPort: Int) extends Actor{

  var sparkMasterProxy: ActorSelection = _
  var id = java.util.UUID.randomUUID().toString

  override def preStart(): Unit = {
    sparkMasterProxy = context.actorSelection(s"akka.tcp://SparkMaster@${masterHost}:${masterPort}/user/SparkMaster-01")
    println("sparkMasterProxy = " + sparkMasterProxy)
  }
  override def receive: Receive = {
    case "start" => {
      println("worker启动了")
      sparkMasterProxy ! RegisterWorkerInfo(id, 16, 16 * 1024)
    }
    case RegisterWorkerInfo => {
      println("workerid = " + id + "注册成功！")
    }
  }
}

object SparkWorker{
  def main(args: Array[String]): Unit = {
    val(workerHost, workerPort, masterHost, masterPort) = ("127.0.0.1", 10001, "127.0.0.1", 10005)
    val config = ConfigFactory.parseString(
      s"""
        akka.actor.provider = "akka.remote.RemoteActorRefProvider"
        akka.remote.netty.tcp.hostname = $workerHost
        akka.remote.netty.tcp.port = $workerPort
       """.stripMargin
    )

    val sparkWorkerSystem = ActorSystem("SparkWorker", config)
    val sparkWorkerRef = sparkWorkerSystem.actorOf(Props(new SparkWorker(masterHost, masterPort)))

    sparkWorkerRef ! "start"


  }
}
