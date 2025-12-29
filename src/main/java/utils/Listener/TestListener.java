package utils.Listener;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import common.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.Bots.DiscordBotMessageSender;
import utils.FileUtil.Config;
import utils.FileUtil.ExtentReportManager;
import utils.FileUtil.ExtentTestManager;
import utils.GoogleApi.GoogleApi;
import utils.GoogleApi.GoogleDriveApi;
import utils.PathUtil.PathUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class TestListener implements ITestListener {
    //Create telegram bot object
    DiscordBotMessageSender discordBotMessageSender = new DiscordBotMessageSender();
    String testSuite;
    //Declare to Category tests
    private int passedCounter = 0;
    private int failedCounter = 0;
    private int skippedCounter = 0;

    //Declare test suites info
    private static int totalSuites = 0;
    private static int completedSuites = 0;
    private List<String> testResults = new ArrayList<>();

    //Get current datetime
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    String formattedDateTime = now.format(formatter);

    //Flag to check project start running
    boolean isProjectStart = false;

    //Get test information
    private static String getTestMethodName(ITestResult result) {
        return result.getMethod().getConstructorOrMethod().getName();
    }

    public String getTestName(ITestResult result) {
        return result.getTestName() != null ? result.getTestName() : result.getMethod().getMethodName();
    }

    public String getTestDescription(ITestResult result) {
        return result.getMethod().getDescription() != null ? result.getMethod().getDescription() : getTestName(result);
    }

    @Override
    public void onStart(ITestContext context) {
        if (!isProjectStart) {
            testSuite = context.getCurrentXmlTest().getName();
            try {
                isProjectStart = true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (totalSuites == 0) {
            totalSuites = context.getSuite().getXmlSuite().getTests().size();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTestManager.saveToReport(getTestName(result), getTestDescription(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
//        ITestContext context = result.getTestContext();
//        ExtentTestManager.addScreenshot(result.getName(), context);
        ExtentTestManager.logMessage(Status.PASS, result.getName() + " PASSED");
        String resultMessage = (String) result.getAttribute("resultMessage");
        ExtentTest test = ExtentTestManager.getTest();
        test.pass(resultMessage);
        passedCounter++;
        testResults.add(getTestMethodName(result) + " PASSED");
        System.out.println("==> Test passed: " + getTestMethodName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ITestContext context = result.getTestContext();
        ExtentTestManager.addScreenshot(result.getName(), context);
        ExtentTestManager.logMessage(Status.FAIL, result.getThrowable().toString());
        ExtentTestManager.logMessage(Status.FAIL, result.getName() + " FAILED");

//        Log.info(getTestMethodName(iTestResult) + " test is failed.");
        WebDriver driver = (WebDriver) result.getTestContext().getAttribute("WebDriver");
        failedCounter++;
        if (driver != null) {
            System.out.println("Screenshot captured for test case: " + getTestMethodName(result));
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } else {
//            Log.error("Driver is null. Unable to capture screenshot.");
        }
        testResults.add(getTestMethodName(result) + " test is failed.");
        System.out.println("==> TEST FAILED: " + getTestMethodName(result));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFinish(ITestContext context) {
        String CREDENTIALS_PATH = "src/main/java/config/credentials.json";

        Map<String, Object> credential = Config.getInstance(Config.InstanceType.CREDENTIAL).getEnvConfig();
        String googleDriveId = (String) credential.get("googleDriveId");

        String startCheck = String.format(">>>>>>>>>>>>>>> START - CHECK BRAND %s <<<<<<<<<<<<<<<\nTime: %s", testSuite, formattedDateTime);
        String endCheck = String.format(">>>>>>>>>>>>>>> END - CHECK BRAND %s <<<<<<<<<<<<<<<", testSuite);
        GoogleDriveApi driveApi = new GoogleDriveApi(CREDENTIALS_PATH);
        try {
            Map<String, Object> env = Config.getInstance(Config.InstanceType.CONFIG).getEnvConfig();
            String reportPath = PathUtil.normalizePath((String) env.get("reportPath"));

            ExtentReportManager.getExtentReports().flush();

            String driveReportLink = driveApi.uploadFile(reportPath, googleDriveId);
            completedSuites++;
            if (completedSuites == totalSuites) {
                discordBotMessageSender.sendMessageToGroupPC(startCheck);

                if (driveReportLink != null && !driveReportLink.isEmpty()) {
                    discordBotMessageSender.sendMessageToGroupPC(
                            String.format("\uD83D\uDCCA [Report Daily - Brand %s](%s)", testSuite, driveReportLink)
                    );
                }
                discordBotMessageSender.sendMessageToGroupPC(endCheck);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
