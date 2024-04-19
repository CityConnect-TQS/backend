package pt.ua.deti.tqs.backend.functional;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TripSteps {
    private WebDriver driver;

    @Given("I navigate to {string}")
    public void iNavigateTo(String url) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");

        driver = new FirefoxDriver(options);
        driver.get(url);
    }

    @When("I click on the signup button")
    public void iClickOnSignupButton() {
        driver.findElement(By.id("signUpBtn")).click();
    }

    @And("I fill in username with {string}")
    public void iFillInUsernameWith(String username) {
        driver.findElement(By.id("username")).sendKeys(username);
    }

    @And("I fill in name with {string}")
    public void iFillInNameWith(String name) {
        driver.findElement(By.id("name")).sendKeys(name);
    }

    @And("I fill in email with {string}")
    public void iFillInEmailWith(String email) {
        driver.findElement(By.id("email")).sendKeys(email);
    }

    @And("I fill in password with {string}")
    public void iFillInPasswordWith(String password) {
        driver.findElement(By.id("password")).sendKeys(password);
    }

    @And("I click on the submit button")
    public void iClickOnTheSubmitButton() {
        driver.findElement(By.id("submitBtn")).click();
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

    @And("I search for trips")
    public void iSearchForTrips() throws InterruptedException {
        driver.findElement(By.id("searchBtn")).click();
    }

    @And("I choose trip {int}")
    public void iChooseTrip(int id) {
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.of(10L, ChronoUnit.SECONDS));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tripCard" + id)));
        }
        driver.findElement(By.id("tripCard" + id)).click();
    }

    @And("I book the trip")
    public void iBookTheTrip() {
        driver.findElement(By.id("buyBtn")).click();
    }

    @Then("I should see a success message")
    public void iShouldSeeASuccessMessage() {
        assertThat(driver.findElement(By.id("successText")).getText()).isEqualTo(
                "Your reservation has been created successfully.");
    }
}
