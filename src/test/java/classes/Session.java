package classes;
import helper.Helper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Session {
    private static Session singleSession = null;
    private String sessionID;
    private String requestToken;

    private Session(){}
    public static Session getInstance()
    {
        if (singleSession == null)
            singleSession = new Session();
        return singleSession;
    }
    public String getSessionID() {
        return sessionID;
    }
    private void requestToken(User currentUser){
        Response response = given().
                                    queryParam("api_key", currentUser.getApiKey()).
                             when().
                                    get("/authentication/token/new").
                             then().
                                    extract().response();
        this.requestToken = response.jsonPath().getString("request_token");
    }
    private void validateRequestToken(User currentUser){
        String requestTBody = Helper.getRequestTokenBody(currentUser, requestToken);
        given().
                contentType(ContentType.JSON).
                body(requestTBody).
                queryParam("api_key", currentUser.getApiKey()).
        when().
                post("/authentication/token/validate_with_login").
        then();
    }
    public Session createSession(User currentUser){
        requestToken(currentUser);
        validateRequestToken(currentUser);
        String createSessionBody = Helper.getSessionBody(requestToken);
        Response response = given().
                                    contentType(ContentType.JSON).
                                    body(createSessionBody).
                                    queryParam("api_key", currentUser.getApiKey()).
                            when().
                                    post("/authentication/session/new").
                            then().
                                    extract().response();
        this.sessionID = response.jsonPath().getString("session_id");
        return this;
    }

}
