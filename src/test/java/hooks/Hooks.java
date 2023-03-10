package hooks;

import classes.Session;
import classes.User;
import helper.Helper;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeTest;
import java.io.IOException;
public class Hooks {
    protected User currentUser;
    protected String sessionID;
    protected String listID;
    private static final Logger LOGGER = LogManager.getLogger(Hooks.class);
    @BeforeTest
    public void setupSession() throws IOException {
        LOGGER.info("Creating a new session...");
        RestAssured.baseURI = "https://api.themoviedb.org/3";
        currentUser = Helper.parseUserJson("testdata\\usercredentials.json");
        LOGGER.info("The user for this session is: " + currentUser.getUsername());
        sessionID = Session.getInstance().createSession(currentUser).getSessionID();
        LOGGER.info("The session was created succesfully. ID: "+ sessionID + "\n");
    }
}
