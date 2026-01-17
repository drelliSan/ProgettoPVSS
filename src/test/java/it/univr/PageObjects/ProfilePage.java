package it.univr.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfilePage extends PageObject {

    @FindBy(id = "firstName") private WebElement firstNameInput;
    @FindBy(xpath = "//button[contains(text(), 'Salva Modifiche')]") private WebElement saveButton;

    public ProfilePage(WebDriver driver) {
        super(driver);
    }

    public void updateName(String newName) {
        firstNameInput.clear();
        firstNameInput.sendKeys(newName);
        saveButton.click();
    }
}