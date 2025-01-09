package kz.tamur.guidesigner.procdesigner;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;
import java.util.*;
import java.io.UnsupportedEncodingException;

import kz.tamur.util.AbstractDesignerTreeNode;


/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 * To change this template use File | Settings | File Templates.
 */
public class ProcNode extends AbstractDesignerTreeNode {

    private List<String> childs;
    private String type;
    private boolean status;
    private boolean isModify = false;
    private String expressionText = "";

    public ProcNode(String type,String tl, List<String> childs, int index) {
        isLoaded = false;
        this.type = type;
        String[] tls=tl.split(";");
        this.title = tls[0];
        if(tls.length>1)
            this.status = "VALID".equals(tls[1]);
        this.childs=childs;
        if(childs==null){
	        Kernel krn = Kernel.instance();
	        try {
	            byte[] data = krn.getProcedureContent(title,type);
	                expressionText = new String(data, "UTF-8");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
    }

    public boolean isLeaf() {
        return childs==null;
    }



    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            if (!isLeaf()) {
                List children = new ArrayList();
                int i=0;
                for (String child:childs) {
                    children.add(new ProcNode(type,child, null, i++));
                }
                addAllChildren(children);
            }
        }
    }

    public boolean isModify() {
        return isModify;
    }

    public void setModify(boolean modify) {
        isModify = modify;
    }

    public String getExpressionText() {
        return expressionText;
    }

    public void setExpressionText(String expressionText) {
        this.expressionText = expressionText;
    }

    public boolean isValid() {
        return status;
    }
    public void setValid(boolean status) {
        this.status=status;
    }
    public String save() {
        Kernel krn = Kernel.instance();
        List<String> params=new ArrayList<String>();
        String res="";
        try {
        	String expressionText_= expressionText.indexOf("CREATE") <0?"CREATE OR REPLACE "+expressionText:expressionText;
            res=krn.createProcedure(title, params,expressionText_);
        } catch (Exception e) {
            e.printStackTrace();
            res=e.getMessage();
        }
        return res;
    }

    public void reloadExpression() {
        Kernel krn = Kernel.instance();
        try {
            byte[] data = krn.getProcedureContent(title,type);
            try {
                expressionText = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }


}
