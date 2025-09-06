package pagestest;

import org.testng.annotations.Test;

import base.BrowserSetup;
import pages.LoginPage;
import utils.CommonUtils;
import utils.JsonReader;
import utils.ReportUtils;
import utils.WaitUtils;

public class LoginJson extends BrowserSetup {
	
	private String Validemail, ValidPassword;

	{
		try {
			String projectPath = System.getProperty("user.dir");
			String filePath = projectPath + "/src/main/resources/data/Jsons/Normalfile.json";
			JsonReader jsonReader = new JsonReader(filePath); // Update with correct path
			Validemail = jsonReader.getValue("Email");
			ValidPassword = jsonReader.getValue("Password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//-----------------------------------------------------------------------------------------------------------//

	@Test(enabled = true, testName = "Test Login Feature", priority = 0)
	public void Login_test_with_Invalid_data_test() throws InterruptedException {
		LoginPage login = new LoginPage();
		ReportUtils.startTest("Invalid Login Test Started with Json");

		login.Login_test_with_Invalid_data(Validemail, CommonUtils.Random_Number(6) + "@" + "V" + "v");
		WaitUtils.Hard_Wait(5000);	}

//-----------------------------------------------------------------------------------------------------------//
	
	@Test(enabled = true, description = "Verifies successful login with valid credentials", priority = 1)
	public void Login_test_with_valid_data_test() throws InterruptedException {
		LoginPage login = new LoginPage();
		ReportUtils.startTest("Valid Login Test Started with Json");

		login.Login_test_with_valid_data(Validemail, ValidPassword);
	}
}
