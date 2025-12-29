package utils.FileUtil;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import utils.PathUtil.PathUtil;

import java.util.Map;

public class ExtentReportManager {

    private static ExtentReports extentReports;

    public synchronized static ExtentReports getExtentReports() {
        if (extentReports == null) {
            Map<String, Object> env = Config.getInstance(Config.InstanceType.CONFIG).getEnvConfig();
            ExtentSparkReporter reporter = new ExtentSparkReporter(PathUtil.normalizePath((String) env.get("reportPath")));
            extentReports = new ExtentReports();
            reporter.config().setTheme(Theme.DARK);
            reporter.config().setReportName("Auto Project");
            reporter.config().setEncoding("UTF-8");
            reporter.config().setCss(".badge-primary{background-color:#6c757d}");
            extentReports.attachReporter(reporter);

            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Author", "Rin & Joy");
        }
        return extentReports;
    }
}
