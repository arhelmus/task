package com.twitter.config

import shapeless.labelled._
import shapeless._

/**
  * There is no comment that can help you to understand that code if you are not familiar with Shapeless... :(
  */
private object MapToClassConverter {

  trait FromMap[L <: HList] {
    def apply(m: Map[String, Any]): Option[L]
  }

  trait LowPriorityFromMap {
    implicit def hconsFromMap[K <: Symbol, V, T <: HList](
        implicit witness: Witness.Aux[K],
        typeable: Typeable[V],
        fromMapT: Lazy[FromMap[T]]): FromMap[FieldType[K, V] :: T] =
      (m: Map[String, Any]) =>
        for {
          v <- m.get(witness.value.name)
          h <- typeable.cast(v)
          t <- fromMapT.value(m)
        } yield field[K](h) :: t
  }

  object FromMap extends LowPriorityFromMap {

    implicit val hnilFromMap: FromMap[HNil] = (m: Map[String, Any]) =>
      Some(HNil)

    implicit def hconsFromMapWithNestedMap[K <: Symbol,
                                           V,
                                           R <: HList,
                                           T <: HList](
        implicit witness: Witness.Aux[K],
        gen: LabelledGeneric.Aux[V, R],
        fromMapH: Lazy[FromMap[R]],
        fromMapT: Lazy[FromMap[T]]): FromMap[FieldType[K, V] :: T] =
      (m: Map[String, Any]) =>
        for {
          v <- m.get(witness.value.name)
          r <- Typeable[Map[String, Any]].cast(v)
          h <- fromMapH.value(r)
          t <- fromMapT.value(m)
        } yield field[K](gen.from(h)) :: t

    implicit def hconsFromMapWithSeq[K <: Symbol, V, R <: HList, T <: HList](
        implicit witness: Witness.Aux[K],
        gen: LabelledGeneric.Aux[V, R],
        tmrH: Lazy[FromMap[R]],
        tmrT: Lazy[FromMap[T]]): FromMap[FieldType[K, Seq[V]] :: T] =
      (map: Map[String, Any]) => {
        map(witness.value.name) match {
          case list: Seq[_] if list.nonEmpty =>
            for {
              r <- Typeable[Seq[Map[String, Any]]].cast(list)
              h = r.map(elem => tmrH.value(elem).get)
              t <- tmrT.value(map)
            } yield field[K](h.map(repr => gen.from(repr))) :: t
          case _ =>
            for {
              tail <- tmrT.value(map)
            } yield field[K](Seq()) :: tail
        }
      }

  }

}
