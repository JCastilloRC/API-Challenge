package classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;
import static io.restassured.RestAssured.given;
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieList {
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private String language;
    private List<Movie> movies;
    private String id;

    public MovieList(){
        name = "";
        description = "";
        language = "";
        id = "";
        movies = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }
    public List<Movie> getMovies() {
        return movies;
    }

    public Response createList(Session session){
        String body = getNewListBody();
        Response response = given().log().all().
                                    header("Content-Type", "application/json;charset=utf-8").
                                    queryParam("api_key", session.getCurrentUser().getApiKey()).
                                    queryParam("session_id",session.getId()).
                                    contentType(ContentType.JSON).
                                    body(body).
                            when().
                                    post("/list").
                            then().log().all().
                                    extract().response();
        this.id = response.jsonPath().getString("list_id");
        if(this.id == null){
            id = "";
        }
        return response;
    }
    public Response getDetails(Session session){
        return  given().log().all().
                        header("Content-Type", "application/json;charset=utf-8").
                        queryParam("api_key", session.getCurrentUser().getApiKey()).
                        queryParam("language","en-US").
                        pathParam("list_id", id).
                when().
                        get("list/{list_id}").
                then().log().all().
                        extract().response();
    }
    public Response addMovie(Session session, Movie movie){
        String addMovieBody = getAddMovieBody(movie);
        movies.add(movie);
        return  given().log().all().
                        header("Content-Type", "application/json;charset=utf-8").
                        queryParam("api_key", session.getCurrentUser().getApiKey()).
                        queryParam("session_id",session.getId()).
                        pathParam("list_id", id).
                        contentType(ContentType.JSON).
                        body(addMovieBody).
                when().
                        post("list/{list_id}/add_item").
                then().log().all().
                        extract().response();
    }

    public Response clearList(Session session){
        return  given().log().all().
                        header("Content-Type", "application/json;charset=utf-8").
                        queryParam("api_key", session.getCurrentUser().getApiKey()).
                        queryParam("session_id",session.getId()).
                        queryParam("confirm", true).
                        pathParam("list_id", id).
                when().
                        post("list/{list_id}/clear").
                then().log().all().
                        extract().response();
    }

    public Response deleteList(Session session){
        Response response = given().log().all().
                                    header("Content-Type", "application/json;charset=utf-8").
                                    queryParam("api_key", session.getCurrentUser().getApiKey()).
                                    queryParam("session_id",session.getId()).
                                    pathParam("list_id", id).
                            when().
                                    delete("list/{list_id}").
                            then().log().all().
                                    extract().response();
        name = "";
        description = "";
        language = "";
        id = "";
        if (null != movies) {
            movies.clear();
        }
        return response;
    }
    private String getNewListBody(){
        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ObjectNode body = new ObjectNode(nodeFactory);
        TextNode name = body.textNode(this.name);
        TextNode description = body.textNode(this.description);
        TextNode language = body.textNode(this.language);
        body.set("name", name);
        body.set("description", description);
        body.set("language",language);
        return body.toString();
    }

    private String getAddMovieBody(Movie movie){
        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ObjectNode body = new ObjectNode(nodeFactory);
        NumericNode mediaID = body.numberNode(movie.getId());
        body.set("media_id", mediaID);
        return body.toString();
    }


}
