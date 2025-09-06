# Seleniumdemo5 – Test Automation Framework 🚀

## 📌 Overview

This is a hybrid **UI + API automation testing framework** built on **Java, Selenium WebDriver, and TestNG** with **Maven**.
It follows the **Page Object Model (POM)** design pattern and integrates **ExtentReports** and **Allure Reports** for advanced reporting.

The framework supports:

* ✅ Web UI automation with Selenium
* ✅ API testing with Rest-Assured
* ✅ Data-driven testing using Excel
* ✅ Parallel execution with ThreadLocal WebDriver
* ✅ Environment-based execution (QA, Live, etc.)
* ✅ Rich HTML & Allure reports with screenshots

---

## 🏗️ Project Structure

```
Seleniumdemo5/
│
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── base/
│   │   │   │   └── BrowserSetup.java       # ThreadLocal WebDriver setup & teardown
│   │   │   │   └── My_Screen_Recorder.java # Captures screen video during test execution 
│   │   │   ├── pages/
│   │   │   │   └── Login_Page.java         # Example Page Object (locators + actions for Login page)
│   │   │   │
│   │   │   ├── utils/
│   │   │   │   ├── CommonUtils.java        # Generic helper methods
                ├── ConfigReader.java       # Reads environment properties
│   │   │   │   ├── ExcelUtils.java         # Utility for reading Excel test data
│   │   │   │   ├── JsonReader.java         # Utility for reading Json test data
                ├── ReportUtils.java        # ExtentReports & Allure setup & logging helpers
│   │   │   │   ├── Utilities.java          # Scroll, handle hidden elements, screenshots, etc.
│   │   │   │   └── WaitUtils.java          # Explicit wait helpers
│   │   │   │
│   │   │   └── api/
│   │   │       └── TestBase.java           # Shared setup for API tests (Rest-Assured config)
│   │   │
│   │   └── resources/
            ├──data                           
                ├── Excels                    #Excel & json files to read
                ├── Jsons                     #Excel & json files to read  
│   │       ├── environments/
│   │       │   ├── QAEnvironment.properties  # QA env config (URLs, creds, etc.)
│   │       │   └── LiveEnvironment.properties# Live env config
│   │       │
│   │       └── log4j2.properties             # Log4j2 logging config
│   │
│   └── test
│       ├── java
│       │   ├── apitest/
│       │   │   ├── Get_Profile.java         # API test example
│       │   │   └── Other_API_Tests.java     # Other REST API tests
│       │   │
│       │   ├── pagestest/
│       │       ├── Login.java               # UI test for login page reading data from excel file
│       │       └── LoginExceldataprovider.java      # UI test for login page reading data from excel file using dataprovider
│       │       └── LoginJson.java           # UI test for login page reading data from Json file
        │   
│       │   
│       │
│       │
│       └── resources/
│           ├── testng-All_in_one.xml        # Runs both API + UI suites
│           ├── smoke.xml                    # Runs only smoke tests
│           └── regression.xml               # Runs only regression tests
│
├── Maven_&_Allure_Commands_to_run           # Terminal commands to run maven & allure reports
├── pom.xml                                  # Maven build config & dependencies
├── README.md                                # Project documentation
└── .gitignore                               # Git ignore rules


---

## ⚙️ Tech Stack

* **Language**: Java 17+
* **Build Tool**: Maven
* **Testing Framework**: TestNG
* **UI Automation**: Selenium WebDriver
* **API Automation**: Rest-Assured
* **Reports**: ExtentReports 5.x & Allure
* **Logging**: Log4j2
* **Data**: Excel(Apache POI),JSON

---

## ▶️ How to Run Tests

### 1. Clone the Repository

```bash
git clone https://github.com/sohansarode/Java-selenium-demo.git
cd Seleniumdemo5
```

### 2. Install Dependencies

```bash
mvn clean install -DskipTests
```

### 3. Run All Tests

```bash
mvn clean test -DsuiteXmlFile=testng-All_in_one.xml -Denv=QA
```

### 4. Run Specific Suite

```bash
mvn clean test -DsuiteXmlFile=smoke.xml
mvn clean test -DsuiteXmlFile=regression.xml
```

📌 `-Denv` flag controls which environment config is loaded from `src/main/resources/environments/`.

---

## 📊 Reporting

### ExtentReports

* HTML report generated inside `/Reports/ExtentReport.html`
* Includes logs and screenshots

### Allure Reports

Generate and open Allure report:

```bash
allure serve allure-results
```

---

## 🧩 Key Utilities

* **BrowserSetup** → Manages WebDriver
* **WaitUtils** → Explicit wait helpers
* **ReportUtils** → ExtentReports with screenshots
* **ExcelUtils** → Excel-based data provider
* **ConfigReader** → Loads environment configs

---

## 🧪 Execution Flow

1. TestNG suite triggers execution
2. `BrowserSetup` initializes driver (parallel-safe)
3. Page objects perform actions on UI
4. API tests validate responses with Rest-Assured
5. Reports/logs/screenshots generated

---

## 📜 License

This project is for **learning and demo purposes**. You may adapt and extend it.

---

## 📂 .gitignore

A `.gitignore` file is included to keep the repository clean:

```gitignore
# Maven
/target/
/dependency-reduced-pom.xml

# Test Reports & Logs
/test-output/
/reports/
/screenshots/
/logs/
/allure-results/
/allure-report/
/surefire-reports/
/extent-reports/
/*.log

# IDE Files
.project
.classpath
.settings/
.idea/
*.iml
out/
.vscode/

# OS Specific
.DS_Store
Thumbs.db
```
