package kz.tamur.fc.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javassist.tools.rmi.RemoteException;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import kz.tamur.admin.ErrorsNotification;
import kz.tamur.fc.bank.record.CheckFlRequestType;
import kz.tamur.fc.bank.record.CloseRecordRequestType;
import kz.tamur.fc.bank.record.CloseRecordResponseType;
import kz.tamur.fc.bank.record.CreateRecordRequestType;
import kz.tamur.fc.bank.record.CreateRecordResponseType;
import kz.tamur.fc.bank.record.EducationOrgRequestType;
import kz.tamur.fc.bank.record.EducationOrgResponseType;
import kz.tamur.fc.bank.record.FCBANKServiceSoap;
import kz.tamur.fc.bank.record.FilePropertyRequestType;
import kz.tamur.fc.bank.record.FilePropertyResponseType;
import kz.tamur.fc.bank.record.FilePropertyType;
import kz.tamur.fc.bank.record.GetConfirmTransmitRecordType;
import kz.tamur.fc.bank.record.GetPermitTransmitType;
import kz.tamur.fc.bank.record.GetStatusRequestType;
import kz.tamur.fc.bank.record.GetTransmitRecordType;
import kz.tamur.fc.bank.record.GetTransmitRequestType;
import kz.tamur.fc.bank.record.GetTransmitResponseType;
import kz.tamur.fc.bank.record.ObjectFactory;
import kz.tamur.fc.bank.record.ResponsePermitType;
import kz.tamur.fc.bank.record.ResponseType;
import kz.tamur.fc.bank.record.StatusResponseType;
import kz.tamur.fc.bank.record.SummReestrRequestType;
import kz.tamur.fc.bank.record.SummReestrResponseType;
import kz.tamur.fc.bank.record.SummRequestType;
import kz.tamur.fc.bank.record.SummResponseType;
import kz.tamur.fc.bank.record.TransferReestrRequestType;
import kz.tamur.fc.bank.record.TransferReestrResponseType;
import kz.tamur.fc.bank.record.TransmitFromRequestType;
import kz.tamur.fc.bank.record.TransmitRequestType;
import kz.tamur.or3ee.server.kit.SrvUtils;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;

//@SchemaValidation
@WebService/*(
                serviceName="FCBANKServiceSoap",
                wsdlLocation="WEB-INF/wsdl/fc.wsdl",
                endpointInterface="kz.tamur.fc.bank.record.FCBANKServiceSoap",
                targetNamespace="http://record.bank.fc.tamur.kz",
                portName="FCBANKServiceSoap")*/
@HandlerChain(file = "chain.xml")
public class BankService implements FCBANKServiceSoap {

        @Resource
        WebServiceContext context;

