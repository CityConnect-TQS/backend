package pt.ua.deti.tqs.backend.functional.staff;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DeleteTripSteps {
    private final WebDriver driver = StaffCucumberTest.getDriver();

    @And("I click on the delete button of the trip {int} witch is from {string} to {string}")
    public void iClickOnTheDeleteButtonOfTheTripWitchIsFromTo(int tripId, String origin, String destination) {
        assertThat(driver.findElement(By.id("departureName" + tripId)).getText()).isEqualTo(origin);
        assertThat(driver.findElement(By.id("arrivalName" + tripId)).getText()).isEqualTo(destination);
        driver.findElement(By.id("deleteTrip-" + tripId)).click();
    }

    @And("I should see a modal with the title {string}")
    public void iShouldSeeAModalWithTheTitle(String title) {
        assertThat(driver.findElement(By.id("modalDeleteTripTitle")).getText()).isEqualTo(title);
    }

    @And("I click on the delete button")
    public void iClickOnTheDeleteButton() {
        driver.findElement(By.id("deleteBtn")).click();
    }

    @Then("I should see a success message with the text {string}")
    public void iShouldSeeASuccessMessageWithTheText(String message) {
        assertThat(driver.findElement(By.id("alertsChip")).getText()).isEqualTo(message);
    }


}
