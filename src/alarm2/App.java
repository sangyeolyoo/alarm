package alarm2;

import java.util.Formatter;
import org.json.simple.JSONObject;
//import java.net.HttpURLConnection;

/**
 * @author User
 *
 */

public class App {

	public static StringBuffer prtFormat(Weather w, Dust d) throws Exception {
		StringBuffer sb = new StringBuffer();
		Formatter f = new Formatter(sb);
		JSONObject temp1;

		temp1 = d.getResult();
		f.format("미세먼지 농도 - %s\n", d.gradeConvert((String) temp1.get("pm10Grade1h")));
		f.format("초미세먼지 농도 - %s\n", d.gradeConvert((String) temp1.get("pm25Grade1h")));
		f.format("강수 확률 - %d%%\n\n", w.getFcstvalue());

		if (w.getFcstvalue() >= 40)
			f.format("우산을 챙기세요.\n");
		else
			f.format("우산을 챙기지 마세요.\n");

		if (d.getGrade() == 1)
			f.format("마스크를 챙기세요.\n");
		else
			f.format("마스크를 챙기지 마세요.\n");

		return sb;
	}

	public static void main(String[] args) throws Exception {
		Telegram t = new Telegram();
		Weather w = new Weather();
		Dust d = new Dust();

		w.getData();
		d.getData();

		StringBuffer sb = new StringBuffer();
		sb = prtFormat(w, d);

		t.sendMessage(sb);
	}
}
