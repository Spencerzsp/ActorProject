package com.fsnip.sparkmasterworker.worker

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.fsnip.sparkmasterworker.common.{HeartBeat, RegisterWorkerInfo, SendHeartBeat}
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

      // 当注册成功后，就定义一个定时器，每隔一定时间，就发送SendHeartBeat给自己
      import scala.concurrent.duration._
      import context.dispatcher
      // 说明
      // 1. 0 millis 不延时，立即执行定时器
      // 2. 3000 millis 表示每隔3秒执行 一次
      // 3. self表示发送给自己
      // 4. SendHeartBeat发送的内容
      context.system.scheduler.schedule(0 millis, 3000 millis, self, SendHeartBeat)
    }
    case SendHeartBeat => {
      sparkMasterProxy ! HeartBeat(id)
      println("worker发送了"+ id + "心跳时间" + System.currentTimeMillis())
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
