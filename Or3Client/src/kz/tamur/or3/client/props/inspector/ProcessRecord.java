package kz.tamur.or3.client.props.inspector;

import java.awt.image.BufferedImage;

import kz.tamur.or3.client.props.Expression;
import kz.tamur.util.Pair;

import com.cifs.or2.kernel.KrnObject;

public final class ProcessRecord {
	
	private KrnObject processObj;
	private Expression actionExpr;
	private String name;
	private Pair<String,Object> shortName;
	private Expression enabledExpr;
	private Expression visibleExpr;
	private BufferedImage image;
	
	public ProcessRecord(KrnObject processObj, String name) {
		super();
		this.processObj = processObj;
		this.name = name;
	}
	
	public ProcessRecord(KrnObject processObj, Expression actionExpr, String name, String shortNameUid, Expression enabledExpr, Expression visibleExpr, BufferedImage image) {
		this(processObj, name);
		this.actionExpr = actionExpr;
		if (shortNameUid != null)
			this.shortName = new Pair<String, Object>(shortNameUid, null);
		this.enabledExpr = enabledExpr;
		this.visibleExpr = visibleExpr;
		this.image = image;
	}

	public ProcessRecord(ProcessRecord pr) {
		this(pr.processObj, pr.name);
		this.actionExpr = pr.actionExpr;
		this.shortName = pr.shortName;
		this.enabledExpr = pr.enabledExpr;
		this.visibleExpr = pr.visibleExpr;
		this.image = pr.image;
	}

	public KrnObject getKrnObject() {
		return processObj;
	}
	
	public String getUid() {
		return processObj != null ? processObj.uid : null;
	}

	public String getName() {
		return name;
	}

	public Expression getActionExpr() {
		return actionExpr;
	}

	public void setActionExpr(Expression actionExpr) {
		this.actionExpr = actionExpr;
	}

	public Pair<String, Object> getShortName() {
		return shortName;
	}
	
	public void setShortName(String name) {
		if (shortName == null) {
			shortName = new Pair<String, Object>(null, name);
		} else {
			shortName = new Pair<String, Object>(shortName.first, name);
		}
	}
	
	public void setShortName(Pair<String, Object> shortName) {
		this.shortName = shortName;
	}

	public Expression getEnabledExpr() {
		return enabledExpr;
	}

	public void setEnabledExpr(Expression enabledExpr) {
		this.enabledExpr = enabledExpr;
	}

	public Expression getVisibleExpr() {
		return visibleExpr;
	}

	public void setVisibleExpr(Expression visibleExpr) {
		this.visibleExpr = visibleExpr;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
