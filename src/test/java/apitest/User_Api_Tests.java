package apitest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import utils.ReportUtils;
import api.Testbase;
import java.util.HashMap;
import java.util.Map;

public class User_Api_Tests extends Testbase {

    @BeforeClass
    public void setUpReport() {
        ReportUtils.setUpReport();
    }

    @AfterClass
    public void tearDownReport() {
        ReportUtils.endReport();
    }

    @Test
    public void getUsersTest() {
        ReportUtils.startTest("Get Users API Test");

        sendGetRequest("https://reqres.in/api/users?page=2");

        Statuscode(200);
        Getbody();
        Contenttype("application/json; charset=utf-8", "Content-Type");
        Getallheaders();
        Checkresponsetime(3000);
        assertAll();

        ReportUtils.passTest("Get Users API Test passed.");
    }

    @Test
    public void loginTest() {
        ReportUtils.startTest("Login API Test");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "eve.holt@reqres.in");
        requestBody.put("password", "cityslicka");

        sendPostRequest("https://reqres.in/api/login", requestBody);

        Statuscode(200);
        Getbody1("token");
        assertAll();

        ReportUtils.passTest("Login API Test passed.");
    }

    @Test
    public void loginFailureTest() {
        ReportUtils.startTest("Login Failure API Test");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "eve.holt@reqres.in");
        // password missing

        sendPostRequest("https://reqres.in/api/login", requestBody);

        Statuscode(400);
        Datavalidation("error", "Missing password");
        assertAll();

        ReportUtils.passTest("Login Failure API Test passed.");
    }
}
