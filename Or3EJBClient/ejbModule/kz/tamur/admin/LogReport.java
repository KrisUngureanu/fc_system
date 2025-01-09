package kz.tamur.admin;

import java.io.*;

public class LogReport {
    private static String fileSepar = File.separator;
    private static String dirlog = "logs";
    private static String filelog = "users.log";
    private static String pathlog = dirlog + fileSepar + filelog;
    
/*    public static void main(String[] args) throws IOException {
        if (args.length ==0 || (!args[0].equals("l") && !args[0].equals("r")&& !args[0].equals("rm") && !args[0].equals("rmu") && !args[0].equals("rmp") && !args[0].equals("a") && !args[0].equals("e") && !args[0].equals("cut"))) {
            System.out.println("Invalid paramters");
            System.out.println("Usage: compress log <l>");
            System.out.println("       log for period <r> <yyyy-MM-dd> <yyyy-MM-dd> [reg_expr]");
            System.out.println("       agregate log report for period <rm> <yyyy-MM-dd> <yyyy-MM-dd>");
            System.out.println("       agregate log report for period <rmu> <yyyy-MM-dd> <yyyy-MM-dd>");
            System.out.println("       agregate log report for period <rmp> <yyyy-MM-dd> <yyyy-MM-dd>");
            System.out.println("       compress file <a>");
            System.out.println("       extract file <e> <file>");
            System.exit(1);
        }
        String cmd=args[0];
        String start = null;
        String end = null;
        Pattern cRe;
        String fileRpt = "remote.txt";
        
        if(cmd.equals("r")){
            if (args.length < 2  || args[1] == null) start = getCurrDate();
            else start = args[1];
            if (args.length < 3  || args[2] == null) end = getCurrDate();
            else end = args[2];
            if (args.length > 3 && args[3] != null)
            cRe = Pattern.compile(args[3]);
            fileRpt = args.length > 3 ? args[3] : "remote.txt";
            collectDataForRpt(new File(pathlog),0);
        }
    }

    private static String getCurrDate(){
        Date curDate = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        
        int mt = cal.get(Calendar.MONTH) + 1;
        int dm = cal.get(Calendar.DAY_OF_MONTH);
        int g = cal.get(Calendar.YEAR);
        String year = "" + g;
        String month = mt > 9 ? "" + mt : "0" + mt;
        String day = dm > 9 ? "" + dm : "0" + dm;
        return year + "-" + month + "-" + day;     // Return format YYYY-MM-DD
    }
    
    private static void collectDataForRpt(File file, int rptType, String start, String end){
        String file_name = file.getName();
        String start_year = start.substring(0, start.indexOf("-"));
        String start_month = start.substring(0, start.lastIndexOf("-"));
        String end_year = end.substring(0, end.indexOf("-"));
        String end_month = end.substring(0, end.lastIndexOf("-"));
        String str_curr_date = getCurrDate();
        ArrayList ay = new ArrayList();
        ArrayList amd = new ArrayList();
        
        amd.add(file);

        File dir = file.getParentFile();
        File[] files = dir.listFiles();
        for(int i=0; i<files.length; ++i){
            if (files[i].getName().indexOf(file_name+".")<0) continue;
            String suffix = files[i].getName().substring(file_name.length()+1);
            if (suffix.indexOf("zip")>0){
                String year_ = suffix.substring(0, suffix.indexOf("."));
                if(year_.compareTo(start_year)<0 || year_.compareTo(end_year)>0) continue;
                ay.add(files[i]);
            } else if(suffix.indexOf("-") > 0){
                int ind = suffix.indexOf("-");
                if (suffix.indexOf("-", ind+1)>0){
                    //Дни месяца
                    if(suffix.compareTo(start) < 0 || suffix.compareTo(end) > 0) continue;
                    amd.add(files[i]);
                }else{
                    //Месяцы
                    if(suffix.compareTo(start_month) < 0 || suffix.compareTo(end_month) > 0) continue;
                    amd.add(files[i]);
                }
            }
        }
        
        boolean gt = false;
        for(int i=0; i < amd.size(); ++i){
            if (gt) break;
            try {
                gt = lp(new FileReader((File)amd.get(i)),rptType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        for(int i=0;i<ay.size();++i){
            if(gt) break;
            File year_=(File)ay.get(i);
            try {
                ZipFile zf = new ZipFile(year_);
                Enumeration e = zf.entries();
                while (e.hasMoreElements()) {
                    ZipEntry ze = (ZipEntry)e.nextElement();
                    String file_name_=ze.getName();
                    file_name_= file_name_.substring(file_name_.lastIndexOf(".")+1);
                    if(file_name_.compareTo(start_month) < 0 || file_name_.compareTo(end_month) > 0) continue;
                    InputStream is = zf.getInputStream(ze);
                    lp(new InputStreamReader(is),rptType);
                    is.close();
                }
                zf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(rptType>0){
            try {
                createReport(rptType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static boolean lp(Reader r, int rptType) throws IOException {
        String date="", event="", user="", organization="", process="";
        
        boolean gt = false;
        boolean lt = false;
        boolean newLn = true;
        int countDelim = 0;
        
        StreamTokenizer tok = new StreamTokenizer(r);
        String str = "";
        tok.resetSyntax();
        tok.eolIsSignificant(true);
        tok.wordChars(33,256);
        
        while (tok.nextToken() != StreamTokenizer.TT_EOF){
            if (rptType>0) {
                switch(tok.ttype){
                   case StreamTokenizer.TT_EOL:
                       if(countDelim == 4) {
                           if((rptType < 3 && (event.equals(" user login") || event.equals(" user logout")))
                                   || (rptType==3 && (event.equals(" start process") || event.equals(" kill process")))){
                           Object li = messageMap.get(organization+"|"+(rptType==1?user:"")+process);
                           Object lu = null;
                               if(rptType==2)
                                   lu = durationMap.get(organization+"|"+user);
                           if(li==null){
                               long[] p= new long[3];
                               p[0]=1;
                               if(rptType<3 && event.equals(" user login")){
                                   try{
                                       if(rptType==1){
                                           p[1]= df.parse(date).getTime();
                                       }else{
                                           if(lu==null){
                                               long[] pu= new long[2];
                                               pu[0]= df.parse(date).getTime();
                                               durationMap.put(organization+"|"+user,pu);
                                           }else{
                                               ((long[])lu)[0]= df.parse(date).getTime();
                                           }
                                       }
                                   } catch (ParseException e) {
                                       e.printStackTrace();
                                   }
                               }
                               messageMap.put(organization+"|"+(rptType==1?user:"")+process,p);
                           }else{
                               if(rptType==3 && event.equals(" kill process"))
                                   ((long[])li)[0]=((long[])li)[0]-1;
                               else if((rptType<3 && event.equals(" user login"))||(rptType==3 && event.equals(" start process")))
                                   ((long[])li)[0]=((long[])li)[0]+1;
                               if(rptType<3){
                                   if(event.equals(" user login")){
                                       try{
                                           if(rptType==1){
                                               if(((long[])li)[1]>0){
                                                   ((long[])li)[2]= ((long[])li)[2]+(df.parse(date).getTime()-((long[])li)[1]);
                                               }
                                               ((long[])li)[1]= df.parse(date).getTime();
                                           }else if(lu==null){
                                                   long[] pu= new long[2];
                                                   pu[0]= df.parse(date).getTime();
                                                   durationMap.put(organization+"|"+user,pu);
                                           }else{
                                               ((long[])lu)[0]= df.parse(date).getTime();
                                           }
                                       } catch (ParseException e) {
                                           e.printStackTrace();
                                       }
                                   }else if(event.equals(" user logout")){
                                       try{
                                           if(rptType==1){
                                               ((long[])li)[2]= ((long[])li)[2]+(df.parse(date).getTime()-((long[])li)[1]);
                                               ((long[])li)[1]= 0;
                                           }else if(lu!=null){
                                               ((long[])lu)[1]= df.parse(date).getTime()-((long[])lu)[0];
                                               ((long[])li)[2]= ((long[])li)[2]+((long[])lu)[1];
                                               ((long[])lu)[0]= 0;
                                           }
                                       } catch (ParseException e) {
                                           e.printStackTrace();
                                       }
                                   }
                               }
                           }
                       }
                       }
                        newLn=true;
                        countDelim=0;
                        date="";event="";user="";organization="";process="";
                        lt=false;
                        break;
                   case StreamTokenizer.TT_WORD:
                        if(lt) break;
                        if(newLn){
                            newLn=false;
                            date=tok.sval;
                            lt=(date.compareTo(start)<0);
                            gt=(date.compareTo(end)>0);
                        }else if(tok.sval.equals("|")){
                            countDelim++;
                        }else{
                            if(countDelim==0){
                                date +=" "+tok.sval;
                            }else if(countDelim==1){
                                event +=" " + tok.sval;
                            }else if(countDelim==2 ){
                                user += " " + tok.sval;
                            }else if(countDelim==3){
                                organization += " " + tok.sval;
                            }else if(countDelim==4){
                                process += rptType==3?" " + tok.sval:"";
                            }
                        }
                        break;
                }
                if(gt) break;
            }else{
                switch(tok.ttype){
                   case StreamTokenizer.TT_EOL:
                        newLn=true;
                        if(lt) lt=false;
                        else{
                            if(cRe==null || cRe.matcher(str).find())
                            System.out.println(str);
                            str="";
                        }
                        break;
                   case StreamTokenizer.TT_WORD:
                        if(lt) break;
                        if(newLn){
                            newLn=false;
                            date=tok.sval;
                            lt=(date.compareTo(start)<0);
                            gt=(date.compareTo(end)>0);
                        }
                       if(!lt && !gt)
                           str += (tok.sval!=null?tok.sval+" ":" ");
                       break;
                }
                if(gt) break;
            }
        }
    }

*/}
