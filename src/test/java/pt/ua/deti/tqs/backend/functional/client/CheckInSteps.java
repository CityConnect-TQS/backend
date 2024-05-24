package pt.ua.deti.tqs.backend.functional.client;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CheckInSteps {
    private final WebDriver driver = ClientCucumberTest.getDriver();
    private Wait<WebDriver> wait;

    @Given("I navigate to {string}")
    public void iNavigateTo(String url) {
        driver.get(url);
    }

    @Given("There is a user account with name {string}, email {string} and password {string}")
    public void thereIsAStaffAccountWithEmailAndPassword(String name, String email, String password) {
        String body = "{\"password\":\"" + password +
                "\",\"name\":\"" + name +
                "\",\"email\":\"" + email +
                "\",\"roles\":[\"USER\"]}";

        RestAssured.given().contentType(ContentType.JSON)
                .body(body)
                .when().post("http://api.localhost/api/public/user")
                .then().statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(name))
                .body("email", equalTo(email));
    }

    @Given("I am signed in")
    public void iAmSignedIn() throws InterruptedException{
        wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("avatarBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.id("avatarBtn")).click();
        driver.findElement(By.id("loginBtn")).click();
        driver.findElement(By.id("email")).sendKeys("joao@ua.pt");
        driver.findElement(By.id("password")).sendKeys("john1234");
        driver.findElement(By.id("loginBtn")).click();
        TimeUnit.SECONDS.sleep(5);
    }

    @When("I search for trips")
    public void i_search_for_trips() {
        driver.get("http://localhost/trips?departure=1&arrival=3&departureTime=2024-05-19");
    }

    @When("I click on the first trip that shows up")
    public void iClickOnTheFirstTrip() throws InterruptedException{
        TimeUnit.SECONDS.sleep(3);
        driver.findElement(By.id("tripCard41")).click();
    }

    @Then("the submit button should be disabled")
    public void theSubmitButtonShouldBeDisabled() {
        assertThat(driver.findElement(By.id("submitBtn")).isEnabled()).isFalse();
    }

    @Then("I should be redirected to {string} page")
    public void iShouldBeRedirectedTo(String title) throws InterruptedException{
        TimeUnit.SECONDS.sleep(1);
        assertThat(driver.findElement(By.id("pageTitle")).getText()).isEqualTo(title);
    }

    @When("I click on seat {string}")
    public void iClickOnSeat(String seat) throws InterruptedException{
        driver.findElement(By.id("seat" + seat)).click();
    }

    @When("I click on submit button")
    public void iClickOnSubmitButton() throws InterruptedException{
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.id("submitBtn")).click();
    }

    @Then("I should see the message {string}")
    public void iShouldSeeTheMessage(String message) throws InterruptedException{
        TimeUnit.SECONDS.sleep(1);
        assertThat(driver.findElement(By.id("confirmText")).getText()).isEqualTo(message);
    }

    @Given("Im navigate to {string}")
    public void aNavigateTo(String url) {
        driver.get(url);
    }


    @And("Im click on the avatar")
    public void aClickOnTheAvatar() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("avatarBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("avatarBtn")).click();
    }

    @When("Im click on the checkin button")
    public void im_click_on_the_button(String string) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("reservationsBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("reservationsBtn")).click();
    }

    @Then("Im should see {string}")
    public void im_should_see(String message) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        assertThat(driver.findElement(By.id("confirmText")).getText()).isEqualTo(message);
    }

    @Then("the seats {string} and {string} should appear")
    public void theSeatsShouldAppear(String seat1, String seat2) {
    	assertThat(driver.findElement(By.id("selectedSeats")).getText()).isEqualTo(seat1 + ", " + seat2);
    }

    @When("Im click on the reservations button")
    public void ik_click_on_the_button() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("reservationsBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("reservationsBtn")).click();
    }

    @When("Im click on the Check in button")
    public void ik2_click_on_the_button() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("docheckin")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("docheckin")).click();
    }

    @When("Im click on the confirm check in button")
    public void ik3_click_on_the_button() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("checkInButton")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("checkInButton")).click();
    }

    @Then("Im should see Checked in")
    public void im_should_see_checkin() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        assertThat(driver.findElement(By.id("checkInChip")).getText()).isEqualTo("Checked-in");
    }
}
