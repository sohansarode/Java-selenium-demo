package pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import base.BrowserSetup;
import utils.CommonUtils;
import utils.ReportUtils;
import utils.WaitUtils;

public class LoginPage  {
	
	private WebDriver driver; 

	@FindBy(xpath = "//*[@name=\"username\"]")
	private WebElement Email;

	@FindBy(xpath = "//*[@name=\"password\"]")
	private WebElement Password;

	@FindBy(xpath = "//*[@type=\"submit\"]")
	private WebElement Loginbtn;

	@FindBy(xpath = "//*[text()=\"Invalid credentials\"]")
	private WebElement Invalid_credential_message;

	@FindBy(xpath = "//*[@class=\"oxd-topbar-header-breadcrumb\"]")
	private WebElement Dashboard;

	@FindBy(xpath = "//*[@class=\"symbol symbol-40px me-5\"]//*[@alt=\"Logo\"]")
	private List<WebElement> Profile1;

	SoftAssert softAssert = new SoftAssert();
	
//-----------------------------------------------------------------------------------------------------------//

	 public LoginPage() {
	        this.driver = BrowserSetup.getDriver();  
	        PageFactory.initElements(driver, this);
	    }

//-----------------------------------------------------------------------------------------------------------//
	public void Login_test_with_Invalid_data(String email, String password) {
		try {

			CommonUtils.Sendkeys(Email, email);

			CommonUtils.Sendkeys(Password, password);

			CommonUtils.Click(Loginbtn);

			ReportUtils.passTest("Invalid Login Test Passed");

		} catch (AssertionError e) {
			ReportUtils.failTest("Invalid Login Test Failed: " + e.getMessage());
			throw e;
		}
	}
	
//-----------------------------------------------------------------------------------------------------------//

	public void Login_test_with_valid_data(String email, String password) throws InterruptedException {
		try {

			CommonUtils.Clear_And_SendKeys(Email, email);
			
			CommonUtils.Clear_And_SendKeys(Password, password);

			CommonUtils.Click(Loginbtn);

			WaitUtils.Hard_Wait(3000);
			
			Assert.assertTrue(CommonUtils.Is_Displayed(Dashboard), "Dashboard is not displayed");

			ReportUtils.passTest("Valid Login Test Passed");
			
		} catch (AssertionError e) {
			ReportUtils.failTest("Valid Login Test Failed: " + e.getMessage());
			throw e;
		}
	}
}
