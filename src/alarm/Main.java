package alarm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author User @
 */
public class Main {
	static String code = "JRnXD1hyDhIXsFwFZ09bYA2u8E5YtM8V4yHGcdZEiQi46Km84sD%2BzFH3GSOxKjjWOSIKInClaYAmAHDaS00BCA%3D%3D";

	public static JSONObject getDust(String date) throws Exception {
		// http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty
		// ?sidoName=서울&pageNo=1&numOfRows=10&ServiceKey=서비스키&ver=1.3

		String dust_url = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";
		String sido_name = "서울";
		JSONObject rst = null;

		try {
			URL url = new URL(dust_url + "?sidoName=" + sido_name + "&pageNo=1&numOfRows=10&ServiceKey=" + code
					+ "&ver=1.3&_returnType=json");

			InputStream is = url.openStream();
			BufferedReader inFile = new BufferedReader(new InputStreamReader(is));

			String line = "";
			String result = "";

			while ((line = inFile.readLine()) != null) {
				result = result.concat(line);
			}

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(result);
			JSONArray parse_parm = (JSONArray) obj.get("list");
			
			rst = (JSONObject) parse_parm.get(0);
			
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rst; 
	}

	public static JSONObject getWeather(String date) throws Exception {
		String url_str = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData";
		String x = "60";
		String y = "127";
		String base_date = date;
		String base_time = "0800";
		String type = "json";
		JSONObject rst = null;

		try {
			URL url = new URL(url_str + "?ServiceKey=" + code + "&base_date=" + base_date + "&base_time=" + base_time
					+ "&nx=" + x + "&ny=" + y + "&_type=" + type);

			InputStream is = url.openStream();
			BufferedReader inFile = new BufferedReader(new InputStreamReader(is));

			String line = "";
			String result = "";

			while ((line = inFile.readLine()) != null) {
				result = result.concat(line);
			}

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(result);

			JSONObject parse_response = (JSONObject) obj.get("response");
			JSONObject parse_body = (JSONObject) parse_response.get("body");
			JSONObject parse_items = (JSONObject) parse_body.get("items");
			JSONArray parse_item = (JSONArray) parse_items.get("item");

			rst = (JSONObject) parse_item.get(0);

			is.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rst;
	}

	public static void sendTelegram(String str) throws Exception {
		String chat_id = "836717531";
		String token = "721797475:AAGoUdjQ7wmie6rGUcgYsjCa3rGfXy8W-Ac";
		String url_str = "https://api.telegram.org/bot";
		String text = str;
//		String temp = "";

		try {
			text = URLEncoder.encode(text, "UTF-8");
			URL url = new URL(url_str + token + "/sendMessage?chat_id=" + chat_id + "&text=" + text);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader inFile = new BufferedReader(new InputStreamReader(con.getInputStream()));

			inFile.close();
			con.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String gradeConvert(String _grade) {
		String grade = _grade;
		String done = "";
		
		switch(grade){
		case "1":
			done = "좋음";
			break;
		case "2":
			done = "보통";
			break;
		case "3":
			done = "나쁨";
			break;
		case "4":
			done = "매우나쁨";
			break;
		default:
			System.out.println("Grade가 잘 못 됐다.");
		}
		
		return done;
	}

	public static void main(String[] args) throws Exception {
		JSONObject json_dust = null;
		JSONObject json_rain = null;

		String rst_info = "";
		String rst_action = "";
		String rain_Percent = "";
		String pm10_grade = "";
		String pm25_grade = "";
		String station_name = "";
		int mid = 0;
				
		CurrentDate cd = new CurrentDate();
		cd.getDate();

		json_rain = getWeather(cd.getYmd());
		json_dust = getDust(cd.getYmd());
		
		rain_Percent = (json_rain.get("fcstValue")).toString();
		
		pm10_grade = (String) json_dust.get("pm10Grade1h");
		pm25_grade = (String) json_dust.get("pm25Grade1h");
		station_name = (String) json_dust.get("stationName");
		
		mid = Integer.parseInt(pm10_grade) + Integer.parseInt(pm25_grade);
		
		pm10_grade = gradeConvert(pm10_grade);
		pm25_grade = gradeConvert(pm25_grade);
		
//		System.out.println(pm10_grade);
//		System.out.println(pm25_grade);
//		System.out.println(station_name);
		
		rst_info += "<서울 " + station_name + "의 날씨 현황>\n*미세먼지 : " + pm10_grade + "\n*초미세먼지 : "+pm25_grade+"\n";
		rst_info += "*강수확률 : " + rain_Percent + "%";
		
		sendTelegram(rst_info);

		if (Integer.parseInt(rain_Percent) >= 40) 
			rst_action += "우산을 챙기세요.";
		else
			rst_action += "우산을 안 챙겨도 됩니다.";
		
		if (mid < 4)
			rst_action += " 마스크를 챙기세요.";
		else 
			rst_action += " 마스크를 안챙겨도 됩니다.";
		
		sendTelegram(rst_action); 	
		
	}
}
