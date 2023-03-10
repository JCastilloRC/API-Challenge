package classes;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class List {
    public static Response createList(String apiKey, String sessionID, String body){
        return given().
                        header("Content-Type", "application/json;charset=utf-8").
                        queryParam("api_key", apiKey).
                        queryParam("session_id",sessionID).
                        contentType(ContentType.JSON).
                        body(body).
                when().
                        post("/list").
                then().
                        extract().response();
    }
    public static Response deleteList(String apiKey, String sessionID, String listID){
        return given().
                        header("Content-Type", "application/json;charset=utf-8").
                        queryParam("api_key", apiKey).
                        queryParam("session_id",sessionID).
                        pathParam("list_id", listID).
                when().
                        delete("list/{list_id}").
                then().
                        extract().response();
    }
}
