package it.univr.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends PageObject {

    @FindBy(id = "email") private WebElement usernameInput;
    @FindBy(id = "password") private WebElement passwordInput;
    @FindBy(xpath = "//button[@type='submit']") private WebElement loginButton;
    @FindBy(className = "alert-danger") private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage loginAs(String username, String password) {
        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        loginButton.click();
        return new DashboardPage(driver);
    }

    public boolean isErrorPresent() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}