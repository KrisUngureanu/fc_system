package kz.tamur.or3.server.plugins.kfm.extdb;

import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

import java.util.Collection;

public abstract class ExtractorPlugin implements SrvPlugin {
	
	private Session session;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Rule createRule() {
		return new Rule();
	}
	
	public OperationType createOperationType(int days) {
		return new OperationType(days);
	}
	
	public abstract Collection<Row> extract(KrnDate date, Collection<Rule> rules);
}
