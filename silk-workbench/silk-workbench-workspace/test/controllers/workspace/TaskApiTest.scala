package controllers.workspace

import helper.IntegrationTestTrait
import org.scalatestplus.play.PlaySpec
import play.api.http.Status
import play.api.libs.json.{JsString, Json}
import play.api.libs.ws.WS

class TaskApiTest extends PlaySpec with IntegrationTestTrait {

  private val project = "project"

  override def workspaceProvider: String = "inMemory"

  protected override def routes = Some("test.Routes")

  private val datasetId = "testDataset"

  private val transformId = "testTransform"

  private val linkTaskId = "testLinkTask"

  "setup" in {
    createProject(project)
  }

  "post dataset task" in {
    var request = WS.url(s"$baseUrl/workspace/projects/$project/tasks")
    val response = request.post(
      <Dataset id={datasetId} type="internal">
        <MetaData>
          <Label>label 1</Label>
          <Description>description 1</Description>
        </MetaData>
        <Param name="graphUri" value="urn:dataset1"/>
      </Dataset>
    )
    checkResponse(response)
  }

  "post dataset task with existing identifier" in {
    var request = WS.url(s"$baseUrl/workspace/projects/$project/tasks")
    val response = request.post(
      <Dataset id={datasetId} type="internal">
        <MetaData>
          <Label>label 1</Label>
          <Description>description 1</Description>
        </MetaData>
        <Param name="graphUri" value="urn:dataset1"/>
      </Dataset>
    )
    checkResponseCode(response, Status.CONFLICT)
  }

  "get dataset task" in {
    var request = WS.url(s"$baseUrl/workspace/projects/$project/tasks/$datasetId")
    request = request.withHeaders("Accept" -> "application/json")
    val response = checkResponse(request.get())
    response.json mustBe
      Json.obj(
        "id" -> datasetId,
        "metadata" ->
          Json.obj(
            "label" -> "label 1",
            "description" -> "description 1"
          ),
        "taskType" -> "Dataset",
        "type" -> "internal",
        "parameters" -> Json.obj(
          "graphUri" -> "urn:dataset1"
        )
      )
  }

  "update dataset task" in {
    var request = WS.url(s"$baseUrl/workspace/projects/$project/tasks/$datasetId")
    request = request.withHeaders("Accept" -> "application/json")
    val response = request.put(
      Json.obj(
        "id" -> datasetId,
        "metadata" ->
          Json.obj(
            "label" -> "label 2",
            "description" -> "description 2"
          ),
        "uriProperty" -> "URI",
        "taskType" -> "Dataset",
        "type" -> "internal",
        "parameters" ->
          Json.obj(
            "graphUri" -> "urn:dataset2"
          )
      )
    )
    checkResponse(response)
  }

  "get updated dataset" in {
    var request = WS.url(s"$baseUrl/workspace/projects/$project/tasks/$datasetId")
    request = request.withHeaders("Accept" -> "application/xml")
    val response = checkResponse(request.get())
    val xml = response.xml

    (xml \ "@id").text mustBe datasetId
    (xml \ "@uriProperty").text mustBe "URI"
    (xml \ "MetaData" \ "Label").text mustBe "label 2"
    (xml \ "MetaData" \ "Description").text mustBe "description 2"
    (xml \ "Param").text mustBe "urn:dataset2"
  }

  "post transform task" in {
    var request = WS.url(s"$baseUrl/workspace/projects/$project/tasks")
    val response = request.post(
      <TransformSpec id={transformId}>
        <SourceDataset dataSource={datasetId} var="a" typeUri="" />
        <TransformRule name="rule" targetProperty="">
            <TransformInput id="constant" function="constant">
              <Param name="value" value="http://example.org/"/>
            </TransformInput>
        </TransformRule>
      </TransformSpec>
    )
    checkResponse(response)
  }

  "get transform task" in {
    var request = WS.url(s"$baseUrl/workspace/projects/$project/tasks/$transformId")
    request = request.withHeaders("Accept" -> "application/json")
    val response = checkResponse(request.get())

    (response.json \ "id").get mustBe JsString(transformId)
    (response.json \ "taskType").get mustBe JsString("Transform")
    (response.json \ "selection" \ "inputId").get mustBe JsString(datasetId)
  }

  "post linking task" in {
    var request = WS.url(s"$baseUrl/workspace/projects/$project/tasks")
    val response = request.post(
      <Interlink id={linkTaskId}>
        <SourceDataset dataSource={datasetId} var="a" typeUri="http://dbpedia.org/ontology/Film">
          <RestrictTo>
          </RestrictTo>
        </SourceDataset>
        <TargetDataset dataSource={datasetId} var="b" typeUri="http://data.linkedmdb.org/resource/movie/film">
          <RestrictTo>
          </RestrictTo>
        </TargetDataset>
        <LinkageRule linkType="owl:sameAs">
          <Aggregate id="combineSimilarities" required="false" weight="1" type="min">
            <Compare id="compareTitles" required="false" weight="1" metric="levenshteinDistance" threshold="0.0" indexing="true">
              <TransformInput id="toLowerCase1" function="lowerCase">
                <Input id="movieTitle1" path="/&lt;http://xmlns.com/foaf/0.1/name&gt;"/>
              </TransformInput>
              <TransformInput id="toLowerCase2" function="lowerCase">
                <Input id="movieTitle2" path="/&lt;http://www.w3.org/2000/01/rdf-schema#label&gt;"/>
              </TransformInput>
            </Compare>
          </Aggregate>
        </LinkageRule>
      </Interlink>
    )
    checkResponse(response)
  }

  "get linking task" in {
    var request = WS.url(s"$baseUrl/workspace/projects/$project/tasks/$linkTaskId")
    request = request.withHeaders("Accept" -> "application/xml")
    val response = checkResponse(request.get())

    (response.xml \ "@id").text mustBe linkTaskId
    (response.xml \ "TargetDataset" \ "@dataSource").text mustBe datasetId
  }

}
