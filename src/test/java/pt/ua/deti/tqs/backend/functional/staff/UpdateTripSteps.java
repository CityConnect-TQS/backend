package pt.ua.deti.tqs.backend.functional.staff;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UpdateTripSteps {

    private final WebDriver driver = StaffCucumberTest.getDriver();

    @And("I click on the edit button of the trip {int} witch is from {string} to {string}")
    public void iClickOnTheEditButtonOfTheTripWitchIsFromTo(int tripId, String origin, String destination) {
        assertThat(driver.findElement(By.id("departureName" +tripId)).getText()).isEqualTo(origin);
        assertThat(driver.findElement(By.id("arrivalName" +tripId)).getText()).isEqualTo(destination);
        driver.findElement(By.id("editTrip" + tripId)).click();
    }
    @And("I change the price from {double} to {double}")
    public void iChangeThePriceFromTo(double oldPrice, double newPrice) {
        assertThat(Double.parseDouble(driver.findElement(By.id("priceInput")).getAttribute("value"))).isEqualTo(oldPrice);
        driver.findElement(By.id("priceInput")).clear();
        driver.findElement(By.id("priceInput")).sendKeys(String.valueOf(newPrice));
    }

    @And("I search for trips in {string}")
    public void iSearchForTrips(String city) {
        driver.findElement(By.id("searchTrips")).click();
        driver.findElement(By.id("searchTrips")).sendKeys(city);
    }

    @And("I click on the save button")
    public void iClickOnTheSaveButton() {
        driver.findElement(By.id("saveTripBtn")).click();
    }

    @And("I should see a edit button")
    public void iShouldSeeAEditButton() {
        assertThat(driver.findElement(By.id("editTripBtn")).isDisplayed()).isTrue();
    }

    @And("I change the arrival city to {string}")
    public void iChangeTheArrivalCityTo(String arrivalCity) {
        driver.findElement(By.id("arrivalCity")).click();
        driver.findElement(By.id("arrival" + arrivalCity)).click();
    }

    @Then("I should see {double} as the price of the trip")
    public void iShouldSeeAsThePriceOfTheTrip(double price) {
        assertThat(Double.parseDouble(driver.findElement(By.id("priceInput")).getAttribute("value"))).isEqualTo(price);
    }

    @Then("I should see a error message saying {string}")
    public void iShouldSeeAErrorMessageSaying(String message) {
        assertThat(driver.findElement(By.id("errorMessageSpan")).getText()).isEqualTo(message);
    }


}
