package alarm2;

import java.net.URL;
import java.net.URLEncoder;

public class Telegram {
	String url_str = "https://api.telegram.org/bot";
	String token = "721797475:AAGoUdjQ7wmie6rGUcgYsjCa3rGfXy8W-Ac";
	String text;
	String chat_id = "836717531";
	Api api = new Api();

	public Telegram() {

	}

	public void sendMessage(StringBuffer sb) throws Exception {
		StringBuffer temp = sb;
		text = URLEncoder.encode(temp.toString(), "UTF-8");

		URL url = new URL(url_str + token + "/sendMessage?chat_id=" + chat_id + "&text=" + text);
		api.getApiData(url);

	}

}
