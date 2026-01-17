package it.univr.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AssociatePage extends PageObject {
    @FindBy(id = "macAddress") private WebElement macInput;
    @FindBy(xpath = "//button[contains(text(), 'Conferma e Attiva')]") private WebElement confirmButton;

    public AssociatePage(WebDriver driver) { super(driver); }

    public void enterMacAndConfirm(String mac) {
        macInput.sendKeys(mac);
        confirmButton.click();
    }
}