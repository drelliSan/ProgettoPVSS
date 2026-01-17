package it.univr.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DashboardPage extends PageObject {

    @FindBy(xpath = "//h2[contains(text(), 'Benvenuto')]")
    private WebElement welcomeMessage;

    @FindBy(linkText = "Gestisci Profilo")
    private WebElement profileButton;

    @FindBy(linkText = "Gestisci Utenti")
    private WebElement adminUsersButton;

    @FindBy(linkText = "Provisiona")
    private WebElement provisionButton;

    @FindBy(linkText = "Vai ai Devices")
    private WebElement myDevicesButton;

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public String getWelcomeMessage() {
        return welcomeMessage.getText();
    }

    public ProfilePage goToProfile() {
        profileButton.click();
        return new ProfilePage(driver);
    }

    public AdminUsersPage goToAdminUsers() {
        adminUsersButton.click();
        return new AdminUsersPage(driver);
    }

    public ProvisionPage goToProvisioning() {
        provisionButton.click();
        return new ProvisionPage(driver);
    }

    public DevicesPage goToMyDevices() {
        myDevicesButton.click();
        return new DevicesPage(driver);
    }
}