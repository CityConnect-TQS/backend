package pt.ua.deti.tqs.backend.functional.client;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {
    private WebDriver driver;

    @Given("I navigate to {string}")
    public void iNavigateTo(String url) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");

        driver = new FirefoxDriver(options);
        driver.get(url);
    }

    @And("I click on the avatar")
    public void iClickOnTheAvatar() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("avatarBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("avatarBtn")).click();
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

    @When("I click on the login button")
    public void iClickOnTheLoginButton() {
        driver.findElement(By.id("loginBtn")).click();
    }

    @When("I click on the sign-up button")
    public void iClickOnTheSignUpButton() {
        driver.findElement(By.id("signUpBtn")).click();
    }

    @Then("I should be logged in as {string}")
    public void iShouldBeLoggedIn(String name) {
        assertThat(driver.findElement(By.id("userText")).getText()).isEqualTo("Signed in as" + name);
    }

    @And("I am logged out")
    public void iAmLoggedOut() {
        assertThat(driver.findElements(By.id("userText"))).isEmpty();
    }
}