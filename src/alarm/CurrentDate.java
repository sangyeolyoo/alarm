package alarm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentDate {
	String ymd;
	String prt_md;
	String time;
	
	public void getDate() {
		SimpleDateFormat msdf1 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		SimpleDateFormat msdf2 = new SimpleDateFormat("HHmm", Locale.KOREA);
		SimpleDateFormat msdf3 = new SimpleDateFormat("MMM dd일 (EEE)", Locale.KOREA);
		Date currentTime = new Date();
		
		ymd = msdf1.format(currentTime);
		time = msdf2.format(currentTime);
		prt_md = msdf3.format(currentTime);
	}
	
	public String getPrtmd() {
		return prt_md;
	}
	
	public String getYmd() {
		return ymd;
	}
	
	public String getTime() {
		return time;
	}
	
	
}
