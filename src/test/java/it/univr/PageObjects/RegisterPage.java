package it.univr.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage extends PageObject {

    @FindBy(id = "firstName") private WebElement firstNameInput;
    @FindBy(id = "lastName") private WebElement lastNameInput;
    @FindBy(id = "username") private WebElement usernameInput;
    @FindBy(id = "email") private WebElement emailInput;
    @FindBy(id = "password") private WebElement passwordInput;
    @FindBy(xpath = "//button[@type='submit']") private WebElement submitButton; // [cite: 382]
    @FindBy(className = "alert-danger") private WebElement errorMessage;

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public void registerUser(String fName, String lName, String user, String mail, String pass) {
        firstNameInput.sendKeys(fName);
        lastNameInput.sendKeys(lName);
        usernameInput.sendKeys(user);
        emailInput.sendKeys(mail);
        passwordInput.sendKeys(pass);
        submitButton.click();
    }

    public boolean isErrorDisplayed() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}