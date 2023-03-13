package hooks;

import classes.Movie;
import classes.MovieList;
import classes.Session;
import classes.User;
import helper.Helper;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeTest;
import java.io.IOException;
public class Hooks {
    private static final Logger LOGGER = LogManager.getLogger("Hooks");
    protected static final String NEW_LIST_BODY_PATH = "testdata\\newlist.json";
    protected static final String MOVIE_PATH = "testdata\\movie.json";
    protected Session session;
    protected MovieList movieList;
    protected MovieList errorMovieList;
    protected Movie movie;
    protected Movie errorMovie;
    @BeforeTest
    public void setupSession() throws IOException {
        LOGGER.info("Creating a new session...");
        RestAssured.baseURI = "https://api.themoviedb.org/3";
        User currentUser = Helper.parseUserJson("testdata\\usercredentials.json");
        LOGGER.info("The user for this session is: " + currentUser.getUsername());
        session = Session.getInstance().createSession(currentUser);
        LOGGER.info("The session was created successfully. ID: "+ session.getId() + "\n");
        LOGGER.info("Initializing test data");
        movieList = Helper.parseListJson(NEW_LIST_BODY_PATH);
        errorMovieList = new MovieList();
        movie = Helper.parseMovieJson(MOVIE_PATH);
        errorMovie = new Movie();
    }
}
