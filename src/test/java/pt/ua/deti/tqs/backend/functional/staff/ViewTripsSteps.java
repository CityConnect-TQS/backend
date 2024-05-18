package pt.ua.deti.tqs.backend.functional.staff;

import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pt.ua.deti.tqs.backend.functional.client.ClientCucumberTest;

public class ViewTripsSteps {
    private final WebDriver driver = ClientCucumberTest.getDriver();

    @Then("I should see a trip with departure {string} and destination {string}")
    public void iShouldSeeATripWithDepartureAndDestination(String departure, String destination) {
        driver.findElement(By.id("deparureName")).getText().equals(departure);
        driver.findElement(By.id("destinationName")).getText().equals(destination);

    }



}
