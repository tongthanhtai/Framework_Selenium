package driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxDriverManager implements BrowserDriver {

    @Override
    public WebDriver initDriver(Object options) {
        if (options instanceof FirefoxOptions) {
            return new FirefoxDriver((FirefoxOptions) options);
        } else {
            throw new IllegalArgumentException("Invalid options for FirefoxDriver");
        }
    }
}
