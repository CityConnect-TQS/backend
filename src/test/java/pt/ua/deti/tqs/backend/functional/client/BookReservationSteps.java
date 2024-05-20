package pt.ua.deti.tqs.backend.functional.client;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

public class BookReservationSteps {
    
    private WebDriver driver;

    @Given("I am at the url {string}")
    public void iNavigateTo(String url) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");

        driver = new FirefoxDriver(options);
        driver.get(url);
    }

    @When("I click on the first trip that shows up")
    public void iClickOnTheFirstTrip() {
        driver.findElement(By.cssSelector("#trips>div:nth-child(1)")).click();
    }

    @Then("I should be redirected to {string} page")
    public void iShouldBeRedirectedTo(String title) {
        assertThat(driver.findElement(By.id("pageTitle")).getText()).isEqualTo(title);
    }
}
