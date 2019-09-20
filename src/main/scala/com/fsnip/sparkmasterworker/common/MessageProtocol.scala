package com.fsnip.sparkmasterworker.common

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 16:18 2019/9/19
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
case class RegisterWorkerInfo(id: String, cpu: Int, ram: Int)

class WorkerInfo(val id: String, val cpu: Int, val ram: Int){
  var lastHeartBeat: Long = System.currentTimeMillis()
}

case object RegisterWorkerInfo

// worker每隔一定时间由定时器发给自己的一个消息
case object SendHeartBeat
// worker每隔一定时间由定时器触发，而向master发送的协议消息
case class HeartBeat(id: String)