package pages;

import com.aventstack.extentreports.ExtentTest;
import common.BaseTest;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import utils.Bots.DiscordBotImageSender;
import utils.Bots.DiscordBotMessageSender;
import utils.FileUtil.Config;
import utils.FileUtil.ExtentTestManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasePage {

    protected WebDriver driver;
    protected Logger log;
    protected Duration timeout;
    protected String baseUrl;

    public BasePage(WebDriver driver) {
        Map<String, Object> baseConfig = Config.getInstance(Config.InstanceType.CONFIG).getAllConfig();

        this.driver = driver;
        this.log = LogManager.getLogger("selenium");
        this.timeout = Duration.ofSeconds((int) ((Map<String, Object>) baseConfig.get("base")).get("timeout"));
        this.baseUrl = (String) ((Map<String, Object>) baseConfig.get(BaseTest.getSelectTestEnv())).get("url");
    }

    public void openPageUrl() {
        driver.get(this.baseUrl);
    }

    public String getPageUrlByKey(String key) {
        Map<String, Object> pageUrls = (Map<String, Object>) Config.getInstance(Config.InstanceType.CONFIG).getEnvConfig().get("pageUrls");
        return (String) pageUrls.get(key);
    }

    public String getEnvInfo(String key) {
        return (String) Config.getInstance(Config.InstanceType.CONFIG).getEnvConfig().get(key);
    }

    public String getCredentialInfo(String key) {
        return (String) Config.getInstance(Config.InstanceType.CREDENTIAL).getEnvConfig().get(key);
    }

    public List<WebElement> getElements(WebDriver driver, By locator) {
        return driver.findElements(locator);
    }

    public void clickElement(WebDriver driver, By locator) {
        WebElement element = driver.findElement(locator);
        try {
            element.click();
        } catch (Exception actionsException) {
            System.out.println("Try clicking with JS for element: " + locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    public WebElement randomElement(WebDriver driver, By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            if (elements.isEmpty()) {
                System.out.println("No elements found with locator: " + locator.toString());
                return null;
            }
            Random rand = new Random();
            int index = rand.nextInt(elements.size());
            return elements.get(index);
        } catch (Exception e) {
            System.out.println("Cannot get random element. Error: " + e.getMessage());
            return null;
        }
    }

    public void clickRandomElement(By locator) {
        WebElement element = randomElement(driver, locator);
        try {
            element.click();
        } catch (Exception ElementClickInterceptedException) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    public void waitForPresenceOfElements(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, this.timeout);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public void waitForElementDisappear(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, this.timeout);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForElementAppear(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, this.timeout);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public void waitForProgressBarDisappear() {
        By PROGRESS_BAR = By.xpath("//div[contains(@class, 'nuxt-loading-indicator') and contains(@style, 'transform: scaleX(1);')]");
        waitForElementDisappear(driver, PROGRESS_BAR);
    }

    public void waitForUrlMatch(String expectedUrl) {
        WebDriverWait wait = new WebDriverWait(driver, this.timeout);
        wait.until(ExpectedConditions.urlToBe(expectedUrl));
    }

    public int sendGetRequest(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public int getUrlStatusCode(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "Mozilla/5.0").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public String getRequestBody(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public ArrayList<String> getWindowHandles() {
        return new ArrayList<String> (driver.getWindowHandles());
    }

    public void switchToTab(ArrayList<String> tabs, int index) {
        driver.switchTo().window(tabs.get(index));
    }

    public void switchToDefaultTab() {
        List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
        if (!windowHandles.isEmpty()) {
            driver.switchTo().window(windowHandles.get(0));
        } else {
            System.out.println("No window handles available to switch.");
        }
    }

    public boolean isNewTabOpened() throws InterruptedException {
        int count = 0;
        while (count < 10) {
            int windowsNumber = getWindowHandles().size();
            if (windowsNumber > 1) {
                return true;
            } else {
                count++;
                System.out.printf("Only one window found, no new tab opened. Try %d\n", count);
                Thread.sleep(2000);
            }
        }
        return false;
    }

    public void switchToIframe(WebDriver driver, By locator) {
        WebElement iframe = driver.findElement(locator);
        driver.switchTo().frame(iframe);
    }

    public String getTextElement(WebDriver driver, By locator) {
        return driver.findElement(locator).getText();
    }

    public List<String> getTextElements(WebDriver driver, By locator) {
        List<String> texts = new ArrayList<>();
        List<WebElement> elementsList = driver.findElements(locator);
        for (WebElement webElement : elementsList) {
            texts.add(webElement.getText());
        }
        return texts;
    }

    public List<String> getVisibleTextElements(WebDriver driver, By locator) {
        List<String> texts = new ArrayList<>();
        List<WebElement> elementsList = driver.findElements(locator);
        for (WebElement webElement : elementsList) {
            if (webElement.isDisplayed()) {
                texts.add(webElement.getText());
            }
        }
        return texts;
    }

    public void inputText(WebDriver driver, By locator, String text) {
        try {
            WebElement element = driver.findElement(locator);
            element.sendKeys(text);
        } catch (Exception e) {
            log.info("Cannot input context: " + text + " to element " + locator + "\nError: " + e.getMessage());
        }
    }

    public void scrollToTopPage(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }

    public void scrollToBottomPage() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void scrollUntilElementIsVisible(By locator) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, this.timeout);
        for (int i = 1; i <= 10; i++) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    return;
                }
            } catch (Exception e) {
                System.out.println("Scrolled " + i + " times...");
            }
            js.executeScript("window.scrollBy(0, 500);");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        wait.until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();
        });
    }

    public void takeScreenshots(WebDriver driver, String fileWithPath) throws IOException {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver argument cannot be null");
        }
        TakesScreenshot scrShot = ((TakesScreenshot) driver);
        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
        File DestFile = new File(fileWithPath);
        FileUtils.copyFile(SrcFile, DestFile);
    }

    public String getAttributeValue(By locator, String attribute) {
        WebElement element = driver.findElement(locator);
        return element.getAttribute(attribute);
    }

    public boolean isDisplayElement(WebDriver driver, By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void takeScreenshotAndSendReport(ITestResult result, String resultMessage) throws IOException {
        String pathToScreenshot = (String) Config.getInstance(Config.InstanceType.CONFIG).getEnvConfig().get("imageReportPath");
        takeScreenshots(driver, pathToScreenshot);
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.info(resultMessage);
        }
    }

    public void takeScreenshotAndSendDiscord(String message) throws IOException, InterruptedException {
        DiscordBotMessageSender discordBotMessageSender = new DiscordBotMessageSender();
        DiscordBotImageSender discordBotImageSender = new DiscordBotImageSender();
        String pathToScreenshot = (String) Config.getInstance(Config.InstanceType.CONFIG).getEnvConfig().get("imageReportPath");
        takeScreenshots(driver, pathToScreenshot);
        discordBotMessageSender.sendMessageToGroupPC(message);
        discordBotImageSender.sendImageToGroup(pathToScreenshot);
    }

    public boolean verifyCurrentUrl(String expectedUrl) {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl.equals(currentUrl);
    }

    public String getUrlTitle() {
        return driver.getTitle();
    }

    public String getValidUrlAfterWaiting() {
        String url = null;
        int count = 0;
        while (count < 5) {
            url = driver.getCurrentUrl();
            if (!"about:blank".equals(url)) {
                return url;
            }
            count++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted, failed to complete getValidUrlAfterWaiting");
                break;
            }
        }
        return url;
    }

    public String getMatchData(String regex, String contents) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contents);
        if (matcher.find()) {
            String title = matcher.group(1);
            System.out.println("Title: " + title);
            return title;
        } else {
            System.out.println("Not found contents matched with pattern: " + regex);
            return null;
        }
    }

    public boolean isValidUrlAndRequestSuccessful(String url) throws IOException, InterruptedException {
        if (!url.isEmpty() && !url.equals("about:blank")) {
            int statusCode = getUrlStatusCode(url);
            System.out.printf("=> Url status code: %d \n=> Url: %s", statusCode, url);
            return statusCode == 200;
        } else {
            return false;
        }
    }

    public void setPageLoadTimeout(int seconds) {
        driver.manage().timeouts().pageLoadTimeout(seconds, TimeUnit.SECONDS);
    }

    public void print(String text) {
        System.out.println(text);
    }

    public void log(String level, String message) {
        switch (level.toLowerCase()) {
            case "info": {
                this.log.info(message);
                break;
            }
            case "warn": {
                this.log.warn(message);
                break;
            }
            case "error": {
                this.log.error(message);
                break;
            }
            case "debug": {
                this.log.debug(message);
                break;
            }
            default:
                this.log.info(message);
        }
    }
}
