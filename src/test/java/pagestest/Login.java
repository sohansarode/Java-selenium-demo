package pagestest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;
import base.BrowserSetup;
import pages.LoginPage;
import utils.CommonUtils;
import utils.ExcelUtils;
import utils.ReportUtils;
import utils.WaitUtils;

public class Login extends BrowserSetup {
	
	private String Validemail, ValidPassword;
	private static List<String> failedTests = new ArrayList<>();
	private static List<String> passedTests = new ArrayList<>();
	
//-----------------------------------------------------------------------------------------------------------//
	
	{
		try {
			Validemail = ExcelUtils.Get_String_Cell_Data("Loginpage", "Email", "Values");
			ValidPassword = ExcelUtils.Get_String_Cell_Data("Loginpage", "Password", "Values");
		} catch (IOException e) {
			ReportUtils.failTest("Error reading login credentials from Excel: " + e.getMessage());
			e.printStackTrace();
		}
	}

// --------------------------------------------------------------------------------------------------//

	@Test(enabled = true, testName = "Test Login Feature", priority = 0, groups = { "regression" })
	public void Login_test_with_Invalid_data_test() {
		try {
			ReportUtils.startTest("Invalid Login Test Started");
			LoginPage login = new LoginPage();
			

			login.Login_test_with_Invalid_data(Validemail, CommonUtils.Random_Number(6) + "@Vv");
			WaitUtils.Hard_Wait(5000);

			passedTests.add("Login_test_with_Invalid_data_test");
		} catch (Exception e) {
			ReportUtils.failTest("Invalid Login Test Failed: " + e.getMessage());
			failedTests.add("Login_test_with_Invalid_data_test");
			e.printStackTrace();
		}
	}

// --------------------------------------------------------------------------------------------------//

	@Test(enabled = true, description = "Verifies successful login with valid credentials", priority = 1, groups = {
			"smoke", "regression" })
	public void Login_test_with_valid_data_test() {
		try {
			ReportUtils.startTest("Valid Login Test Started");
			LoginPage login = new LoginPage();
			

			login.Login_test_with_valid_data(Validemail, ValidPassword);

			passedTests.add("Login_test_with_valid_data_test");
		} catch (Exception e) {
			ReportUtils.failTest("Valid Login Test Failed: " + e.getMessage());
			failedTests.add("Login_test_with_valid_data_test");
			e.printStackTrace();
		}
	}
}
