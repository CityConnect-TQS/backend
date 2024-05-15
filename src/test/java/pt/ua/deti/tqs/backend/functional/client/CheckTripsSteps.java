package pt.ua.deti.tqs.backend.functional.client;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CheckTripsSteps {
    private WebDriver driver;

    @When("I navigate to {string}")
    public void iNavigateTo(String url) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");

        driver = new FirefoxDriver(options);
        driver.get(url);
    }

    @When("I fill in departure with {int}")
    public void iFillInDepartureWith(int number) throws InterruptedException {
        Thread.sleep(1000);
        driver.findElement(By.id("departure")).click();
        driver.findElement(By.id("departure" + number)).click();

    }

    @And("I fill in arrival with {int}")
    public void iFillInArrivalWith(int number) {
        driver.findElement(By.id("arrival")).click();
        driver.findElement(By.id("arrival" + number)).click();
    }

    @And("I fill in date with {string}")
    public void iFillInDateWith(String date) {
        driver.findElement(By.id("departureTimeInput")).sendKeys(date);
    }

    @And("I search for trips")
    public void iSearchForTrips() throws InterruptedException {
        driver.findElement(By.id("searchBtn")).click();
    }


    @Then("I should see a list of trips on date {string}")
    public void iShouldSeeAListOfTripsOnDate(String date) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.findElement(By.id("trips")));

        String text = driver.findElement(By.id("trips")).getText();
        assert text.contains(date);

    }

    @Then("I should see a message saying there {string}")
    public void iShouldSeeAMessageSayingThere(String message) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.findElement(By.id("message")));

        String text = driver.findElement(By.id("message")).getText();
        assert text.contains(message);
    }

    @Then("I should see a list of trips")
    public void i_should_see_a_list_of_trips() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.findElement(By.id("trips")));
    }

}
