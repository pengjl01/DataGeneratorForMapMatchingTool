package tools;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * @author pjl
 * @version 创建时间：2019年3月26日 下午2:02:08
 * 类说明
 */
public class MyUtil {
	public static final String SDFSTR = "yyyyMMddHHmmss";

//	// 将时间向上取整至半小时yyyymmddhhmmss
//	// 时间已经通过转换测试
//	public static String timeTrans(String time) {
//		String hour = time.substring(0, 10);
//		int minutes = Integer.valueOf(time.substring(10, 12));
//		if (minutes < 30)
//			return hour + "3000";
//		else {
//			Date time1;
//			SimpleDateFormat SDF = new SimpleDateFormat(SDFSTR);
//			try {
//				time1 = SDF.parse(hour + "0000");
//				time1.setTime(time1.getTime() + 3600000);
//				return SDF.format(time1);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
//		}
//	}
//	time向下取整
	public static String timeTrans(String time) {
		String hour = time.substring(0, 10);
		int minutes = Integer.valueOf(time.substring(10, 12));
		if (minutes < 30)
			return hour + "0000";
		else {
			return hour + "3000";
		}
	}

//无任何边界条件检测，必须确保time1、time2格式、大小合适
	public static List<String> getTimeList(String time1, String time2, long delta) {
		List<String> ans = new ArrayList<>();
		SimpleDateFormat SDF = new SimpleDateFormat(SDFSTR);
		Date t1, t2;
		try {
			t1 = SDF.parse(timeTrans(time1));
			t2 = SDF.parse(timeTrans(time2));
			ans.add(SDF.format(t1));
			while (t1.getTime() < t2.getTime()) {
				t1.setTime(t1.getTime() + delta * 1000);
				ans.add(SDF.format(t1));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ans;
	}

	public static void main(String[] args) {
		List<String> a = getTimeList("20180101232300", "20180102030000", 10);
		for (String t : a) {
			System.out.println(t);
		}
	}
}
