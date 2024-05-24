package pt.ua.deti.tqs.backend.functional.staff;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class createTripSteps {

    private final WebDriver driver = StaffCucumberTest.getDriver();
    private int trips = 0;

    @And("I check how many trips are in the database")
    public void iCheckHowManyTripsAreInTheDatabase() {
        trips = Integer.parseInt(driver.findElement(By.id("numTrips")).getText().split(" ")[1]);
    }
    @And("I click on the add trip button")
    public void iClickOnTheAddTripButton() {
        driver.findElement(By.id("createTripBtn")).click();
    }
    @And("I fill in the departure city with {string}")
    public void iFillInTheDepartureCityWith(String departureCity) {
        driver.findElement(By.id("departureCity")).click();
        driver.findElement(By.id("departure-" + departureCity)).click();
    }
    @And("I fill in the arrival city with {string}")
    public void iFillInTheArrivalCityWith(String arrivalCity) {
        driver.findElement(By.id("arrivalCity")).click();
        driver.findElement(By.id("arrival-" + arrivalCity)).click();
    }

    @And("I fill in the departure date with {string} and time with {string}")
    public void iFillInTheDepartureDateWithAndTimeWith(String date, String time) {
        String[] partsDate = date.split("/");

        driver.findElement(By.cssSelector("#departureTime>div>div>div:nth-child(1)")).sendKeys(partsDate[0]);
        driver.findElement(By.cssSelector("#departureTime>div>div>div:nth-child(3)")).sendKeys(partsDate[1]);
        driver.findElement(By.cssSelector("#departureTime>div>div>div:nth-child(5)")).sendKeys(partsDate[2]);

        String[] parts = time.split("\\s+");
        String[] partsTime = parts[0].split(":");
        driver.findElement(By.cssSelector("#departureTime>div>div>div:nth-child(7)")).sendKeys(partsTime[0]);
        driver.findElement(By.cssSelector("#departureTime>div>div>div:nth-child(9)")).sendKeys(partsTime[1]);
        driver.findElement(By.cssSelector("#departureTime>div>div>div:nth-child(11)")).sendKeys(parts[1]);
    }
    @And("I fill in the arrival date with {string} and time with {string}")
    public void iFillInTheArrivalDateWithAndTimeWith(String date, String time) {
        String[] partsDate = date.split("/");
        driver.findElement(By.cssSelector("#arrivalTime>div>div>div:nth-child(1)")).sendKeys(partsDate[0]);
        driver.findElement(By.cssSelector("#arrivalTime>div>div>div:nth-child(3)")).sendKeys(partsDate[1]);
        driver.findElement(By.cssSelector("#arrivalTime>div>div>div:nth-child(5)")).sendKeys(partsDate[2]);

        String[] parts = time.split("\\s+");
        String[] partsTime = parts[0].split(":");
        driver.findElement(By.cssSelector("#arrivalTime>div>div>div:nth-child(7)")).sendKeys(partsTime[0]);
        driver.findElement(By.cssSelector("#arrivalTime>div>div>div:nth-child(9)")).sendKeys(partsTime[1]);
        driver.findElement(By.cssSelector("#arrivalTime>div>div>div:nth-child(11)")).sendKeys(parts[1]);
    }
    @And("I fill in the price with {double}")
    public void iFillInThePriceWith(double price) {
        driver.findElement(By.id("price")).clear();
        driver.findElement(By.id("price")).sendKeys(String.valueOf(price));
    }
    @And("I choose the bus {int}")
    public void iChooseTheBus(int bus) {
        driver.findElement(By.id("bus")).click();
        driver.findElement(By.id("bus" + bus)).click();
    }
    @Then("I should have one more trip in the database")
    public void iShouldHaveOneMoreTripInTheDatabase() {

        assertThat(Integer.parseInt(driver.findElement(By.id("numTrips")).getText().split(" ")[1])).isEqualTo(trips + 1);
    }

    @Then("I should see an error message saying {string}")
    public void iShouldSeeAnErrorMessageSaying(String message) {
        assertThat(driver.findElement(By.id("spanError")).getText()).isEqualTo(message);
    }
}

