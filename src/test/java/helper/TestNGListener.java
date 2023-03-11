package helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

public class TestNGListener implements ITestListener, ISuiteListener, IInvokedMethodListener {

    protected Logger logger = LogManager.getLogger("TestRunner");

    @Override
    public void onTestStart(ITestResult iTestResult) {
        logger.info("- - - - - - - > START TEST CASE: " + iTestResult.getName());
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        logger.info("Test successfully executed.");
        logger.info("- - - - - - - > TEST CASE END \n");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        logger.error("Test Failed with message: " + iTestResult.getThrowable().getMessage());
        logger.info("- - - - - - - > TEST CASE END \n");
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        logger.info("Test run: " + iTestContext.getAllTestMethods().length +
                    ", Passed: " + iTestContext.getPassedTests().size() +
                    ", Failures: " + iTestContext.getFailedTests().size() +
                    ", Skipped: " + iTestContext.getSkippedTests().size());
    }
}