package it.univr.PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AdminUsersPage extends PageObject {

    public AdminUsersPage(WebDriver driver) {
        super(driver);
    }

    private WebElement getUserRow(String username) {
        return driver.findElement(By.xpath("//tr[td[text()='" + username + "']]"));
    }

    public void changeRole(String username) {
        WebElement row = getUserRow(username);
        row.findElement(By.xpath(".//button[contains(text(), 'Cambia ruolo')]")).click();
    }

    public void deleteUser(String username) {
        WebElement row = getUserRow(username);
        row.findElement(By.xpath(".//button[contains(text(), 'Elimina')]")).click();
    }

    public boolean isUserPresent(String username) {
        return !driver.findElements(By.xpath("//td[text()='" + username + "']")).isEmpty();
    }
}