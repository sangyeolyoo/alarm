package alarm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Formatter;

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

	public static void sendTelegram(StringBuffer sb) throws Exception {
		String chat_id = "836717531";
		String token = "721797475:AAGoUdjQ7wmie6rGUcgYsjCa3rGfXy8W-Ac";
		String url_str = "https://api.telegram.org/bot";
		String text = "";
		StringBuffer temp = sb;
		
		try {
			text = temp.toString();
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

		String rain_percent = "";
		String pm10_grade = "";
		String pm25_grade = "";
		String station_name = "";
		int mid = 0;
				
		CurrentDate cd = new CurrentDate();
		StringBuffer sb = new StringBuffer();
		Formatter f= new Formatter(sb); 
		
		cd.getDate();

		json_rain = getWeather(cd.getYmd());
		json_dust = getDust(cd.getYmd());
		
		rain_percent = (json_rain.get("fcstValue")).toString();
		
		pm10_grade = (String) json_dust.get("pm10Grade1h");
		pm25_grade = (String) json_dust.get("pm25Grade1h");
		station_name = (String) json_dust.get("stationName");
		
		mid = Integer.parseInt(pm10_grade) + Integer.parseInt(pm25_grade);
		
		pm10_grade = gradeConvert(pm10_grade);
		pm25_grade = gradeConvert(pm25_grade);

		f.format("****%s****\n", cd.getPrtmd());
		f.format("미세먼지 : %s\n초미세먼지 : %s\n강수확률 : %s%%\n", pm10_grade, pm25_grade, rain_percent);
		
		if (Integer.parseInt(rain_percent) >= 40) 
			f.format("우산을 챙기세요\n");
		else
			f.format("우산을 챙기지 마세요\n");
		
		if (mid < 4)
			f.format("마스크를 챙기세요.\n");
		else 
			f.format("마스크를 챙기지 마세요.\n");
		
		f.format("(서울 %s기준)", station_name);
//		
//		System.out.println(f);
		sendTelegram(sb); 	
		
	}
}
