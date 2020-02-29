//package app;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.geotools.data.DataStore;
//import org.geotools.data.DataStoreFinder;
//import org.geotools.data.FeatureWriter;
//import org.geotools.data.Transaction;
//import org.geotools.data.mysql.MySQLDataStoreFactory;
//import org.geotools.data.shapefile.ShapefileDataStore;
//import org.geotools.data.simple.SimpleFeatureCollection;
//import org.geotools.data.simple.SimpleFeatureIterator;
//import org.geotools.data.simple.SimpleFeatureSource;
//import org.geotools.data.store.ContentEntry;
//import org.geotools.feature.NameImpl;
//import org.geotools.feature.simple.SimpleFeatureBuilder;
//import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
//import org.geotools.geometry.jts.JTSFactoryFinder;
//import org.geotools.jdbc.JDBCDataStore;
//
//import org.opengis.feature.simple.SimpleFeature;
//import org.opengis.feature.simple.SimpleFeatureType;
//import org.opengis.feature.type.AttributeDescriptor;
//
///*
// * @author pjl
// * @version 创建时间：2019年3月27日 下午10:09:08
// * 类说明
// */
//public class shp2mysql2 {
//	public static SimpleFeatureSource readSHP(String shpfile) {
//		SimpleFeatureSource featureSource = null;
//		try {
//			File file = new File(shpfile);
//			ShapefileDataStore shpDataStore = null;
//
//			shpDataStore = new ShapefileDataStore(file.toURL());
//			// 设置编码
//			Charset charset = Charset.forName("utf8");
//			shpDataStore.setCharset(charset);
//			String tableName = shpDataStore.getTypeNames()[0];
//			featureSource = shpDataStore.getFeatureSource(tableName);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return featureSource;
//	}
//
//	public static JDBCDataStore connnection2mysql(String host, String dataBase, int port, String userName, String pwd) {
//		JDBCDataStore ds = null;
//		DataStore dataStore = null;
//		// 连接数据库参数
//		java.util.Map params = new java.util.HashMap();
//		params.put(MySQLDataStoreFactory.DBTYPE.key, "mysql");
//		params.put(MySQLDataStoreFactory.HOST.key, host);
//		params.put(MySQLDataStoreFactory.PORT.key, port);
//		params.put(MySQLDataStoreFactory.DATABASE.key, dataBase);
//		params.put(MySQLDataStoreFactory.USER.key, userName);
//		params.put(MySQLDataStoreFactory.PASSWD.key, pwd);
//		try {
//			dataStore = DataStoreFinder.getDataStore(params);
//			if (dataStore != null) {
//				ds = (JDBCDataStore) dataStore;
//				System.out.println(dataBase + "连接成功");
//			} else {
//
//				System.out.println(dataBase + "连接失败");
//			}
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//
//			e.printStackTrace();
//
//		}
//
//		return ds;
//	}
//
//	public static JDBCDataStore createTable(JDBCDataStore ds, SimpleFeatureSource featureSource) {
//		SimpleFeatureType schema = featureSource.getSchema();
//		try {
//			// 创建数据表
//			ds.createSchema(schema);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return ds;
//	}
//
//	public static void writeShp2Mysql(JDBCDataStore ds, SimpleFeatureSource featureSource) {
//		SimpleFeatureType schema = featureSource.getSchema();
//		// 开始写入数据
//		try {
//			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds
//					.getFeatureWriter(schema.getTypeName().toLowerCase(), Transaction.AUTO_COMMIT);
//			SimpleFeatureCollection featureCollection = featureSource.getFeatures();
//			SimpleFeatureIterator features = featureCollection.features();
//			while (features.hasNext()) {
//				writer.hasNext();
//				SimpleFeature next = writer.next();
//				SimpleFeature feature = features.next();
//				for (int i = 0; i < feature.getAttributeCount(); i++) {
//					System.out.print(feature.getAttribute(i).toString() + " ");
//					next.setAttribute(i, feature.getAttribute(i));
//				}
//				System.out.println();
//
//				try {
//					writer.write();
//				} catch (Exception e) {
//					for (int i = 0; i < feature.getAttributeCount(); i++) {
//						System.out.print(feature.getAttribute(i).toString() + " ");
//					}
//					System.out.println();
//				}
//			}
//
//			writer.close();
//			ds.dispose();
//			System.out.println("导入成功");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // SimpleFeatureIterator itertor = featureSource.getFeatures() // .features();
//			// //create the builder SimpleFeatureBuilder builder = new
//			// SimpleFeatureBuilder(schema);
//
//	}
//
//	// 测试代码
//	public static void main(String[] args) {
//		JDBCDataStore connnection2mysql = shp2mysql2.connnection2mysql("127.0.0.1", "geotools", 3306, "geotools",
//				"123456");
//		SimpleFeatureSource featureSource = readSHP(
//				"D:\\study\\研究生\\毕业论文\\data\\map\\osm\\gis_osm_roads_free\\gis_osm_roads_free_1.shp");
//		JDBCDataStore ds = createTable(connnection2mysql, featureSource);
//		writeShp2Mysql(ds, featureSource);
//	}
//
//}
