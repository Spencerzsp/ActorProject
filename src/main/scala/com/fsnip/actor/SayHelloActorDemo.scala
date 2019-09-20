package com.fsnip.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * @ Author     ：zsp
  * @ Date       ：Created in 17:35 2019/9/17
  * @ Description：
  * @ Modified By：
  * @ Version:     
  */
object SayHelloActorDemo {
  // 创建一个ActorSystem工厂，并同时通过反射Props[SayHelloActor]创建sayHelloActorRef
  private val actorFactory = ActorSystem("actorFactory")
  private val sayHelloActorRef: ActorRef = actorFactory.actorOf(Props[SayHelloActor], "sayHelloActor")

  def main(args: Array[String]): Unit = {
    sayHelloActorRef ! "hello"
    sayHelloActorRef ! "ok"
    sayHelloActorRef ! "hello~"
    Thread.sleep(2000)

    actorFactory.shutdown()
  }
}

class SayHelloActor extends Actor{
  override def receive: Receive = {
    case "hello" => println("收到hello,回应hello too")
    case "ok" => println("收到ok,回应ok too")
    case _ => println("匹配不到")
  }


}
