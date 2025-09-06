package utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.github.javafaker.Faker;
import base.BrowserSetup;

public class CommonUtils {

	private static final Faker faker = new Faker(Locale.forLanguageTag("en-IN"));
	private static final Random random = new Random();

	private static WebDriver getDriver() {
		return BrowserSetup.getDriver(); // fetch ThreadLocal driver safely
	}

//========================================-- Log Methods Starts --======================================================================//
	// Logs information along with the class name of the caller.
	private static void logInfos(Object obj) {
		// Get the current call stack
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		// Default class name (if not enough stack frames)
		String className = "Unknown";

		// Check if there are enough stack frames to retrieve the caller's class name
		if (stackTrace.length >= 4) {
			// Retrieve the caller's class name
			className = stackTrace[3].getClassName();
		}

		// Log the class name and object's string representation
		ConfigReader.logger.info(className + " ===>  " + obj.toString());
	}

//========================================-- Dropdown Methods Starts --======================================================================//
	public static Object Select_By_Element(WebElement dropdownElement, String selectionType, String selectionValue) {
		ReportUtils.info("Starting dropdown selection - Type : " + selectionType + ", Value : " + selectionValue);

		Select select = new Select(dropdownElement);
		switch (selectionType.toLowerCase()) {
		case "text":
			select.selectByVisibleText(selectionValue);
			ReportUtils.info("Selected by visible text : " + selectionValue);
			break;
		case "value":
			select.selectByValue(selectionValue);
			ReportUtils.info("Selected by value : " + selectionValue);
			break;
		case "index":
			select.selectByIndex(Integer.parseInt(selectionValue));
			ReportUtils.info("Selected by index : " + selectionValue);
			break;
		default:
			String errorMsg = "Invalid selection type : " + selectionType;
			ReportUtils.info(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}
		return select;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static Object Select_Random_Option_From_Dropdown(WebElement dropdownElement) {
		ReportUtils.info("Starting random dropdown selection");

		Select dropdown = new Select(dropdownElement);
		List<WebElement> options = dropdown.getOptions();

		if (!options.isEmpty()) {
			int randomIndex = new Random().nextInt(options.size());
			dropdown.selectByIndex(randomIndex);
			String selectedOptionText = dropdown.getFirstSelectedOption().getText();
			ReportUtils.info("Selected Option ---> " + selectedOptionText);
			return selectedOptionText;
		} else {
			String errorMsg = "Dropdown has no options available";
			ReportUtils.info(errorMsg);
			throw new NoSuchElementException(errorMsg);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Select_Random_Option_From_Dropdown_Ignore_Index(WebElement dropdownElement, int ignoredCount) {
		ReportUtils.info("Starting random dropdown selection ignoring first " + ignoredCount + " options");

		Select dropdown = new Select(dropdownElement);
		List<WebElement> options = dropdown.getOptions();

		if (options.size() > ignoredCount) {
			int randomIndex;
			do {
				randomIndex = new Random().nextInt(options.size());
			} while (randomIndex < ignoredCount);

			dropdown.selectByIndex(randomIndex);
			ReportUtils.info("Selected option at index: " + randomIndex);
		} else {
			ReportUtils.info("Not enough options to select from after ignoring first " + ignoredCount + " options");
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Select_Random_Option_From_Dropdown_Ignore_Indices(WebElement dropdownElement,
			List<Integer> ignoredIndices) {
		ReportUtils.info("Starting random dropdown selection with ignored indices : " + ignoredIndices);

		Select dropdown = new Select(dropdownElement);
		List<WebElement> options = dropdown.getOptions();
		Set<Integer> ignoredIndicesSet = new HashSet<>(ignoredIndices);

		if (!options.isEmpty()) {
			int randomIndex;
			do {
				randomIndex = new Random().nextInt(options.size());
			} while (ignoredIndicesSet.contains(randomIndex));

			dropdown.selectByIndex(randomIndex);
			ReportUtils.info("Selected option at index : " + randomIndex);
		} else {
			ReportUtils.info("No options available in dropdown");
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Dynamic_Dropdown_Selection(List<WebElement> elements, String attribute, String value) {
		ReportUtils.info("Starting dynamic dropdown selection - Attribute : " + attribute + ", Value : " + value);

		for (WebElement element : elements) {
			if (element.getAttribute(attribute).equalsIgnoreCase(value)) {
				element.click();
				ReportUtils.info("Selected option with " + attribute + " = " + value);
				break;
			}
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static Object Dropdown_And_Other_Selection_By_Text(List<WebElement> elements, String text) {
		ReportUtils.info("Starting selection by text : " + text);

		for (WebElement element : elements) {
			if (element.getText().equalsIgnoreCase(text)) {
				element.click();
				ReportUtils.info("Selected element with text : " + text);
				return text;
			}
		}
		return text;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static Object Select_Random_Option_From_Dropdown(List<WebElement> options) {
		ReportUtils.info("Starting random selection from options list");

		String optionText = "";
		if (!options.isEmpty()) {
			int randomIndex = new Random().nextInt(options.size());
			WebElement selectedOption = options.get(randomIndex);
			optionText = selectedOption.getText().trim();
			selectedOption.click();
			ReportUtils.info("Selected option ---> " + optionText);
		} else {
			ReportUtils.info("No options available in the dropdown.");
		}
		return optionText;
	}

//========================================-- Dropdown Methods Ends --=========================================================================//

//========================================-- Alerts Methods Starts --=========================================================================//	

	public static void Handle_Alerts(String Action) {
		ReportUtils.info("Attempting to handle alert with action : " + Action);

		try {
			Alert alert = getDriver().switchTo().alert();
			if ("accept".equalsIgnoreCase(Action)) {
				alert.accept();
				ReportUtils.info("Alert accepted");
			} else if ("dismiss".equalsIgnoreCase(Action)) {
				alert.dismiss();
				ReportUtils.info("Alert dismissed");
			} else {
				ReportUtils.info("Invalid alert action --> " + Action);
			}
		} catch (NoAlertPresentException e) {
			ReportUtils.info("Alert is not present");
		}
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static String Switch_To_Alert_And_Get_Text() {
		ReportUtils.info("Attempting to switch to alert and get text");

		try {
			Alert alert = getDriver().switchTo().alert();
			String alertText = alert.getText();
			ReportUtils.info("Retrieved alert text : " + alertText);
			return alertText;
		} catch (NoAlertPresentException e) {
			ReportUtils.info("No alert present");
			return null;
		}
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Send_Keys_To_Alert(String text) {
		ReportUtils.info("Attempting to send keys to alert : " + text);

		try {
			Alert alert = getDriver().switchTo().alert();
			alert.sendKeys(text);
			ReportUtils.info("Successfully sent keys to alert");
		} catch (NoAlertPresentException e) {
			ReportUtils.info("No alert present");
		}
	}

//========================================-- Alerts Methods Ends --=========================================================================//

//========================================-- Windows Methods Starts --======================================================================//	

	public static void Switch_To_Different_Browser_Window() {
		ReportUtils.info("Attempting to switch to different browser window");

		String currentWindowHandle = getDriver().getWindowHandle();
		Set<String> windowHandles = getDriver().getWindowHandles();

		for (String nextWindowHandle : windowHandles) {
			if (!currentWindowHandle.equalsIgnoreCase(nextWindowHandle)) {
				getDriver().switchTo().window(nextWindowHandle);
				logInfos("Switched to window : " + nextWindowHandle);
				break;
			}
		}
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Switch_To_Specific_Browser_Window(String windowHandleToSwitch) {
		ReportUtils.info("Attempting to switch to specific browser window : " + windowHandleToSwitch);

		String currentWindowHandle = getDriver().getWindowHandle();
		Set<String> windowHandles = getDriver().getWindowHandles();

		for (String nextWindowHandle : windowHandles) {
			if (!currentWindowHandle.equalsIgnoreCase(nextWindowHandle)) {
				if ("second".equalsIgnoreCase(windowHandleToSwitch)) {
					getDriver().switchTo().window(nextWindowHandle);
					ReportUtils.info("Switched to second window");
					break;
				} else if ("first".equalsIgnoreCase(windowHandleToSwitch)) {
					getDriver().switchTo().window(currentWindowHandle);
					ReportUtils.info("Switched back to first window");
					break;
				}
			}
		}
	}

//========================================-- Windows Methods Ends --=========================================================================//

//========================================-- All Click Methods Starts --=====================================================================//

	public static void Click(WebElement Element) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

		try {
			wait.until(ExpectedConditions.elementToBeClickable(Element));

			String elementName = GetElementName(Element);

			highlightElement(Element);

			ReportUtils.info("Attempting to click element : " + elementName);

			Element.click();

			ReportUtils.info("Successfully clicked element : " + elementName);
		} catch (TimeoutException e) {
			ReportUtils.failTest("Timeout waiting for element to be clickable : " + Element);
			throw new AssertionError("Timeout waiting for element to be clickable : " + Element);
		} catch (Exception e) {
			ReportUtils.failTest("Error clicking element : " + e.getMessage());
			throw new RuntimeException("Error clicking element : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Click_Using_JavaScript_Executor(WebElement Element) {
		try {
			if (!Element.isDisplayed()) {
				throw new AssertionError("Element is not visible, JavaScript click might fail : " + Element);
			}

			highlightElement(Element);

			String elementName = GetElementName(Element);
			ReportUtils.info("Attempting to click element using JavaScript : " + elementName);

			JavascriptExecutor js = (JavascriptExecutor) getDriver();
			js.executeScript("arguments[0].click();", Element);

			ReportUtils.info("Successfully clicked element using JavaScript : " + elementName);
		} catch (Exception e) {
			ReportUtils.failTest("Error clicking element using JavaScript : " + e.getMessage());
			throw new RuntimeException("Error clicking element using JavaScript : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Click_All_Elements(List<WebElement> elements) {
		int count = 0;

		try {
			if (elements.isEmpty()) {
				throw new AssertionError("Element list is empty, nothing to click.");
			}

			ReportUtils.info("Attempting to click all elements in list");

			for (WebElement element : elements) {
				if (!element.isDisplayed() || !element.isEnabled()) {
					throw new AssertionError("Element " + (count + 1) + " is not visible or enabled.");
				}

				// Highlight element before click
				highlightElement(element);

				element.click();
				count++;
				ReportUtils.info("Clicked element " + count + " of " + elements.size());
			}

			ReportUtils.info("Successfully clicked all " + elements.size() + " elements.");
		} catch (Exception e) {
			ReportUtils.failTest("Error clicking all elements : " + e.getMessage());
			throw new RuntimeException("Error clicking all elements : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Double_Click(WebElement element) {
		Actions actions = new Actions(getDriver());

		try {
			if (!element.isDisplayed() || !element.isEnabled()) {
				throw new AssertionError("Element is not visible or enabled for double click : " + element);
			}

			// Highlight element before double click
			highlightElement(element);

			String elementName = GetElementName(element);
			ReportUtils.info("Attempting double click on element : " + elementName);

			actions.doubleClick(element).perform();

			ReportUtils.info("Successfully performed double click on element: " + elementName);
		} catch (Exception e) {
			ReportUtils.failTest("Error performing double click : " + e.getMessage());
			throw new RuntimeException("Error performing double click : " + e.getMessage());
		}
	}

//========================================-- All Click Methods Ends --========================================================================//

//========================================-- Send Keys Methods Starts --======================================================================//	

	public static void Sendkeys(WebElement Element, Object Keys) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		try {
			wait.until(ExpectedConditions.visibilityOf(Element)); // Ensure visibility

			if (!Element.isDisplayed() || !Element.isEnabled()) {
				throw new AssertionError("Element is not visible or enabled : " + Element);
			}

			String elementName = GetElementName(Element);
			highlightElement(Element);

			ReportUtils.info("Sending keys to element : " + elementName);

			if (Keys instanceof String) {
				Element.sendKeys((String) Keys);
			} else if (Keys instanceof Integer) {
				Element.sendKeys(String.valueOf(Keys));
			} else {
				throw new IllegalArgumentException(
						"Unsupported data type for Sendkeys : " + Keys.getClass().getSimpleName());
			}

			ReportUtils.info("Successfully sent keys: " + Keys + " to element : " + elementName);

		} catch (TimeoutException e) {
			ReportUtils.failTest("Timeout waiting for element : " + Element);
			throw new AssertionError("Timeout waiting for element : " + Element);
		} catch (Exception e) {
			ReportUtils.failTest("Error sending keys to element : " + e.getMessage());
			throw new RuntimeException("Error sending keys to element : " + e.getMessage());
		}
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Sendkeys_And_Click(WebElement Element, String Keys) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		try {
			wait.until(ExpectedConditions.visibilityOf(Element)); // Wait for visibility

			if (!Element.isDisplayed() || !Element.isEnabled()) {
				throw new AssertionError("Element is not visible or enabled : " + Element);
			}

			ReportUtils.info("Attempting to send keys and click element");

			Element.sendKeys(Keys);
			Element.click();

			ReportUtils.info("Successfully sent keys : " + Keys + " and clicked element");

		} catch (TimeoutException e) {
			ReportUtils.failTest("Timeout waiting for element: " + Element);
			throw new AssertionError("Timeout waiting for element : " + Element);
		} catch (Exception e) {
			ReportUtils.failTest("Error in Sendkeys_And_Click: " + e.getMessage());
			throw new RuntimeException("Error in Sendkeys_And_Click : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Clear_And_SendKeys(WebElement Element, Object Keys) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		try {
			wait.until(ExpectedConditions.visibilityOf(Element)); // Wait for visibility

			if (!Element.isDisplayed() || !Element.isEnabled()) {
				throw new AssertionError("Element is not visible or enabled : " + Element);
			}

			// Highlight element before clearing and sending keys
			highlightElement(Element);

			ReportUtils.info("Attempting to clear and send keys to element");

			Element.clear();
			ReportUtils.info("Cleared element content");

			String elementName = GetElementName(Element);
			if (elementName == null || elementName.isEmpty()) {
				elementName = Element.toString();
			}

			if (Keys instanceof String) {
				Element.sendKeys((String) Keys);
			} else if (Keys instanceof Integer) {
				Element.sendKeys(String.valueOf(Keys));
			} else {
				throw new IllegalArgumentException(
						"Unsupported data type for SendKeys : " + Keys.getClass().getSimpleName());
			}

			ReportUtils.info("Successfully sent keys : " + Keys + " to element : " + elementName);

		} catch (TimeoutException e) {
			ReportUtils.failTest("Timeout waiting for element : " + Element);
			throw new AssertionError("Timeout waiting for element : " + Element);
		} catch (Exception e) {
			ReportUtils.failTest("Error in Clear_And_SendKeys : " + e.getMessage());
			throw new RuntimeException("Error in Clear_And_SendKeys : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Clear_Data(WebElement Element) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		try {
			wait.until(ExpectedConditions.visibilityOf(Element)); // Wait for visibility

			if (!Element.isDisplayed() || !Element.isEnabled()) {
				throw new AssertionError("Element is not visible or enabled : " + Element);
			}

			// Highlight element before clearing
			highlightElement(Element);

			String elementName = GetElementName(Element);

			ReportUtils.info("Attempting to clear element data : " + elementName);

			Element.clear();

			ReportUtils.info("Successfully cleared element data : " + elementName);

		} catch (TimeoutException e) {
			ReportUtils.failTest("Timeout waiting for element : " + Element);
			throw new AssertionError("Timeout waiting for element : " + Element);
		} catch (Exception e) {
			ReportUtils.failTest("Error in Clear_Data : " + e.getMessage());
			throw new RuntimeException("Error in Clear_Data : " + e.getMessage());
		}
	}

//========================================-- Send Keys Methods Ends --========================================================================//

//========================================-- Radio Button Methods Starts --===================================================================//

	public static boolean Is_Radio_Button_Selected(WebElement RadioButton) {
		ReportUtils.info("Checking if radio button is selected");

		try {
			boolean isSelected = RadioButton.isSelected();
			ReportUtils.info("Radio button selected status : " + isSelected);
			return isSelected;
		} catch (NoSuchElementException e) {
			ReportUtils.failTest("Radio button not found : " + e.getMessage());
			return false;
		} catch (Exception e) {
			ReportUtils.failTest("Error checking radio button selection : " + e.getMessage());
			throw new RuntimeException("Error checking radio button selection : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Select_Radio_Button(WebElement RadioButton) {
		ReportUtils.info("Attempting to select radio button");
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		try {
			if (!Is_Radio_Button_Selected(RadioButton)) {
				wait.until(ExpectedConditions.elementToBeClickable(RadioButton));

				RadioButton.click();
				ReportUtils.info("Successfully selected the radio button");
			} else {
				ReportUtils.info("Radio button is already selected");
			}
		} catch (TimeoutException e) {
			ReportUtils.failTest("Radio button not clickable within timeout : " + e.getMessage());
			throw new RuntimeException("Radio button not clickable within timeout : " + e.getMessage());
		} catch (NoSuchElementException e) {
			ReportUtils.failTest("Failed to select radio button - element not found : " + e.getMessage());
			throw new RuntimeException("Failed to select radio button - element not found : " + e.getMessage());
		} catch (Exception e) {
			ReportUtils.failTest("Error selecting radio button : " + e.getMessage());
			throw new RuntimeException("Error selecting radio button : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Unselect_Radio_Button(WebElement RadioButton) {
		ReportUtils.info("Attempting to unselect radio button");
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		try {
			if (Is_Radio_Button_Selected(RadioButton)) {
				wait.until(ExpectedConditions.elementToBeClickable(RadioButton));

				RadioButton.click();
				ReportUtils.info("Successfully unselected the radio button");
			} else {
				ReportUtils.info("Radio button is already unselected");
			}
		} catch (TimeoutException e) {
			ReportUtils.failTest("Radio button not clickable within timeout : " + e.getMessage());
			throw new RuntimeException("Radio button not clickable within timeout : " + e.getMessage());
		} catch (NoSuchElementException e) {
			ReportUtils.failTest("Failed to unselect radio button - element not found : " + e.getMessage());
			throw new RuntimeException("Failed to unselect radio button - element not found : " + e.getMessage());
		} catch (Exception e) {
			ReportUtils.failTest("Error unselecting radio button : " + e.getMessage());
			throw new RuntimeException("Error unselecting radio button : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static String Get_Selected_Radiobtn_Value(List<WebElement> RadioButtons) {
		ReportUtils.info("Getting selected radio button value");

		try {
			for (WebElement radioButton : RadioButtons) {
				if (Is_Radio_Button_Selected(radioButton)) {
					String value = radioButton.getAttribute("value");
					ReportUtils.info("Found selected radio button with value : " + value);
					return value;
				}
			}
			ReportUtils.info("No selected radio button found");
			return null;
		} catch (NoSuchElementException e) {
			ReportUtils.failTest("Error getting selected radio button value - element not found : " + e.getMessage());
			return null;
		} catch (Exception e) {
			ReportUtils.failTest("Error getting selected radio button value : " + e.getMessage());
			throw new RuntimeException("Error getting selected radio button value : " + e.getMessage());
		}
	}

//========================================-- Radio Button Methods Ends --=====================================================================//

//========================================-- Check Box Methods Starts --======================================================================//	

	public static void Check_Checkbox(List<WebElement> Checkboxes, String Attribute, String Value) {
		ReportUtils.info("Attempting to check checkbox with " + Attribute + "=" + Value);

		try {
			for (WebElement checkbox : Checkboxes) {
				if (checkbox.getAttribute(Attribute).equalsIgnoreCase(Value) && !checkbox.isSelected()) {
					checkbox.click();
					ReportUtils.info("Checked checkbox with " + Attribute + "=" + Value);
					return;
				}
			}
			ReportUtils.info("No matching checkbox found or already checked.");
		} catch (NoSuchElementException e) {
			ReportUtils.failTest("Checkbox not found: " + e.getMessage());
			throw new RuntimeException("Checkbox not found : " + e.getMessage());
		} catch (Exception e) {
			ReportUtils.failTest("Error checking checkbox : " + e.getMessage());
			throw new RuntimeException("Error checking checkbox : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Uncheck_Checkbox(List<WebElement> Checkboxes, String Attribute, String Value) {
		ReportUtils.info("Attempting to uncheck checkbox with " + Attribute + "=" + Value);

		try {
			for (WebElement checkbox : Checkboxes) {
				if (checkbox.getAttribute(Attribute).equalsIgnoreCase(Value) && checkbox.isSelected()) {
					checkbox.click();
					ReportUtils.info("Unchecked checkbox with " + Attribute + "=" + Value);
					return;
				}
			}
			ReportUtils.info("No matching checkbox found or already unchecked.");
		} catch (NoSuchElementException e) {
			ReportUtils.failTest("Checkbox not found : " + e.getMessage());
			throw new RuntimeException("Checkbox not found : " + e.getMessage());
		} catch (Exception e) {
			ReportUtils.failTest("Error unchecking checkbox: " + e.getMessage());
			throw new RuntimeException("Error unchecking checkbox : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Check_Multiple_Checkboxes(List<WebElement> Checkboxes, String Attribute,
			String[] ValuesToSelect) {
		ReportUtils.info("Attempting to check multiple checkboxes");

		int checkedCount = 0;

		try {
			for (WebElement checkbox : Checkboxes) {
				String checkboxValue = checkbox.getAttribute(Attribute);
				for (String value : ValuesToSelect) {
					if (checkboxValue.equalsIgnoreCase(value) && !checkbox.isSelected()) {
						checkbox.click();
						checkedCount++;
						ReportUtils.info("Checked checkbox with value : " + value);
					}
				}
			}
			ReportUtils.info("Checked " + checkedCount + " checkboxes in total.");
		} catch (NoSuchElementException e) {
			ReportUtils.failTest("Some checkboxes not found: " + e.getMessage());
			throw new RuntimeException("Some checkboxes not found : " + e.getMessage());
		} catch (Exception e) {
			ReportUtils.failTest("Error checking multiple checkboxes : " + e.getMessage());
			throw new RuntimeException("Error checking multiple checkboxes : " + e.getMessage());
		}
	}

//========================================-- Check Box Methods Ends --=======================================================================//	

//========================================-- Actions Methods Starts --=======================================================================//	

	public static void Move_To_Element(WebElement Element) {
		ReportUtils.info("Attempting to move to element");

		try {
			String elementName = GetElementName(Element);
			Actions actions = new Actions(getDriver());
			actions.moveToElement(Element).perform();
			ReportUtils.info("Successfully moved to element :" + elementName);
		} catch (Exception e) {
			ReportUtils.failTest("Failed to move to element : " + e.getMessage());
			throw new RuntimeException("Error moving to element : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Move_To_Element_And_Click(WebElement Element) {
		ReportUtils.info("Attempting to move to element and click");

		try {
			String elementName = GetElementName(Element);
			Actions actions = new Actions(getDriver());
			actions.moveToElement(Element).click().perform();
			ReportUtils.info("Successfully moved to element and clicked :" + elementName);
		} catch (Exception e) {
			ReportUtils.failTest("Failed to move and click element : " + e.getMessage());
			throw new RuntimeException("Error moving and clicking element : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Drag_And_Drop(WebElement Source, WebElement Target) {
		ReportUtils.info("Attempting drag and drop operation");

		try {
			String elementName = GetElementName(Source);
			String elementName1 = GetElementName(Target);
			Actions actions = new Actions(getDriver());
			actions.dragAndDrop(Source, Target).perform();
			ReportUtils.info(
					"Successfully completed drag and drop from source " + elementName + "to target " + elementName1);
		} catch (Exception e) {
			ReportUtils.failTest("Drag and drop failed : " + e.getMessage());
			throw new RuntimeException("Error performing drag and drop : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Drag_And_Drop_By_Axis(WebElement Source, int Xaxis, int Yaxis) {
		ReportUtils.info("Attempting drag and drop by axis - X: " + Xaxis + ", Y: " + Yaxis);

		try {
			Actions actions = new Actions(getDriver());
			actions.dragAndDropBy(Source, Xaxis, Yaxis).perform();
			ReportUtils.info("Successfully completed drag and drop by axis");
		} catch (Exception e) {
			ReportUtils.failTest("Drag and drop by axis failed: " + e.getMessage());
			throw new RuntimeException("Error performing drag and drop by axis: " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Right_Click(WebElement Element) {
		ReportUtils.info("Attempting right click on element");

		try {
			String elementName = GetElementName(Element);
			Actions actions = new Actions(getDriver());
			actions.contextClick(Element).perform();
			ReportUtils.info("Successfully performed right click :" + elementName);
		} catch (Exception e) {
			ReportUtils.failTest("Right-click failed : " + e.getMessage());
			throw new RuntimeException("Error performing right-click : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Click_And_Hold(WebElement Element) {
		ReportUtils.info("Attempting click and hold on element");

		try {
			String elementName = GetElementName(Element);
			Actions actions = new Actions(getDriver());
			actions.clickAndHold(Element).perform();
			ReportUtils.info("Successfully performed click and hold :" + elementName);
		} catch (Exception e) {
			ReportUtils.failTest("Click and hold failed : " + e.getMessage());
			throw new RuntimeException("Error performing click and hold : " + e.getMessage());
		}
	}

//========================================-- Actions Methods Ends --=========================================================================//

//========================================-- Boolean Methods Starts --=========================================================================//	

	public static boolean Is_Displayed(WebElement Element) {
		String elementName = GetElementName(Element);
		ReportUtils.info("Checking if element is displayed : " + elementName);

		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			wait.until(ExpectedConditions.visibilityOf(Element));

			boolean isDisplayed = Element.isDisplayed();
			ReportUtils.info("Element displayed status : " + isDisplayed);
			return isDisplayed;
		} catch (Exception e) {
			ReportUtils.failTest("Element not displayed : " + e.getMessage());
			return false;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static boolean Is_Clickable(WebElement Element) {
		String elementName = GetElementName(Element);
		ReportUtils.info("Checking if element is clickable : " + elementName);

		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			wait.until(ExpectedConditions.elementToBeClickable(Element));

			boolean isClickable = Element.isEnabled();
			ReportUtils.info("Element clickable status : " + isClickable);
			return isClickable;
		} catch (Exception e) {
			ReportUtils.failTest("Element not clickable : " + e.getMessage());
			return false;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static boolean Is_Enabled(WebElement Element) {
		String elementName = GetElementName(Element);
		ReportUtils.info("Checking if element is enabled : " + elementName);

		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			wait.until(ExpectedConditions.visibilityOf(Element));

			boolean isEnabled = Element.isEnabled();
			ReportUtils.info("Element enabled status : " + isEnabled);
			return isEnabled;
		} catch (Exception e) {
			ReportUtils.failTest("Element not enabled : " + e.getMessage());
			return false;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static boolean Is_Selected(WebElement Element) {
		String elementName = GetElementName(Element);
		ReportUtils.info("Checking if element is selected : " + elementName);

		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			wait.until(ExpectedConditions.visibilityOf(Element));

			boolean isSelected = Element.isSelected();
			ReportUtils.info("Element selected status : " + isSelected);
			return isSelected;
		} catch (Exception e) {
			ReportUtils.failTest("Element not selected : " + e.getMessage());
			return false;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static boolean Is_Present(WebElement Element) {
		String elementName = GetElementName(Element);
		ReportUtils.info("Checking if element is present : " + elementName);

		try {
			boolean isPresent = Element != null;
			ReportUtils.info("Element present status : " + isPresent);
			return isPresent;
		} catch (Exception e) {
			ReportUtils.failTest("Element presence check failed : " + e.getMessage());
			return false;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static boolean Contains_Text(WebElement Element, String text) {
		String elementName = GetElementName(Element);
		ReportUtils.info("Checking if element contains text : '" + text + "' in " + elementName);

		try {
			JavascriptExecutor js = (JavascriptExecutor) getDriver();
			boolean containsText = (Boolean) js.executeScript("return arguments[0].textContent.includes(arguments[1]);",
					Element, text);

			ReportUtils.info("Element contains text status : " + containsText);
			return containsText;
		} catch (Exception e) {
			ReportUtils.failTest("Error checking text in element : " + e.getMessage());
			return false;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

//========================================-- Boolean Methods Ends --=======================================================================//

//========================================-- Random Methods Starts --======================================================================//

	public static String Random_String(int length) {
		ReportUtils.info("Generating random string of length : " + length);

		String randomString = RandomStringUtils.randomAlphabetic(length);
		ReportUtils.info("Generated random string : " + randomString);
		return randomString;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static int Random_Number(int digit) {
		ReportUtils.info("Generating random number with " + digit + " digits");

		String randomNumberString = faker.number().digits(digit);
		int randomNumber = Integer.parseInt(randomNumberString);

		ReportUtils.info("Generated random number : " + randomNumber);
		return randomNumber;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static Object Random_Number(int digit, boolean asString) {
		ReportUtils.info("Generating random number with " + digit + " digits, return as string : " + asString);

		String randomNumberString = faker.number().digits(digit);

		if (asString) {
			ReportUtils.info("Generated random number string : " + randomNumberString);
			return randomNumberString;
		} else {
			int randomNumber = Integer.parseInt(randomNumberString);
			ReportUtils.info("Generated random number : " + randomNumber);
			return randomNumber;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static int Random_Number_Between(int start, int end) {
		ReportUtils.info("Generating random number between " + start + " and " + end);

		int randomNumber = faker.number().numberBetween(start, end);
		ReportUtils.info("Generated random number : " + randomNumber);
		return randomNumber;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static String Random_Email() {
		ReportUtils.info("Generating random email address");

		String randomEmail = faker.internet().emailAddress();
		ReportUtils.info("Generated random email : " + randomEmail);
		return randomEmail;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static String Random_Name(String nameType) {
		ReportUtils.info("Generating random name of type : " + nameType);

		String generatedName;
		switch (nameType.toLowerCase()) {
		case "firstname":
			generatedName = faker.name().firstName();
			break;
		case "lastname":
			generatedName = faker.name().lastName();
			break;
		case "fullname":
			generatedName = faker.name().fullName();
			break;
		case "username":
			generatedName = faker.name().username();
			break;
		default:
			ReportUtils.info("Invalid name type specified : " + nameType);
			return "";
		}

		ReportUtils.info("Generated random name : " + generatedName);
		return generatedName;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static String Random_Address() {
		ReportUtils.info("Generating random address");

		String randomAddress = faker.address().streetAddress();
		ReportUtils.info("Generated random address: " + randomAddress);
		return randomAddress;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static String Random_LoremIpsum(int paragraphs) {
		ReportUtils.info("Generating " + paragraphs + " paragraphs of Lorem Ipsum");

		String randomLoremIpsum = faker.lorem().paragraph(paragraphs);
		ReportUtils.info("Generated Lorem Ipsum text");
		return randomLoremIpsum;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static String Get_Random_Value(List<String> values) {
		if (values == null || values.isEmpty()) {
			ReportUtils.info("Provided list is null or empty.");
			return "";
		}

		ReportUtils.info("Getting random value from list of size: " + values.size());

		int randomIndex = random.nextInt(values.size());
		String randomValue = values.get(randomIndex);
		ReportUtils.info("Selected random value: " + randomValue);
		return randomValue;
	}

//========================================-- Random Methods Ends --=======================================================================//

//========================================-- Web Table Methods Starts --=================================================================//

	public static void Webtable(List<WebElement> Rows, List<WebElement> Columns) {
		ReportUtils.info("Processing web table");

		int rowCount = Rows.size();
		ReportUtils.info("Number of rows : " + rowCount);

		int columnCount = Columns.size();
		ReportUtils.info("Number of columns : " + columnCount);

		WebElement cellAddress = getDriver().findElement(null);
		String value = cellAddress.getText();
		ReportUtils.info("Cell valu e: " + value);
	}

//========================================-- Frame Methods Starts --=======================================================================//

	public static void Switch_To_Frame_By_Index(int index) {
		ReportUtils.info("Switching to frame by index : " + index);
		try {
			getDriver().switchTo().frame(index);
			ReportUtils.info("Successfully switched to frame at index : " + index);
		} catch (NoSuchFrameException e) {
			ReportUtils.info("Frame not found at index : " + index);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Switch_Out_Of_Frame() {
		ReportUtils.info("Switching out of all frames to default content");
		getDriver().switchTo().defaultContent();
		ReportUtils.info("Successfully switched to default content");
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Switch_To_Frame_By_Name_Or_Id(String frameNameOrId) {
		ReportUtils.info("Switching to frame by name/id : " + frameNameOrId);
		try {
			getDriver().switchTo().frame(frameNameOrId);
			ReportUtils.info("Successfully switched to frame : " + frameNameOrId);
		} catch (NoSuchFrameException e) {
			ReportUtils.info("Frame not found with name/id : " + frameNameOrId);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Switch_To_Parent_Frame() {
		ReportUtils.info("Switching to parent frame");
		getDriver().switchTo().parentFrame();
		ReportUtils.info("Successfully switched to parent frame");
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static int Get_Number_Of_Frames() {
		ReportUtils.info("Getting the total number of frames on the page");
		int numberOfFrames = getDriver().findElements(By.tagName("iframe")).size();
		ReportUtils.info("Total number of frames found: " + numberOfFrames);
		return numberOfFrames;
	}

//========================================-- Frame Methods Ends --=======================================================================//	

//========================================-- Navigation Methods Starts --====================================================================//

	public static void Navigate_Back() {
		ReportUtils.info("Navigating back");

		getDriver().navigate().back();
		ReportUtils.info("Navigated back to previous page");
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Navigate_Forward() {
		ReportUtils.info("Navigating forward");

		getDriver().navigate().forward();
		ReportUtils.info("Navigated forward to next page");
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Refresh_Page() {
		ReportUtils.info("Refreshing page");

		getDriver().navigate().refresh();
		ReportUtils.info("Page refreshed");
	}

//========================================-- Navigation Methods Ends --=====================================================================//

//========================================-- Upload files Methods Starts --===============================================================//

	public static void Upload_File_By_JS(WebElement inputElement, String filePath) {
		ReportUtils.info("Uploading file using JavaScript : " + filePath);

		File file = new File(filePath);
		if (!file.exists()) {
			ReportUtils.info("File not found: " + filePath);
			throw new IllegalArgumentException("File not found: " + filePath);
		}

		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("arguments[0].value = arguments[1];", inputElement, filePath);
		ReportUtils.info("File uploaded successfully via JavaScript");
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Upload_File_By_SendKeys(WebElement inputElement, String filePath) {
		ReportUtils.info("Uploading file using sendKeys : " + filePath);

		File file = new File(filePath);
		if (!file.exists()) {
			ReportUtils.info("File not found : " + filePath);
			throw new IllegalArgumentException("File not found : " + filePath);
		}

		inputElement.sendKeys(filePath);
		ReportUtils.info("File uploaded successfully via sendKeys");
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Upload_File_By_Robot(WebElement inputElement, String filePath) {
		ReportUtils.info("Uploading file using Robot class : " + filePath);

		File file = new File(filePath);
		if (!file.exists()) {
			ReportUtils.info("File not found : " + filePath);
			throw new IllegalArgumentException("File not found : " + filePath);
		}

		try {
			inputElement.click();

			Robot robot = new Robot();
			StringSelection stringSelection = new StringSelection(filePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

			ReportUtils.info("File uploaded successfully via Robot class");
		} catch (AWTException e) {
			ReportUtils.info("Failed to upload file using Robot class : " + e.getMessage());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Upload_File_From_Local(String filePath) {
		ReportUtils.info("Uploading local file : " + filePath);

		File file = new File(filePath);
		if (!file.exists()) {
			ReportUtils.info("File not found : " + filePath);
			throw new IllegalArgumentException("File not found : " + filePath);
		}

		try {
			StringSelection ss = new StringSelection(filePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

			Thread.sleep(1000);

			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

			ReportUtils.info("Local file uploaded successfully");
		} catch (AWTException | InterruptedException e) {
			ReportUtils.info("Failed to upload local file : " + e.getMessage());
		}
	}
//========================================-- Upload files Methods Ends --===============================================================//

//========================================-- Utility Methods Starts --================================================================//

	public static List<String> Get_Text_From_WebElement_List(List<WebElement> elementList) {
		ReportUtils.info("Getting text from WebElement list of size : " + elementList.size());

		List<String> textList = new ArrayList<>();
		for (WebElement element : elementList) {
			textList.add(element.getText().trim()); // Trim to remove leading/trailing spaces
		}

		ReportUtils.info("Retrieved " + textList.size() + " text elements");
		return textList;
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static boolean Compare_WebElement_Text_Contains(List<WebElement> List1, List<WebElement> List2) {
		ReportUtils.info("Comparing text between two WebElement lists");

		if (List1.size() != List2.size()) {
			ReportUtils.info("Lists have different sizes : " + List1.size() + " vs " + List2.size());
			return false;
		}

		boolean result = IntStream.range(0, List1.size())
				.allMatch(i -> List1.get(i).getText().contains(List2.get(i).getText())
						|| List2.get(i).getText().contains(List1.get(i).getText()));

		ReportUtils.info("Text comparison result : " + result);
		return result;
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static boolean compareListTextContains(List<String> cellTexts, List<WebElement> list2) {
		ReportUtils.info("Comparing text between String list and WebElement list");

		if (cellTexts.size() != list2.size()) {
			ReportUtils.info("Lists have different sizes : " + cellTexts.size() + " vs " + list2.size());
			return false;
		}

		for (int i = 0; i < cellTexts.size(); i++) {
			String text1 = cellTexts.get(i);
			String text2 = list2.get(i).getText();

			if (!(text1.contains(text2) || text2.contains(text1))) {
				ReportUtils.info("Text mismatch at index " + i);
				return false;
			}
		}

		ReportUtils.info("All texts match");
		return true;
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static boolean Compare_String_List_Ignoring_Icons(List<String> list1, List<String> list2) {
		ReportUtils.info("Comparing string lists ignoring icons");

		if (list1.size() != list2.size()) {
			ReportUtils.info("Lists have different sizes : " + list1.size() + " vs " + list2.size());
			return false;
		}

		boolean result = IntStream.range(0, list1.size()).allMatch(i -> {
			String text1 = list1.get(i).replaceAll("[↓↑]", "").trim();
			String text2 = list2.get(i).replaceAll("[↓↑]", "").trim();
			return text1.contains(text2) || text2.contains(text1);
		});

		ReportUtils.info("String comparison result : " + result);
		return result;
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public List<String> Get_Text_Of_Checked_Options(List<WebElement> List1) {
		ReportUtils.info("Getting text of checked options");

		List<String> checkedOptionsText = new ArrayList<>();

		for (WebElement option : List1) {
			WebElement checkbox = option.findElement(By.xpath("preceding-sibling::input[@type='checkbox']"));
			if (checkbox.isSelected()) {
				checkedOptionsText.add(option.getText());
				ReportUtils.info("Found checked option : " + option.getText());
			}
		}

		ReportUtils.info("Found " + checkedOptionsText.size() + " checked options");
		return checkedOptionsText;
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static void Click_Data_In_CusNo_Column(String dataToClick, String cusNoHeaderText) {
		logInfos("Clicking data in CusNo column - Header: " + cusNoHeaderText + ", Data : " + dataToClick);

		WebElement cusNoHeader = getDriver().findElement(
				By.xpath("//table[@class=\"table cus-table-row-spacing mb-0\"]//th[text()='" + cusNoHeaderText + "']"));

		int cusNoColumnIndex = Get_Index_Of_Element(cusNoHeader);
		ReportUtils.info("Found CusNo column at index: " + cusNoColumnIndex);

		List<WebElement> rows = getDriver().findElements(
				By.xpath("//table[@class=\"table cus-table-row-spacing mb-0\"]/tbody/tr[@class=\"bg-white\"]"));

		for (WebElement row : rows) {
			WebElement cusNoCell = row.findElement(By.xpath("./td[" + cusNoColumnIndex + "]"));
			if (cusNoCell.getText().equals(dataToClick)) {
				cusNoCell.click();
				ReportUtils.info("Clicked on matching cell with data : " + dataToClick);
				break;
			}
		}
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	private static int Get_Index_Of_Element(WebElement Element) {
		ReportUtils.info("Getting index of element");

		List<WebElement> siblings = Element.findElements(By.xpath("./preceding-sibling::*"));
		int index = siblings.size() + 1;
		ReportUtils.info("Element index: " + index);
		return index;
	}

//-----------------------------------------------------------------------------------------------------------------------------------------//

	public static String getElementText(WebElement element) {
		ReportUtils.info("Getting text from element using multiple methods");

		if (element == null) {
			ReportUtils.info("Element is null");
			return "Element is null";
		}

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOf(element));

		String text = element.getText().trim();
		if (!text.isEmpty()) {
			ReportUtils.info("Found text using getText(): " + text);
			return text;
		}

		text = element.getAttribute("value");
		if (text != null && !text.trim().isEmpty()) {
			ReportUtils.info("Found text using value attribute: " + text);
			return text;
		}

		text = element.getAttribute("placeholder");
		if (text != null && !text.trim().isEmpty()) {
			ReportUtils.info("Found text using placeholder attribute: " + text);
			return text;
		}

		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		text = (String) js.executeScript("return arguments[0].textContent.trim();", element);
		if (text != null && !text.isEmpty()) {
			ReportUtils.info("Found text using JavaScript: " + text);
			return text;
		}

		ReportUtils.info("No text found in element");
		return "No text available";
	}
	
//-----------------------------------------------------------------------------------------------------------//

	public static String GetElementName(WebElement element) {
		if (element == null) {
			return "Null WebElement";
		}

		String elementName = "";

		try {
			// 1. Try getAccessibleName (if supported)
			elementName = element.getAccessibleName();

			// 2. Try useful attributes
			if (elementName == null || elementName.trim().isEmpty())
				elementName = element.getAttribute("aria-label");

			if (elementName == null || elementName.trim().isEmpty())
				elementName = element.getAttribute("placeholder");

			if (elementName == null || elementName.trim().isEmpty())
				elementName = element.getAttribute("name");

			if (elementName == null || elementName.trim().isEmpty())
				elementName = element.getAttribute("id");

			if (elementName == null || elementName.trim().isEmpty())
				elementName = element.getAttribute("title");

			if (elementName == null || elementName.trim().isEmpty())
				elementName = element.getAttribute("value");

			// 3. Try visible text
			if (elementName == null || elementName.trim().isEmpty())
				elementName = element.getText();

			// 4. Try using JS to fetch label text
			if ((elementName == null || elementName.trim().isEmpty()) && getDriver() instanceof JavascriptExecutor) {
				JavascriptExecutor js = (JavascriptExecutor) getDriver();
				elementName = (String) js.executeScript("var el = arguments[0];"
						+ "if (el.labels && el.labels.length > 0) return el.labels[0].innerText;"
						+ "if (el.getAttribute('aria-label')) return el.getAttribute('aria-label');"
						+ "if (el.getAttribute('title')) return el.getAttribute('title');"
						+ "return el.innerText || el.getAttribute('value') || el.getAttribute('name') || el.getAttribute('id');",
						element);
			}

			// 5. Final fallback
			if (elementName == null || elementName.trim().isEmpty())
				elementName = element.toString();

		} catch (Exception e) {
			elementName = "UnknownElement(" + element.toString() + ")";
		}

		return elementName.trim();
	}
//-----------------------------------------------------------------------------------------------------------//
	
	private static void highlightElement(WebElement element) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) getDriver();
			js.executeScript("arguments[0].style.border='3px solid " + ConfigReader.prop.getProperty("color") + "'",
					element);
			// Optional: keep highlight for 200ms
			Thread.sleep(200);
			js.executeScript("arguments[0].style.border=''", element); // remove highlight
		} catch (Exception e) {

		}
	}
}