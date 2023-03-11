package classes;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Session {
    private static Session singleSession = null;

    private User currentUser;
    private String id;
    private String requestToken;

    private Session(){}
    public static Session getInstance()
    {
        if (singleSession == null)
            singleSession = new Session();
        return singleSession;
    }
    public String getId() {
        return id;
    }
    public User getCurrentUser(){return currentUser;}
    private void requestToken(){
        Response response = given().
                                    queryParam("api_key", currentUser.getApiKey()).
                             when().
                                    get("/authentication/token/new").
                             then().
                                    extract().response();
        this.requestToken = response.jsonPath().getString("request_token");
    }
    private void validateRequestToken(){
        String requestTBody = getRequestTokenBody(currentUser);
        given().
                contentType(ContentType.JSON).
                body(requestTBody).
                queryParam("api_key", currentUser.getApiKey()).
        when().
                post("/authentication/token/validate_with_login").
        then();
    }
    public Session createSession(User currentUser){
        this.currentUser = currentUser;
        requestToken();
        validateRequestToken();
        String createSessionBody = getSessionBody();
        Response response = given().
                                    contentType(ContentType.JSON).
                                    body(createSessionBody).
                                    queryParam("api_key", currentUser.getApiKey()).
                            when().
                                    post("/authentication/session/new").
                            then().
                                    extract().response();
        this.id = response.jsonPath().getString("session_id");
        return this;
    }

    private String getRequestTokenBody(User currentUser){
        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ObjectNode body = new ObjectNode(nodeFactory);
        TextNode username = body.textNode(currentUser.getUsername());
        TextNode password = body.textNode(currentUser.getPassword());
        TextNode requestT = body.textNode(requestToken);
        body.set("username", username);
        body.set("password", password);
        body.set("request_token", requestT);
        return body.toString();
    }
    private String getSessionBody(){
        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ObjectNode body = new ObjectNode(nodeFactory);
        TextNode requestT = body.textNode(requestToken);
        body.set("request_token", requestT);
        return body.toString();
    }

}
