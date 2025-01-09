package kz.tamur.ekyzmet.test;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;
import com.eclipsesource.json.JsonObject;

public final class UserAnswer implements Serializable {

	private static final long serialVersionUID = -2735897178564786420L;

	public final KrnObject obj;
	public final SubSection sec;
	public final Question qsn;
	public final int ordNum;
	public Answer awr;
	public Date time;
	private boolean lastInSection;

	public UserAnswer(SubSection sec, Question qsn, int ordNum, Answer awr, Date time, KrnObject obj) {
		super();
		this.sec = sec;
		this.qsn = qsn;
		this.ordNum = ordNum;
		this.awr = awr;
		this.time = time;
		this.obj = obj;
		this.lastInSection = false;
	}

	public boolean isLastInSection() {
		return lastInSection;
	}
	
	public void setLastInSection() {
		this.lastInSection = true;
	}

	public JsonObject answer(Answer awr, Date time, HttpSession hs) throws Exception {
		Session ors = null;
		try {
			ors = TestServlet.getOr3Session(hs);

			this.awr = awr;
			this.time = time;

			KrnClass uawrCls = ors.getClassByName("ек::тест::Зап таб ответа");
			KrnAttribute awrAttr = ors.getAttributeByName(uawrCls, "ответ");
			ors.setValue(obj, awrAttr.id, 0, 0, 0, awr != null ? awr.obj : null, false);
			ors.setValue(obj, ors.getAttributeByName(uawrCls, "время ответа").id, 0, 0, 0, new java.sql.Timestamp(time.getTime()), false);

			KrnClass stepCls = ors.getClassByName("ек::тест::История заявки");
			KrnObject stepObj = (KrnObject) hs.getAttribute("step");
			KrnAttribute awrCntAttr = ors.getAttributeByName(stepCls,
					"кол-во ответов");
			ors.setValue(stepObj, awrCntAttr.id, 0, 0, 0, countAnswers(hs),
					false);

			ors.commitTransaction();

			return TestServlet.getSuccessJSON();
		} finally {
			if (ors != null)
				ors.release();
		}
	}

	private int countAnswers(HttpSession hs) {
		Map<Long, UserAnswer> uawrs = (Map<Long, UserAnswer>) hs.getAttribute("userAnswers");
		int count = 0;
		for (UserAnswer uawr : uawrs.values())
			if (uawr.awr != null)
				count++;
		return count;
	}
}
