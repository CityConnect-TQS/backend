package pt.ua.deti.tqs.backend.functional.staff;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class LoginSteps {
    private final WebDriver driver = StaffCucumberTest.getDriver();

    @Given("There is a staff account with name {string}, email {string} and password {string}")
    public void thereIsAStaffAccountWithEmailAndPassword(String name, String email, String password) {
        String body = "{\"password\":\"" + password +
                "\",\"name\":\"" + name +
                "\",\"email\":\"" + email +
                "\",\"roles\":[\"USER\",\"STAFF\"]}";

        RestAssured.given().contentType(ContentType.JSON)
                   .body(body)
                   .when().post("http://api.localhost/api/backoffice/user")
                   .then().statusCode(HttpStatus.CREATED.value())
                   .body("name", equalTo(name))
                   .body("email", equalTo(email));
    }

    @Given("There is a user account with name {string}, email {string} and password {string}")
    public void thereIsAUserAccountWithEmailAndPassword(String name, String email, String password) {
        String body = "{\"password\":\"" + password +
                "\",\"name\":\"" + name +
                "\",\"email\":\"" + email +
                "\"}";

        RestAssured.given().contentType(ContentType.JSON)
                   .body(body)
                   .when().post("http://api.localhost/api/public/user")
                   .then().statusCode(HttpStatus.CREATED.value())
                   .body("name", equalTo(name))
                   .body("email", equalTo(email));
    }

    @Given("I navigate to {string}")
    public void iNavigateTo(String url) {
        driver.get(url);
    }

    @And("I click on the avatar")
    public void iClickOnTheAvatar() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("avatarBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("avatarBtn")).click();
    }

    @And("I fill in name with {string}")
    public void iFillInNameWith(String name) {
        driver.findElement(By.id("name")).sendKeys(name);
    }

    @And("I fill in email with {string}")
    public void iFillInEmailWith(String email) {
        driver.findElement(By.id("email")).sendKeys(email);
    }

    @And("I fill in password with {string}")
    public void iFillInPasswordWith(String password) {
        driver.findElement(By.id("password")).sendKeys(password);
    }

    @When("I click on the login button")
    public void iClickOnTheLoginButton() {
        driver.findElement(By.id("loginBtn")).click();
    }

    @When("I click on the sign-up button")
    public void iClickOnTheSignUpButton() {
        driver.findElement(By.id("signUpBtn")).click();
    }

    @Then("I should be logged in as {string}")
    public void iShouldBeLoggedIn(String name) {
        assertThat(driver.findElement(By.id("userText")).getText()).isEqualTo("Signed in as\n" + name);
    }

    @And("I am logged out")
    public void iAmLoggedOut() {
        if (!driver.findElements(By.id("userText")).isEmpty()) {
            driver.findElement(By.id("logoutBtn")).click();
            driver.findElement(By.id("avatarBtn")).click();
        }
        assertThat(driver.findElements(By.id("userText"))).isEmpty();
    }

    @Then("there is a message about unsufficient permissions")
    public void thereIsAMessageAboutUnsufficientPermissions() {
        assertThat(driver.findElement(By.id("loginError")).getText()).isEqualTo("error\nUnsufficient permissions");
    }
}