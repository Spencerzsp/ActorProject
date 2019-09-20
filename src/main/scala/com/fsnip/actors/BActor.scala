package com.fsnip.actors

import akka.actor.{Actor, ActorRef}

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 10:00 2019/9/18
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
class BActor extends Actor{
  var count = 0
  override def receive: Receive = {
    case "我打" => {
      count += 1
      if (count == 3) {
        println("AActor(乔峰): 黄师傅过奖了！")
        context.stop(self)
      }
      println("BActor(乔峰): 挺猛，看我降龙十八掌！")
      Thread.sleep(2000)
      sender() ! "我打"
    }
  }
}
