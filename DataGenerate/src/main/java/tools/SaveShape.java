package tools;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

//对接es导出数据
public class SaveShape {
	public static double MIN_MOVE_RATE = 0.85;

	public void SavePoints(String filePath, List<String[]> pointData) {
		try {
			// 创建shape对象
			File file = new File(filePath);
			Map<String, Serializable> params = new HashMap<>();
			params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
			// 定义图形信息和属性信息
			SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
			tb.setCRS(DefaultGeographicCRS.WGS84);
			tb.setName("shapefile");
			tb.add("the_geom", Point.class);
			tb.add("id", Long.class);
//            tb.add("number", Integer.class);
			tb.add("time", String.class);
//            tb.add("duration", Integer.class);
			tb.add("longitude", Double.class);
			tb.add("latitude", Double.class);
			tb.add("direction", Integer.class);
//            tb.add("distance", Double.class);	
			int count = 0;
			for (int i = 1; i < pointData.size(); i++) {
				String[] split = pointData.get(i);
				Double longitude = Double.valueOf(split[3]);
				Double latitude = Double.valueOf(split[2]);
				String[] presplit = pointData.get(i - 1);
				Double prelongitude = Double.valueOf(presplit[3]);
				Double prelatitude = Double.valueOf(presplit[2]);
				if (!longitude.equals(prelongitude) || !latitude.equals(prelatitude))
					count += 1;
			}
			double moveRate = (double) count / pointData.size();
//			变动率大于85%
			if (moveRate > MIN_MOVE_RATE) {
//				System.out.println(filePath + "包含超过" + count + "个位移，已保留" + " 共包含" + pointData.size() + "个gps点");
				ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
				ds.createSchema(tb.buildFeatureType());
				ds.setCharset(Charset.forName("UTF-8"));
				// 设置Writer
				FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0],
						Transaction.AUTO_COMMIT);
				for (int i = 0; i < pointData.size(); i++) {
					SimpleFeature feature = writer.next();
					String[] split = pointData.get(i);
					Long id = Long.valueOf(split[0]);
					String time = split[4];
					Double longitude = Double.valueOf(split[3]);
					Double latitude = Double.valueOf(split[2]);
					Integer direction = Integer.valueOf(split[1]);
					double[] gps84 = PointTrans.gcj02_To_Gps84(longitude, latitude);
					Coordinate pp = new Coordinate(gps84[0], gps84[1]);
					feature.setAttribute("the_geom", new GeometryFactory().createPoint(pp));
					feature.setAttribute("id", id);
					feature.setAttribute("time", time);
					feature.setAttribute("longitude", longitude);
					feature.setAttribute("latitude", latitude);
					feature.setAttribute("direction", direction);
				}
				writer.write();
				writer.close();
				ds.dispose();
			} else {
				System.out.println(filePath + " 共包含" + pointData.size() + "个gps点，位移率 " + moveRate + "舍弃");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

	}
}