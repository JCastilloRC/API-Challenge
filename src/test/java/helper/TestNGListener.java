package helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IInvokedMethodListener;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListener implements ITestListener, ISuiteListener, IInvokedMethodListener {

    protected Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void onTestStart(ITestResult iTestResult) {

    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {

        logger.info("Test successfully executed.\n");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {

        logger.error("Test Failed with message: " + iTestResult.getThrowable().getMessage() + "\n");
    }
}