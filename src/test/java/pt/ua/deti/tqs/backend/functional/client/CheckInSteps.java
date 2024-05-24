package pt.ua.deti.tqs.backend.functional.client;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class CheckInSteps {
    private final WebDriver driver = ClientCucumberTest.getDriver();
    private Wait<WebDriver> wait;

    @Given("I am at the url {string}")
    public void iAmAt(String url) {
        driver.get(url);
    }

    @Given("I am signed in")
    public void iAmSignedIn() throws InterruptedException{
        wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("avatarBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("avatarBtn")).click();
        driver.findElement(By.id("loginBtn")).click();
        driver.findElement(By.id("email")).sendKeys("admin@gmail.com");
        driver.findElement(By.id("password")).sendKeys("admin123");
        driver.findElement(By.id("loginBtn")).click();
    }

    @And("I click on the avatar")
    public void iClickOnTheAvatar() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("avatarBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("avatarBtn")).click();
    }

    @And("I click on the reservations button")
    public void iClickOnTheReservationsButton() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
        wait.until(d -> d.findElement(By.id("reservationsBtn")).isDisplayed());
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("reservationsBtn")).click();
    }
}
