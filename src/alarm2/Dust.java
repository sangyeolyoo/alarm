package alarm2;

import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Dust {
	JSONObject rst;
	String url_str = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";
	String code;
	String date;
	Api api = new Api();

	public Dust() {
		this.code = api.code;
	}

	public void getData() throws Exception {
		URL url = new URL(
				url_str + "?sidoName=서울&pageNo=1&numOfRows=10&ServiceKey=" 
						+ code + "&ver=1.3&_returnType=json");
		JSONObject obj = api.getApiData(url);
		JSONArray parse_parm = (JSONArray) obj.get("list");
		rst = (JSONObject) parse_parm.get(0);
	}

	public String gradeConvert(String _grade) {
		String grade = _grade;
		String done = "";

		switch (grade) {
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
			System.out.println("Grade가 잘 못 됐습니다.");
		}

		return done;
	}

	public JSONObject getResult() {
		return rst;
		
	}

	public int getGrade() {
		int pm10 = Integer.parseInt((String) rst.get("pm10Grade1h"));
		int pm25 = Integer.parseInt((String) rst.get("pm25Grade1h"));
		
		if (pm10 > 3 || pm25 > 3)
			return 1;
		else
			return 0;
	}
}
