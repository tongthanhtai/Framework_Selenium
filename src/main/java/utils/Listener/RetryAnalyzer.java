package utils.Listener;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int count  = 0;

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) {
            //Run the failed test 1 times
            int maxRetry = 1;
            if (count < maxRetry) {
                count++;
                iTestResult.setStatus(ITestResult.FAILURE);
                return true;
            }
        } else {
            iTestResult.setStatus(ITestResult.SUCCESS);
        }
        return false;
    }
}
