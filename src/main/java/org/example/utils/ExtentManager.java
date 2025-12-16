package org.example.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentManager {
    private static ExtentReports extent;

    // ThreadLocal to maintain separate ExtentTest for each thread
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static ExtentReports createInstance() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String fileName = "test-output/ExtentReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(fileName);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("API Automation Report");
        sparkReporter.config().setReportName("REST API Test Results");
        sparkReporter.config().setEncoding("utf-8");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Environment", System.getProperty("env", "QA"));
        extent.setSystemInfo("Tester", "Automation Team");
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));

        return extent;
    }

    public static synchronized ExtentReports getExtent() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    /**
     * Set ExtentTest for current thread - THREAD SAFE
     */
    public static void setTest(ExtentTest extentTest) {
        test.set(extentTest);
    }

    /**
     * Get ExtentTest for current thread - THREAD SAFE
     */
    public static ExtentTest getTest() {
        return test.get();
    }

    /**
     * Remove ThreadLocal test to prevent memory leaks
     */
    public static void unload() {
        test.remove();
    }
}

