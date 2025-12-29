package utils.FileUtil;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtentTestManager {
    static Map<Integer, ExtentTest> extentTestMap = new HashMap<>();
    static ExtentReports extent = ExtentReportManager.getExtentReports();

    public static ExtentTest getTest() {
        return extentTestMap.get((int) Thread.currentThread().getId());
    }

    public static synchronized ExtentTest saveToReport( String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        extentTestMap.put((int) Thread.currentThread().getId(), test);
        return test;
    }

    public static void addScreenshot(String message, ITestContext context) {
        WebDriver driver = (WebDriver) context.getAttribute("driver");
        //Create screenshot
        String base64Image = "data:image/png;base64," + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        getTest().log(Status.INFO, message,
                MediaEntityBuilder.createScreenCaptureFromBase64String(base64Image).build());
    }

    public static void logMessage(Status status, String message) {
        getTest().log(status, message);
    }
}
