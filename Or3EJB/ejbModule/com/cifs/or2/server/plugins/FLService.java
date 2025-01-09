package com.cifs.or2.server.plugins;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.rmi.RemoteException;

import javax.xml.rpc.Stub;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import com.cifs.or2.server.orlang.SrvPlugin;

import kz.tamur.ws.fl.*;
import kz.tamur.ws.fl.shep.*;

public class FLService extends Thread implements SrvPlugin {
    private String endpoint = "";
    private int timeout = 10000;
    private int times = 10;
    private static Log log = LogFactory.getLog(FLService.class);
    //Constants
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final String noErrors = "00";

    private static final String transportPath = "transport.properties";
    private static final String WS_ADDRESS = "ws_endpoint_address";
    private static final String WS_TIMEOUT = "ws_timeout";
    private static final String WS_TIMES = "ws_times";

    private Element personTag;
    private int number;
    com.cifs.or2.server.Session s;

    public FLService(Element personTag, int number) {
        this();
        this.personTag = personTag;
        this.number = number;
    }
    
    public com.cifs.or2.server.Session getSession() {
        return s;
    }

    public void setSession(com.cifs.or2.server.Session session) {
        s = session;
    }

    public void run() {
        try {
            long begin = System.currentTimeMillis();
            Element res = checkPersons(personTag);
            long end = System.currentTimeMillis();
            System.out.println("Process " + number + ". Time: " + (end - begin) + " ms.");
            if (res != null) {
                Format ft = Format.getPrettyFormat();
                ft.setEncoding("UTF-8");
                XMLOutputter f = new XMLOutputter(ft);
                File xmlFile = new File("doc/out" + number + ".xml");

                OutputStream os = new FileOutputStream(xmlFile);
                f.output(res, os);
                os.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public FLService() {
        try {
            Properties props = new Properties();
            File cf = new File(transportPath);
            if (cf.exists()) {
                props.load(new FileInputStream(cf));
                endpoint = props.getProperty(WS_ADDRESS);
                String tmp = props.getProperty(WS_TIMEOUT);
                if (tmp != null && tmp.length() > 0) {
                    timeout = Integer.parseInt(tmp);
                }
                tmp = props.getProperty(WS_TIMES);
                if (tmp != null && tmp.length() > 0) {
                    times = Integer.parseInt(tmp);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Element checkPersonsFL(Element person) {
        try {
            Stub stub = (Stub) (new GbdflServiceHttp_Impl().getGbdflServicePort());
            stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY,
                    endpoint);
            GbdflService pSrv = (GbdflService) stub;

            String guid = UUID.randomUUID().toString();

            SystemInfoType si = new SystemInfoType();
            si.setMessageId(guid);
            si.setChainId(guid);
            si.setMessageDate(Calendar.getInstance());
            si.setMessageType(MessageType.fromString("other"));
            si.setType("request");
            si.setResponse("");
            si.setInfo("");
            si.setOperator("GBDUL");
            si.setConId("conId");
            si.setDigiSign("digiSign");
            si.setDigiSignOpt("digiSignOpt");

            long begin = 0;
            PersonInfoType pi = null;
            String response = "";
            String info = "";
            
            String iin = person.getChildText("IN");
            if (iin != null && iin.length() > 0) {
            	GBDFLPersonInfoByIinRequest req = new GBDFLPersonInfoByIinRequest();
            	req.setSystemInfo(si);
            	req.setIIN(iin);

	            begin = System.currentTimeMillis();
	            System.out.println("Process " + guid + " started.");
	            GBDFLPersonInfoResponse psres = pSrv.getPersonByIin(req);
	            long end = System.currentTimeMillis();
	            System.out.println("Process " + guid + ". CheckStatus Time: " + (end - begin) + " ms.");

	            SystemInfoType sir = psres.getSystemInfo();
	            response = sir.getResponse();
	            info = sir.getInfo();

	            pi = psres.getPersonInfo();
	            
                PersonDocumentType docObj = findMostRelevantDocument(pi);
                
                if (docObj != null) {
                    Element tag = person.getChild("IN");
                    String val = pi.getIIN();
                    if (val != null) {
                        if (tag == null) {
                        	tag = new Element("IN");
                        	person.addContent(tag);
                        }
                    	tag.setText(val);
                    } else if (tag != null) {
                    	tag.setText("");
                    }

                    tag = person.getChild("FirstName");
                    val = pi.getPersonName().getFirstName();
                    if (val != null) {
                        if (tag == null) {
                        	tag = new Element("FirstName");
                        	person.addContent(tag);
                        }
                    	tag.setText(val);
                    } else if (tag != null) {
                    	tag.setText("");
                    }

                    tag = person.getChild("LastName");
                    val = pi.getPersonName().getLastName();
                    if (val != null) {
                        if (tag == null) {
                        	tag = new Element("LastName");
                        	person.addContent(tag);
                        }
                    	tag.setText(val);
                    } else if (tag != null) {
                    	tag.setText("");
                    }

                    tag = person.getChild("SecondName");
                    val = pi.getPersonName().getMiddleName();
                    if (val != null) {
                        if (tag == null) {
                        	tag = new Element("SecondName");
                        	person.addContent(tag);
                        }
                    	tag.setText(val);
                    } else if (tag != null) {
                    	tag.setText("");
                    }

                    Element doc = person.getChild("Document");
                    if (doc == null) {
                    	doc = new Element("Document");
                    	person.addContent(doc);
                    }

                	tag = doc.getChild("DocumentType");
                	if (tag == null) {
                		tag = new Element("DocumentType");
                		doc.addContent(tag);
                	}
                	Element tag2 = tag.getChild("CodeFL");
                	if (tag2 == null) {
                		tag2 = new Element("CodeFL");
                		tag.addContent(tag2);
                	}
                    val = docObj.getTypeCode();
                    if (val != null) tag2.setText(val);
                    else tag2.setText("");

                	tag = doc.getChild("Series");
                	if (tag == null) {
                		tag = new Element("Series");
                		doc.addContent(tag);
                	}
                    val = docObj.getSeries();
                    if (val != null) tag.setText(val);
                    else tag.setText("");

                	tag = doc.getChild("Number");
                	if (tag == null) {
                		tag = new Element("Number");
                		doc.addContent(tag);
                	}
                    val = docObj.getNumber();
                    if (val != null) tag.setText(val);
                    else tag.setText("");

                	tag = doc.getChild("IssueDate");
                	if (tag == null) {
                		tag = new Element("IssueDate");
                		doc.addContent(tag);
                	}
                    val = docObj.getIssueDate() != null ? sdf.format(docObj.getIssueDate().getTime()) : null;
                    if (val != null) tag.setText(val);
                    else tag.setText("");

                	tag = doc.getChild("AuthorityCode");
                	if (tag == null) {
                		tag = new Element("AuthorityCode");
                		doc.addContent(tag);
                	}
                    val = docObj.getIssuer_CODE();
                    if (val != null) tag.setText(val);
                    else tag.setText("");
                } else {
                	response = "1";
                	info = "Документы не найдены";
                }
            } else {

	            GBDFLPersonInfoByDocRequest req = new GBDFLPersonInfoByDocRequest();
	            req.setSystemInfo(si);
	            req.setDocumentIssuer("");
	
	            String tmp = person.getChild("Document").getChild("DocumentType").getChildText("CodeFL");
	            req.setDocumentType(tmp);
	
	            tmp = person.getChild("Document").getChildText("Series");
	            req.setDocumentSeries(tmp);
	            tmp = person.getChild("Document").getChildText("Number");
	            req.setDocumentNumber(tmp);
	            tmp = person.getChild("Document").getChildText("IssueDate");
	            Calendar docDate = null;
	            if (tmp != null && tmp.length() > 0) {
	                docDate = Calendar.getInstance();
	                Date date;
	                synchronized(sdf) {
	                    date = sdf.parse(tmp);
	                }
	                docDate.setTimeInMillis(date.getTime()
	                        + docDate.getTimeZone().getRawOffset());
	            }
	            req.setDocumentIssueDate(docDate);
	
	            begin = System.currentTimeMillis();
	            System.out.println("Process " + guid + " started.");
	            GBDFLPersonInfoListResponse psres = pSrv.getPersonByDocument(req);
	            long end = System.currentTimeMillis();
	            System.out.println("Process " + guid + ". CheckStatus Time: " + (end - begin) + " ms.");

	            SystemInfoType sir = psres.getSystemInfo();
	            response = sir.getResponse();
	            info = sir.getInfo();

	            if ("0".equals(response)) {
	                PersonInfoListType pilt = psres.getPersonInfoList();

	                if (pilt.getItem() != null && pilt.getItem().length > 0) {
	                    pi = pilt.getItem()[0];

	                    Element doc = person.getChild("Document");
	                    String docType = doc.getChild("DocumentType").getChildText("CodeFL");
	                    String docSeries = doc.getChildText("Series");
	                    String docNumber = doc.getChildText("Number");
	                    String docIssueDate = doc.getChildText("IssueDate");

	                    Object docObj = findDocument(docType, docSeries, docNumber, docIssueDate, pi);
	                    if (docObj instanceof PersonDocumentType) {
	                        Element tag = person.getChild("IN");
	                        String val = pi.getIIN();
	                        if (val != null) {
	                            if (tag == null) {
	                            	tag = new Element("IN");
	                            	person.addContent(tag);
	                            }
	                        	tag.setText(val);
	                        } else if (tag != null) {
	                        	tag.setText("");
	                        }

	                        tag = person.getChild("FirstName");
	                        val = pi.getPersonName().getFirstName();
	                        if (val != null) {
	                            if (tag == null) {
	                            	tag = new Element("FirstName");
	                            	person.addContent(tag);
	                            }
	                        	tag.setText(val);
	                        } else if (tag != null) {
	                        	tag.setText("");
	                        }

	                        tag = person.getChild("LastName");
	                        val = pi.getPersonName().getLastName();
	                        if (val != null) {
	                            if (tag == null) {
	                            	tag = new Element("LastName");
	                            	person.addContent(tag);
	                            }
	                        	tag.setText(val);
	                        } else if (tag != null) {
	                        	tag.setText("");
	                        }

	                        tag = person.getChild("SecondName");
	                        val = pi.getPersonName().getMiddleName();
	                        if (val != null) {
	                            if (tag == null) {
	                            	tag = new Element("SecondName");
	                            	person.addContent(tag);
	                            }
	                        	tag.setText(val);
	                        } else if (tag != null) {
	                        	tag.setText("");
	                        }

	                        tag = doc.getChild("AuthorityCode");
	                        val = ((PersonDocumentType) docObj).getIssuer_CODE();
	                        if (val != null) {
	                            if (tag == null) {
	                            	tag = new Element("AuthorityCode");
	                            	doc.addContent(tag);
	                            }
	                        	tag.setText(val);
	                        } else if (tag != null) {
	                        	tag.setText("");
	                        }
	                        
	                    } else if (docObj instanceof IrrelevantPersonDocumentType) {
	                        response = "1";
	                        info = "Документ не действителен (код " + ((IrrelevantPersonDocumentType) docObj).getValidityCode() + ")";
	                    } else {
	                    	response = "1";
	                    	info = "Документ не найден";
	                    }
	                }
	            }
            }

            Element infoTag = new Element("Info");
            infoTag.setText(info);
            person.addContent(infoTag);
            Element responseTag = new Element("Response");
            responseTag.setText(response);
            person.addContent(responseTag);

        } catch (RemoteException re) {
//            MessageCash.writeLogRecord(ErrorConstants.WSC_100, guid, GBL_IN,
//                    "", endpoint);
            log.error(re.getMessage());
            re.printStackTrace();
            return null;
        } catch (Throwable e) {
//            MessageCash.writeLogRecord(ErrorConstants.WSC_101, guid, GBL_IN,
//                    "", endpoint);
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return person;
    }

    public Element checkPersons(Element person) {
        try {
            Stub stub = (Stub) (new GBDFL2009ServiceService_Impl().getGBDFL2009Service());
            stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY,
                    endpoint);
            GBDFL2009Service pSrv = (GBDFL2009Service) stub;

            String guid = UUID.randomUUID().toString();

            SystemInfo_ si = new SystemInfo_();
            si.setMessageID(guid);
            si.setChainID(guid);
            si.setMessageDate(Calendar.getInstance());
//            si.setMessageType(MessageType.fromString("other"));
//            si.setType("request");
            si.setResponse("");
//            si.setInfo("");
            si.setOperatorId("{3DF14D85-245F-46d0-8C75-B8FB28AFD42A}");
            si.setConId("conId");
            si.setDigiSign("digiSign");
//            si.setDigiSignOpt("digiSignOpt");

//            req.setDocumentIssuer("");

//            String tmp = person.getChild("Document").getChild("DocumentType").getChildText("CodeFL");
//            req.setDocumentType(tmp);

//            tmp = person.getChild("Document").getChildText("Series");
//            req.setDocumentSeries(tmp);
            FullResponse_ psres = null;
            long begin = 0;
            
            String iin = person.getChildText("IN");
            if (iin != null && iin.length() > 0) {
            	IINRequest_ req = new IINRequest_();
            	req.setInfo(si);
            	req.setIIN(iin);

                begin = System.currentTimeMillis();
                System.out.println("Process " + guid + " started.");
                psres = pSrv.getPersonByIIN(req);
            } else {
                String docNum = person.getChild("Document").getChildText("Number");
            	DocumentRequest_ req = new DocumentRequest_();
                req.setInfo(si);
	            req.setDocNumber(docNum);
//            tmp = person.getChild("Document").getChildText("IssueDate");
//            Calendar docDate = null;
//            if (tmp != null && tmp.length() > 0) {
//                docDate = Calendar.getInstance();
//                Date date;
//                synchronized(sdf) {
//                    date = sdf.parse(tmp);
//                }
//                docDate.setTimeInMillis(date.getTime()
//                        + docDate.getTimeZone().getRawOffset());
//            }
//            req.setDocumentIssueDate(docDate);
                begin = System.currentTimeMillis();
                System.out.println("Process " + guid + " started.");
	            psres = pSrv.getPersonByDocument(req);
            }

            long end = System.currentTimeMillis();
            System.out.println("Process " + guid + ". CheckStatus Time: " + (end - begin) + " ms.");

            SystemInfo_ sir = psres.getInfo();
            String response = sir.getResponse();
            if (response == null) response = "0";
            String info = ""; //sir.getInfo();

            if ("0".equals(response)) {
                CommonInfo_ ci = psres.getCommonInfo();
                FIO_ fio = psres.getCurrentFIO();

                Element tag = person.getChild("IN");
                String val = ci.getIIN();
                if (val != null) {
                    if (tag == null) {
                    	tag = new Element("IN");
                    	person.addContent(tag);
                    }
                	tag.setText(val);
                } else if (tag != null) {
                	tag.setText("");
                }

                tag = person.getChild("FirstName");
                val = fio.getFirstName();
                if (val != null) {
                    if (tag == null) {
                    	tag = new Element("FirstName");
                    	person.addContent(tag);
                    }
                	tag.setText(val);
                } else if (tag != null) {
                	tag.setText("");
                }

                tag = person.getChild("LastName");
                val = fio.getSurName();
                if (val != null) {
                    if (tag == null) {
                    	tag = new Element("LastName");
                    	person.addContent(tag);
                    }
                	tag.setText(val);
                } else if (tag != null) {
                	tag.setText("");
                }

                tag = person.getChild("SecondName");
                val = fio.getMiddleName();
                if (val != null) {
                    if (tag == null) {
                    	tag = new Element("SecondName");
                    	person.addContent(tag);
                    }
                	tag.setText(val);
                } else if (tag != null) {
                	tag.setText("");
                }

    //                if (pilt.getItem() != null && pilt.getItem().length > 0) {
//                    PersonInfoType pi = pilt.getItem()[0];

                Element doc = person.getChild("Document");
                if (doc == null) {
                	doc = new Element("Document");
                	person.addContent(doc);
                }
                
                if (iin != null && iin.length() > 0) {
	                Document_ docObj = findMostRelevantDocument(psres);
	                if (docObj != null) {
	                	tag = doc.getChild("DocumentType");
	                	if (tag == null) {
	                		tag = new Element("DocumentType");
	                		doc.addContent(tag);
	                	}
	                	Element tag2 = tag.getChild("CodeFL");
	                	if (tag2 == null) {
	                		tag2 = new Element("CodeFL");
	                		tag.addContent(tag2);
	                	}
	                    val = docObj.getDocNameCode();
	                    if (val != null) tag2.setText(val);
	                    else tag2.setText("");

	                	tag = doc.getChild("Series");
	                	if (tag == null) {
	                		tag = new Element("Series");
	                		doc.addContent(tag);
	                	}
	                    val = docObj.getDocSeries();
	                    if (val != null) tag.setText(val);
	                    else tag.setText("");

	                	tag = doc.getChild("Number");
	                	if (tag == null) {
	                		tag = new Element("Number");
	                		doc.addContent(tag);
	                	}
	                    val = docObj.getDocNumber();
	                    if (val != null) tag.setText(val);
	                    else tag.setText("");

	                	tag = doc.getChild("IssueDate");
	                	if (tag == null) {
	                		tag = new Element("IssueDate");
	                		doc.addContent(tag);
	                	}
	                    val = docObj.getDocIssueDate() != null ? sdf.format(docObj.getDocIssueDate().getTime()) : null;
	                    if (val != null) tag.setText(val);
	                    else tag.setText("");

	                	tag = doc.getChild("AuthorityCode");
	                	if (tag == null) {
	                		tag = new Element("AuthorityCode");
	                		doc.addContent(tag);
	                	}
	                    val = docObj.getDocIssuerNameCode();
	                    if (val != null) tag.setText(val);
	                    else tag.setText("");
	                    
	                    String invCode = docObj.getDocInvalidityCauseCode();
	                    String invName = docObj.getDocInvalidityCauseName();
	                    
	                    if (invCode != null && !noErrors.equals(invCode)) {
	                        response = "1";
	                        info = "Документ не действителен (код " + invCode + ", \"" + invName + "\")";
	                    }
	                } else {
	                	response = "1";
	                	info = "Документы не найдены";
	                }
                } else {
	                String docType = doc.getChild("DocumentType").getChildText("CodeFL");
	                
	                String docSeries = doc.getChildText("Series");
	                String docNumber = doc.getChildText("Number");
	                String docIssueDate = doc.getChildText("IssueDate");
	
	                Document_ docObj = findDocument(docType, docSeries, docNumber, docIssueDate, psres);
	                if (docObj != null) {
	                    tag = doc.getChild("AuthorityCode");
	                    val = docObj.getDocIssuerNameCode();
	                    if (val != null) tag.setText(val);
	                    
	                    String invCode = docObj.getDocInvalidityCauseCode();
	                    String invName = docObj.getDocInvalidityCauseName();
	                    
	                    if (invCode != null && !noErrors.equals(invCode)) {
	                        response = "1";
	                        info = "Документ не действителен (код " + invCode + ", \"" + invName + "\")";
	                    }
	                } else {
	                	response = "1";
	                	info = "Документ не найден";
	                }
                }
            } else {
            	response = "1";
            	info = "Документ не найден";
            }

            Element infoTag = new Element("Info");
            infoTag.setText(info);
            person.addContent(infoTag);
            Element responseTag = new Element("Response");
            responseTag.setText(response);
            person.addContent(responseTag);

        } catch (RemoteException re) {
//            MessageCash.writeLogRecord(ErrorConstants.WSC_100, guid, GBL_IN,
//                    "", endpoint);
            log.error(re.getMessage());
            re.printStackTrace();
            return null;
        } catch (Throwable e) {
//            MessageCash.writeLogRecord(ErrorConstants.WSC_101, guid, GBL_IN,
//                    "", endpoint);
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return person;
    }

    private Object findDocument(String docType, String docSeries, String docNumber, String docIssueDate, PersonInfoType pi) {
        PersonDocumentListType list = pi.getDocumentList();
        if (list != null && list.getItem() != null) {
            for (PersonDocumentType pd : list.getItem()) {
                String docType2 = pd.getTypeCode();
                String docSeries2 = pd.getSeries();
                String docNumber2 = pd.getNumber();
                String docIssueDate2 = sdf.format(pd.getIssueDate().getTime());
                if (docType2.equals(docType) && docSeries2.equals(docSeries) &&
                    docNumber2.equals(docNumber) && docIssueDate2.equals(docIssueDate)) {
                    return pd;
                }
            }
        }
        IrrelevantPersonDocumentListType list2 = pi.getIrrelevantDocumentList();
        if (list2 != null && list2.getItem() != null) {
            for (IrrelevantPersonDocumentType pd : list2.getItem()) {
                String docType2 = pd.getTypeCode();
                String docSeries2 = pd.getSeries();
                String docNumber2 = pd.getNumber();
                String docIssueDate2 = sdf.format(pd.getIssueDate().getTime());
                if (docType2.equals(docType) && docSeries2.equals(docSeries) &&
                    docNumber2.equals(docNumber) && docIssueDate2.equals(docIssueDate)) {
                    return pd;
                }
            }
        }
        return null;
    }

    private Document_ findDocument(String docType, String docSeries, String docNumber, String docIssueDate, FullResponse_ resp) {
        ArrayOf_tns2_nillable_Document_ list = resp.getDocumentList();
        if (list != null && list.getDocument_() != null) {
            for (Document_ pd : list.getDocument_()) {
                String docType2 = pd.getDocNameCode();
                String docSeries2 = (pd.getDocSeries() != null) ? pd.getDocSeries() : "";
                String docNumber2 = pd.getDocNumber();
                String docIssueDate2 = sdf.format(pd.getDocIssueDate().getTime());
                if (docType2.equals(docType) && (docSeries2.equals(docSeries)) &&
                    docNumber2.equals(docNumber) && docIssueDate2.equals(docIssueDate)) {
                    return pd;
                }
            }
        }
        ArrayOf_tns2_nillable_Document_ list2 = resp.getDocumentIrrelevantList();
        if (list2 != null && list2.getDocument_() != null) {
            for (Document_ pd : list2.getDocument_()) {
                String docType2 = pd.getDocNameCode();
                String docSeries2 = (pd.getDocSeries() != null) ? pd.getDocSeries() : "";
                String docNumber2 = pd.getDocNumber();
                String docIssueDate2 = sdf.format(pd.getDocIssueDate().getTime());
                if (docType2.equals(docType) && docSeries2.equals(docSeries) &&
                    docNumber2.equals(docNumber) && docIssueDate2.equals(docIssueDate)) {
                    return pd;
                }
            }
        }
        return null;
    }

    private Document_ findMostRelevantDocument(FullResponse_ resp) {
        ArrayOf_tns2_nillable_Document_ list = resp.getDocumentList();
        Document_ res = null;
        if (list != null && list.getDocument_() != null) {
            for (Document_ pd : list.getDocument_()) {
                String docType2 = pd.getDocNameCode();
                String invCode2 = pd.getDocInvalidityCauseCode();
                
                if (noErrors.equals(invCode2)) {
	                if ("002".equals(docType2)) 
	                	return pd;
	                else if (res != null && !"001".equals(res.getDocNameCode())) {
	                	res = pd;
	                } else {
	                	res = pd;
	                }
                }
            }
        }
        return res;
    }

    private PersonDocumentType findMostRelevantDocument(PersonInfoType pi) {
        PersonDocumentListType list = pi.getDocumentList();
        PersonDocumentType res = null;
        if (list != null && list.getItem() != null) {
            for (PersonDocumentType pd : list.getItem()) {
                String docType2 = pd.getTypeCode();
                
                if ("002".equals(docType2)) 
                	return pd;
                else if (res != null && !"001".equals(res.getTypeCode())) {
                	res = pd;
                } else {
                	res = pd;
                }
            }
        }
        return res;
    }

    public int waitTimeOut(int time, int times) {
        if (times >= this.times) return 1;
        int t = (time > 0) ? time : timeout;
        try {
            Thread.sleep(t);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 0;
    }

    public int waitTimeOut(int time, double times) {
        return waitTimeOut(time, (int)times);
    }
}