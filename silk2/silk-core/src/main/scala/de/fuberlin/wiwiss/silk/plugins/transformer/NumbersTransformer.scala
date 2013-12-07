/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fuberlin.wiwiss.silk.plugins.transformer

import de.fuberlin.wiwiss.silk.linkagerule.input.SimpleTransformer
import de.fuberlin.wiwiss.silk.util.plugin.Plugin

@Plugin(id = "numbers", label = "Numbers", description = "add or substracted a number to another one")
class NumbersTransformer(substractedValue: String, sign: String) extends SimpleTransformer {
  override def evaluate(value: String) = {
    sign match {
      case "+" => (value.toInt + substractedValue.toInt).toString
      case "-" => (value.toInt - substractedValue.toInt).toString
    }
  }
}
