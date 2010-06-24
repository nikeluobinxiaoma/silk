package de.fuberlin.wiwiss.silk.linkspec.aggegrator

import de.fuberlin.wiwiss.silk.linkspec.Aggregator
import scala.math._

/**
 * Computes the weighted geometric mean.
 */
class GeometricMeanAggregator(val params: Map[String, String] = Map.empty) extends Aggregator
{
    override def evaluate(values : Traversable[(Int, Double)]) =
    {
        if(!values.isEmpty)
        {
            val weightedProduct = values.map{case (weight, value) => pow(value, weight)}.reduceLeft(_ * _)
            val totalWeights = values.map{case (weight, value) => weight}.sum

            Some(pow(weightedProduct, 1.0 / totalWeights))
        }
        else
        {
            None
        }
    }
}
