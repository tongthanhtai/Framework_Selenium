package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class EdgeDriverManager implements BrowserDriver{
    @Override
    public WebDriver initDriver(Object options) {
        if (!(options instanceof EdgeOptions)) {
            throw new IllegalArgumentException("Invalid options for Edge");
        }

        System.out.println("✅ Initializing Edge driver for: "
                + System.getProperty("os.name") + " ("
                + System.getProperty("os.arch") + ")");

        return new EdgeDriver((EdgeOptions) options);
    }
}
