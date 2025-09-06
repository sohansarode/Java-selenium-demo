package utils;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BrowserSetup;

public class WaitUtils {

	private static WebDriver getDriver() {
		return BrowserSetup.getDriver(); // fetch ThreadLocal driver safely
	}
	
//-----------------------------------------------------------------------------------------------------------//

	// This method is use to apply explicit wait to specific element
	public static void Wait_For_Element(WebElement Element, int timeoutInSeconds) {
		// Create a WebDriverWait instance with the specified timeout
		WebDriverWait Wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));

		// Wait until the element becomes visible within the specified timeout
		Wait.until(ExpectedConditions.visibilityOf(Element));
	}

//----------------------------------------------------------------------------------------------------------//
	// This method is use to apply explicit wait to specific element

	public static void Wait_For_Visibility_Of_Element(By Element, int timeoutInSeconds) {
		// Create a WebDriverWait instance with the specified timeout
		WebDriverWait Wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));

		// Wait until the element located by the specified By object becomes visible
		// within the specified timeout
		Wait.until(ExpectedConditions.visibilityOfElementLocated(Element));
	}

	public static void Wait_For_Clickability_Of_Element(WebElement ticketLink, int timeoutInSeconds) {
		// Create a WebDriverWait instance with the specified timeout
		WebDriverWait Wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));

		// Wait until the element located by the specified By object becomes visible
		// within the specified timeout
		Wait.until(ExpectedConditions.elementToBeClickable(ticketLink));
	}
//----------------------------------------------------------------------------------------------------------//
	// This method verify that locator is visible or page or not

	public static boolean waitForVisibilityOfElement(By Element, int timeoutInSeconds) {
		try {
			// Create a WebDriverWait instance with the specified timeout
			WebDriverWait Wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));

			// Wait until the element located by the specified By object becomes visible
			// within the specified timeout
			Wait.until(ExpectedConditions.visibilityOfElementLocated(Element));

			return true; // Element is visible
		} catch (TimeoutException e) {
			return false; // Element is not visible within the time
		}
	}
	// ----------------------------------------------------------------------------------------------------------//

	public static boolean waitForVisibilityOfElementbypass(By Element, int timeoutInSeconds) {
		WebDriverWait Wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));
		boolean IsElementVisible;

		try {
			// Wait until the element located by the specified By object becomes visible
			// within the specified timeout
			Wait.until(ExpectedConditions.visibilityOfElementLocated(Element));
			IsElementVisible = true;
		} catch (TimeoutException e) {
			IsElementVisible = false;
		}

		return IsElementVisible;
	}

//----------------------------------------------------------------------------------------------------------//	

	public static void WaitForElement(List<WebElement> Elements, int timeoutInSeconds) {
		// Create a WebDriverWait instance with the specified timeout
		WebDriverWait Wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));

		// Wait until all elements in the list become visible within the specified
		// timeout
		Wait.until(ExpectedConditions.visibilityOfAllElements(Elements));
	}

//----------------------------------------------------------------------------------------------------------//

	// This method is use to stop execution for sometime

	public static void Hard_Wait(int timeOutInMiliSec) throws InterruptedException {
		// Pause the execution thread for the specified time duration
		Thread.sleep(timeOutInMiliSec);
	}
//----------------------------------------------------------------------------------------------------------//	

	// This method is use to wait till frame appears
	public static void Wait_For_Iframe(WebElement Element) {
		// Retrieve the wait time from the properties file.
		Object a = ConfigReader.prop.get("Waittime");

		// Create a WebDriverWait instance with the specified wait time.
		WebDriverWait Wait = new WebDriverWait(getDriver(), Duration.ofSeconds((long) ConfigReader.prop.get(a)));

		// Wait for the iframe to be available and switch to it.
		Wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(Element));

		// Switch to the specified iframe after it becomes available.
		getDriver().switchTo().frame(Element);
	}

//----------------------------------------------------------------------------------------------------------//

	public void Wait_Until_Page_Loads_Completely() {
		// Get the wait time from configuration properties
		Object WaitTimeObject = ConfigReader.prop.get("Waittime");

		// Convert wait time to seconds
		long waitTimeInSeconds = (long) ConfigReader.prop.get(WaitTimeObject);

		// Create a JavascriptExecutor instance
		JavascriptExecutor javascriptExecutor = (JavascriptExecutor) getDriver();

		// Create a WebDriverWait instance with the specified wait time
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTimeInSeconds));

		// Wait until the document.readyState is 'complete'
		wait.until(d -> javascriptExecutor.executeScript("return document.readyState").toString().equals("complete"));
	}
}
