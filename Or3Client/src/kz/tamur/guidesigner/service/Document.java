package kz.tamur.guidesigner.service;

import org.tigris.gef.graph.presentation.JGraph;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.09.2004
 * Time: 18:49:58
 * To change this template use File | Settings | File Templates.
 */
public class Document {
    private JGraph graph;
    private String title;
    private KrnObject obj;
    private boolean readOnly = false;

    public Document(KrnObject obj, String title, ServiceModel model) {
        this.title = title;
        this.obj = obj;
        if (model != null) {
            this.graph = new JGraph(model);
        } else {
        	KrnObject lang = Kernel.instance().getInterfaceLanguage();
            long langId = (lang != null) ? lang.id : 0;
            this.graph = new JGraph(new ServiceModel(true,obj,langId));
        }
    }

    public KrnObject getKrnObject() {
        return obj;
    }

    public String getTitle() {
        return title;
    }

    public JGraph getGraph() {
        return graph;
    }

    public ServiceModel getModel() {
        return (ServiceModel)graph.getGraphModel();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
