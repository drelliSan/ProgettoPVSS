package it.univr.Tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SmartTrackingApiTest {

    @BeforeAll
    public static void setBaseUri() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    // --- TEST REGISTRAZIONE UTENTE (Scenario 2.1) ---

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUserRegistrationSuccess() {
        Map<String, String> user = new HashMap<>();
        user.put("firstName", "Mario");
        user.put("lastName", "Rossi");
        user.put("username", "mariorossi");
        user.put("email", "mario@test.com");
        user.put("password", "password123");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/users/register")
                .then()
                .statusCode(201)
                .body("username", equalTo("mariorossi"))
                .body("status", equalTo("ATTIVO"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUserRegistrationConflict() {
        Map<String, String> user1 = new HashMap<>();
        user1.put("username", "mariorossi");
        user1.put("email", "mario@test.com");
        user1.put("password", "password123");

        given().contentType(ContentType.JSON).body(user1).post("/api/users/register");

        Map<String, String> user2 = new HashMap<>();
        user2.put("username", "mariorossi");
        user2.put("email", "luigi@test.com");
        user2.put("password", "password456");

        given()
                .contentType(ContentType.JSON)
                .body(user2)
                .when()
                .post("/api/users/register")
                .then()
                .statusCode(409)
                .body(containsString("Username già in uso"));
    }

    // --- TEST LOGIN UTENTE (Scenario 2.2) ---

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testLoginSuccess() {
        Map<String, String> user = new HashMap<>();
        user.put("username", "luigiverdi");
        user.put("email", "luigi@test.com");
        user.put("password", "pass123");
        given().contentType(ContentType.JSON).body(user).post("/api/users/register");

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "luigiverdi");
        credentials.put("password", "pass123");

        given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(200)
                .body("email", equalTo("luigi@test.com"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testLoginFailureWrongPassword() {
        Map<String, String> user = new HashMap<>();
        user.put("username", "testuser");
        user.put("password", "correctpass");
        given().contentType(ContentType.JSON).body(user).post("/api/users/register");

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "testuser");
        credentials.put("password", "wrongpass");

        given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(401)
                .body(equalTo("Credenziali non valide"));
    }

    // --- TEST PROVISIONING DEVICE (Scenario 2.6) ---

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeviceProvisioning() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/devices/provision")
                .then()
                .statusCode(201)
                .body("device.id", notNullValue())
                .body("device.status", equalTo("INATTIVO"))
                .body("qrCode", notNullValue());
    }

    // --- TEST ASSOCIAZIONE DEVICE (Scenario 2.7) ---

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeviceAssociation() {
        int deviceId = given()
                .post("/api/devices/provision")
                .then()
                .extract().path("device.id");

        given()
                .queryParam("deviceId", deviceId)
                .queryParam("macAddress", "AA:BB:CC:11:22:33")
                .when()
                .post("/api/devices/associate")
                .then()
                .statusCode(200)
                .body("macAddress", equalTo("AA:BB:CC:11:22:33"))
                .body("status", equalTo("ATTIVO"));
    }

    @Test
    public void testDeviceAssociationNotFound() {
        given()
                .queryParam("deviceId", 99999)
                .queryParam("macAddress", "AA:BB:CC:11:22:33")
                .when()
                .post("/api/devices/associate")
                .then()
                .statusCode(404)
                .body(equalTo("Device non trovato"));
    }

    // --- TEST AUTENTICAZIONE DEVICE ---

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeviceAuthSuccess() {
        int deviceId = given().post("/api/devices/provision").then().extract().path("device.id");

        given().queryParam("deviceId", deviceId)
                .queryParam("macAddress", "11:22:33:44:55:66")
                .post("/api/devices/associate");

        Map<String, String> authPayload = new HashMap<>();
        authPayload.put("id", String.valueOf(deviceId));
        authPayload.put("macAddress", "11:22:33:44:55:66");

        given()
                .contentType(ContentType.JSON)
                .body(authPayload)
                .when()
                .post("/api/devices/auth")
                .then()
                .statusCode(200)
                .body(equalTo("AUTHORIZED"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeviceAuthUnauthorized() {
        Map<String, String> authPayload = new HashMap<>();
        authPayload.put("id", "123");
        authPayload.put("macAddress", "FAKE:MAC");

        given()
                .contentType(ContentType.JSON)
                .body(authPayload)
                .when()
                .post("/api/devices/auth")
                .then()
                .statusCode(401)
                .body(equalTo("UNAUTHORIZED"));
    }
}