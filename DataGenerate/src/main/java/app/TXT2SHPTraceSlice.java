package app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import tools.SaveShape;

/*
 * @author pjl
 * @version 创建时间：2019年5月8日 下午9:35:21
 * 将轨迹数据进行切分，间隔一定时间以上的两个点之间将被截开，分成2段轨迹
 */
public class TXT2SHPTraceSlice {
	public static long threshold = 180000;// 间隔3分钟截断
	public static int mintracesize = 300;// 轨迹至少含100个点

	public static void main(String[] args) throws IOException {
		// 配置输入输出路径
		// D:\study\研究生\毕业论文\data\DataGenerate
		String path = "D:\\study\\研究生\\毕业论文\\data\\DataGenerate\\";
//		String path="D:\\study\\研究生\\毕业论文\\李亚光\\output\\";
		String input = "mytxtdata\\";
		String outputshp = "myshpdata\\";
		String outputtxt = "mytxtdata_silced\\";
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
						if (preDatetime == null) {
							pdata.add(temp);
							preDatetime = datetime;

						} else {
							Long delta = datetime - preDatetime;
//							差值为0是重复数据，去个重。 ||true表示不去重
							if ((delta > 0) || true) {
								if (delta < threshold) {
									pdata.add(temp);
									preDatetime = datetime;
								} else {
									if (pdata.size() > mintracesize) {
										// 保存为shapefile
										SaveShape ss = new SaveShape();
										String shapepath = path + outputshp + file.getName().replaceAll("[.][^.]+$", "")
												+ "_" + String.format("%05d", tracenum) + ".shp";
										ss.SavePoints(shapepath, pdata);
										// 保存为txt
										String txtpath = path + outputtxt + file.getName().replaceAll("[.][^.]+$", "")
												+ "_" + String.format("%05d", tracenum) + ".txt";
										saveAsTXT(txtpath, pdata);
									}
									++tracenum;
									preDatetime = null;
									pdata.clear();
								}
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("ALL DONE");
	}

	public static void saveAsTXT(String file, List<String[]> data) {
		try {
			BufferedWriter br = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file + ".txt"), "UTF-8"));
			for (String[] strs : data) {
				for (String str : strs) {
					br.write(str + " ");
				}
				br.newLine();
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
