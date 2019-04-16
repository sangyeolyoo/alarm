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
	
	public static String getWeather(String date) throws Exception {
		String code = "JRnXD1hyDhIXsFwFZ09bYA2u8E5YtM8V4yHGcdZEiQi46Km84sD%2BzFH3GSOxKjjWOSIKInClaYAmAHDaS00BCA%3D%3D";
		String url_str = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData";
		String x = "62";
		String y = "125";
		String base_date = date;
		String base_time = "0800";
		String type = "json";
		String pop="";
		
		try {
			URL url = new URL(url_str + "?ServiceKey=" + code + "&base_date=" + base_date + "&base_time=" + base_time + "&nx="+x+"&ny="+y+"&_type="+type);
			
			InputStream is = url.openStream();
			BufferedReader inFile = new BufferedReader(new InputStreamReader(is));
			
			String line = "";
			String result = "";
			
			while((line= inFile.readLine()) != null) {
				result = result.concat(line);
			}
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(result);
			
			JSONObject parse_response = (JSONObject) obj.get("response");
			JSONObject parse_body = (JSONObject) parse_response.get("body");
			JSONObject parse_items = (JSONObject) parse_body.get("items");
			JSONArray parse_item = (JSONArray) parse_items.get("item");
			
			String category;
			JSONObject weather;
			
			weather= (JSONObject) parse_item.get(0);
			pop = (weather.get("fcstValue")).toString();
			
//			category = (String) weather.get("category");
			
//			System.out.print("  category : " + category);
//			System.out.print("  fcst_value : " + pop);
			
//			for( int i = 0; i< parse_item.size(); i++) {
//				weather = (JSONObject) parse_item.get(i);
//				double fcst_Value = ((Long)weather.get("fcstValue")).doubleValue();
//				
//				category = (String) weather.get("category");
//				
//				System.out.print("배열의"+i+"번째 요소");
//				System.out.print("  category : " + category);
//				System.out.print("  fcst_value : " + fcst_Value);
//				System.out.println();
//			}
			
			is.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return pop;
	}
	
	public static void spendTelegram(String str) throws Exception {
		String chat_id = "836717531";
		String token = "721797475:AAGoUdjQ7wmie6rGUcgYsjCa3rGfXy8W-Ac";
		String url_str = "https://api.telegram.org/bot";
		String text = str;
		
		String temp = "";

		try {
			temp = URLEncoder.encode(text,"UTF-8");
			URL url = new URL(url_str + token + "/sendMessage?chat_id=" + chat_id + "&text=" + temp);
			
//			System.out.println(url.toString());
//			InputStream is = url.openStream();

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			BufferedReader inFile = new BufferedReader(new InputStreamReader(con.getInputStream()));

//			System.out.println("t");
//			String line = null;
//			while((line= inFile.readLine()) != null) {
//				System.out.println(line);
//			}

//			is.close();
			inFile.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String str = "";
		String rainPercent = "";
		
		CurrentDate cd = new CurrentDate();
		cd.getDate();

		rainPercent = getWeather(cd.getYmd());

		str += "현재 " + rainPercent + "%의 강수 확률로";
		
		if (Integer.parseInt(rainPercent) >= 40) {
			spendTelegram(str + "우산을 챙기세요");
		} else {
			spendTelegram(str + "우산을 챙기지 마세요");
		}
	}
}
