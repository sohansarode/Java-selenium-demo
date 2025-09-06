package utils;

import java.io.FileInputStream;
import java.util.Properties;
import org.apache.logging.log4j.*;


public class ConfigReader {

	public static Properties prop, prop1,prop2;
	public static Logger logger;

//----------------------------------------------------------------------------------------------------------//
	// This method is use to Read properties from property file
	public static void Read_Property_File() {
		logger = LogManager.getLogger(ConfigReader.class);

		try {
			String projectPath = System.getProperty("user.dir");
			// Construct relative path to config.properties
			String path = projectPath + "/src/test/resources/config.properties";

			prop = new Properties();
			prop.load(new FileInputStream(path));

		} catch (Exception e) {
			System.out.println("Not able to read property file");
			e.printStackTrace();
		}
	}

//----------------------------------------------------------------------------------------------------------//
	// This method is use to Read properties from QA Environment property file
	public static void Read_QA_Property() {
		logger = LogManager.getLogger(ConfigReader.class);

		try {
			String projectPath = System.getProperty("user.dir");
			String path = projectPath + "/src/main/resources/environments/QAEnvironment.properties";

			prop1 = new Properties();
			prop1.load(new FileInputStream(path));

		} catch (Exception e) {
			System.out.println("Not able to read QA property file");
			e.printStackTrace();
		}
	}

//----------------------------------------------------------------------------------------------------------//
	// This method is use to Read properties from Dev Environment property file
	public static void Read_Live_Property() {
		logger = LogManager.getLogger(ConfigReader.class);

		
		try {
			
			String projectPath = System.getProperty("user.dir");
			String path = projectPath + "/src/main/resources/environments/LiveEnvironment.properties";
			prop2 = new Properties();
			prop2.load(new FileInputStream(path));

		} catch (Exception E) {
			System.out.println("Not able to read Dev property file");
		}

	}


}
