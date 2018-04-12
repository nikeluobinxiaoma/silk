package org.silkframework.execution.local

import org.silkframework.config.{Task, TaskSpec}
import org.silkframework.entity._
import org.silkframework.util.Uri

case class LinksTable(links: Seq[Link], linkType: Uri, task: Task[TaskSpec]) extends LocalEntities {

  val entitySchema = LinksTable.linkEntitySchema

  val entities = {
    for (link <- links) yield
      Entity(
        uri = link.source,
        values = IndexedSeq(Seq(link.target), Seq(link.confidence.getOrElse(0.0).toString)),
        schema = entitySchema
      )
  }

  /**
    * The task that generated this table.
    * If the entity table has been generated by a workflow this is a copy of the actual task that has been executed.
    */
  override def taskOption: Option[Task[TaskSpec]] = Some(task)
}

object LinksTable {

  val linkEntitySchema = EntitySchema("", IndexedSeq(
    TypedPath(Path("targetUri"), UriValueType, isAttribute = false),
    TypedPath(Path("confidence"), DoubleValueType, isAttribute = false)))
}
