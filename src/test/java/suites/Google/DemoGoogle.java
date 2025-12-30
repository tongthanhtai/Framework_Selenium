package suites.Google;

import common.BaseTest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.Google.HomePageLogin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DemoGoogle extends BaseTest {
    ITestResult result = Reporter.getCurrentTestResult();
    @Test (description = "Demo Test google search")
    public void demoTestGoogleSearch() throws InterruptedException {
        HomePageLogin homePageLogin = new HomePageLogin();
        homePageLogin.loginToHomePage(driver);
    }
}
