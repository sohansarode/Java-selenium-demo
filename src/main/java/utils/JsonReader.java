package utils;

import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonReader {

	private JSONObject jsonObject;

//-----------------------------------------------------------------------------------------------------------//
	// Constructor: Load JSON file
	public JsonReader(String filePath) {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(filePath));
			jsonObject = (JSONObject) obj;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
// -----------------------------------------------------------------------------------------------------------//

	// Get value from JSON by key
	public String getValue(String key) {
		return (String) jsonObject.get(key);
	}

}
