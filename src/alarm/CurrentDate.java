package alarm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentDate {
	String ymd;
	String air_ymd;
	String time;
	
	public void getDate() {
		SimpleDateFormat msdf1 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		SimpleDateFormat msdf2 = new SimpleDateFormat("HHmm", Locale.KOREA);
		SimpleDateFormat msdf3 = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		Date currentTime = new Date();
		
		ymd = msdf1.format(currentTime);
		time = msdf2.format(currentTime);
		air_ymd = msdf3.format(currentTime);
	}
	
	public String getAirymd() {
		return air_ymd;
	}
	
	public String getYmd() {
		return ymd;
	}
	
	public String getTime() {
		return time;
	}
	
	
}
