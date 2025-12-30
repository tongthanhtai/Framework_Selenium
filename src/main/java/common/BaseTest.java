package common;

import driver.BrowserTypes;
import driver.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestContext;
import org.testng.annotations.*;
import utils.FileUtil.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseTest {

    protected static ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();
    protected WebDriver driver;
    protected String runTestConfig;
    protected Logger log;

    boolean multiBrowser;
    boolean grid;
    boolean headless;
    Config config;
    Map<String, Object> baseConfig;
    String selectTestEnv;
    String selectCredential;
    public BaseTest() {
        config = Config.getInstance(Config.InstanceType.CONFIG);
        baseConfig = (Map<String, Object>) config.getAllConfig().get("base");
        multiBrowser = (boolean) baseConfig.get("multi-browser");
        grid = (boolean) baseConfig.get("grid");
        headless = (boolean) baseConfig.get("headless");
        selectTestEnv = getSelectTestEnv();
        selectCredential = getSelectCredential();
    }

    public static String getSelectTestEnv() {
        Config config = Config.getInstance(Config.InstanceType.CONFIG);
        Map<String, Object> baseConfig = (Map<String, Object>) config.getAllConfig().get("base");
        List<String> envList = (List<String>) baseConfig.get("env");

        String selectedEnv = null;

        if (System.getProperty("env") != null) {
            selectedEnv = System.getProperty("env");
        } else if (System.getenv("ENVIRONMENT") != null) {
            selectedEnv = System.getenv("ENVIRONMENT");
        }

        if (selectedEnv != null && envList.contains(selectedEnv)) {
            System.out.println("Selected Environment: " + selectedEnv);
            return selectedEnv;
        }

        return envList.get(0);
    }

    public static String getSelectCredential() {
        Config config = Config.getInstance(Config.InstanceType.CONFIG);
        Map<String, Object> baseConfig = (Map<String, Object>) config.getAllConfig().get("base");
        List<String> credentialsList = (List<String>) baseConfig.get("credentials");

        String selectedCredential = null;

        if (System.getProperty("credentials") != null) {
            selectedCredential = System.getProperty("credentials");
        } else if (System.getenv("CREDENTIALS") != null) {
            selectedCredential = System.getenv("CREDENTIALS");
        }

        if (selectedCredential != null && credentialsList.contains(selectedCredential)) {
            return selectedCredential;
        }

        return credentialsList.get(0);
    }

    @BeforeTest
    public void prepareTestSetUp(ITestContext context) {
        config.loadEnvConfig(selectTestEnv);
        Config credential = Config.getInstance(Config.InstanceType.CREDENTIAL);
        Config.getInstance(Config.InstanceType.CREDENTIAL).loadEnvConfig(getSelectCredential());
        if (!multiBrowser && !grid) {
            runTestConfig = "local";
        } else if (multiBrowser && !grid) {
            runTestConfig = "multi-browser";
        } else if (!multiBrowser && grid) {
            runTestConfig = "grid";
        } else {
            throw new Error("[ERR] - Both multi-browser and grid configurations cannot be TRUE simultaneously");
        }
        context.setAttribute("runTestConfig", runTestConfig);
    }

    @Parameters("browser")
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional String browser, ITestContext context) {
        String testName = context.getCurrentXmlTest().getName();
        log = LogManager.getLogger(testName);
        String runTestConfig = (String) context.getAttribute("runTestConfig");
        switch (runTestConfig) {
            case "local":
                setUpLocalDriver();
                break;
            case "multi-browser":
                setUpMultiBrowserDriver(browser);
                break;
            case "grid":
                // Process run test on Selenium grid later
                break;
            default:
                throw new Error("[ERR] Please provide valid keyword how to run test");
        }
        context.setAttribute("driver", driver);
    }

    private void setUpLocalDriver() {
        String localBrowser = (String) baseConfig.get("browser");
        driver = createDriver(localBrowser);
        threadLocalDriver.set(driver);
    }

    private void setUpMultiBrowserDriver(String browser) {
        if (browser == null) {
            throw new Error("[ERR] Browser must be specified for multi-browser config");
        }
        driver = createDriver(browser);
        threadLocalDriver.set(driver);
    }

    private WebDriver createDriver(String browser) {
        Object options = null;
        switch (browser.toLowerCase()) {
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--start-maximized");
                edgeOptions.addArguments("--disable-extensions");
                edgeOptions.addArguments("--disable-notifications");

                edgeOptions.setExperimentalOption("excludeSwitches",
                        Arrays.asList("enable-automation"));
                edgeOptions.setExperimentalOption("useAutomationExtension", false);
                options = edgeOptions;
                break;
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-extensions");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.setExperimentalOption("excludeSwitches",
                        Arrays.asList("enable-automation"));
                chromeOptions.setExperimentalOption("useAutomationExtension", false);

                // Add the --disable-infobars argument
                chromeOptions.addArguments("--disable-infobars");
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");

                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                chromeOptions.setExperimentalOption("prefs", prefs);

                if (System.getenv("JENKINS_URL") != null || headless) {
                    chromeOptions.addArguments("window-size=1920,1080");
                    chromeOptions.addArguments("--headless");
                }
                options = chromeOptions;
                break;
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--disable-notifications");
                firefoxOptions.addArguments("--start-maximized");
                options = firefoxOptions;
                break;
            default:
                throw new Error("[ERR] Invalid browser type specified: " + browser);
        }
        return DriverFactory.getDriver(BrowserTypes.valueOf(browser)).initDriver(options);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        driver.quit();
        threadLocalDriver.remove();
    }
}
