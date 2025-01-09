import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Test {

	public static void main(String[] args) throws Exception {
		
/*		System.out.println(UUID.randomUUID());
		System.out.println("database.log.2013-09-27".compareTo("database.log.2013-09-21"));
		System.out.println("database.log.2013-09-27".compareTo("database.log"));
		
		Date dateFrom = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("27.09.2013 11:20:00");
		Date dateTo = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("28.09.2013 16:30:54");
		
		List<String[]> arr = ReportLogHelper.readLogAsList("C:/jboss-as-7.1.1.Final/standalone/log", "protocol", 0, 10, dateFrom, dateTo, "Вход в систему", null, null);
		ReportLogHelper.printToFile(dateFrom, dateTo, "Вход в систему", null, null, arr);
*/
		
		File dir = new File("C:/ekyz-real");
		File dir2 = new File("C:/ekyz-real3");
		
		List<Long> classIds = new ArrayList<Long>();

		InputStream in = new BufferedInputStream(new FileInputStream(dir + "/" + "t_classes"));
        BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String row = r.readLine();
        while ((row = r.readLine()) != null) {
        	int end = row.indexOf("-|-");
            Long id = Long.valueOf(row.substring(0, end));
            
        	classIds.add(id);
        }
        r.close();
        in.close();
		
		in = new BufferedInputStream(new FileInputStream(dir + "/" + "t_attrs"));
        r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dir2+"/t_attrs"));
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

        row = r.readLine();
        w.write(row);
        w.newLine();
        
        List<Attr> attrs = new ArrayList<Test.Attr>();

        while ((row = r.readLine()) != null) {
            int beg = row.indexOf("-|-");
            Long id = Long.valueOf(row.substring(0, beg));

            int end = row.indexOf("-|-", beg + 3);
            Long classId = Long.valueOf(row.substring(beg + 3, end));

        	beg = row.indexOf("-|-", end + 3) + 3;
        	end = row.indexOf("-|-", beg);
            Long typeClassId = Long.valueOf(row.substring(beg, end));
            
        	beg = row.indexOf("-|-", end + 3) + 3;
        	beg = row.indexOf("-|-", beg) + 3;
        	beg = row.indexOf("-|-", beg) + 3;
        	beg = row.indexOf("-|-", beg) + 3;
        	beg = row.indexOf("-|-", beg) + 3;
        	beg = row.indexOf("-|-", beg) + 3;
        	beg = row.indexOf("-|-", beg) + 3;
        	end = row.indexOf("-|-", beg);
            
            Long rAttrId = Long.valueOf(row.substring(beg, end));
            
            attrs.add(new Attr(id, rAttrId, classId, typeClassId, row));
            
            if (!classIds.contains(classId) || !classIds.contains(typeClassId))
            	System.out.println(row);
            
        }
        r.close();
        in.close();
        
        Attr[] arr = attrs.toArray(new Attr[attrs.size()]);
        
        Arrays.sort(arr, new Comparator<Attr>() {
			@Override
			public int compare(Attr o1, Attr o2) {
				
				return (o1.rattrId * 100000000 + o1.classId * 10000 + o1.id) > (o2.rattrId * 100000000 + o2.classId * 10000 + o2.id) ? 1 : -1;
			}
		});
		
        for (Attr attr : arr) {
            w.write(attr.row);
            w.newLine();
        }
        w.close();
        out.close();
        
        //File attrFile = new File(dir+"/t_attrs");
        //attrFile.delete();
        //new File(dir2+"/t_attrs").renameTo(attrFile);

		//System.exit(0);
		
		File[] fs = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File f) {
				return f.getName().startsWith("ct");
			}
		});
		
		for (File f : fs) {
			System.out.println(f.getName());
			
			final String classId = f.getName().substring(2);
			
			File[] afs = dir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File af) {
					return af.getName().startsWith("at" + classId + "_");
				}
			});
			
			if (afs != null && afs.length > 0) {
				List<Long> objIds = new ArrayList<Long>();
				
                in = new BufferedInputStream(new FileInputStream(f));
                r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                row = r.readLine();
                while ((row = r.readLine()) != null) {
                	int beg = row.indexOf("-|-") + 3;
                	int end = row.indexOf("-|-", beg);
                    Long id = Long.valueOf(row.substring(beg, end));
                    
                    if (!objIds.contains(id))
                    	objIds.add(id);
                    
                }
                r.close();
                in.close();

    			for (File af : afs) {
    				System.out.println(af.getName());
    				
                    in = new BufferedInputStream(new FileInputStream(af));
                    r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    row = r.readLine();

                    out = new BufferedOutputStream(new FileOutputStream(dir2+"/" + af.getName() + "_"));
                    w = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    w.write(row);
                    w.newLine();
                    
                    while ((row = r.readLine()) != null) {
                    	int end = row.indexOf("-|-");
                    	
                    	Long id = Long.valueOf(row.substring(0, end));
                        
                        if (objIds.contains(id)) {
                            w.write(row);
                            w.newLine();
                        }
                    }
                    r.close();
                    in.close();
                    w.close();
                    out.close();
                    af.delete();
                    new File(dir2+"/" + af.getName() + "_").renameTo(af);

    			}
			}
		}
		
		
		//Pattern filter = Pattern.compile(".*\\.gif|.*\\.js|.*\\.jpeg|.*\\.jpg|.*\\.png|.*\\.htm|.*\\.html|.*\\.css|.*\\.txt|.*\\.js\\?.*|.*\\.css\\?.*|.*/chat/poller\\?initialRequest=false|.*/mail/new_messages_col|.*/npa/indicator|.*/alerts/checkCal");
		
		//boolean res = filter.matcher("http://195.12.104.37:8282/web/guest/tools/-/mail/new_messages").matches();
		
		//String res = URLEncoder.encode("Бастыков А A A", "UTF-8").replace("+", "%20");
		
		//System.out.println(res);
		/*
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		//Properties props = new Properties();
		//props.put("charSet", "Cp1251");
		Connection conn = java.sql.DriverManager.getConnection("jdbc:odbc:TestDSN");
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM BUILD WHERE OBJECTID=644");
		int count = 10;
		while (rs.next() && count-- > 0) {
			System.out.println(rs.getString("ARHIV_HOUSE_1"));
		}
		rs.close();
		st.close();
		conn.close();
		
		DOMOutputter out = new DOMOutputter();
		out.setForceNamespaceAware(true);
		*/
	}
	
	static class Attr {
		private long id;
		private long rattrId;
		private long classId;
		private long typeId;
		private String row;
		
		public Attr(long id, long rattrId, long classId, long typeId, String row) {
			super();
			this.id = id;
			this.rattrId = rattrId;
			this.classId = classId;
			this.typeId = typeId;
			this.row = row;
		}
	}
}
