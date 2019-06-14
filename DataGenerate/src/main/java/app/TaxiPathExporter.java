package app;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import tools.Constants;
import tools.MyUtil;
import tools.TaxiDAO;

/*
 * @author pjl
 * @version 创建时间：2019年3月26日 下午12:51:24
 * 类说明
 */
public class TaxiPathExporter {
	String path;
	double top, left, bottom, right;
	long delta;
	Map<String, BufferedWriter> bws;

	public TaxiPathExporter(double top, double left, double bottom, double right, long delta, String path) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.delta = delta;
		bws = new HashMap<>();
		this.path = path;
	}

	public TaxiPathExporter(double[] edge, long delta, String path) {
		if (edge.length < 4) {
			System.out.println("应有4个数描述边界");
		} else {
			this.top = edge[0];
			this.left = edge[1];
			this.bottom = edge[2];
			this.right = edge[3];
			this.delta = delta;
			bws = new HashMap<>();
			this.path = path;
		}
	}

	void getData(String starttime, String endtime) {
		List<String> timeList = MyUtil.getTimeList(starttime, endtime, delta);
		for (int i = 0; i < timeList.size() - 1; ++i) {
			SearchResponse searchResponse = TaxiDAO.taxiESSearchAreaTime(top, left, bottom, right, timeList.get(i),
					timeList.get(i + 1));
			SearchHits hits = searchResponse.getHits();
			SearchHit[] searchHits = hits.getHits();
			writeData(searchHits);
			System.out.println(
					timeList.get(i) + " to " + timeList.get(i + 1) + " Done searchHits.length=" + searchHits.length);
		}
		for (Map.Entry<String, BufferedWriter> entry : bws.entrySet()) {
			try {
				entry.getValue().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("All Done");
	}

	void writeData(SearchHit[] searchHits) {
		if (searchHits.length != 0) {
			for (SearchHit hit : searchHits) {
				StringBuilder sb = new StringBuilder();
				Map<String, Object> sourceAsMap = hit.getSourceAsMap();
				String tempid = sourceAsMap.get(Constants.TAXI_ID).toString();
				try {
					Long.valueOf(tempid);
					if (!bws.containsKey(tempid)) {
						try {
							bws.put(tempid, new BufferedWriter(
									new OutputStreamWriter(new FileOutputStream(path + tempid + ".txt"), "UTF-8")));
							System.out.println(tempid + ".txt Created");
						} catch (UnsupportedEncodingException e) {

							e.printStackTrace();
						} catch (FileNotFoundException e) {

							e.printStackTrace();
						}
					}
					String[] locations = sourceAsMap.get(Constants.TAXI_LOCATION).toString().split(",");
					sb.append(tempid).append(" ").append(sourceAsMap.get(Constants.TAXI_DIRECTION)).append(" ")
							.append(locations[0]).append(" ").append(locations[1]).append(" ")
							.append(sourceAsMap.get(Constants.TAXI_TIME).toString()).append(" ")
							.append(sourceAsMap.get(Constants.TAXI_SPEED).toString());
					try {
						bws.get(tempid).write(sb.toString());
						bws.get(tempid).newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {

				}

			}
		}
	}

	public static void main(String[] args) {
		double[] bjbig = { 40.2659, 115.9767, 39.5943, 116.8491 };
		double[] bjsmall = { 40.0809, 116.2015, 39.7451, 116.6048 };
		double[] custom = { 39.8684, 116.3953, 39.8568, 116.4145 };
		double[] custom2 = { 39.9282, 116.4057, 39.9061, 116.4389 };
		long delta = 10;
		// D:\study\研究生\毕业论文\data\DataGenerate
		TaxiPathExporter tpe = new TaxiPathExporter(bjsmall, delta,
				"D:\\study\\研究生\\毕业论文\\data\\DataGenerate\\mytxtdata\\");
		tpe.getData("20121103000000", "20121104000000");
	}
}
