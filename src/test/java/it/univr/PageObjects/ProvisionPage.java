package it.univr.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProvisionPage extends PageObject {

    @FindBy(xpath = "//button[contains(text(), 'Genera QR Code')]")
    private WebElement generateQrButton;

    @FindBy(xpath = "//img[contains(@src, 'base64')]")
    private WebElement qrImage;

    public ProvisionPage(WebDriver driver) {
        super(driver);
    }

    public void clickGenerate() {
        generateQrButton.click();
    }

    public boolean isQrDisplayed() {
        return qrImage.isDisplayed();
    }
}