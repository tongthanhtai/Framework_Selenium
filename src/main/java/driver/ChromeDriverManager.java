package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverManager implements BrowserDriver{

    @Override
    public WebDriver initDriver(Object options) {
        if (options instanceof ChromeOptions) {
            WebDriverManager.chromedriver().setup();
            return new ChromeDriver((ChromeOptions) options);
        } else {
            throw new IllegalArgumentException("Invalid options for ChromeDriver");
        }
    }
}
