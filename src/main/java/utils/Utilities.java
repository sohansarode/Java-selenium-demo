package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import base.BrowserSetup;

public class Utilities {

	private static WebDriver getDriver() {
        return BrowserSetup.getDriver();  // ✅ ThreadLocal driver access
    }
//----------------------------------------------------------------------------------------------------------//

	// This method is use to Scroll till the element on page

	protected void Scroll_Into_View(WebElement Element) {
		// Create a JavascriptExecutor instance
		JavascriptExecutor ScriptExecutor = (JavascriptExecutor)getDriver() ;

		// Execute JavaScript to scroll the specified element into view
		ScriptExecutor.executeScript("arguments[0].scrollIntoView();", Element);

		// Log information about scrolling
		ConfigReader.logger.info("Scrolled to element: " + Element.getText());
	}
//----------------------------------------------------------------------------------------------------------//

	// This method is use to scroll by pixel[X-Axis,Y-Axis]

	protected void Scroll(int Horizontal, int Vertical) {
		// Create a JavascriptExecutor instance
		JavascriptExecutor ScriptExecutor = (JavascriptExecutor)getDriver() ;

		// Execute JavaScript to scroll the window by the specified horizontal and
		// vertical offsets
		ScriptExecutor.executeScript("window.scrollBy(" + Horizontal + "," + Vertical + ")");

		// Log information about the scroll operation
		ConfigReader.logger.info("Scrolled horizontally & vertically by " + Horizontal + " pixels and " + Vertical + " pixels");
	}

//----------------------------------------------------------------------------------------------------------//
	// This method is use to scroll directly at bottom of page

	protected void Scroll_At_Bottom() {
		// Create a JavascriptExecutor instance
		JavascriptExecutor ScriptExecutor = (JavascriptExecutor) getDriver() ;

		// Execute JavaScript to scroll the window to the bottom of the document
		ScriptExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");

		// Log information about scrolling to the bottom
		ConfigReader.logger.info("Scrolled to the bottom of the page");
	}
//----------------------------------------------------------------------------------------------------------//

	// This method is use to scroll directly at top of page

	protected void Scroll_At_Top() {
		// Create a JavascriptExecutor instance
		JavascriptExecutor ScriptExecutor = (JavascriptExecutor) getDriver() ;

		// Execute JavaScript to scroll the window to the top of the document
		ScriptExecutor.executeScript("window.scrollTo(0, 0);");

		// Log information about scrolling to the top
		ConfigReader.logger.info("Scrolled to the top of the page");
	}
//----------------------------------------------------------------------------------------------------------//
	// This method is use to find the hiddenelement which is not visible on
	// screen ,so used javascript executor for it

	protected WebElement Find_Hidden_Element(String Element, String Loacatortype) {
		// Create a JavascriptExecutor instance
		JavascriptExecutor ScriptExecutor = (JavascriptExecutor) getDriver() ;

		// Initialize script variable
		String Script = "";

		// Build script based on locator type
		switch (Loacatortype.toLowerCase()) {
		case "id":
			Script = "return document.getElementById(arguments[0]);";
			break;
		case "class":
			Script = "return document.getElementsByClassName(arguments[0])[0];";
			break;
		case "tag":
			Script = "return document.getElementsByTagName(arguments[0])[0];";
			break;
		default:
			throw new IllegalArgumentException("Unsupported locator type");
		}

		// Execute JavaScript to find the hidden element
		WebElement Ele = (WebElement) ScriptExecutor.executeScript(Script, Element);

		return Ele;
	}

//----------------------------------------------------------------------------------------------------------//

	// This method is use to take screenshot of failed test

	 public static String Capture_Screenshot() {
	        try {
	            String timestamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());

	            // Take screenshot
	            TakesScreenshot scrshot = ((TakesScreenshot) getDriver());
	            File srcFile = scrshot.getScreenshotAs(OutputType.FILE);

	            // Build screenshot folder inside current run folder
	            String folderPath = ReportUtils.getReportFolderPath() + "/Screenshots";
	            new File(folderPath).mkdirs();

	            String destinationPath = folderPath + "/Screenshot_" + timestamp + ".png";
	            File destFile = new File(destinationPath);

	            // Copy screenshot
	            FileUtils.copyFile(srcFile, destFile);

	            return destinationPath;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
//----------------------------------------------------------------------------------------------------------//

	protected static void Capture_Screenshot_Ints() {
		File srcfile = ((TakesScreenshot) getDriver() ).getScreenshotAs(OutputType.FILE);
		String destinationpath = ReportUtils. getReportFolderPath() + "/screenshot.png";

		try {
			FileUtils.copyFile(srcfile, new File(destinationpath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
