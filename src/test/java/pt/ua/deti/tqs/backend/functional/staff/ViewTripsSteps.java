package pt.ua.deti.tqs.backend.functional.staff;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ViewTripsSteps {
    private final WebDriver driver = StaffCucumberTest.getDriver();

    @And("I should see a list of trips")
    public void iShouldSeeAListOfTrips() {
        assertThat(driver.findElement(By.id("trips")).isDisplayed()).isTrue();
    }


}
