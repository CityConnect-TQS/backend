package pt.ua.deti.tqs.backend.functional.client;

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

    @Given("There is a user account with name {string}, email {string} and password {string}")
    public void xthereIsAStaffAccountWithEmailAndPassword(String name, String email, String password) {
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

    @Given("I am signed in!")
    public void iAmSignedInX() throws InterruptedException{
        wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("avatarBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.id("avatarBtn")).click();
        driver.findElement(By.id("loginBtn")).click();
        driver.findElement(By.id("email")).sendKeys("john@ua.pt");
        driver.findElement(By.id("password")).sendKeys("john2024");
        driver.findElement(By.id("loginBtn")).click();
        TimeUnit.SECONDS.sleep(2);
    }

    @When("Im click on the checkin button")
    public void xim_click_on_the_button(String string) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("reservationsBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("reservationsBtn")).click();
    }

    @When("I click on the reservations button")
    public void xk_click_on_the_button() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("reservationsBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("reservationsBtn")).click();
    }

    @When("I click on the Check in button")
    public void xk2_click_on_the_button() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("docheckin")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("docheckin")).click();
    }

    @When("I click on the confirm check in button")
    public void xk3_click_on_the_button() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("checkInButton")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("checkInButton")).click();
    }

    @Then("I should see Checked in")
    public void xm_should_see_checkin() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        assertThat(driver.findElement(By.id("checkInChip")).getText()).isEqualTo("check\nChecked-in");
    }
}
