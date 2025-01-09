package kz.tamur.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.server.Session;

import kz.tamur.or3ee.common.ModelChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;

public class ModelChangeAdapter implements ModelChangeListener {
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ModelChangeListener.class.getName());
	
	private int configNumber;
	private String dsName;
	
	public ModelChangeAdapter(int configNumber, String dsName) {
		this.configNumber = configNumber;
		this.dsName = dsName;
	}

	@Override
	public void classCreated(KrnClass cls) {
        try {
	        Session s = SrvUtils.getSession(dsName, "sys", null);
	        try {
	            ClassNode newChild = LocalKernel.getClassNodeById(configNumber, cls.id, s);
	        } catch (Exception e) {
            	log.error(e, e);
            } finally {
                if (s != null) {
                    s.release();
                }
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
	}

	@Override
	public void attrCreated(KrnAttribute attr) {
        try {
	        Session s = SrvUtils.getSession(dsName, "sys", null);
	        try {
	            ClassNode cnode = LocalKernel.getClassNodeById(configNumber, attr.classId, s);
	            cnode.addAttribute(attr);
	        } catch (Exception e) {
            	log.error(e, e);
            } finally {
                if (s != null) {
                    s.release();
                }
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
	}

	@Override
	public void classDeleted(KrnClass cls) {
        try {
            LocalKernel.removeClassNode(configNumber, cls);
        } catch (Exception e) {
        	log.error(e, e);
        }
	}

	@Override
	public void attrDeleted(KrnAttribute attr) {
        try {
	        Session s = SrvUtils.getSession(dsName, "sys", null);
	        try {
	            ClassNode cnode = LocalKernel.getClassNodeById(configNumber, attr.classId, s);
	            cnode.removeAttribute(attr);
	        } catch (Exception e) {
            	log.error(e, e);
            } finally {
                if (s != null) {
                    s.release();
                }
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
	}

	@Override
	public void classChanged(KrnClass clsOld, KrnClass clsNew) {
        try {
	        Session s = SrvUtils.getSession(dsName, "sys", null);
	        try {
	            LocalKernel.removeClassNode(configNumber, clsOld);
	            ClassNode newChild = LocalKernel.getClassNodeById(configNumber, clsNew.id, s);
	        } catch (Exception e) {
            	log.error(e, e);
            } finally {
                if (s != null) {
                    s.release();
                }
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
	}

	@Override
	public void attrChanged(KrnAttribute attrOld, KrnAttribute attrNew) {
        try {
	        Session s = SrvUtils.getSession(dsName, "sys", null);
	        try {
	            ClassNode cnode = LocalKernel.getClassNodeById(configNumber, attrNew.classId, s);
	            cnode.removeAttribute(attrOld);
	            cnode.addAttribute(attrNew);
	        } catch (Exception e) {
            	log.error(e, e);
            } finally {
                if (s != null) {
                    s.release();
                }
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
	}

	@Override
	public void methodCreated(KrnMethod m) {
        try {
	        Session s = SrvUtils.getSession(dsName, "sys", null);
	        try {
	            ClassNode cnode = LocalKernel.getClassNodeById(configNumber, m.classId, s);
	            cnode.addMethod(m);
	        } catch (Exception e) {
            	log.error(e, e);
            } finally {
                if (s != null) {
                    s.release();
                }
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
	}

	@Override
	public void methodDeleted(KrnMethod m) {
        try {
	        Session s = SrvUtils.getSession(dsName, "sys", null);
	        try {
	        	LocalKernel.removeMethodFromCache(configNumber, m.uid);
	            ClassNode cnode = LocalKernel.getClassNodeById(configNumber, m.classId, s);
	            cnode.removeMethod(m);
	        } catch (Exception e) {
            	log.error(e, e);
            } finally {
                if (s != null) {
                    s.release();
                }
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
	}

	@Override
	public void methodChanged(KrnMethod oldm, KrnMethod newm) {
        try {
	        Session s = SrvUtils.getSession(dsName, "sys", null);
	        try {
	        	LocalKernel.removeMethodFromCache(configNumber, oldm.uid);
	            ClassNode cnode = LocalKernel.getClassNodeById(configNumber, oldm.classId, s);
	            cnode.removeMethod(oldm);
	            cnode.addMethod(newm);
	        } catch (Exception e) {
            	log.error(e, e);
            } finally {
                if (s != null) {
                    s.release();
                }
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
	}
}
