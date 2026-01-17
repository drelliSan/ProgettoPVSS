package it.univr.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends PageObject {

    @FindBy(linkText = "Login")
    private WebElement loginLink;

    @FindBy(linkText = "Registrati")
    private WebElement registerLink;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public LoginPage clickLogin() {
        loginLink.click();
        return new LoginPage(driver);
    }

    public RegisterPage clickRegister() {
        registerLink.click();
        return new RegisterPage(driver);
    }
}
