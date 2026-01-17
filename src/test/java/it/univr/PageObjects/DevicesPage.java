package it.univr.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

// DevicesPage
public class DevicesPage extends PageObject {
    @FindBy(linkText = "Associa MAC")
    private WebElement associateButton;

    public DevicesPage(WebDriver driver) { super(driver); }

    public AssociatePage clickAssociate() {
        associateButton.click();
        return new AssociatePage(driver);
    }
}