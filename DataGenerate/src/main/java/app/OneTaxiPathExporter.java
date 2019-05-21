//package app;
//
//import java.io.BufferedWriter;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.UnsupportedEncodingException;
//import java.util.List;
//import java.util.Map;
//
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//
//import tools.Constants;
//import tools.MyUtil;
//import tools.TaxiDAO;
//
///*
// * @author pjl
// * @version 创建时间：2019年3月26日 下午12:51:24
// * 类说明
// */
//public class OneTaxiPathExporter {
//	String path;
//	long delta;
//	long id;
//	BufferedWriter bw;
//
//	public OneTaxiPathExporter(long id, long delta, String path) {
//		this.delta = delta;
//		this.id = id;
//		try {
//			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + ".txt"), "UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		this.path = path;
//	}
//
//	void getData(String starttime, String endtime) {
//		List<String> timeList = MyUtil.getTimeList(starttime, endtime, delta);
//		for (int i = 0; i < timeList.size() - 1; ++i) {
//			SearchResponse searchResponse = TaxiDAO.taxiESSearchAreaTime(top, left, bottom, right, timeList.get(i),
//					timeList.get(i + 1));
//			SearchHits hits = searchResponse.getHits();
//			SearchHit[] searchHits = hits.getHits();
//			writeData(searchHits);
//			System.out.println(
//					timeList.get(i) + " to " + timeList.get(i + 1) + " Done searchHits.length=" + searchHits.length);
//		}
//		for (Map.Entry<String, BufferedWriter> entry : bws.entrySet()) {
//			try {
//				entry.getValue().close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		System.out.println("All Done");
//	}
//
//	void writeData(SearchHit[] searchHits) {
//		if (searchHits.length != 0) {
//			for (SearchHit hit : searchHits) {
//				StringBuilder sb = new StringBuilder();
//				Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//				String tempid = sourceAsMap.get(Constants.TAXI_ID).toString();
//				try {
//					Long.valueOf(tempid);
//					if (!bws.containsKey(tempid)) {
//						try {
//							bws.put(tempid, new BufferedWriter(
//									new OutputStreamWriter(new FileOutputStream(path + tempid + ".txt"), "UTF-8")));
//							System.out.println(tempid + ".txt Created");
//						} catch (UnsupportedEncodingException e) {
//
//							e.printStackTrace();
//						} catch (FileNotFoundException e) {
//
//							e.printStackTrace();
//						}
//					}
//					String[] locations = sourceAsMap.get(Constants.TAXI_LOCATION).toString().split(",");
//					sb.append(tempid).append(" ").append(sourceAsMap.get(Constants.TAXI_DIRECTION)).append(" ")
//							.append(locations[0]).append(" ").append(locations[1]).append(" ")
//							.append(sourceAsMap.get(Constants.TAXI_TIME).toString());
//					try {
//						bws.get(tempid).write(sb.toString());
//						bws.get(tempid).newLine();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				} catch (Exception e) {
//
//				}
//
//			}
//		}
//	}
//
//	public static void main(String[] args) {
//
//		long delta = 3600000 * 12;
//		OneTaxiPathExporter tpe = new OneTaxiPathExporter(bjsmall, delta, "./mytxtdata/");
//		tpe.getData("20121103000000", "20121104000000");
//	}
//}
