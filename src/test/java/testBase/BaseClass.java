package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseClass {

    public static WebDriver driver;
    public Logger logger;
    public Properties p;

    @BeforeClass(groups = {"Master","Sanity","Regression"})
    @Parameters({"os","browser"})
    public void setup(String os, String br) throws IOException {

        // Load config.properties
        FileReader file = new FileReader(".//src//test//resources//config.properties");
        p = new Properties();
        p.load(file);

        // Initialize Logger
        logger = LogManager.getLogger(this.getClass());

        // =========================
        // Remote Execution (Docker Grid)
        // =========================
        if(p.getProperty("execution_env").equalsIgnoreCase("remote"))
        {
            switch(br.toLowerCase())
            {
                case "chrome":

                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");

                    driver = new RemoteWebDriver(
                            new URL("http://localhost:4444"),
                            chromeOptions);
                    break;

                case "firefox":

                    FirefoxOptions firefoxOptions = new FirefoxOptions();

                    driver = new RemoteWebDriver(
                            new URL("http://localhost:4444"),
                            firefoxOptions);
                    break;

                case "edge":

                    EdgeOptions edgeOptions = new EdgeOptions();

                    driver = new RemoteWebDriver(
                            new URL("http://localhost:4444"),
                            edgeOptions);
                    break;

                default:
                    System.out.println("No matching browser");
                    return;
            }
        }

        // =========================
        // Local Execution
        // =========================
        if(p.getProperty("execution_env").equalsIgnoreCase("local"))
        {
            switch(br.toLowerCase())
            {
                case "chrome":
                    driver = new ChromeDriver();
                    break;

                case "edge":
                    driver = new EdgeDriver();
                    break;

                case "firefox":
                    driver = new FirefoxDriver();
                    break;

                default:
                    System.out.println("No matching browser");
                    return;
            }
        }

        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get(p.getProperty("appURL"));
        driver.manage().window().maximize();
    }

    @AfterClass(groups = {"Master","Sanity","Regression"})
    public void tearDown()
    {
        driver.quit();
    }

    // =========================
    // Random Data Methods
    // =========================

    public String randomeString()
    {
        return RandomStringUtils.randomAlphabetic(5);
    }

    public String randomeNumber()
    {
        return RandomStringUtils.randomNumeric(10);
    }

    public String randomAlphaNumeric()
    {
        String str = RandomStringUtils.randomAlphabetic(3);
        String num = RandomStringUtils.randomNumeric(3);
        return (str + "@" + num);
    }

    // =========================
    // Screenshot Method
    // =========================

    public String captureScreen(String tname) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

        TakesScreenshot ts = (TakesScreenshot) driver;
        File sourceFile = ts.getScreenshotAs(OutputType.FILE);

        String targetFilePath = System.getProperty("user.dir") + "\\screenshots\\" + tname + "_" + timeStamp + ".png";

        File targetFile = new File(targetFilePath);

        sourceFile.renameTo(targetFile);

        return targetFilePath;
    }
}