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
    protected Session session;
    private static final Logger LOGGER = LogManager.getLogger("Hooks");
    @BeforeTest
    public void setupSession() throws IOException {
        LOGGER.info("Creating a new session...");
        RestAssured.baseURI = "https://api.themoviedb.org/3";
        User currentUser = Helper.parseUserJson("testdata\\usercredentials.json");
        LOGGER.info("The user for this session is: " + currentUser.getUsername());
        session = Session.getInstance().createSession(currentUser);
        LOGGER.info("The session was created successfully. ID: "+ session.getId() + "\n");
    }
}
