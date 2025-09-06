package api;

import java.util.Map;
import org.apache.logging.log4j.*;
import org.json.simple.JSONObject;
import org.testng.asserts.SoftAssert;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.ReportUtils;

public class Testbase {

    protected static RequestSpecification httprequest;
    protected static Response response;
    protected static String filepath;
    protected static JSONObject js;
    private static Logger logger = LogManager.getLogger(Testbase.class);

    final String url = "";
    protected String bearerToken;

    // Shared SoftAssert object (reset in @BeforeMethod ideally)
    protected SoftAssert softAssert = new SoftAssert();

    public Testbase() {
        httprequest = RestAssured.given();
    }

    // ---------------------- Authentication ----------------------

    protected void setBearerToken(String token) {
        bearerToken = token;
        httprequest.header("Authorization", "Bearer " + bearerToken);
        ReportUtils.info("Bearer token set successfully.");
    }

    protected String login(String baseURI, Map<String, String> requestBody) {
        String fullURI = url + baseURI;
        httprequest.baseUri(fullURI);
        httprequest.header("Content-Type", "application/json");
        httprequest.body(requestBody);

        response = httprequest.post();
        ReportUtils.logApiResponse(response);

        if (response.getStatusCode() == 200) {
            bearerToken = response.jsonPath().getString("data.token");
            setBearerToken(bearerToken);
            ReportUtils.passTest("Login successful. Bearer token set.");
        } else {
            ReportUtils.failTestNoScreenshot("Login failed. Status code: " + response.getStatusCode());
            softAssert.fail("❌ Login failed. Expected 200 but got " + response.getStatusCode());
        }
        return bearerToken;
    }

    // ---------------------- Request Methods ----------------------

    protected void sendGetRequest(String baseURI) {
        String fullURI = url + baseURI;
        httprequest.header("Content-Type", "application/json");
        httprequest.baseUri(fullURI);
        response = httprequest.get();

        ReportUtils.info("GET Request sent to: " + fullURI);
        ReportUtils.logApiResponse(response);
    }

    protected void sendPostRequest(String baseURI, Map<String, String> requestBody) {
        String fullURI = url + baseURI;
        httprequest.baseUri(fullURI);
        httprequest.header("Content-Type", "application/json");
        httprequest.body(requestBody);
        response = httprequest.post();

        ReportUtils.info("POST Request sent to: " + fullURI);
        ReportUtils.logApiResponse(response);
    }

    protected void sendPutRequest(String baseURI) {
        String fullURI = url + baseURI;
        httprequest.baseUri(fullURI);
        response = httprequest.put();

        ReportUtils.info("PUT Request sent to: " + fullURI);
        ReportUtils.logApiResponse(response);
    }

    protected void sendDeleteRequest(String baseURI) {
        String fullURI = url + baseURI;
        httprequest.baseUri(fullURI);
        response = httprequest.delete();

        ReportUtils.info("DELETE Request sent to: " + fullURI);
        ReportUtils.logApiResponse(response);
    }

    // ---------------------- Validations ----------------------

    protected void Getbody() {
        String responsebody = response.body().asString();
        logger.info("Response Body: " + responsebody);
        ReportUtils.info("Response Body: " + responsebody);

        boolean notNull = responsebody != null;
        if (notNull) {
            ReportUtils.passTest("Response body is not null");
        } else {
            ReportUtils.failTest("Response body is null");
        }
        softAssert.assertTrue(notNull, "❌ Response body should not be null");
    }

    protected void Statuscode(int expectedStatusCode) {
        int actualCode = response.getStatusCode();
        if (actualCode == expectedStatusCode) {
            ReportUtils.passTest("Status code validated: " + actualCode);
        } else {
            ReportUtils.failTestNoScreenshot("Expected: " + expectedStatusCode + " but got: " + actualCode);
        }
        softAssert.assertEquals(actualCode, expectedStatusCode, "❌ Wrong status code");
    }

    protected void Checkresponsetime(long maxResponseTime) {
        long actualTime = response.getTime();
        ReportUtils.info("Response time: " + actualTime + " ms");

        if (actualTime <= maxResponseTime) {
            ReportUtils.passTest("Response time is within limit: " + actualTime + " ms");
        } else {
            ReportUtils.failTestNoScreenshot("Response time exceeded: " + actualTime + " ms");
        }
        softAssert.assertTrue(actualTime <= maxResponseTime, "❌ Response time too high: " + actualTime);
    }

    protected void Statusline(String expectedLine) {
        String actualLine = response.getStatusLine();
        ReportUtils.info("Status Line: " + actualLine);

        if (expectedLine.equals(actualLine)) {
            ReportUtils.passTest("Status line validated: " + actualLine);
        } else {
            ReportUtils.failTest("Expected: " + expectedLine + " but got: " + actualLine);
        }
        softAssert.assertEquals(actualLine, expectedLine, "❌ Wrong status line");
    }

    protected void Contenttype(String expectedType, String headerName) {
        String actualType = response.header(headerName);
        ReportUtils.info("Content Type (" + headerName + "): " + actualType);

        if (expectedType.equals(actualType)) {
            ReportUtils.passTest("Content type validated: " + actualType);
        } else {
            ReportUtils.failTest("Expected: " + expectedType + " but got: " + actualType);
        }
        softAssert.assertEquals(actualType, expectedType, "❌ Wrong content type");
    }

    protected void Getallheaders() {
        Headers allHeaders = response.getHeaders();
        StringBuilder headersLog = new StringBuilder("Headers:\n");
        for (Header header : allHeaders) {
            headersLog.append(header.toString()).append("\n");
        }
        ReportUtils.info(headersLog.toString());
    }

    protected void Cookies() {
        Map<String, String> cookies = response.cookies();
        ReportUtils.info("Cookies: " + cookies);
    }

    protected void Getbody1(String jsonPath) {
        String responsebody = response.body().asString();
        JsonPath j = new JsonPath(responsebody);
        String value = j.getString(jsonPath);
        ReportUtils.info("Extracted value for [" + jsonPath + "] : " + value);
    }

    protected void Datavalidation(String jsonPath, String expectedValue) {
        String responsebody = response.body().asString();
        JsonPath j = new JsonPath(responsebody);
        String actual = String.valueOf(j.get(jsonPath));

        if (expectedValue.equals(actual)) {
            ReportUtils.passTest("Data validation passed for [" + jsonPath + "] Expected: " + expectedValue + " | Actual: " + actual);
        } else {
            ReportUtils.failTest("Data validation failed for [" + jsonPath + "] Expected: " + expectedValue + " | Actual: " + actual);
        }
        softAssert.assertEquals(actual, expectedValue, "❌ Data mismatch at: " + jsonPath);
    }

    protected void Getsessionid() {
        String sessionId = response.getSessionId();
        ReportUtils.info("Session ID: " + sessionId);
    }

    protected void Hashcode() {
        int hash = response.hashCode();
        ReportUtils.info("Response Hashcode: " + hash);
    }

    // ---------------------- Final assertion trigger ----------------------

    protected void assertAll() {
        softAssert.assertAll();
    }
}
