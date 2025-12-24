package org.example.haidar;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.client.SpecBuilder;
import org.example.utils.ConfigReader;
import org.example.utils.ExtentManager;
import org.example.utils.JsonUtils;
import org.testng.ITestResult;
import org.testng.annotations.*;

public class BaseTest {

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected static ExtentReports extent;

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        logger.info("Setting up test suite");
        extent = ExtentManager.createInstance();
    }

    @BeforeClass(alwaysRun = true)
    public void setupClass() {
        logger.info("Setting up test class: {} in thread: {}",
                this.getClass().getSimpleName(), Thread.currentThread().getId());
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(ITestResult result) {
        logger.info("Starting test: {} in thread: {}",
                result.getMethod().getMethodName(), Thread.currentThread().getId());

        // Create test in ExtentReports for current thread
        ExtentTest test = extent.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        test.assignCategory(result.getTestClass().getRealClass().getSimpleName());
        ExtentManager.setTest(test);

        // Reset specs for new test
        SpecBuilder.resetSpecs();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        logger.info("Finishing test: {} in thread: {}",
                result.getMethod().getMethodName(), Thread.currentThread().getId());

        ExtentTest test = ExtentManager.getTest();

        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Test Failed");
            test.fail(result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, "Test Passed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "Test Skipped");
            test.skip(result.getThrowable());
        }

        // Clean up ThreadLocal variables to prevent memory leaks
        ExtentManager.unload();
        SpecBuilder.removeThreadLocalSpecs();
        JsonUtils.removeThreadLocalMapper();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        logger.info("Tearing down test class: {} in thread: {}",
                this.getClass().getSimpleName(), Thread.currentThread().getId());
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        logger.info("Tearing down test suite");

        // Final cleanup of all ThreadLocal variables
        ConfigReader.getInstance().removeThreadLocalProperties();

        if (extent != null) {
            extent.flush();
        }
    }
}
