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
import java.util.ArrayList;
import java.util.List;

import tools.SaveShape;

public class TXT2SHP {
	public static void main(String[] args) throws IOException {
		// 配置输入输出路径
		// D:\study\研究生\毕业论文\data\DataGenerate
		String path = "D:\\study\\研究生\\毕业论文\\data\\DataGenerate\\";
//		String path="D:\\study\\研究生\\毕业论文\\李亚光\\output\\";
		String input = "mytxtdata\\";
		String output = "myshpdata\\";
		File[] filelist = new File(path + input).listFiles();
		for (File file : filelist) {
			String shapepath = path + output + file.getName().replaceAll("[.][^.]+$", "") + ".shp";
			try {
				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String line = "";
				// 读取数据
				List<String[]> pdata = new ArrayList<>();
				while ((line = bufferedReader.readLine()) != null) {
					pdata.add(line.split(" "));
				}
				// 保存为shapefile
				SaveShape ss = new SaveShape();
				ss.SavePoints(shapepath, pdata);

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