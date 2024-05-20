package pt.ua.deti.tqs.backend.functional.client;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@SuppressWarnings("java:S2187")
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("pt/ua/deti/tqs/backend/functional/client")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "pt.ua.deti.tqs.backend.functional.client")
public class ClientCucumberTest {
    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("-headless");
            driver = new FirefoxDriver(options);
        }
        return driver;
    }
}
