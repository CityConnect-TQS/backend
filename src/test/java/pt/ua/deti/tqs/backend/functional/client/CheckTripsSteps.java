package pt.ua.deti.tqs.backend.functional.client;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.NoSuchElementException;

public class CheckTripsSteps {
    private final WebDriver driver = ClientCucumberTest.getDriver();

    @When("I fill in departure with {int}")
    public void iFillInDepartureWith(int number) throws InterruptedException {
        Thread.sleep(1000);
        driver.findElement(By.id("origin")).click();
        driver.findElement(By.id("departure" + number)).click();
    }

    @And("I fill in arrival with {int}")
    public void iFillInArrivalWith(int number) {
        driver.findElement(By.id("destination")).click();
        driver.findElement(By.id("arrival" + number)).click();
    }

    @And("I fill in date with {string}")
    public void iFillInDateWith(String date) {
        String[] parts = date.split("/");
        driver.findElement(By.cssSelector("#departureTime>div>div>div:nth-child(1)")).sendKeys(parts[0]);
        driver.findElement(By.cssSelector("#departureTime>div>div>div:nth-child(3)")).sendKeys(parts[1]);
        driver.findElement(By.cssSelector("#departureTime>div>div>div:nth-child(5)")).sendKeys(parts[2]);

    }

    @And("I search for trips")
    public void iSearchForTrips() throws InterruptedException {
        driver.findElement(By.id("searchBtn")).click();
    }

    @Then("I should see a list of trips on date {string} or after this date")
    public void iShouldSeeAListOfTripsOnDate(String date) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.findElement(By.id("trips")));

        String text = driver.findElement(By.id("trips")).getText();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date newDate = dateFormat.parse(date);

            dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String formattedDate = dateFormat.format(newDate);

            if (!text.contains(formattedDate)) {
                throw new NoSuchElementException("Date not found");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Then("I should see a message saying there {string}")
    public void iShouldSeeAMessageSayingThere(String message) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.findElement(By.id("message")));

        String text = driver.findElement(By.id("message")).getText();
        if (!text.contains(message)) {
            throw new NoSuchElementException("Message not found");
        }
    }

    @Then("I should see a list of trips")
    public void i_should_see_a_list_of_trips() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.findElement(By.id("trips")));
        driver.findElement(By.id("trips"));
    }

}
