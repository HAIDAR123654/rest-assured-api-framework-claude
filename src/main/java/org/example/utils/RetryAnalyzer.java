package org.example.utils;


import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    // ThreadLocal to maintain separate retry count for each thread
    private final ThreadLocal<Integer> retryCount = ThreadLocal.withInitial(() -> 0);
    private static final int MAX_RETRY = 2;

    @Override
    public boolean retry(ITestResult result) {
        int currentCount = retryCount.get();
        if (currentCount < MAX_RETRY) {
            retryCount.set(currentCount + 1);
            return true;
        }
        retryCount.remove(); // Clean up
        return false;
    }
}
