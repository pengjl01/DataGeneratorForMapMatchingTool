package app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import tools.SaveShape;

/*
 * @author pjl
 * @version 创建时间：2019年5月8日 下午9:35:21
 * 将轨迹数据进行切分，间隔10分钟以上的两个点将被截开成2段轨迹
 */
public class TraceSlice {
	public static long threshold = 600000;// 间隔10分钟截断
	public static int mintracesize = 100;// 轨迹至少含100个点

	public static void main(String[] args) throws IOException {
		// 配置输入输出路径
		// D:\study\研究生\毕业论文\data\DataGenerate
		String path = "D:\\study\\研究生\\毕业论文\\data\\DataGenerate\\";
//		String path="D:\\study\\研究生\\毕业论文\\李亚光\\output\\";
		String input = "mytxtdata\\";
		String output = "myshpdata\\";
		File[] filelist = new File(path + input).listFiles();
		for (File file : filelist) {
			int tracenum = 1;
			try {
				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String line = "";
				// 读取数据
				List<String[]> pdata = new ArrayList<>();
				Long preDatetime = null;
				while ((line = bufferedReader.readLine()) != null) {
					String[] temp = line.split(" ");
					SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");
					Long datetime = 0L;
					try {
						datetime = SDF.parse(temp[4]).getTime();
						// 是否截断
						if (preDatetime == null || (datetime - preDatetime) < threshold) {
							pdata.add(temp);
							preDatetime = datetime;
						} else {
							if (pdata.size() > mintracesize) {
								// 保存为shapefile
								SaveShape ss = new SaveShape();
								String shapepath = path + output + file.getName().replaceAll("[.][^.]+$", "") + "_"
										+ String.format("%05d", tracenum) + ".shp";
								ss.SavePoints(shapepath, pdata);
							}
							++tracenum;
							preDatetime = null;
							pdata.clear();
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("ALL DONE");
	}
}
