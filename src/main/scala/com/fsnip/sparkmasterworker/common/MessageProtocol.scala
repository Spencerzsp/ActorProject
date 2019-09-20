package com.fsnip.sparkmasterworker.common

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 16:18 2019/9/19
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
case class RegisterWorkerInfo(id: String, cpu: Int, ram: Int)

class WorkerInfo(val id: String, val cpu: Int, val ram: Int)

case object RegisterWorkerInfo
