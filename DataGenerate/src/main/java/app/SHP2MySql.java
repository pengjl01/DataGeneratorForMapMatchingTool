package app;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.Font.Style;
import org.geotools.styling.SLD;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class SHP2MySql {
	public static void createFeatures() {

		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
		try {
			ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory.createDataStore(
					new File("C:\\Users\\82121\\Documents\\ArcGIS\\osm\\gis_osm_roads_free\\gis_osm_roads_free_1.shp")
							.toURI().toURL());
			sds.setCharset(Charset.forName("UTF-8"));
			SimpleFeatureSource featureSource = sds.getFeatureSource();
			SimpleFeatureType schema = featureSource.getSchema();
			Map<String, Comparable> params = new HashMap<String, Comparable>();
			params.put(MySQLDataStoreFactory.DBTYPE.key, "mysql");
			params.put(MySQLDataStoreFactory.HOST.key, "localhost");
			params.put(MySQLDataStoreFactory.PORT.key, 3306);
			params.put(MySQLDataStoreFactory.DATABASE.key, "geotools");
			params.put(MySQLDataStoreFactory.USER.key, "geotools");
			params.put(MySQLDataStoreFactory.PASSWD.key, "123456");
			DataStore ds = DataStoreFinder.getDataStore(params);
			try {
				ds.createSchema(schema);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds
						.getFeatureWriter(schema.getTypeName().toLowerCase(), Transaction.AUTO_COMMIT);
				SimpleFeatureIterator itertor = featureSource.getFeatures().features();
				Set<String> set = new HashSet<>();
				set.add("motorway");
				set.add("motorway_link");
				set.add("path");
				set.add("primary");
				set.add("primary_link");
				set.add("residential");
				set.add("road");
				set.add("secondary");
				set.add("secondary_link");
				set.add("service");
				set.add("tertiary");
				set.add("tertiary_link");
				set.add("trunk");
				set.add("trunk_link");
				set.add("unclassified");
				set.add("living_street");
				set.add("pedestrian");
				set.add("track");
				set.add("road");
				while (itertor.hasNext()) {
					SimpleFeature feature = itertor.next();
					if (set.contains(feature.getAttribute(3).toString())) {
						writer.hasNext();
						SimpleFeature feature1 = writer.next();
						feature1.setAttributes(feature.getAttributes());
//						for (int i = 0; i < feature1.getAttributeCount(); i++) {
//							System.out.print(feature1.getAttribute(i).toString() + " ");
//						}
//						System.out.println();
						/*
						 * MULTILINESTRING ((113.146418 27.704379, 113.1487593 27.7042756, 113.1509294
						 * 27.7042903, 113.1511092 27.7042915)) 508991265 5115 tertiary \ B 0 0 F F
						 * 
						 * MULTILINESTRING ((105.3782978 30.0763712, 105.3788048 30.0755747, 105.3790891
						 * 30.0752404, 105.3792179 30.0740706, 105.3791696 30.0739174, 105.3770935
						 * 30.0716891, 105.3768414 30.0714941, 105.3766054 30.0711691, 105.3764498
						 * 30.0708535, 105.3760958 30.0705192, 105.3759026 30.0704681, 105.3756559
						 * 30.0705146, 105.3754413 30.0705238, 105.3749263 30.0703381, 105.3741753
						 * 30.0700596, 105.3735208 30.0696325, 105.3731936 30.0695304, 105.3726733
						 * 30.0692147, 105.372566 30.0690615, 105.3723997 30.068314, 105.3722548
						 * 30.0681376, 105.3719544 30.0681283, 105.3714663 30.0679055, 105.3711069
						 * 30.0675434, 105.3708494 30.067418, 105.3704631 30.0673762, 105.3701627
						 * 30.0672323, 105.369519 30.0668377, 105.3693366 30.0665266, 105.3688645
						 * 30.0662017, 105.3686821 30.066132, 105.3685534 30.0654356, 105.3686392
						 * 30.0651339, 105.368607 30.0645442, 105.3688002 30.06382, 105.3694653
						 * 30.0630028, 105.3687787 30.0588336, 105.3688967 30.0577285, 105.3684139
						 * 30.0564656, 105.3685641 30.0558992, 105.3684246 30.055472, 105.3679418
						 * 30.0550727, 105.3677165 30.0545712, 105.3677002 30.0544324, 105.3676522
						 * 30.0540233, 105.3677595 30.0533454, 105.3678882 30.0527697, 105.3687894
						 * 30.0523889, 105.3702485 30.0511259, 105.3704953 30.050773, 105.3705597
						 * 30.0503458, 105.3705597 30.0500951, 105.3701305 30.0497886, 105.3681564
						 * 30.0492779, 105.3680706 30.0488321, 105.3675985 30.0485071, 105.3672767
						 * 30.0485906, 105.3667188 30.0484235, 105.3657853 30.0483956, 105.365206
						 * 30.0486742, 105.3648044 30.0487648, 105.3647017 30.048788, 105.3645247
						 * 30.0487787, 105.3643799 30.0487021, 105.3640795 30.0483515, 105.3639159
						 * 30.0479754, 105.3636879 30.0476434, 105.3634491 30.0472324, 105.3615031
						 * 30.0481283, 105.3608823 30.0484142, 105.3605926 30.0483492, 105.3594661
						 * 30.0479963, 105.3591871 30.0479313, 105.3588867 30.0481727)) 547039141 5115
						 * tertiary \ B 0 0 F F
						 * 
						 * MULTILINESTRING ((104.057744 30.6371413, 104.0583909 30.638189)) 569688449
						 * 5121 unclassified \ B 0 0 F F
						 * 
						 * 
						 * insert into gis_osm_roads_free_1
						 * values(null,MultiLineStringFromText('MULTILINESTRING((104.057744 30.6371413,
						 * 104.0583909
						 * 30.638189))'),'569688449',5121,'unclassified','','','B',0,0,'F','F');
						 * 
						 */
						try {
							writer.write();
						} catch (Exception e) {
							for (int i = 0; i < feature1.getAttributeCount(); i++) {
								System.out.print(feature1.getAttribute(i).toString() + " ");
							}
							System.out.println();
						}
					}
					/*
					 * 0: MULTILINESTRING ((120.1213609 30.2946272, 120.1217075 30.2939512,
					 * 120.1217942 30.2935699, 120.1217942 30.2932059)) 1: 5054070 2: 5115 3:
					 * tertiary 4: 育新路 5: 6: B 7: 0 8: 0 9: F 10: F trunk primary secondary tertiary
					 * unclassified track ?residential cycleway pedestrian
					 */
//					int i = 0;
//					for (Object o : feature.getAttributes()) {
//						System.out.println(i + ": " + o.toString());
//						++i;
//					}
				}
				System.out.println("Done");
				itertor.close();
				writer.close();
				ds.dispose();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		createFeatures();
//		// display a data store file chooser dialog for shapefiles
//		File file = JFileDataStoreChooser.showOpenFile("shp", null);
//		if (file == null) {
//			return;
//		}
//
//		FileDataStore store = FileDataStoreFinder.getDataStore(file);
//		SimpleFeatureSource featureSource = store.getFeatureSource();
//
//		// Create a map content and add our shapefile to it
//		MapContent map = new MapContent();
//		map.setTitle("Quickstart");
//
//		Style style = (Style) SLD.createSimpleStyle(featureSource.getSchema());
//		FeatureLayer layer = new FeatureLayer(featureSource, (org.geotools.styling.Style) style);
//
//		map.addLayer(layer);
//
//		// Now display the map
//		JMapFrame.showMap(map);

//		String shpPath = "C:\\Users\\82121\\Documents\\ArcGIS\\osm\\gis_osm_roads_free\\gis_osm_roads_free_1.shp";
//		System.out.println(shpPath);
//		ShapefileDataStore shpDataStore = null;
//
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//
//		File file1 = new File(shpPath);
//		shpDataStore = new ShapefileDataStore(file1.toURL());
//		// 设置字符编码
//		Charset charset = Charset.forName("utf-8");
//		shpDataStore.setCharset(charset);
//		String typeName = shpDataStore.getTypeNames()[0];
//		SimpleFeatureSource featureSource1 = null;
//		featureSource1 = shpDataStore.getFeatureSource(typeName);
//		SimpleFeatureCollection result = featureSource1.getFeatures();
//		SimpleFeatureIterator itertor = result.features();
//		while (itertor.hasNext()) {
//			Map<String, Object> data = new HashMap<String, Object>();
//			SimpleFeature feature = itertor.next();
//			Collection<Property> p = feature.getProperties();
//			Iterator<Property> it = p.iterator();
//			while (it.hasNext()) {
//				Property pro = it.next();
//				String field = pro.getName().toString();
//				String value = pro.getValue().toString();
//				field = field.equals("the_geom") ? "wkt" : field;
//				data.put(field, value);
//			}
//			list.add(data);
//		}
	}

}