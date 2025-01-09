package com.cifs.or2.server.plugins;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class ShapePlugin implements SrvPlugin {

	private static final int BUFFER_SIZE = 4096;
	private static final String TMP_DIR = "shapeTmp";
	private String fileName = null;
	private String dirName = null;
	
	private Session session;

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}
	
	public byte[] writeToByte(List multipolygon) throws Exception {
		byte[] res = null;
        try {
	        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
	        builder.setName("polygonFeature");
	        builder.setCRS(DefaultGeographicCRS.WGS84);
	        builder.add("the_geom", Polygon.class);
	        SimpleFeatureType POLYGON = builder.buildFeatureType();
	
	        DefaultFeatureCollection collection = new DefaultFeatureCollection();
	        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
	        
	
	        for (Object polygon : multipolygon) {
	        	List listPolygon = (List) polygon;
	        	
		        Coordinate[] coords = new Coordinate[listPolygon.size()];
		
		        int i = 0;
		        for (Object coord : listPolygon) {
		        	List listCoord = (List) coord;
		            Coordinate geoCoord = new Coordinate((Double) (listCoord.get(0)), (Double) (listCoord.get(1)), 0);
		            coords[i] = (geoCoord);
		            i++;
		        }
		
		        Polygon geoPolygon = geometryFactory.createPolygon(coords);
		
		        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(POLYGON);
		        featureBuilder.add(geoPolygon);
		        SimpleFeature feature = featureBuilder.buildFeature(null);
		        
		        collection.add(feature);
	        
	        }
	        
	
	        File tmpDir = new File(TMP_DIR);
	        deleteFolderContent(tmpDir);
	        tmpDir.mkdir();
	        File shapeFile = new File(tmpDir.getAbsolutePath() + File.separator + "shapefile.shp");
	
	        Map<String, Serializable> params = new HashMap<>();
	        params.put("url", shapeFile.toURI().toURL());
	        params.put("create spatial index", Boolean.TRUE);
	
	        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
	
	        ShapefileDataStore dataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
	        dataStore.createSchema(POLYGON);
	
	        Transaction transaction = new DefaultTransaction("create");
	
	        String typeName = dataStore.getTypeNames()[0];
	        SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
	
	        if (featureSource instanceof SimpleFeatureStore) {
	            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
	            featureStore.setTransaction(transaction);
	            try {
	                featureStore.addFeatures(collection);
	                transaction.commit();
	
	            } catch (Exception problem) {
	                transaction.rollback();
	            } finally {
	                transaction.close();
	            }
	        }
	        
	        File[] files = new File(TMP_DIR).listFiles();
	        File zipFile = new File(TMP_DIR + File.separator + TMP_DIR + ".zip");
	        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
	        for (File file : files) {
	        	zipFile(file, zos);
	        }
	        zos.flush();
	        zos.close();
	        
	        res =  Files.readAllBytes(zipFile.toPath());
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
			try {
				deleteFolderContent(new File(TMP_DIR));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return res;
	}
	
	public List<List<List<Double>>> readFromByte(byte[] archive) {
		List<List<List<Double>>> res = new ArrayList<>();
		try {
			unzip(archive);
	        
			String filePath = dirName != null ? (dirName + File.separator + fileName) 
											  : (TMP_DIR + File.separator + fileName);
			
	        FileDataStore store = FileDataStoreFinder.getDataStore(new File(filePath));
	        SimpleFeatureSource featureSource = store.getFeatureSource();
	        SimpleFeatureCollection features = featureSource.getFeatures();
	
	        try(SimpleFeatureIterator itr1 = features.features()){
	        	while(itr1.hasNext()){
	        		List<List<Double>> polygon = new ArrayList<>();
	        		res.add(polygon);
		    	    SimpleFeature f = itr1.next();
		    	    Geometry g = (Geometry)f.getDefaultGeometry();
		    	    Coordinate[] coordinates = g.getCoordinates();
		    	    for (Coordinate coord : coordinates) {
		    	    	List<Double> coords = new ArrayList<Double>();
		    	    	polygon.add(coords);
		    	    	coords.add(Math.round(coord.x * 100.0) / 100.0);
		    	    	coords.add(Math.round(coord.y * 100.0) / 100.0);
		    	    }
	        	}
	    	}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				deleteFolderContent(new File(TMP_DIR));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return res;
	}
	
	private void zipFile(File file, ZipOutputStream zos)
            throws Exception {
        zos.putNextEntry(new ZipEntry(file.getName()));
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                file));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = bis.read(bytesIn)) != -1) {
            zos.write(bytesIn, 0, read);
        }
        bis.close();
        zos.closeEntry();
    }
	
	private void deleteFolderContent(File folder){
	    File[] files = folder.listFiles();
	    if(files!=null)
	        for(File f: files)
	            if(f.isDirectory()) deleteFolderContent(f);
	            else f.delete();
	    folder.delete();
	}
	
	private void extractFile(ZipInputStream zipIn, String filePath) throws Exception {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
	
	private boolean unzip(byte[] archive) throws Exception {
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(archive));
		File destDir = new File(TMP_DIR);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

		ZipEntry zipEntry = null;
		int count = 0;
		while ((zipEntry = zipStream.getNextEntry()) != null) {
			count++;
			String entryName = zipEntry.getName();
			String[] extensions = entryName.split("\\.");
			if ("shp".equals(extensions[extensions.length - 1])) {
				fileName = entryName;
			}
			String filePath = TMP_DIR + File.separator + entryName;
			if (!zipEntry.isDirectory()) {
                extractFile(zipStream, filePath);
            } else {
            	if (dirName == null) {
            		String splitter = File.separator.replace("\\","\\\\");
            		dirName = filePath.split(splitter)[0];
            	}
                File dir = new File(filePath);
                dir.mkdirs();
            }
			zipStream.closeEntry();
		}
		zipStream.close();
		
		return count > 0;
	}
}
