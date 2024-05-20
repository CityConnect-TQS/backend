package pt.ua.deti.tqs.backend.functional.client;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.By;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class BookReservationSteps {
    
    private final WebDriver driver = ClientCucumberTest.getDriver();
    private Wait<WebDriver> wait;

    @Given("I am at the url {string}")
    public void iAmAt(String url) {
        driver.get(url);
    }

    @Given("I am signed in")
    public void iAmSignedIn() throws InterruptedException{
        wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("avatarBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("avatarBtn")).click();
        driver.findElement(By.id("loginBtn")).click();
        driver.findElement(By.id("email")).sendKeys("admin@gmail.com");
        driver.findElement(By.id("password")).sendKeys("admin123");
        driver.findElement(By.id("loginBtn")).click();
    }

    @When("I click on the first trip that shows up")
    public void iClickOnTheFirstTrip() throws InterruptedException{
    	TimeUnit.SECONDS.sleep(3);
        driver.findElement(By.id("tripCard41")).click();
    }

    @Then("I should be redirected to {string} page")
    public void iShouldBeRedirectedTo(String title) throws InterruptedException{
    	TimeUnit.SECONDS.sleep(1);
        assertThat(driver.findElement(By.id("pageTitle")).getText()).isEqualTo(title);
    }

    @Then("the submit button should be disabled")
    public void theSubmitButtonShouldBeDisabled() {
        assertThat(driver.findElement(By.id("submitBtn")).isEnabled()).isFalse();
    }

    @When("I click on seat {string} and seat {string}")
    public void iClickOnSeat(String seat1, String seat2) throws InterruptedException{
        driver.findElement(By.id("seat" + seat1)).click();
        driver.findElement(By.id("seat" + seat2)).click();
    }

    @When("I click on submit button")
    public void iClickOnSubmitButton() throws InterruptedException{
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("submitBtn")).click();
    }

    @Then("I should see the message {string}")
    public void iShouldSeeTheMessage(String message) throws InterruptedException{
        TimeUnit.SECONDS.sleep(1);
        assertThat(driver.findElement(By.id("confirmText")).getText()).isEqualTo(message);
    }
    
    @Then("the seats {string} and {string} should appear")
    public void theSeatsShouldAppear(String seat1, String seat2) {
    	assertThat(driver.findElement(By.id("selectedSeats")).getText()).isEqualTo(seat1 + ", " + seat2);
    }
}
