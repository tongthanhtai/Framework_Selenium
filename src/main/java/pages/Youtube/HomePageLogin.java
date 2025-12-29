package pages.Youtube;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import pages.Youtube.HomeManagement.HomePage;

import java.util.ArrayList;

public class HomePageLogin {

    @BeforeMethod
    public void loginToHomePage(WebDriver driver) throws InterruptedException {
        HomePage homePage = new HomePage(driver);
        homePage.openPageUrl();
        homePage.closePopUp();
        homePage.enterLoginInfoOnHeader(homePage.getCredentialInfo("accountNameRefund1Point5Percent"), homePage.getCredentialInfo("accountPassRefund1Point5Percent"));
        homePage.clickLoginButtonOnHeader();
        homePage.closePopUp();
    }
}
