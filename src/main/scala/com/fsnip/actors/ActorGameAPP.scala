package com.fsnip.actors

import akka.actor.{ActorSystem, Props}

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 10:00 2019/9/18
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
object ActorGameAPP {
  def main(args: Array[String]): Unit = {
    val actorFactory = ActorSystem("actorFactory")
    val bActorRef = actorFactory.actorOf(Props[BActor], "bActor")
    val aActorRef = actorFactory.actorOf(Props(new AActor(bActorRef)), "aActor")

    aActorRef ! "start"
//    Thread.sleep(12)
//    actorFactory.shutdown()
  }
}
