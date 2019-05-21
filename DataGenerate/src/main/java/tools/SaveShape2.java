package tools;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
//对接2012数据input_*数据
public class SaveShape2 {

    public void SavePoints(String filePath, List<String> pointData) {
        int count=0;

        try {
            // 创建shape对象
            File file = new File(filePath);
            Map<String, Serializable> params = new HashMap<>();
            params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);

            // 定义图形信息和属性信息
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
            tb.setCRS(DefaultGeographicCRS.WGS84);
            tb.setName("shapefile");
            tb.add("the_geom", Point.class);
            tb.add("number", Integer.class);
            tb.add("longitude", Double.class);
            tb.add("latitude", Double.class);
            ds.createSchema(tb.buildFeatureType());
            ds.setCharset(Charset.forName("UTF-8"));

            // 设置Writer
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0],
                    Transaction.AUTO_COMMIT);



            for (int i = 0; i< pointData.size(); i++) {
                SimpleFeature feature = writer.next();

                String line = pointData.get(i);
                String[] split = line.split("	");//output用
//                String[] split = line.split(",");//input用
                Integer number = Integer.valueOf(split[0]);
                Double longitude = Double.valueOf(split[2]);
                Double latitude = Double.valueOf(split[1]);
//                Double distance = Double.valueOf(split[22]);

                Coordinate pp = new Coordinate(longitude, latitude);

                feature.setAttribute("the_geom", new GeometryFactory().createPoint(pp));
                feature.setAttribute("number", number);
                feature.setAttribute("longitude", longitude);
                feature.setAttribute("latitude", latitude);
                count++;
//                System.out.println("第" + count + "条success！");
            }
            System.out.println("总共" + count + "条success！");
            writer.write();
            writer.close();
            ds.dispose();
//            System.out.println("******数据转线*******");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}