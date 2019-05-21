package tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

//对接2009数据
public class SaveShape3 {
	static public void SavePoints(String output, List<String[]> pointData) {
		try {
			// 创建shape对象
			File file = new File(output);
			Map<String, Serializable> params = new HashMap<>();
			params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
			// 定义图形信息和属性信息
			SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
			tb.setCRS(DefaultGeographicCRS.WGS84);
			tb.setName("pointfile");
			tb.add("the_geom", Point.class);
			tb.add("time", String.class);
			tb.add("longitude", Double.class);
			tb.add("latitude", Double.class);
			tb.add("direction", Double.class);
			ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
			ds.createSchema(tb.buildFeatureType());
			ds.setCharset(Charset.forName("UTF-8"));
			// 设置Writer
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0],
					Transaction.AUTO_COMMIT);
			SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat SDF2 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", java.util.Locale.ENGLISH);
			Coordinate prepoint = null;
			for (int i = 0; i < pointData.size(); i++) {
				SimpleFeature feature = writer.next();
				String[] split = pointData.get(i);
				String time0 = split[0] + " " + split[1];
				String time = SDF.format(SDF2.parse(time0));
				Double longitude = Double.valueOf(split[3]);
				Double latitude = Double.valueOf(split[2]);
				double[] gps84 = { longitude, latitude };
				Coordinate pp = new Coordinate(gps84[0], gps84[1]);
				feature.setAttribute("the_geom", new GeometryFactory().createPoint(pp));
				feature.setAttribute("time", time);
				feature.setAttribute("longitude", longitude);
				feature.setAttribute("latitude", latitude);
				if (i == 0) {
					prepoint = pp;
					pp = new Coordinate(Double.valueOf(pointData.get(1)[3]), Double.valueOf(pointData.get(1)[2]));
					Double direction = calcDirection(prepoint, pp);
					feature.setAttribute("direction", direction);
					System.out.println(direction);
				} else {
					Double direction = calcDirection(prepoint, pp);
					feature.setAttribute("direction", direction);
					prepoint = pp;
					System.out.println(direction);
				}
			}
			writer.write();
			writer.close();
			ds.dispose();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static double calcDirection(Coordinate p1, Coordinate p2) {
		return Math.atan2((p2.x - p1.x), (p2.y - p1.y));
	}

	static public void SaveRoads(String output, List<String[]> roadData) {
		try {
			// 创建shape对象
			File file = new File(output);
			Map<String, Serializable> params = new HashMap<>();
			params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
			// 定义图形信息和属性信息
			SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
			tb.setCRS(DefaultGeographicCRS.WGS84);
			tb.setName("roadfile");
			tb.add("the_geom", LineString.class);
			tb.add("osm_id", String.class);
			tb.add("oneway", char.class);
			tb.add("speed", Double.class);
			tb.add("from_id", String.class);
			tb.add("to_id", String.class);
			tb.add("vcount", Integer.class);
			ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
			ds.createSchema(tb.buildFeatureType());
			ds.setCharset(Charset.forName("UTF-8"));
			// 设置Writer
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0],
					Transaction.AUTO_COMMIT);
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
			WKTReader reader = new WKTReader(geometryFactory);
			for (int i = 0; i < roadData.size(); i++) {
				SimpleFeature feature = writer.next();
				String[] split = roadData.get(i);
				LineString the_geom = (LineString) reader.read(split[6]);
				String osm_id = split[0];
				char oneway = 'B';
				int twoway = Integer.valueOf(split[3]);
//				twoway为0目前认为是F而不是T？
				if (twoway == 0)
					oneway = 'F';
				Double speed = Double.valueOf(split[4]);
				String from_id = split[1];
				String to_id = split[2];
				int vcount = Integer.valueOf(split[5]);
				feature.setAttribute("the_geom", the_geom);
				feature.setAttribute("osm_id", osm_id);
				feature.setAttribute("oneway", oneway);
				feature.setAttribute("speed", speed);
				feature.setAttribute("from_id", from_id);
				feature.setAttribute("to_id", to_id);
				feature.setAttribute("vcount", vcount);
			}
			writer.write();
			writer.close();
			ds.dispose();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String inputRoad = "D:\\study\\研究生\\毕业论文\\小论文\\mapmatching\\隐马2009\\road_network.txt";
		String inputPoint = "D:\\study\\研究生\\毕业论文\\小论文\\mapmatching\\隐马2009\\gps_data.txt";
		String output = "D:\\study\\研究生\\毕业论文\\小论文\\mapmatching\\隐马2009\\shpdata";
		String shapeRoad = output + "\\road_network.shp";
		String shapePoint = output + "\\pointdata\\gps_data.shp";

		try {
//			Point
			InputStream inputStream = new BufferedInputStream(new FileInputStream(inputPoint));
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = bufferedReader.readLine();
			// 读取数据
			List<String[]> pdata = new ArrayList<>();
			while ((line = bufferedReader.readLine()) != null) {
				pdata.add(line.split("	"));
			}
			SavePoints(shapePoint, pdata);
//		Road
			InputStream inputStreamRoad = new BufferedInputStream(new FileInputStream(inputRoad));
			InputStreamReader inputStreamReaderRoad = new InputStreamReader(inputStreamRoad, "UTF-8");
			@SuppressWarnings("resource")
			BufferedReader bufferedReaderRoad = new BufferedReader(inputStreamReaderRoad);
			String lineRoad = bufferedReaderRoad.readLine();
			// 读取数据
			List<String[]> rdata = new ArrayList<>();
			while ((lineRoad = bufferedReaderRoad.readLine()) != null) {
				rdata.add(lineRoad.split("	"));
			}
			SaveRoads(shapeRoad, rdata);
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

		System.out.println("ALL DONE");
	}
}