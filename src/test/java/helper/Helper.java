package helper;

import classes.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class Helper {

    public static User parseUserJson(String jsonPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(jsonPath), User.class);
    }
    public static String getRequestTokenBody(User currentUser, String requestToken){
        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ObjectNode body = new ObjectNode(nodeFactory);
        TextNode username = body.textNode(
                                            currentUser.getUsername());
        TextNode password = body.textNode(
                                            currentUser.getPassword());
        TextNode requestT = body.textNode(
                                            requestToken);
        body.set("username", username);
        body.set("password", password);
        body.set("request_token", requestT);
        return body.toString();
    }
    public static String getSessionBody(String requestToken){
        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ObjectNode body = new ObjectNode(nodeFactory);
        TextNode requestT = body.textNode(
                requestToken);
        body.set("request_token", requestT);
        return body.toString();
    }
    public static String getBodyFromFile(String path) throws IOException {
        return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
    }
}
