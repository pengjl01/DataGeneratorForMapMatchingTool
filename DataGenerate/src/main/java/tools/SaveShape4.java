package tools;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

//对接bj2011地图
public class SaveShape4 {
	static public void SaveRoads(String output, SimpleFeatureCollection roadData) {
		// 创建shape对象
		File file = new File(output);
		Map<String, Serializable> params = new HashMap<>();
		try {
			params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
			SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
			tb.setCRS(DefaultGeographicCRS.WGS84);
			tb.setName("roadfile");
			tb.add("the_geom", LineString.class);
			tb.add("osm_id", String.class);
			tb.add("oneway", char.class);
			ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
			ds.createSchema(tb.buildFeatureType());
			ds.setCharset(Charset.forName("UTF-8"));
			// 设置Writer
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0],
					Transaction.AUTO_COMMIT);
			SimpleFeatureIterator i = roadData.features();
			while (i.hasNext()) {
				SimpleFeature origin = i.next();
				SimpleFeature feature = writer.next();
//				坐标转换
				MultiLineString the_geom = (MultiLineString) origin.getProperty("the_geom").getValue();
				trans(the_geom);
				String osm_id = (String) origin.getProperty("ID").getValue();
				String onewaystr = (String) origin.getProperty("Oneway").getValue();
				char oneway = 'B';
				if (onewaystr.length() > 0) {
					oneway = onewaystr.charAt(0);
				}
				feature.setAttribute("the_geom", the_geom);
				feature.setAttribute("osm_id", osm_id);
				feature.setAttribute("oneway", oneway);
			}
			writer.write();
			writer.close();
			ds.dispose();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void trans(Geometry geom) {
		if (geom instanceof LineString || geom instanceof MultiLineString) {
			int parts = geom.getNumGeometries();
			for (int i = 0; i < parts; i++) {
				LineString l = (LineString) geom.getGeometryN(i);
				for (int j = 0, num = l.getNumPoints(); j < num; j++) {
					Coordinate coor = l.getCoordinateN(j);
					double[] gps84 = PointTrans.gcj02_To_Gps84(coor.y, coor.x);
					coor.setX(gps84[1]);
					coor.setY(gps84[0]);
				}
			}
		} else if (geom instanceof Point) {
			Coordinate coor = geom.getCoordinate();
			double[] gps84 = PointTrans.gcj02_To_Gps84(coor.y, coor.x);
			coor.setX(gps84[1]);
			coor.setY(gps84[0]);
		} else {
			System.out.println("CoordinateTrans Error: Unknown geom type");
		}
	}

	public static void main(String[] args) {
		String inputRoadSHP = "D:\\study\\研究生\\毕业论文\\data\\map\\bj2011\\full\\Road.shp";
//		String inputRoad = "D:\\study\\研究生\\毕业论文\\data\\map\\bj2011\\Beijing_2011\\geos.txt";
		String shapeRoad = "D:\\study\\研究生\\毕业论文\\data\\map\\bj2011\\myshp\\road_network.shp";
		try {
			FileDataStore store = FileDataStoreFinder.getDataStore(new File(inputRoadSHP));
			SimpleFeatureCollection origin = store.getFeatureSource().getFeatures();
			SaveRoads(shapeRoad, origin);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ALL DONE");
	}
}