package alarm2;

import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Weather {
	JSONObject rst;
	String url_str = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData";
	String code;
	String date;
	String x = "60";
	String y = "127";
	String base_date;
	String base_time = "0200";
	String type = "json";
	Api api = new Api();
	SimpleDateFormat msdf1 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
	Date currentTime = new Date();

	public Weather() {
		this.code = api.code;
		this.base_date = msdf1.format(currentTime);

	}

	public void getData() throws Exception {
		URL url = new URL(url_str + "?ServiceKey=" + code + 
				"&base_date=" + base_date + "&base_time=" + base_time
				+ "&nx=" + x + "&ny=" + y + "&_type=" + type);

		JSONObject obj = api.getApiData(url);
		JSONObject parse_response = (JSONObject) obj.get("response");
		JSONObject parse_body = (JSONObject) parse_response.get("body");
		JSONObject parse_items = (JSONObject) parse_body.get("items");
		JSONArray parse_item = (JSONArray) parse_items.get("item");
		rst = (JSONObject) parse_item.get(0);

	}

	public JSONObject getResult() {
		return rst;
	}

	public int getFcstvalue() {
		return ((Long) rst.get("fcstValue")).intValue();
	}

}
