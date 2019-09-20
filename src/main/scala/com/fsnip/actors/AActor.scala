package com.fsnip.actors

import akka.actor.{Actor, ActorRef}

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 9:59 2019/9/18
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
class AActor(actorRef: ActorRef) extends Actor {

  val bActorRef: ActorRef = actorRef
  var count = 0

  override def receive: Receive = {
    case "start" => {
      println("开始出招, start ok!")
      self ! "我打"
    }


    case "我打" => {
      println("AActor(黄飞鸿): 厉害，看我佛山无影脚！")
      Thread.sleep(2000)
      bActorRef ! "我打"
      count += 1
      if (count == 3) {
        println("AActor(黄飞鸿): 乔帮主好功夫，承让了！")
        context.stop(self)
      }
    }

  }
}
