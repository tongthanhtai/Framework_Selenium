package driver;

public class DriverFactory {

    public static BrowserDriver getDriver(BrowserTypes browser) {
        switch (browser) {
            case CHROME:
                return new ChromeDriverManager();
            case FIREFOX:
                return new FirefoxDriverManager();
            default:
                break;
        }
        return null;
    }
}
