package testrunner;
import classes.List;
import helper.Helper;
import helper.TestNGListener;
import hooks.Hooks;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.io.IOException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
@Listeners(TestNGListener.class)
public class TestRunner extends Hooks {
    private static final Logger LOGGER = LogManager.getLogger(TestRunner.class);
    private static final String NEW_LIST_BODY_PATH = "testdata\\newlist.json";

    @Test
    @Description("Create a new list successfully")
    @Feature("Lists")
    public void createListHappyPath() throws IOException {
        String newListBody = Helper.getBodyFromFile(NEW_LIST_BODY_PATH);
        LOGGER.info("- - - - - - - > START TEST CASE 'Create a new List successfully'");
        LOGGER.info("Sending a POST request to create a list with body: \n" + newListBody);
        Response response = List.createList(
                                            currentUser.getApiKey(),
                                            sessionID,
                                            newListBody);
        LOGGER.info("- - - - - - - > TEST CASE END");
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(201)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The item/record was created successfully.")
        );
        super.listID = response.jsonPath().getString("list_id");
        LOGGER.info("List created with id: " + listID);
    }
    @Test
    @Description("Trying to create a new list unsuccessfully")
    @Feature("Lists")
    public void createListSadPath() throws IOException {
        String newListBody = Helper.getBodyFromFile(NEW_LIST_BODY_PATH);
        LOGGER.info("- - - - - - - > START TEST CASE 'Fail trying to create a new list'");
        LOGGER.info("Setting empty string for 'session_id' query parameter");
        LOGGER.info("Sending a POST request to create a list with body: \n" + newListBody);
        Response response = List.createList(
                                            currentUser.getApiKey(),
                                            "",
                                            newListBody);
        LOGGER.info("- - - - - - - > TEST CASE END");
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(401)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("Authentication failed: You do not have permissions to access the service.")
        );
    }
    @Test
    @Description("Delete a list successfully")
    @Feature("Lists")
    public void deleteListHappyPath() throws IOException {
        LOGGER.info("- - - - - - - > START TEST CASE 'Delete a List successfully'");
        LOGGER.info("Sending a DELETE request for list with ID: " + listID);
        Response response = List.deleteList(
                currentUser.getApiKey(),
                sessionID,
                listID);
        LOGGER.info("- - - - - - - > TEST CASE END");
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(401)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The item/record was updated successfully.")
        );
    }

    @Test
    @Description("Delete a list successfully")
    @Feature("Lists")
    public void deleteListSadPath() throws IOException {
        LOGGER.info("- - - - - - - > START TEST CASE 'Fail trying to delete a nonexistent list'");
        LOGGER.info("Sending a DELETE request for list with empty ID");
        Response response = List.deleteList(
                currentUser.getApiKey(),
                sessionID,
                "");
        LOGGER.info("- - - - - - - > TEST CASE END");
        assertThat("Wrong status code",
                response.getStatusCode(),
                equalTo(404)
        );
        assertThat("Wrong status message",
                response.jsonPath().getString("status_message"),
                equalTo("The resource you requested could not be found.")
        );
    }


}
