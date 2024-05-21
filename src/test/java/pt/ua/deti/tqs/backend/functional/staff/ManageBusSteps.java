package pt.ua.deti.tqs.backend.functional.staff;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ManageBusSteps {

    private final WebDriver driver = StaffCucumberTest.getDriver();

    private Integer numBus = 0;

    @And("I click on the buses button")
    public void iClickOnTheBusesButton() {
        driver.findElement(By.id("BusesNavBtn")).click();
    }

    @And("I click on the add bus button")
    public void iClickOnTheAddBusButton() {
        driver.findElement(By.id("addNewBtn")).click();
    }

    @Then("I should see a modal for create bus with the title {string}")
    public void iShouldSeeAModalForCreateBusWithTheTitle(String title) {
        assertThat(driver.findElement(By.id("headerModalCreateBus")).getText()).isEqualTo(title);
    }
    @And("I fill in the company name with {string}")
    public void iFillInTheCompanyNameWith(String companyName) {
        driver.findElement(By.id("companyInput")).clear();
        driver.findElement(By.id("companyInput")).sendKeys(companyName);
    }
    @And("I fill in the capacity with {string}")
    public void iFillInTheCapacityWith(String capacity) {
        driver.findElement(By.id("capacityInput")).clear();
        driver.findElement(By.id("capacityInput")).sendKeys(capacity);
    }

    @And("I click on the save bus button")
    public void iClickOnTheSaveBusButton() {
        driver.findElement(By.id("saveBusBtn")).click();
    }
    @And("I click on the add bus button on {string} company")
    public void iClickOnTheAddBusButtonOnCompany(String companyName) {
        driver.findElement(By.id(companyName + "AddNewBtn")).click();
    }

    @And("I check how many buses are on {string} company")
    public void iCheckHowManyBusesAreOnCompany(String companyName) {
        numBus =  driver.findElements(By.id(companyName + "Bus")).size();
    }

    @And("I click on the edit bus button on trip with id {int}")
    public void iClickOnTheEditBusButtonOnCompany(int id) {
        driver.findElement(By.id("editBusBtn" + id)).click();
    }
    @And("I click on the on delete bus button on trip with id {int}")
    public void iClickOnTheOnDeleteBusButtonOnTripWithId(int id) {
        driver.findElement(By.id("deleteBusBtn" + id)).click();
    }
    @Then("I should see a modal for delete bus with the title {string}")
    public void iShouldSeeAModalForDeleteBusWithTheTitle(String title) {
        assertThat(driver.findElement(By.id("headerModalDeleteBus")).getText()).isEqualTo(title);
    }

    @And("I click on the delete bus button")
    public void iClickOnTheDeleteBusButton() {
        driver.findElement(By.id("deleteBusBtn")).click();
    }

    @Then("I should see one less bus on {string} company")
    public void iShouldSeeOneLessBusOnCompany(String companyName) {
        assertThat(driver.findElements(By.id(companyName + "Bus")).size()).isEqualTo(numBus - 1);
    }


    @Then("I should see a card with the company name {string}")
    public void iShouldSeeACardWithTheCompanyName(String companyName) {
        assertThat(driver.findElement(By.id(companyName + "Name")).getText()).isEqualTo(companyName);
    }

    @Then("I should see one more bus on {string} company")
    public void iShouldSeeOneMoreBusOnCompany(String companyName) {
        assertThat(driver.findElements(By.id(companyName + "Bus")).size()).isEqualTo(numBus + 1);
    }

    @Then("I should see {int} as capacity for bus {int}")
    public void iShouldSeeAsCapacityForBus(int capacity, int id) {
        assertThat(driver.findElement(By.id("capacity" + id)).getText()).isEqualTo(String.valueOf(capacity));
    }

    @And("I should see a success message with {string}")
    public void iShouldSeeASuccessMessageWith(String message) {
        assertThat(driver.findElement(By.id("chipAlerts")).getText()).isEqualTo(message);
    }


}
