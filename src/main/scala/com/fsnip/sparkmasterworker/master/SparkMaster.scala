package com.fsnip.sparkmasterworker.master

import akka.actor.{Actor, ActorSystem, Props}
import com.fsnip.sparkmasterworker.common.{HeartBeat, RegisterWorkerInfo, WorkerInfo}
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 15:22 2019/9/19
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
class SparkMaster extends Actor{

  val workers = mutable.Map[String, WorkerInfo]()

  override def receive: Receive = {
    case "start" => println("master服务器启动了...")

    case RegisterWorkerInfo(id, cpu, ram) => {
      if(!workers.contains(id)){
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
      println("master更新了" + id + "心跳时间...")
    }
  }
}

object SparkMaster{
  def main(args: Array[String]): Unit = {
    val config: Config = ConfigFactory.parseString(
      s"""
      akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      akka.remote.netty.tcp.hostname = 127.0.0.1
      akka.remote.netty.tcp.port = 10005
     """.stripMargin
    )
    val sparkMasterSystem = ActorSystem("SparkMaster", config)
    val sparkMasterRef = sparkMasterSystem.actorOf(Props[SparkMaster], "SparkMaster-01")

    sparkMasterRef ! "start"
  }
}