        @Override
        public ResponseType checkFl(CheckFlRequestType checkRequest) {
                Session s = null;
                String method="bank_checkFl";
                try {
                    s = getSession();
                    KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	                Context ctx = new Context(new long[0], 0, 0);
	                ctx.langId = 0;
	                ctx.trId = 0;
	                s.setContext(ctx);
	                List<Object> args = new ArrayList<Object>();
	                args.add(checkRequest);
	                args.add(new ObjectFactory());
	                SrvOrLang orlang = s.getSrvOrLang();
	                ResponseType res = (ResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                    s.commitTransaction();
                    return res;
        		} catch (RemoteException e) {
        			throw e;
                } catch (Throwable e) {
                        e.printStackTrace();
                        if(ErrorsNotification.isInitialize()){
                        	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                        }
                        throw new RemoteException(e.getMessage());
                } finally {
                        if (s != null)
                                s.release();
                }
        }

        @Override
        public CreateRecordResponseType createRecord(CreateRecordRequestType checkRequest) {
                Session s = null;
                String method="bank_createRecord";
                try {
                    s = getSession();
                    KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	                Context ctx = new Context(new long[0], 0, 0);
	                ctx.langId = 0;
	                ctx.trId = 0;
	                s.setContext(ctx);
	                List<Object> args = new ArrayList<Object>();
	                args.add(checkRequest);
	                args.add(new ObjectFactory());
	                SrvOrLang orlang = s.getSrvOrLang();
	                CreateRecordResponseType res = (CreateRecordResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                    s.commitTransaction();
                    return res;
                } catch (Throwable e) {
                        e.printStackTrace();
                        if(ErrorsNotification.isInitialize()){
                        	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                        }
                        throw new RemoteException(e.getMessage());
                } finally {
                        if (s != null)
                                s.release();
                }
        }
        
        @Override
        public CloseRecordResponseType closeRecord(CloseRecordRequestType checkRequest) {
                Session s = null;
                String method= "bank_closeRecord";
                try {
                    s = getSession();
                    KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	                Context ctx = new Context(new long[0], 0, 0);
	                ctx.langId = 0;
	                ctx.trId = 0;
	                s.setContext(ctx);
	                List<Object> args = new ArrayList<Object>();
	                args.add(checkRequest);
	                args.add(new ObjectFactory());
	                SrvOrLang orlang = s.getSrvOrLang();
	                CloseRecordResponseType res = (CloseRecordResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                    s.commitTransaction();
                    return res;
                } catch (Throwable e) {
                        e.printStackTrace();
                        if(ErrorsNotification.isInitialize()){
                        	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                        }
                        throw new RemoteException(e.getMessage());
                } finally {
                        if (s != null)
                                s.release();
                }
        }

        @Override
        public ResponseType transmitFromRecord(TransmitFromRequestType transmitRequest) {
            Session s = null;
            String method="bank_transmitFromRecord";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(transmitRequest);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            ResponseType res = (ResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                    e.printStackTrace();
                    if(ErrorsNotification.isInitialize()){
                    	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                    }
                    throw new RemoteException(e.getMessage());
            } finally {
                    if (s != null)
                            s.release();
            }
        }

        @Override
        public ResponseType transmitRecord(TransmitRequestType transmitRequest) {
            Session s = null;
            String method="bank_transmitRecord";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(transmitRequest);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            ResponseType res = (ResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                    e.printStackTrace();
                    if(ErrorsNotification.isInitialize()){
                    	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                    }
                    throw new RemoteException(e.getMessage());
            } finally {
                    if (s != null)
                            s.release();
            }
        }

        @Override
        public SummReestrResponseType summReestr(SummReestrRequestType summReestrRequest) {
            Session s = null;
            String method="bank_summReestr";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(summReestrRequest);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            SummReestrResponseType res = (SummReestrResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                    e.printStackTrace();
                    if(ErrorsNotification.isInitialize()){
                    	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                    }
                    throw new RemoteException(e.getMessage());
            } finally {
                    if (s != null)
                            s.release();
            }
        }

        @Override
        public TransferReestrResponseType transferReestr(TransferReestrRequestType transferReestrRequest) {
            Session s = null;
            String method="bank_transferReestr";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(transferReestrRequest);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            TransferReestrResponseType res = (TransferReestrResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                e.printStackTrace();
                if(ErrorsNotification.isInitialize()){
                	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                }
                throw new RemoteException(e.getMessage());
            } finally {
                if (s != null)
                        s.release();
            }
        }

        @Override
        public ResponseType fileProperty(FilePropertyType filePropertyRequest) {
            Session s = null;
            String method="bank_fileProperty";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(filePropertyRequest);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            ResponseType res = (ResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                e.printStackTrace();
                if(ErrorsNotification.isInitialize()){
                	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                }
                throw new RemoteException(e.getMessage());
            } finally {
                if (s != null)
                        s.release();
            }
        }

        @Override
        public EducationOrgResponseType educationOrg(EducationOrgRequestType educationOrgRequest) {
            Session s = null;
            String method="bank_educationOrg";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(educationOrgRequest);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            EducationOrgResponseType res = (EducationOrgResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                    e.printStackTrace();
                    if(ErrorsNotification.isInitialize()){
                    	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                    }
                    throw new RemoteException(e.getMessage());
            } finally {
                    if (s != null)
                            s.release();
            }
        }

        @Override
        public StatusResponseType getStatus(GetStatusRequestType request) {
            Session s = null;
            String method="bank_getStatus";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(request);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            StatusResponseType res = (StatusResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                    e.printStackTrace();
                    if(ErrorsNotification.isInitialize()){
                    	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                    }
                    throw new RemoteException(e.getMessage());
            } finally {
                    if (s != null)
                            s.release();
            }
        }

        @Override
        public SummResponseType summBonus(SummRequestType summRequest) {
            Session s = null;
            String method = "bank_summRequest";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(summRequest);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            SummResponseType res = (SummResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                e.printStackTrace();
                if(ErrorsNotification.isInitialize()){
                	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                }
                throw new RemoteException(e.getMessage());
            } finally {
                if (s != null)
                        s.release();
            }
        }
        
        private Session getSession() throws Exception {
	        ServletContext servletContext =
	            (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
	        String dsName = servletContext.getInitParameter("dataSourceName");
	        String user = servletContext.getInitParameter("user");
	        String password = servletContext.getInitParameter("password");
	        return SrvUtils.getSession(dsName, user, password);
	    }

        @Override
        public ResponsePermitType getPermitTransmit(GetPermitTransmitType request) {
            Session s = null;
            String method ="bank_getPermitTransmit";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(request);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            ResponsePermitType res = (ResponsePermitType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                e.printStackTrace();
                if(ErrorsNotification.isInitialize()){
                	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                }
                throw new RemoteException(e.getMessage());
            } finally {
                if (s != null)
                        s.release();
            }
        }

        @Override
		public GetTransmitResponseType getTransmit(GetTransmitRequestType request) {
			Session s = null;
			String method="bank_getTransmit";
			try {
				s = getSession();
				KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
				Context ctx = new Context(new long[0], 0, 0);
				ctx.langId = 0;
				ctx.trId = 0;
				s.setContext(ctx);
				List<Object> args = new ArrayList<Object>();
				args.add(request);
				args.add(new ObjectFactory());
				SrvOrLang orlang = s.getSrvOrLang();
				GetTransmitResponseType res = (GetTransmitResponseType) orlang.exec(wsCls, wsCls, method, args, new Stack<String>());
				s.commitTransaction();
				return res;
			} catch (Throwable e) {
				e.printStackTrace();
                if(ErrorsNotification.isInitialize()){
                	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                }
				throw new RemoteException(e.getMessage());
			} finally {
				if (s != null)
					s.release();
			}
		}
        
        @Override
        public ResponseType getTransmitRecord(GetTransmitRecordType request) {
            Session s = null;
            String method="bank_getTransmitRecord";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(request);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            ResponseType res = (ResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                e.printStackTrace();
                if(ErrorsNotification.isInitialize()){
                	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                }
                throw new RemoteException(e.getMessage());
            } finally {
                if (s != null)
                        s.release();
            }
        }
        @Override
        public ResponseType getConfirmTransmitRecord(GetConfirmTransmitRecordType request) {
            Session s = null;
            String method ="bank_getConfirmTransmitRecord";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
		        Context ctx = new Context(new long[0], 0, 0);
		        ctx.langId = 0;
		        ctx.trId = 0;
		        s.setContext(ctx);
		        List<Object> args = new ArrayList<Object>();
		        args.add(request);
		        args.add(new ObjectFactory());
		        SrvOrLang orlang = s.getSrvOrLang();
		        ResponseType res = (ResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                e.printStackTrace();
                if(ErrorsNotification.isInitialize()){
                	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                }
                throw new RemoteException(e.getMessage());
            } finally {
                if (s != null)
                        s.release();
            }
        }
        @Override
        public FilePropertyResponseType getFileProperty(FilePropertyRequestType filePropertyRequest) {
            Session s = null;
            String method = "bank_getFileProperty";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	            Context ctx = new Context(new long[0], 0, 0);
	            ctx.langId = 0;
	            ctx.trId = 0;
	            s.setContext(ctx);
	            List<Object> args = new ArrayList<Object>();
	            args.add(filePropertyRequest);
	            args.add(new ObjectFactory());
	            SrvOrLang orlang = s.getSrvOrLang();
	            FilePropertyResponseType res = (FilePropertyResponseType)orlang.exec(wsCls,wsCls, method, args, new Stack<String>());
                s.commitTransaction();
                return res;
            } catch (Throwable e) {
                e.printStackTrace();
                if(ErrorsNotification.isInitialize()){
                	ErrorsNotification.notifyErrors("TO_104", "SERVICE_"+method, "Внутренняя ошибка.", e, s);
                }
                throw new RemoteException(e.getMessage());
            } finally {
                if (s != null)
                        s.release();
            }
        }
}
