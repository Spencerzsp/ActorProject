package com.fsnip.sparkmasterworker.master

import akka.actor.{Actor, ActorSystem, Props}
import com.fsnip.sparkmasterworker.common._
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 15:22 2019/9/19
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
class SparkMaster extends Actor {

  val workers = mutable.Map[String, WorkerInfo]()

  override def receive: Receive = {
    case "start" => {
      println("master服务器启动了...")
      self ! StartTimeOutWorker
    }

    case RegisterWorkerInfo(id, cpu, ram) => {
      if (!workers.contains(id)) {
        val workerInfo = new WorkerInfo(id, cpu, ram)
        workers += ((id, workerInfo))
        println("服务器的workers = " + workers)

        sender() ! RegisterWorkerInfo
      }
    }
    case HeartBeat(id) => {
      // 更新对应的worker的心跳时间
      // 从workers中取出workerInfo
      val workerInfo = workers(id)
      workerInfo.lastHeartBeat = System.currentTimeMillis()
      println("master更新了" + id + "心跳时间..." + System.currentTimeMillis())
    }
    case StartTimeOutWorker => {
      println("开始了定时检测worker心跳的任务")
      import scala.concurrent.duration._
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, 9000 millis, self, RemoveTimeOutWorker)
    }
    case RemoveTimeOutWorker => {
      val workerInfos = workers.values
      val now = System.currentTimeMillis()
      workerInfos.filter(workerInfo => (now - workerInfo.lastHeartBeat) > 6000)
        .foreach(workerInfo => workers.remove(workerInfo.id))
      println("当前有" + workers.size + "个worker存活")
    }
  }
}

object SparkMaster {
  def main(args: Array[String]): Unit = {
    if (args.length != 3){
      println("请输入 masterHost masterPort masterName")
      sys.exit()
    }
    val masterHost = args(0)
    val masterPort = args(1)
    val masterName = args(2)
    val config: Config = ConfigFactory.parseString(
      s"""
      akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      akka.remote.netty.tcp.hostname = ${masterHost}
      akka.remote.netty.tcp.port = ${masterPort}
     """.stripMargin
    )
    val sparkMasterSystem = ActorSystem("SparkMaster", config)
    val sparkMasterRef = sparkMasterSystem.actorOf(Props[SparkMaster], s"${masterName}")

    sparkMasterRef ! "start"
  }
}
