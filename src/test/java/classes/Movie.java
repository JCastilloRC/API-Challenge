package classes;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {

    @JsonProperty
    @JsonAlias({"title"})
    private String name;
    @JsonProperty
    private int id;

    public Movie(){
        name = "";
        id = 0;
    }
    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    public Response getDetails(Session session){
        return  given().log().all().
                        header("Content-Type", "application/json;charset=utf-8").
                        queryParam("api_key", session.getCurrentUser().getApiKey()).
                        queryParam("language","en-US").
                        pathParam("movie_id", id).
                when().
                        get("movie/{movie_id}").
                then().log().all().
                        extract().response();
    }
    public Response rateMovie(Session session, float rating){
        String rateMovieBody = getRateMovieBody(rating);
        return  given().log().all().
                        header("Content-Type", "application/json;charset=utf-8").
                        queryParam("api_key", session.getCurrentUser().getApiKey()).
                        queryParam("session_id",session.getId()).
                        pathParam("movie_id", id).
                        contentType(ContentType.JSON).
                        body(rateMovieBody).
                when().
                        post("movie/{movie_id}/rating").
                then().log().all().
                        extract().response();
    }
    private String getRateMovieBody(float rating){
        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ObjectNode body = new ObjectNode(nodeFactory);
        NumericNode mediaID = body.numberNode(rating);
        body.set("value", mediaID);
        return body.toString();
    }

}
