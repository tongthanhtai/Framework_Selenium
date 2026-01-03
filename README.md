# Selenium Automation Framework
This is a Selenium-based automation testing framework built with Java and TestNG. It provides flexibility in running automated test cases by offering different execution modes such as running tests in parallel across multiple browsers, running tests on a single browser, and executing tests on a Selenium Grid.
## Key Features
- **Multiple Execution Modes**: Choose from various test execution options:
    - **Parallel Testing**: Execute a single test case simultaneously on multiple browsers.
    - **Single Browser Testing**: Run tests sequentially on a single browser of your choice.
    - **Selenium Grid Support**: Execute tests on a Selenium Grid for distributed testing across multiple machines.
- **Cross-Browser Testing**: Easily configure tests to run on different browsers such as Chrome, Firefox, etc.
- **TestNG Integration**: Leverage the power of TestNG for test configuration, parallel execution, reporting, and test grouping.
- **Customizable Configuration**: Set up and customize browsers, environments, and grid URLs through a configuration file (`env.xml` in config package).
- **Modular Design**: The framework is designed in a modular way, making it easy to add new test cases, extend functionality, and integrate with CI/CD pipelines.
## Getting Started
### Prerequisites
1. **Java JDK 8 or above**
2. **Maven** (for managing dependencies)
3. **Selenium WebDriver** (ChromeDriver, FirefoxDriver, etc.)
4. **TestNG**
5. **Selenium Grid** (Optional: If you want to execute tests on a grid)
### Installation
1. Setup environment:
    - JAVA_HOME: jdk21
    - MAVEN_HOME: apache-maven-3.9.9
2. Clone the repository:
    ```bash
   cd framework_java
   git clone https://github.com/framework_java.git
   
3. Install the required dependencies:
    ```bash
   mvn clean install
4. Download the necessary WebDriver binaries (e.g., `chromedriver`, `firefox`) and ensure they are added to the system path.
### Framework Structure
```
core-framework-selenium-java-testng
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── common              # Common utilities and helper methods (e.g., initlialize driver)
│   │   │   ├── config              # Configuration-related classes (e.g., properties, yml, etc.)
│   │   │   ├── data                # Data handling (e.g., data providers, etc.)
│   │   │   ├── driver              # Driver Factory management
│   │   │   ├── pages               # Page Object Model (POM) classes representing web pages
│   │   │   └── utils               # General utility classes (e.g., listener, file readers, etc.)
│   │   └── resources               # Resources for the main application (e.g., log4j.xml, etc.)
│   ├── test
│   │   ├── java
│   │   │   └── TestSuites          # Test suite classes defining test groups and flow
│   │   ├── runner
│   │   │   ├── RunOnSingleBrowser  # Classes to execute tests on a single browser
│   │   │   └── RunOnMultiBrowsers  # Classes to execute tests in parallel on multiple browsers
│   │   └── testdata                # Test data files used in test cases
│   └── output                      # Default folder where reports, logs are generated
│            ├── logs
│            └── reports
├── pom.xml                         # Maven POM file for managing dependencies and build process
├── .env                            # Environment variables for configuration
├── README.md                       # Project documentation
└── .gitignore                      # Git ignore file to exclude unnecessary files from version control
```
### Configuration
Customize `testng.xml` (e.g., `RunAllTestSuites.xml`, `RunAllTestSuitesMultiBrowsers`) to specify your test suite, browser configurations, parallel execution settings, etc.
**Example:**
```xml
    <test name="Test Chrome">
        <parameter name="browser" value="CHROME"></parameter>
        <classes>
            <class name="TestSuites.Home.TestDemo"/>
        </classes>
    </test>
```
### grid.properties (For Selenium Grid)
You can configure the Selenium Grid Hub and Node URLs via a property file or directly in the code.
**Example:**
```properties
grid.hub.url=http://localhost:4444/wd/hub
```
### Running Tests
#### Run Single Browser Tests
1. In the `env.yml` file, you need to modify the following settings:
```yaml
base:
  browser: CHROME
  timeout: 30
  headless: false
  multi-browser: false
  grid: false
  env: 
    - pro-env-google
    - pro-env-youtube
    - ...
  credentials: 
    - test-credentials
    - pro-credentials-google
    - ...
prod-env:
  url: https://www.namebrand.com/
  imageReportPath: src/reports/ExtentReports/screenshots/output_image.png
  reportPath: src/output/reports/ExtentReports/namebrand-extent-report.html
```
2. Run local and test
    ```bash
   mvn clean install -Pgoogle -Denv=prod-env-google -Dcredentials=test-credentials
    ```
   
   <b> * Note Argument </b>: <br><br>
   -Pgoogle: Argument ID in file POM <br>
   -Denv=prod-env-google: Argument env in file yml <br>
   -Dcredentials=test-credentials: Argument credentials in file yml <br><br>

3. Run appropriate TestSuites xml file (e.g, `RunOnSingleBrowser` package)
#### Run Parallel Tests on Multiple Browsers
1. In the `env.yml` file, you need to modify the following settings:
```yaml
base:
  browser: CHROME
  timeout: 30
  headless: false
  multi-browser: true
  grid: false
  env:
  credentials: test-credentials
prod-env:
  url: https://www.youtube.com/
  imageReportPath: src/reports/ExtentReports/screenshots/output_image.png
  reportPath: src/output/reports/ExtentReports/nhatban-extent-report.html
```
2. Run appropriate TestSuites xml file (e.g, `RunOnMultiBrowsers` package)
#### Run Tests on Selenium Grid
Update later!!!
### Reports
This report uses extent HTML reports after each test execution. You can find the reports in the output folder after the test run.
### Browser Support
- Google Chrome
- Mozilla Firefox
- Configurable for additional browsers via WebDriver setup.
### CI/CD Integration
This framework can be easily integrated with CI/CD pipelines such as Jenkins, GitLab CI, etc. I will mention later as soon as the setup complete.
### Contributing
1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Open a pull request with a clear description of your changes.