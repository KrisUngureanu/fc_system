package kz.tamur.ekyzmet.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.bcel.generic.INSTANCEOF;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import kz.tamur.ods.Driver;
import kz.tamur.ods.Value;
import kz.tamur.or3.server.plugins.ekyzmet.BlackBoxPlugin;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.AttrRequestBuilder;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

public class TestServlet extends HttpServlet {
	
	private static final Log log = LogFactory.getLog(TestServlet.class);

	private static final long serialVersionUID = -6862845233887321813L;
	
	private static final String[] LETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

	public static final String HS_IIN = "iin";
	public static final String HS_FIO = "fio";
	private static final String HS_SECTION = "section";
	private static final String HS_SECTION_START = "section.start";
	private static final String HS_STEP_START = "step_start";
	private static final String HS_NAV_MODE = "nav_mode";
	private static final String HS_NAV_CURR = "nav_curr";
	private static final String HS_NAV_BWD_DEPTH = "nav_bwd_depth";
	public static final String HS_TEST_DIRTYPE = "test_dirtype";
	public static final String HS_TEST_CORPUS = "test_corpus";
	public static final String HS_TEST_ERK = "test_erk";
	
	public static final String HS_RESULT = "res";
	public static final String HS_RESULT_GMA = "res_gma";
	public static final String HS_RESULT_RECORDS = "res_records";
	
	public static final String HS_QVR_UAWR = "qvr_uwar";
	public static final String HS_QVR_START_TIME = "qvr_start_time";

	private static String DS_NAME;
	private static String USER;
	private static String PD;
	
	private static String BB_DS_NAME;
	private static String BB_SCHEMA;
	
	private static KrnObject LANG_RU;
	private static KrnObject LANG_KZ;
	
	private static KrnClass CLS_ZAP_QVIEW;
	private static KrnAttribute ATTR_ZQ_UAWR;
	private static KrnAttribute ATTR_ZQ_START_TIME;
	private static KrnAttribute ATTR_ZQ_END_TIME;
	
	public static KrnObject OBJ_RES_PASSED;
	public static KrnObject OBJ_RES_FAILED;

	public static KrnObject OBJ_DIRTYPE_GMA;
	public static KrnObject OBJ_DIRTYPE_COMP;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		TestServlet.DS_NAME = config.getInitParameter("dsName");
		TestServlet.USER = config.getInitParameter("user");
		TestServlet.PD = config.getInitParameter("password");

		TestServlet.BB_DS_NAME = config.getInitParameter("bbDsName");
		TestServlet.BB_SCHEMA = config.getInitParameter("bbSchema");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Object res = new JsonObject();
		Session ors = null;
		String contentType = "application/json; charset=UTF-8";
		try {
			HttpSession hs = req.getSession();
			ors = getOr3Session(hs);
			
			String login = Funcs.getValidatedParameter(req, "login");
			if (login != null && login.matches(".+")) {
				StringBuilder err = new StringBuilder();
				int act = login(req, ors, err);
				if (act == 1)
					res = getSuccessJSON();
				else if (act == 2) {
					JsonObject json = new JsonObject();
					json.add("result", "continue");
					res = json;
				} else if (act == 10) {
					res = getErrorJSON("Доступ запрещен с IP: " + getIpAddress(req));
				} else if (act == 11) {
					res = getErrorJSON("Участник с ИИН " +  login + " уже проходит тестирование");
				} else if (act == 12) {
					res = getErrorJSON(err.toString());
				} else if (act == 13) {
					res = getErrorJSON(err.toString());
				} else {
					res = getErrorJSON("Нет заявки на прохождение конкурса для ИИН: " + login);
				}

			} else if (req.getParameter("tests") != null) {
				Program prg = getProgram(req.getSession());
				if (prg != null) {
					res = getProgramJSON(prg, ors, req);
				}
			} else if (req.getParameter("dirs") != null) {
				res = getKnowledgeSpaceJSON(ors, req.getSession());
			
			} else if (req.getParameter("blocks") != null) {
				Program prg = getProgram(req.getSession());
				SubSection subSec = prg.subSections.get(Long.valueOf(req.getParameter("blocks")));
				res = getSectionJSON(subSec, ors, req);
			
			} else if (req.getParameter("questions") != null) {
				if (isInterrupted(req, ors)) {
					res = getErrorJSON("Тестирование приостановлено или аннулировано.");
				} else {
					Program prg = getProgram(req.getSession());
					Long blkId = Long.valueOf(req.getParameter("questions"));
					Block blk = prg.blocks.get(blkId);
					if (blk != null) {
						computeBackwardDepth(blk, (long)hs.getAttribute(HS_NAV_CURR), hs);
						hs.setAttribute(HS_NAV_CURR, blkId);
						res = getQuestionJSON(blk, ors, req);
					}
				}
			} else if (req.getParameter("answer") != null) {
				if (isInterrupted(req, ors)) {
					res = getErrorJSON("Тестирование приостановлено или аннулировано.");
				} else {
					String awrStr = req.getParameter("answer");
					String qsnStr = req.getParameter("question");
					if (qsnStr != null) {
						Program prg = getProgram(req.getSession());
						Map<Long, UserAnswer> uawrs = loadUserAnswers(ors, req.getSession());
						UserAnswer uawr = uawrs.get(Long.valueOf(qsnStr));
						long awrId = Long.valueOf(awrStr);
						Answer awr = awrId != 0 ? prg.answers.get(awrId) : null;
						
						JsonObject json = uawr.answer(awr, new Date(), req.getSession());
						json.add("unanswered", getUnansweredQuestions(uawr.qsn.blk.subSec, ors, req.getSession()).size());
						res = json;
					}
				}			
			} else if (req.getParameter("exit") != null) {
				res = exit(req.getSession());
				
			} else if (req.getParameter("finish") != null) {
				if (isInterrupted(req, ors)) {
					res = getErrorJSON("Тестирование приостановлено или аннулировано.");
				} else {
					Object iin = hs.getAttribute(HS_IIN);
					Object src = req.getParameter("src");
					log.error(iin + " finish (src:" + req.getParameter("src") + ",sid:" + req.getParameter("sid") + ",info:" + req.getParameter("info") + ")");
					if ("user".equals(src)) {
						Object currBlkId = hs.getAttribute(HS_NAV_CURR);
						if (currBlkId instanceof Long) {
							Block blk = getProgram(hs).blocks.get(currBlkId);
							Map<Long, UserAnswer> uawrs = loadUserAnswers(ors, req.getSession());
							UserAnswer uawr = uawrs.get(blk.questions.get(0).obj.id);
							if (uawr.isLastInSection()) {
								res = finishTest(ors, req.getSession());
							} else {
								log.error(iin + " Unexpected finish (currBlkId:" + currBlkId + ", lastInSection:false)");
							}
						}
					} else if ("timer".equals(src)) {
						Section currKsp = getKnowledgeSpace(hs);
						if (currKsp.obj.id == Long.parseLong(req.getParameter("sid"))) {
							res = finishTest(ors, req.getSession());
						} else {
							log.error(iin + " Unexpected finish (currKspId:" + currKsp.obj.id + ")");
						}
					} else {
						log.error(iin + " Unexpected finish");
					}
				}
				
			} else if (req.getParameter("media") != null) {
				long[] objIds =  { Long.parseLong(req.getParameter("media"))};
				int mediaType =  Integer.parseInt(req.getParameter("type"));
				KrnAttribute fileAttr = ors.getAttributeByName(ors.getClassByName("MSDoc"), "file");
				SortedSet<Value> vs = ors.getValues(objIds, fileAttr.id, LANG_RU.id,0);
				if (vs.size() > 0) {
					byte[] data = (byte[])vs.first().value;
					if (mediaType == Block.MT_AUDIO)
						resp.setContentType("audio/mpeg");
					else if (mediaType == Block.MT_VIDEO)
						resp.setContentType("video/x-flv");
					resp.setHeader("Content-length", "" + data.length);
					OutputStream os = resp.getOutputStream();
					os.write(data);
					os.flush();
				}
				return;
			} else if (req.getParameter("cert") != null) {
				if ((Boolean)hs.getAttribute(HS_TEST_ERK)) {
					res = createCertificateErk(req.getSession(), ors);
					if (res instanceof byte[]) {
						contentType = "application/pdf";
						resp.setHeader("Content-Transfer-Encoding", "Binary"); 
						resp.setHeader("Content-disposition", "attachment; filename=\"certificate.pdf\"");
					}
				} else {
					res = createCertificate(req.getSession(), ors);
					contentType = "text/html; charset=UTF-8";
				}
			}
		} catch (Throwable ex) {
			log.error("Ошибка при обработке запроса POST", ex);
			res = getErrorJSON(ex.getMessage());
		} finally {
			if (ors != null)
				ors.release();
		}
		
		resp.setContentType(contentType);
    	resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "must-revalidate");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Cache-Control", "no-store");
        resp.setDateHeader("Expires", 0);
        
        if (res instanceof byte[]) {
        	resp.getOutputStream().write((byte[])res);
        } else if (res != null) {
	        String text = res.toString();
	        //if (Funcs.isValid(text))
	        	resp.getOutputStream().write(text.getBytes());
		}
	}
	
	private boolean isInterrupted(HttpServletRequest req, Session ors) throws KrnException {
		KrnClass stepCls = ors.getClassByName("ек::тест::История заявки");
		KrnObject stepObj = (KrnObject)req.getSession().getAttribute("step");
		SortedSet<Value> vs = ors.getValues(new long[] { stepObj.id }, ors.getAttributeByName(stepCls, "результат").id, 0, 0);
		KrnObject resObj = vs.size() > 0 ? (KrnObject)vs.first().value : null;
		if (resObj != null && ("1014162.3511596".equals(resObj.uid) || "1014162.3412575".equals(resObj.uid)))
			return true;
		return false;
	}
	
	private JsonObject getErrorJSON(String msg) {
		JsonObject res = new JsonObject();
		res.add("result", "error");
		res.add("msg", msg);
		return res;
	}
	
	public static JsonObject getSuccessJSON() {
		JsonObject res = new JsonObject();
		res.add("result", "ok");
		return res;
	}

	private JsonArray getProgramJSON(Program prg, Session ors, HttpServletRequest req) throws Exception {
		HttpSession hs = req.getSession();
		String langCode = Funcs.getValidatedParameter(req, "lang");
		KrnObject langObj = getLangObj(langCode, ors);
		hs.setAttribute("lang", langObj);
		if (langCode.matches(".+") && langCode.length() == 2)
			hs.setAttribute("langCode", langCode);
		
		JsonArray res = new JsonArray();
		for (Section ksp : prg.sections) {
			if (ksp.joinQuestions) {
				continue;
			}
			int kspQsnCnt = 0;
			for (SubSection sec : ksp.sections) {
				JsonObject kspJSON = new JsonObject();
				kspJSON.add("title", "ru".equals(langCode) ? sec.nameRu : sec.nameKz);
				kspJSON.add("totalq", sec.qsnCount);
				kspJSON.add("level", sec.level > 0 ? sec.level : "-");
				res.add(kspJSON);
				kspQsnCnt += sec.qsnCount;
			}
			if (ksp.level > 0) {
				JsonObject totalJSON = new JsonObject();
				totalJSON.add("title", "ru".equals(langCode) ? "ИТОГО" : "ЖИЫНЫ");
				totalJSON.add("totalq", kspQsnCnt);
				totalJSON.add("level", ksp.level);
				res.add(totalJSON);
			}
		}
		return res;
	}
	
	private JsonObject getKnowledgeSpaceJSON(Session ors, HttpSession hs) throws Exception {
		Program prg = getProgram(hs);
		Section currKsp = getKnowledgeSpace(hs);
		if (prg != null && currKsp != null) {
			Object langCode = hs.getAttribute("langCode");
			JsonObject kspJSON = new JsonObject();
			//kspJSON.add("title", ksp.nameRu);
			Date startTime = startTest(ors, hs);
			kspJSON.add("time", currKsp.time * 60 - (new Date().getTime() - startTime.getTime()) / 1000);
			kspJSON.add("total_time", prg.getTimeRemaining(currKsp) * 60 - (new Date().getTime() - startTime.getTime()) / 1000);
			kspJSON.add("navMode",  (Boolean)hs.getAttribute(HS_TEST_ERK) ? 2 : currKsp.navNext ? 1 : 0);
			kspJSON.add("navCurr", hs.getAttribute(HS_NAV_CURR));
			kspJSON.add("navBwdDepth", hs.getAttribute(HS_NAV_BWD_DEPTH));
			JsonArray secJSONs = new JsonArray();
			int status = 2;
			for (Section ksp : prg.sections) {
				if (ksp.obj.id == currKsp.obj.id)
					status = 1;
				else if (status == 1)
					status = 0;
				if (ksp.joinQuestions) {
					SubSection sec = ksp.joinedSubSection;
					JsonObject secJSON = new JsonObject();
					secJSON.add("sid", sec.obj.id);
					secJSON.add("title", "ru".equals(langCode) ? sec.nameRu : sec.nameKz);
					secJSON.add("pid", sec.obj.id);
					secJSON.add("ptitle", "ru".equals(langCode) ? ksp.nameRu : ksp.nameKz);
					secJSON.add("status", status);
					secJSON.add("unanswered", getUnansweredQuestions(sec, ors, hs).size());
					secJSONs.add(secJSON);
				} else {
					for (int i = 0; i < ksp.sections.size(); i++) {
						SubSection sec = ksp.sections.get(i);
						JsonObject secJSON = new JsonObject();
						secJSON.add("sid", sec.obj.id);
						secJSON.add("title", "ru".equals(langCode) ? sec.nameRu : sec.nameKz);
						secJSON.add("pid", ksp.obj.id);
						secJSON.add("ptitle", "ru".equals(langCode) ? ksp.nameRu : ksp.nameKz);
						secJSON.add("status", status);
						secJSON.add("unanswered", getUnansweredQuestions(sec, ors, hs).size());
						secJSONs.add(secJSON);
					}
				}
			}
			kspJSON.add("section", secJSONs);
			return kspJSON;
		}
		return null;
	}
	
	private JsonArray getQuestionJSON(Block blk, Session ors, HttpServletRequest req) throws Exception {
		final HttpSession hs = req.getSession();
		JsonArray qsnJSONs = new JsonArray();
		for (Question qsn : blk.questions) {
			JsonObject qsnJSON = new JsonObject();
			qsnJSON.add("qid", qsn.obj.id);
			JsonArray qs = new JsonArray();
			JsonObject qtxt = new JsonObject();
			qtxt.add("text", qsn.text);
			qs.add(qtxt);
			qsnJSON.add("q", qs);
			
			UserAnswer uawr = loadUserAnswers(ors, req.getSession()).get(qsn.obj.id);
			if (uawr.awr != null)
				qsnJSON.add("selected", uawr.awr.obj.id);
			
			JsonArray awrJSONs = new JsonArray();
			for (int i = 0; i < qsn.answers.size(); i++) {
				Answer awr = qsn.answers.get(i);
				JsonObject awrJSON = new JsonObject();
				awrJSON.add("oid", awr.obj.id);
				awrJSON.add("letter", LETTERS[i]);
				JsonObject cnt = new JsonObject();
				cnt.add("text", awr.nameRu);
				if (awr.mediaType != Block.MT_NONE) {
					String mediaUrl = StringEscapeUtils.escapeHtml(req.getRequestURI()) + "?media=" + awr.mediaObj.id + "&type=" + awr.mediaType;
					mediaUrl = Funcs.sanitizeSQL(mediaUrl);
					if (awr.mediaType == Block.MT_AUDIO)
						cnt.add("audio", mediaUrl + "&file.mp3");
					else if (blk.mediaType == Block.MT_VIDEO)
						cnt.add("video", mediaUrl);
					else if (blk.mediaType == Block.MT_IMAGE)
						cnt.add("image", mediaUrl);
				}
				awrJSON.add("content", cnt);
				awrJSONs.add(awrJSON);
			}
			qsnJSON.add("opts", awrJSONs);
			qsnJSON.add("navBwdDepth", hs.getAttribute(HS_NAV_BWD_DEPTH));
			qsnJSONs.add(qsnJSON);
			
			createViewRecord(ors, hs, uawr);
		}
		return qsnJSONs;
	}

	private JsonArray getSectionJSON(SubSection sec, Session ors, HttpServletRequest req) throws Exception {
		HttpSession hs = req.getSession();
		JsonArray blkJSONs = new JsonArray();
		int i = 1;
		int blockNum = 0;
		for (Block blk : sec.blocks) {
			JsonObject blkJSON = new JsonObject();
			blkJSON.add("bid", blk.obj.id);
			blkJSON.add("num", blockNum++);
			String text = "" + i;
			if (blk.questions.size() > 1) {
				i += blk.questions.size() - 1;
				text += "-" + i;
			}
			blkJSON.add("text", text);
			i++;
			JsonArray blkCntJSONs = new JsonArray();
			JsonObject blkCntJSON = new JsonObject();
			if (blk.text != null && blk.text.length() > 0)
				blkCntJSON.add("text", blk.text);
			if (blk.mediaType != Block.MT_NONE) {
				String mediaUrl = StringEscapeUtils.escapeHtml(req.getRequestURI()) + "?media=" + blk.mediaObj.id + "&type=" + blk.mediaType;
				mediaUrl = Funcs.sanitizeSQL(mediaUrl);
				if (blk.mediaType == Block.MT_AUDIO)
					blkCntJSON.add("audio", mediaUrl + "&file.mp3");
				else if (blk.mediaType == Block.MT_VIDEO)
					blkCntJSON.add("video", mediaUrl);
				else if (blk.mediaType == Block.MT_IMAGE)
					blkCntJSON.add("image", mediaUrl);
			}
			blkCntJSONs.add(blkCntJSON);
			blkJSON.add("content", blkCntJSONs);

			int awrCount = 0;
			for (Question qsn : blk.questions) {
				UserAnswer uawr = loadUserAnswers(ors, hs).get(qsn.obj.id);
				if (uawr != null && uawr.awr != null)
					awrCount++;
			}
			blkJSON.add("ready", awrCount == 0 ? 0 : awrCount == blk.questions.size() ? 2 : 1);
			blkJSONs.add(blkJSON);
		}
		return blkJSONs;
	}

	private Program loadTestProgram(KrnObject prgObj, KrnObject secObj, Session ors, HttpSession hs) throws KrnException {
		
		Program prg = (Program) hs.getAttribute("program");
		if (prg != null)
			return prg;

		KrnClass prgCls = ors.getClassByName("ек::тест::Программа");
		KrnAttribute secAttr = ors.getAttributeByName(prgCls, "разделы");
		KrnClass secCls = ors.getClassByName("ек::тест::Раздел программы");
		
		AttrRequestBuilder prgRb = new AttrRequestBuilder(prgCls, ors)
			.add("идентификатор")
			.add("тип направления")
			.add("корпус");
		AttrRequestBuilder secRb = new AttrRequestBuilder(secCls, ors)
			.add("название", LANG_RU.id)
			.add("название", LANG_KZ.id)
			.add("время")
			.add("пороговое значение")
			.add("объединить вопросы подраздела?")
			.add("строгий переход по вопросам?");
		
		QueryResult qr = ors.getObjects(new long[] {prgObj.id}, prgRb.build(), 0);
		if (qr.totalRows > 0) {
			Object[] row = qr.rows.get(0);
			String prgNameRu = prgRb.getStringValue("идентификатор", row);
			KrnObject dirType = prgRb.getObjectValue("тип направления", row);
			if (dirType != null && dirType.classId > 0)
				hs.setAttribute(HS_TEST_DIRTYPE, new KrnObject(dirType.id, dirType.uid, dirType.classId));
			else
				hs.setAttribute(HS_TEST_DIRTYPE, null);
			
			KrnObject corps = prgRb.getObjectValue("корпус", row);
			if (corps != null && corps.classId > 0)
				hs.setAttribute(HS_TEST_CORPUS, new KrnObject(corps.id, corps.uid, corps.classId));
			else
				hs.setAttribute(HS_TEST_CORPUS, null);
			
			if (prgNameRu.matches(".+") && prgNameRu.length() > 0 && prgObj != null && prgObj.classId > 0) {
				prg = new Program(new KrnObject(prgObj.id, prgObj.uid, prgObj.classId), prgNameRu);
				hs.setAttribute("program", prg);
			
				QueryResult kspQr = secObj == null
						? ors.getObjectValues(new long[] {prgObj.id}, secAttr.id, new long[0], 0, secRb.build())
						: ors.getObjects(new long[] {secObj.id}, secRb.build(), 0);
				for (Object[] secRow : kspQr.rows) {
					Section ksp = new Section(
							secRb.getObject(secRow),
							secRb.getStringValue("название", LANG_RU.id, secRow),
							secRb.getStringValue("название", LANG_KZ.id, secRow),
							secRb.getIntValue("время", secRow),
							secRb.getIntValue("пороговое значение", secRow),
							secRb.getBooleanValue("объединить вопросы подраздела?", secRow),
							secRb.getBooleanValue("строгий переход по вопросам?", secRow)
							);
					prg.sections.add(ksp);
					loadSections(ksp, ors, hs);
				}
			}
			
			return prg;
		}
		return null;
	}

	private void loadSections(Section ksp, Session ors, HttpSession hs) throws KrnException {
		
		Program prg = getProgram(hs);
		
		KrnClass secCls = ors.getClassByName("ек::тест::Раздел программы");
		KrnAttribute subSecAttr = ors.getAttributeByName(secCls, "подразделы");
		KrnClass subSecCls = ors.getClassByName("ек::тест::Подраздел программы");
		
		AttrRequestBuilder subSecRb = new AttrRequestBuilder(subSecCls, ors)
			.add("количество заданий")
			.add("пороговое значение")
			.add("область знаний")
			.add("название", LANG_RU.id)
			.add("название", LANG_KZ.id)
			.add("не_перемешивать_вопросы?");
		
		QueryResult qr = ors.getObjectValues(new long[] {ksp.obj.id}, subSecAttr.id, new long[0], 0, subSecRb.build());
		int kspQsnCount = 0;
		for (Object[] row : qr.rows) {
			KrnObject secObj = subSecRb.getObject(row);
			String nameRu = subSecRb.getStringValue("название", LANG_RU.id, row);
			String nameKz = subSecRb.getStringValue("название", LANG_KZ.id, row);
			KrnObject kspObj = subSecRb.getObjectValue("область знаний", row);
			int qsnCount = subSecRb.getIntValue("количество заданий", row);
			int level = subSecRb.getIntValue("пороговое значение", row);
			boolean dontShuffle = subSecRb.getBooleanValue("не_перемешивать_вопросы?", row, false);
			SubSection sec = new SubSection(ksp, secObj, nameRu, nameKz, kspObj, qsnCount, level, !dontShuffle);
			ksp.sections.add(sec);
			prg.subSections.put(sec.obj.id, sec);
			prg.subSectionsByKspId.put(sec.kspObj.id, sec);
			kspQsnCount += qsnCount;
		}
		
		if (ksp.joinQuestions) {
			ksp.joinedSubSection = new SubSection(ksp, ksp.obj, ksp.nameRu, ksp.nameKz, ksp.obj, kspQsnCount, 0, true);
			prg.subSections.put(ksp.obj.id, ksp.joinedSubSection);
			prg.subSectionsByKspId.put(ksp.obj.id, ksp.joinedSubSection);
		}
	}
	
	private void loadBlocks(Session ors, HttpSession hs) throws Exception {
		final Object iin = hs.getAttribute(HS_IIN);
		final Program prg = getProgram(hs);

		KrnObject blkFilter = ors.getObjectByUid("1014162.3512794", 0);
		KrnObject lang = (KrnObject)hs.getAttribute("lang");
		
		ors.setFilterParam(blkFilter.uid, "%Язык", lang);
		
		List<KrnObject> blkObjs = new ArrayList<KrnObject>();
		Map<Long, SubSection> subSecByKspId = new HashMap<Long, SubSection>();
		
		KrnClass ztBlkCls = ors.getClassByName("ек::тест::Зап таб задания");
		KrnAttribute qsnAttr = ors.getAttributeByName(ztBlkCls, "вопросы");

		KrnClass qsnCls = ors.getClassByName("ек::тест::спр::Вопрос");
		AttrRequestBuilder relQsnRb = new AttrRequestBuilder(qsnCls, ors)
				.add("зап таб задания");
		AttrRequestBuilder qsnRb = new AttrRequestBuilder(qsnCls, ors)
				.add("парный_вопрос", relQsnRb);

		for (Section sec : prg.sections) {
			List<KrnObject> secBlkObjs = new ArrayList<KrnObject>();
			for (SubSection subSec : sec.sections) {
				subSecByKspId.put(subSec.kspObj.id, subSec);
				ors.setFilterParam(blkFilter.uid, "%Область знаний", subSec.kspObj);
				List<KrnObject> objs = ors.filter(blkFilter, 0,-1,-1, 0);
				
				if (objs.size() < subSec.qsnCount) {
					final String errMsg = "Недостаточно вопросов в области знаний"
							+ "(ID подраздела: " + subSec.obj.id + ", ID обл знаний:" + subSec.kspObj.id + ", ID языка:" + lang.id;
					log.error(iin + " " + errMsg);
					throw new Exception(errMsg);
				}
				
				if (subSec.shuffle) {
					// Перемешиваем вопросы и отбираем первые qsnCount
					Collections.shuffle(objs);
				}
				List<KrnObject> tmpList = new ArrayList<>(subSec.qsnCount);
				for (int i = 0; i < objs.size() && tmpList.size() < subSec.qsnCount; i++) {
					KrnObject ztBlkObj = objs.get(i);
					// Обработка связанных вопросов
					QueryResult qsnQr = ors.getObjectValues(new long[] {ztBlkObj.id}, qsnAttr.id, new long[0], 0, qsnRb.build());
					KrnObject relZtBlk = qsnRb.getObjectValue("парный_вопрос.зап таб задания", qsnQr.rows.get(0));
					if (relZtBlk != null && !tmpList.contains(relZtBlk)) {
						tmpList.add(relZtBlk);
					}
					if (!tmpList.contains(ztBlkObj)) {
						tmpList.add(ztBlkObj);
					}
				}
				if (sec.joinQuestions) {
					// Перемешивание будет выполнено позже
					secBlkObjs.addAll(tmpList);
				} else {
					// Необходимо еще раз перемешать, из-за связанных вопросов.
					if (subSec.shuffle) {
						Collections.shuffle(tmpList);
					}
					blkObjs.addAll(tmpList);
				}
			}
			if (sec.joinQuestions) {
				Collections.shuffle(secBlkObjs);
				blkObjs.addAll(secBlkObjs);
			}
		}
		
		log.info(iin + " Общее количество вопросов: " + blkObjs.size());
		loadBlocks(blkObjs, ors, hs);
	}
	
	private void loadBlocks(List<KrnObject> blkObjs, Session ors, HttpSession hs) throws Exception {
		
		long[] blkObjIds = Funcs.makeObjectIdArray(blkObjs);
		
		KrnObject lang = (KrnObject)hs.getAttribute("lang");
		Program prg = getProgram(hs);

		KrnClass blkCls = ors.getClassByName("ек::тест::Зап таб задания");
		KrnAttribute qsnAttr = ors.getAttributeByName(blkCls, "вопросы");
		KrnClass qsnCls = ors.getClassByName("ек::тест::спр::Вопрос");
		KrnAttribute awrAttr = ors.getAttributeByName(qsnCls, "варианты ответа");
		KrnClass awrCls = ors.getClassByName("ек::тест::спр::Вариант ответа");
		AttrRequestBuilder tskRb = new AttrRequestBuilder("ек::тест::спр::Задание", ors)
			.add("область знаний");
		AttrRequestBuilder blkRb = new AttrRequestBuilder(blkCls, ors)
			.add("медиа")
			.add("формат вопроса")
			.add("идентификатор")
			.add("задание", tskRb)
			.add("текст", lang.id);
		AttrRequestBuilder qsnRb = new AttrRequestBuilder(qsnCls, ors)
			.add("текст", lang.id);
		AttrRequestBuilder awrRb = new AttrRequestBuilder(awrCls, ors)
			.add("медиа")
			.add("формат")
			.add("текст", lang.id);
		
		//Map<Long, Integer> qsnCounters = new HashMap<Long, Integer>();

		QueryResult blkQr = ors.getObjects(blkObjIds, blkRb.build(), 0);
		for (Object[] blkRow : blkQr.rows) {
			KrnObject blkObj = blkRb.getObject(blkRow);
			String blkName = blkRb.getStringValue("идентификатор", blkRow);
			String blkText = blkRb.getStringValue("текст", lang.id, blkRow);
			KrnObject blkAudio = blkRb.getObjectValue("медиа", blkRow);
			KrnObject blkType = blkRb.getObjectValue("формат вопроса", blkRow);
			KrnObject kspObj = blkRb.getObjectValue("задание.область знаний", blkRow);
			
			SubSection subSec = prg.subSectionsByKspId.get(kspObj.id);
			Block blk = new Block(subSec, blkObj, blkName, blkText, blkAudio, getMediaType(blkType));
			prg.blocks.put(blkObj.id, blk);

			QueryResult qsnQr = ors.getObjectValues(new long[] {blkObj.id}, qsnAttr.id, new long[0], 0, qsnRb.build());
			
			if (qsnQr.totalRows == 0) {
				log.error("ек::тест::Зап таб задания[" + blkObj.id + "] не содержит вопросов.");
			}
			
			for (Object[] qsnRow : qsnQr.rows) {
				KrnObject qsnObj = qsnRb.getObject(qsnRow);
				String qsnName = qsnRb.getStringValue("текст", lang.id, qsnRow);
				Question qsn = new Question(blk, qsnObj, qsnName);
				prg.questions.put(qsnObj.id, qsn);

				QueryResult awrQr = ors.getObjectValues(new long[] {qsnObj.id}, awrAttr.id, new long[0], 0, awrRb.build());

				if (awrQr.totalRows == 0) {
					log.error("ек::тест::спр::Вопрос[" + qsnObj.id + "] не содержит вариантов ответа.");
				}

				for (Object[] awrRow : awrQr.rows) {
					KrnObject awrObj = awrRb.getObject(awrRow);
					String awrName = awrRb.getStringValue("текст", lang.id, awrRow);
					KrnObject awrMedia = awrRb.getObjectValue("медиа", awrRow);
					KrnObject awrMediaType = awrRb.getObjectValue("формат", awrRow);
					Answer awr = new Answer(qsn, awrObj, awrName, awrMedia, getMediaType(awrMediaType));
					qsn.answers.add(awr);
					prg.answers.put(awrObj.id, awr);
				}
				Collections.shuffle(qsn.answers);
			}
		}
	}
	
	private Map<Long, UserAnswer> loadUserAnswers(Session ors, HttpSession hs) throws Exception {
		
		Map<Long, UserAnswer> uawrs = (Map<Long, UserAnswer>)hs.getAttribute("userAnswers");
		if (uawrs != null)
			return uawrs;

		Program prg = getProgram(hs);
		uawrs = new HashMap<Long, UserAnswer>();
		hs.setAttribute("userAnswers", uawrs);
		
		KrnObject stepObj = (KrnObject)hs.getAttribute("step");
		
		KrnClass stepCls = ors.getClassByName("ек::тест::История заявки");
		KrnAttribute uawrAttr = ors.getAttributeByName(stepCls, "ответы");
		KrnClass uawrCls = ors.getClassByName("ек::тест::Зап таб ответа");

		AttrRequestBuilder qsnRb = new AttrRequestBuilder("ек::тест::спр::Вопрос", ors)
			.add("зап таб задания");
		AttrRequestBuilder uawrRb = new AttrRequestBuilder("ек::тест::Зап таб ответа", ors)
			.add("вопрос", qsnRb)
			.add("ответ")
			.add("подраздел")
			.add("время ответа")
			.add("номер");

		QueryResult blkQr = ors.getObjectValues(new long [] {stepObj.id}, uawrAttr.id, new long[0], 0, uawrRb.build());
		if (blkQr.totalRows > 0) {
			// В первом проходе загружаем связанные блоки
			List<KrnObject> blkObjs = new ArrayList<KrnObject>();
			for (Object[] uawrRow : blkQr.rows) {
				KrnObject blkObj = uawrRb.getObjectValue("вопрос.зап таб задания", uawrRow);
				if (!blkObjs.contains(blkObj))
					blkObjs.add(blkObj);
			}
			loadBlocks(blkObjs, ors, hs);
			// Во втором проходе загружаем ответы пользователя
			KrnObject prevSecObj = null;
			UserAnswer uawr = null;
			for (Object[] uawrRow : blkQr.rows) {
				KrnObject uawrObj = uawrRb.getObject(uawrRow);
				KrnObject qsnObj = uawrRb.getObjectValue("вопрос", uawrRow);
				KrnObject awrObj = uawrRb.getObjectValue("ответ", uawrRow);
				KrnObject secObj = uawrRb.getObjectValue("подраздел", uawrRow);
				Date awrTime = Funcs.convertTime((com.cifs.or2.kernel.Time)uawrRb.getValue("время ответа", uawrRow));
				int ordNum = (int)uawrRb.getLongValue("номер", uawrRow);
				Answer awr = awrObj != null ? prg.answers.get(awrObj.id) : null;
				
				if (!secObj.equals(prevSecObj)) {
					if (prevSecObj != null) {
						uawr.setLastInSection();
					}
					prevSecObj = secObj;
				}
				
				uawr = new UserAnswer(
						prg.subSections.get(secObj.id),
						prg.questions.get(qsnObj.id),
						ordNum,
						awr,
						awrTime,
						uawrObj);
				uawrs.put(qsnObj.id, uawr);
			}
			uawr.setLastInSection();
		} else {
			// Создание ек::тест::Зап таб ответа
			KrnAttribute appAttr = ors.getAttributeByName(uawrCls, "заявка");
			KrnAttribute stepAttr = ors.getAttributeByName(uawrCls, "история заявки");
			KrnAttribute qsnAttr = ors.getAttributeByName(uawrCls, "вопрос");
			KrnAttribute secAttr = ors.getAttributeByName(uawrCls, "подраздел");
			KrnAttribute numAttr = ors.getAttributeByName(uawrCls, "номер");
			KrnAttribute qsnCntAttr = ors.getAttributeByName(stepCls, "кол-во вопросов");
	
			loadBlocks(ors, hs);
			int i = 0;
			for (Section sec : prg.sections) {
				UserAnswer uawr = null;
				List<SubSection> subSecs = sec.joinQuestions ? Collections.singletonList(sec.joinedSubSection) : sec.sections;
				for (SubSection subSec : subSecs) {
					for (Block blk : subSec.blocks) {
						for (Question qsn : blk.questions) {
							Map<Pair<KrnAttribute, Long>, Object> values = new HashMap<Pair<KrnAttribute,Long>, Object>();
							values.put(new Pair<KrnAttribute, Long>(appAttr, 0L), hs.getAttribute("application"));
							values.put(new Pair<KrnAttribute, Long>(stepAttr, 0L), stepObj);
							values.put(new Pair<KrnAttribute, Long>(secAttr, 0L), blk.subSec.obj);
							values.put(new Pair<KrnAttribute, Long>(qsnAttr, 0L), qsn.obj.id);
							values.put(new Pair<KrnAttribute, Long>(numAttr, 0L), ++i);
							final KrnObject uawrObj = ors.createObject(uawrCls, values, 0);

							uawr = new UserAnswer(blk.subSec, qsn, i, null, null, uawrObj);
							uawrs.put(qsn.obj.id, uawr);
						}
					}
				}
				uawr.setLastInSection();
			}
			ors.setValue(stepObj, qsnCntAttr.id, 0, 0, 0, uawrs.size(), false);
			ors.commitTransaction();
		}
		return uawrs;
	}
	
	public static String getInstruction(HttpSession hs) throws Exception {
		Session ors = null;
		try {
			ors = TestServlet.getOr3Session(hs);

			final String langCode = (String)hs.getAttribute("langCode");
			KrnObject langObj = getLangObj(langCode, ors);
			if (langCode.matches(".+") && langCode.length() == 2)
				hs.setAttribute("lang", new KrnObject(langObj.id, langObj.uid, langObj.classId));
			
			final String instrFilterUid = "1023003.15617984";
			ors.setFilterParam(instrFilterUid, "%тип_напр", hs.getAttribute(HS_TEST_DIRTYPE));
			ors.setFilterParam(instrFilterUid, "%напр", hs.getAttribute(HS_TEST_CORPUS));
			ors.setFilterParam(instrFilterUid, "%язык", langObj);
			List<KrnObject> instrObjs = ors.filter(ors.getObjectByUid(instrFilterUid, 0), 0,-1,-1, 0);
			if (instrObjs.size() > 0) {
				KrnObject instrObj = instrObjs.get(0);

				AttrRequestBuilder fileRb = new AttrRequestBuilder(ors.getClassByName("MSDoc"), ors)
						.add("file", LANG_RU.id);
				AttrRequestBuilder instrRb = new AttrRequestBuilder(ors.getClassByName("ек::тест::спр::Инструкция"), ors)
						.add("файл", fileRb);
					
				QueryResult qr = ors.getObjects(new long[] {instrObj.id}, instrRb.build(), 0);
				if (qr.totalRows > 0) {
					Object[] row = qr.rows.get(0);
					byte[] data = (byte[])instrRb.getValue("файл.file" + LANG_RU.id, row);
					return new String(data, "UTF-8");
				}
			}
			return "";
		} finally {
			if (ors != null)
				ors.release();
		}
	}

	public static Session getOr3Session(HttpSession hs) throws AuthException, KrnException {
		Session ors = null;
		UserSession us = (UserSession) hs.getAttribute("userSession");
		if (us == null) {
			ors = SrvUtils.getSession(DS_NAME, USER, PD);
			us = ors.getUserSession();
			hs.setAttribute("userSession", us);
			initMetadata(ors);
		} else {
			ors = SrvUtils.getSession(us);
		}
		return ors;
	}
	
	private static Program getProgram(HttpSession hs) {
		return (Program)hs.getAttribute("program");
	}
	
	private Section getKnowledgeSpace(HttpSession hs) {
		return (Section)hs.getAttribute(HS_SECTION);
	}

	private Date startTest(Session ors, HttpSession hs) throws KrnException {
		
		final Object iin = hs.getAttribute(HS_IIN);
		final Section currKsp = getKnowledgeSpace(hs);
		
		Date startTime = (Date) hs.getAttribute(HS_SECTION_START);
		if (startTime == null) {
			startTime = new Date();
			hs.setAttribute(HS_SECTION_START, startTime);

			KrnClass stepCls = ors.getClassByName("ек::тест::История заявки");
			KrnObject stepObj = (KrnObject)hs.getAttribute("step");

			ors.setValue(stepObj, ors.getAttributeByName(stepCls, "тек сост раздел").id, 0, 0, 0, getKnowledgeSpace(hs).obj, false);
			ors.setValue(stepObj, ors.getAttributeByName(stepCls, "тек сост время начала").id, 0, 0, 0, new java.sql.Timestamp(startTime.getTime()), false);

			if (hs.getAttribute(HS_STEP_START) == null) {
				ors.setValue(stepObj, ors.getAttributeByName(stepCls, "результат").id, 0, 0, 0, ors.getObjectByUid("1014162.3511574", 0), false);
				ors.setValue(stepObj, ors.getAttributeByName(stepCls, "язык тестирования").id, 0, 0, 0, hs.getAttribute("lang"), false);
				ors.setValue(stepObj, ors.getAttributeByName(stepCls, "время начала факт").id, 0, 0, 0, new java.sql.Timestamp(startTime.getTime()), false);
				Program prg = getProgram(hs);
				ors.setValue(stepObj, ors.getAttributeByName(stepCls, "время конца план").id, 0, 0, 0, new java.sql.Timestamp(startTime.getTime() + prg.getTimeRemaining(currKsp) * 60000), false);
				hs.setAttribute(HS_STEP_START, startTime);
			}
			ors.commitTransaction();
			
			log.info(iin + " Начало теста (ID ист заявки:" + stepObj.id + ", ID раздела:" + currKsp.obj.id + ")");
		}
		hs.setAttribute(HS_NAV_MODE, (Boolean)hs.getAttribute(HS_TEST_ERK) ? 2 : currKsp.navNext ? 1 : 0);
		if (hs.getAttribute(HS_NAV_CURR) == null) {
			hs.setAttribute(HS_NAV_CURR, 0L);
		}
		if (hs.getAttribute(HS_NAV_BWD_DEPTH) == null) {
			hs.setAttribute(HS_NAV_BWD_DEPTH, 0);
		}
		
		KrnObject appObj = (KrnObject)hs.getAttribute("application");
		executeOr3TsonIntegration(ors, appObj, true);

		return startTime;
	}

	private JsonObject finishTest(Session ors, HttpSession hs) throws Throwable {
		
		final Object iin = hs.getAttribute(HS_IIN);

		createViewRecord(ors, hs, null);

		hs.removeAttribute(HS_SECTION_START);
		
		Program prg = getProgram(hs);
		Section currKsp = getKnowledgeSpace(hs);
		Section nextKsp = prg.getNextSection(currKsp);
		
		KrnClass stepCls = ors.getClassByName("ек::тест::История заявки");
		KrnObject stepObj = (KrnObject)hs.getAttribute("step");

		if (nextKsp != null) {
			hs.setAttribute(HS_SECTION, nextKsp);
			hs.setAttribute(HS_NAV_MODE, (Boolean)hs.getAttribute(HS_TEST_ERK) ? 2 : nextKsp.navNext ? 1 : 0);
			hs.setAttribute(HS_NAV_CURR, 0L);
			hs.setAttribute(HS_NAV_BWD_DEPTH, 0);

			JsonObject resJSON = new JsonObject();
			resJSON.add("section", nextKsp.obj.id);
			log.info(iin + " Переход на след раздел (ID ист заявки:" + stepObj.id + ", ID раздела:" + currKsp.obj.id + ", ID след раздела:" + nextKsp.obj.id + ")");
			return resJSON;
		}
		
		log.info(iin + " Завершение тестирования (ID ист заявки:" + stepObj.id + ", ID раздела:" + currKsp.obj.id + ")");

		Driver orDrv = ors.getDriver();
		CallableStatement call = null;
		long resObjId = -1;
		
		try {
			call = orDrv.getConnection().prepareCall("{?=call TEST_GET_RESULT(?,?)}");
			call.registerOutParameter (1, Types.BIGINT);
			call.setLong(2, stepObj.id);

			if ((boolean)hs.getAttribute(HS_TEST_ERK)) {
				call.setBoolean(3, true);
			} else {
				call.setLong(3, 0);
			}
			
			call.execute();
			resObjId = call.getLong(1);
		} catch (Exception e) {
			log.error(e, e);
			throw e;
		} finally {
			DbUtils.closeQuietly(call);
		}
		
		if ((boolean)hs.getAttribute(HS_TEST_ERK)) {
			try {
				BlackBoxPlugin plugin = new BlackBoxPlugin();
				plugin.setSession(ors);
				KrnObject report = plugin.get_result_2	(TestServlet.BB_DS_NAME, TestServlet.BB_SCHEMA, stepObj.id);
				ors.setValue(stepObj, ors.getAttributeByName(stepCls, "report_result").id, 0, 0, 0, report, false);
				resObjId = ors.getObjectValues(
						new long[] {stepObj.id},
						ors.getAttributeByName(stepCls, "результат").id,
						new long[0], 0)[0].value.id;
			} catch (Exception e) {
				log.error(iin + " Ошибка при получении результатов из BlackBox", e);
			}
		}

		KrnObject resObj = ors.getObjectById(resObjId, 0);
		hs.setAttribute(HS_RESULT, resObj);
		
		// Запись в БД
		ors.setValue(stepObj, ors.getAttributeByName(stepCls, "время конца факт").id, 0, 0, 0, new java.sql.Timestamp(new Date().getTime()), false);
		
		// В Заявку записываем только результат первого тестирования
		KrnObject appObj = (KrnObject)hs.getAttribute("application");
		KrnClass appCls = ors.getClassByName("ек::тест::Заявка");
		SortedSet<Value> vs = ors.getValues(new long[] { appObj.id }, ors.getAttributeByName(appCls, "результат").id, 0, 0);
		KrnObject oldRes = vs.size() > 0 ? (KrnObject)vs.first().value : null;
		// Неявка, В процессе тестирования, Приостановлен/Аннулирован
		if (oldRes == null || "1014162.3412574".equals(oldRes.uid) || "1014162.3511574".equals(oldRes.uid) || "1014162.3412575".equals(oldRes.uid))
			ors.setValue(appObj, ors.getAttributeByName(appCls, "результат").id, 0, 0, 0, resObj, false);
		
		ors.commitTransaction();
		
		hs.setAttribute("userStatus", "finish");
		
		if (OBJ_DIRTYPE_GMA.equals(hs.getAttribute(HS_TEST_DIRTYPE))) {
			KrnObject obj = (KrnObject)hs.getAttribute("step");
	        Context ctx = new Context(new long[0], 0, 0);
	        ctx.langId = 0;
	        ctx.trId = 0;
	        ors.setContext(ctx);
	        List<Object> args = new ArrayList<Object>();
	        SrvOrLang orlang = ors.getSrvOrLang();
	        
	        KrnObject objVar = obj != null ? new KrnObject(obj.id, obj.uid, obj.classId) : null;
	        if (hs.getAttribute("lang") instanceof KrnObject) {
	        	KrnObject lang = (KrnObject) hs.getAttribute("lang");
	        	args.add(lang != null ? new KrnObject(lang.id, lang.uid, lang.classId) : null);
	        }
	        
	        hs.setAttribute(HS_RESULT_GMA, orlang.exec(objVar, objVar, "расчет набранных баллов", args, new Stack<String>()));
		}
		
		executeOr3TsonIntegration(ors, appObj, false);

		return getSuccessJSON();
	}
	
	private void executeOr3TsonIntegration(Session ors, KrnObject appObj, boolean startOrFinish) {
		try {
			Context ctx = new Context(new long[0], 0, 0);
	        ctx.langId = 0;
	        ctx.trId = 0;
	        ors.setContext(ctx);
	        List<Object> args = new ArrayList<Object>();
	        SrvOrLang orlang = ors.getSrvOrLang();
	        
			KrnClass wsCls = ors.getClassByName("WsUtilNew");
	        args.add(appObj);
	        args.add(startOrFinish);
	        
	        orlang.exec(wsCls, wsCls, "startFinTson", args, new Stack<String>());
		} catch (Throwable e) {
			log.error("Ошибка при вызове метода при " + (startOrFinish ? "начале" : "окончании") + " тестирования!");
			log.error(e, e);
		}
	}

	private JsonObject exit(HttpSession hs) {
		Object iin = hs.getAttribute(HS_IIN);
		if (iin instanceof String) {
			SessionCounter counter = SessionCounter.getInstance(getServletContext());
			counter.removeIin((String)iin);
		}

		hs.removeAttribute("answers");
		hs.removeAttribute(HS_SECTION);
		hs.removeAttribute(HS_IIN);
		hs.removeAttribute(HS_FIO);
		hs.removeAttribute("programName");
		hs.removeAttribute("category");
		hs.removeAttribute("application");
		hs.removeAttribute("step");
		hs.removeAttribute(HS_STEP_START);
		hs.removeAttribute("person");
		hs.removeAttribute("userStatus");
		hs.removeAttribute("program");
		hs.removeAttribute("userAnswers");
		hs.removeAttribute("lang");
		hs.removeAttribute("langCode");
		hs.removeAttribute(HS_NAV_CURR);
		hs.removeAttribute(HS_NAV_BWD_DEPTH);
		hs.removeAttribute(HS_TEST_DIRTYPE);
		hs.removeAttribute(HS_TEST_CORPUS);
		hs.removeAttribute(HS_RESULT);
		hs.removeAttribute(HS_RESULT_GMA);
		hs.removeAttribute(HS_RESULT_RECORDS);
		hs.removeAttribute(HS_QVR_UAWR);
		hs.removeAttribute(HS_QVR_START_TIME);
		hs.removeAttribute(HS_TEST_ERK);
		
		return getSuccessJSON();
	}
	
	private int login(HttpServletRequest req, Session ors, StringBuilder err) throws Exception {
		HttpSession hs = req.getSession();
		
		exit(hs);

		String userStatus = (String)hs.getAttribute("userStatus");
		String userIIN = (String)hs.getAttribute("userIIN");

		String login = StringEscapeUtils.escapeHtml(Funcs.getParameter(req, "login"));

		if ("work".equals(userStatus) && login.equals(userIIN))
			return 2;
		if (login != null && login.length() > 0 && Funcs.isValid(login)) {
			
			// 1014162.3416522 Фильтр Текущая заявка
			ors.setFilterParam("1014162.3416522", "%ИИН", login);
			List<KrnObject> stepObjs = ors.filter(ors.getObjectByUid("1014162.3416522", 0), 0,-1,-1, 0);
			if (stepObjs.size() > 0) {
				KrnObject stepObj = stepObjs.get(0);
				
				AttrRequestBuilder plnRb = new AttrRequestBuilder("ек::тест::Зап таб графика", ors)
				.add("зал тестирования")
				.add("дата время");
				
				AttrRequestBuilder psnRb = new AttrRequestBuilder("ек::тест::Персона", ors)
				.add("фамилия")
				.add("имя")
				.add("отчество");

				getLangObj("ru", ors);
				AttrRequestBuilder catRb = new AttrRequestBuilder("Категория госслужащего", ors).add("наименование", LANG_RU.id);
				
				AttrRequestBuilder prgRb = new AttrRequestBuilder("ек::тест::Программа", ors)
						.add("наименование", LANG_RU.id)
						.add("наименование", LANG_KZ.id);

				AttrRequestBuilder appRb = new AttrRequestBuilder("ек::тест::Заявка", ors)
				.add("корпус")
				.add("программа", prgRb)
				.add("персона", psnRb)
				.add("категория", catRb)
				.add("госорган", LANG_RU.id)
				.add("госорган", LANG_KZ.id)
				.add("место работы", LANG_RU.id)
				.add("место работы", LANG_KZ.id)
				.add("должность", LANG_RU.id)
				.add("должность", LANG_KZ.id);
				
				AttrRequestBuilder zipRb = new AttrRequestBuilder("ек::тест::спр::Зап IP адреса рабочей станции", ors)
				.add("ip адрес")
				.add("номер компа");

				AttrRequestBuilder stepRb = new AttrRequestBuilder("ек::тест::История заявки", ors)
				.add("раздел программы")
				.add("тек сост раздел")
				.add("тек сост время начала")
				.add("время начала факт")
				.add("язык тестирования")
				.add("заявка", appRb)
				.add("зап таб графика", plnRb)
				.add("зап IP адрес рабочей станции", zipRb)
				.add("по_ЕРК?");

				QueryResult qr = ors.getObjects(new long[] { stepObj.id }, stepRb.build(), 0);
				if (qr.totalRows > 0) {
					Object[] row = qr.rows.get(0);
					
					boolean erk = stepRb.getBooleanValue("по_ЕРК?", row, false);
					hs.setAttribute(HS_TEST_ERK, erk);
					
					final String ip = getIpAddress(req);

					if (!erk) {
						// Проверка времени начала тестирования
						KrnDate startTime = Funcs.convertTime((Time)stepRb.getValue("зап таб графика.дата время", row));
						if (startTime != null && System.currentTimeMillis() < startTime.getTime()) {
							err.append("Тестирование еще не началось.\nНачало в " + startTime.toString("HH:mm dd.MM.yyyy"));
							return 12;
						}
						// Проверка IP адреса рабочей станции
						KrnObject aud = stepRb.getObjectValue("зап таб графика.зал тестирования", row);
						// 1014162.4074649 Фильтр Проверка IP-адреса
						ors.setFilterParam("1014162.4074649", "%Зал", aud);
						ors.setFilterParam("1014162.4074649", "%IP", ip);
						List<KrnObject> ztIps = ors.filter(ors.getObjectByUid("1014162.4074649", 0), 0,-1,-1, 0);
						if (ztIps.size() == 0)
							return 10;
						
						if ("true".equalsIgnoreCase(System.getProperty("test.checkIp", "true"))) {
							// Проверка случайно присвоенного IP-адреса
							String stepIp = stepRb.getStringValue("зап IP адрес рабочей станции.ip адрес", row);
							if (!ip.equals(stepIp)) {
								int compNum = stepRb.getIntValue("зап IP адрес рабочей станции.номер компа", row);
								err.append("Вам необходимо проходить тестирование за компьютером №" + compNum + " (IP:" + stepIp + ")");
								return 13;
							}
						}
					}
					
					SessionCounter counter = SessionCounter.getInstance(getServletContext());
					if (!counter.addIin(login, ip))
						return 11;
					
					String fio = stepRb.getStringValue("заявка.персона.фамилия", row) + " " + stepRb.getStringValue("заявка.персона.имя", row);
					String middle = stepRb.getStringValue("заявка.персона.отчество", row);
					if (middle != null)
						fio += " " + middle;

					KrnObject langObj = stepRb.getObjectValue("язык тестирования", row);
					if (langObj == null) {
						String lang = req.getParameter("lang");
						langObj = getLangObj(lang, ors);
					}
					String lang = langObj.id == LANG_RU.id ? "ru" : "kz";
						
					if (langObj != null && langObj.classId > 0 && lang.length() == 2)
						hs.setAttribute("lang", new KrnObject(langObj.id, langObj.uid, langObj.classId));
					
					if (Funcs.isValid(lang))
						hs.setAttribute("langCode", lang);
					
					if (login != null && login.length() == 12 && login.matches(".+")) {
						hs.setAttribute(HS_IIN, login);
						hs.setAttribute("userIIN", login);
					}
					if (fio != null && fio.length() > 1 && fio.matches(".+"))
						hs.setAttribute(HS_FIO, fio);

					hs.setAttribute("userStatus", "work");

					hs.setAttribute("programName", stepRb.getStringValue("заявка.программа.наименование", LANG_RU.id, row));
					hs.setAttribute("programName_ru", stepRb.getStringValue("заявка.программа.наименование", LANG_RU.id, row));
					hs.setAttribute("programName_kz", stepRb.getStringValue("заявка.программа.наименование", LANG_KZ.id, row));
					hs.setAttribute("orgName_ru", stepRb.getStringValue("заявка.госорган", LANG_RU.id, row));
					hs.setAttribute("orgName_kz", stepRb.getStringValue("заявка.госорган", LANG_KZ.id, row));
					hs.setAttribute("depName_ru", stepRb.getStringValue("заявка.место работы", LANG_RU.id, row));
					hs.setAttribute("depName_kz", stepRb.getStringValue("заявка.место работы", LANG_KZ.id, row));
					hs.setAttribute("posName_ru", stepRb.getStringValue("заявка.должность", LANG_RU.id, row));
					hs.setAttribute("posName_kz", stepRb.getStringValue("заявка.должность", LANG_KZ.id, row));
					hs.setAttribute("category", stepRb.getStringValue("заявка.категория.наименование", LANG_RU.id, row));
					hs.setAttribute("application", stepRb.getObjectValue("заявка", row));
					hs.setAttribute("step", stepObj);
					hs.setAttribute(HS_STEP_START, Funcs.convertTime((Time)stepRb.getValue("время начала факт", row)));
					hs.setAttribute("person", stepRb.getObjectValue("заявка.персона", row));
					Program prg = loadTestProgram(
							stepRb.getObjectValue("заявка.программа", row),
							stepRb.getObjectValue("раздел программы", row),
							ors,
							hs);

					KrnObject kspObj = stepRb.getObjectValue("тек сост раздел", row);
					Section ksp = kspObj != null ? prg.getSection(kspObj.id) : prg.getFirstSection();
					
					hs.setAttribute(HS_SECTION, ksp);
					hs.setAttribute(HS_SECTION_START, Funcs.convertTime((Time)stepRb.getValue("тек сост время начала", row)));
					
					// Записываем данные сессии пользователя
					KrnClass stepCls = ors.getClassByName("ек::тест::История заявки");
					KrnAttribute ipAttr = ors.getAttributeByName(stepCls, "ip адрес");
					KrnAttribute hostAttr = ors.getAttributeByName(stepCls, "имя хоста");
					
					ors.setValue(stepObj, ipAttr.id, 0, 0, 0, getIpAddress(req), false);
					ors.setValue(stepObj, hostAttr.id, 0, 0, 0, req.getRemoteHost(), false);
					
					ors.commitTransaction();
					
					hs.setAttribute(HS_NAV_MODE, erk ? 2 : ksp.navNext ? 1 : 0);
					
					return kspObj == null ? 1 : 2;
				}
			}
		}
		return 0;
	}
	
	private List<Question> getUnansweredQuestions(SubSection subSec, Session ors, HttpSession hs) throws Exception {
		List<Question> res = new ArrayList<Question>();
		Map<Long, UserAnswer> uawrs = loadUserAnswers(ors, hs);
		
		if (subSec.sec.joinQuestions)
			subSec = subSec.sec.joinedSubSection;
		
		for (Block blk : subSec.blocks) {
			for (Question qsn : blk.questions) {
				UserAnswer uawr = uawrs.get(qsn.obj.id);
				if (uawr.awr == null)
					res.add(qsn);
			}
		}
		return res;
	}
	
	private int getMediaType(KrnObject formatObj) {
		int mediaType = Block.MT_NONE;
		if (formatObj == null) {
		} else if ("1014162.3486916".equals(formatObj.uid)) {
			mediaType = Block.MT_AUDIO;
		} else if ("1014162.3486917".equals(formatObj.uid)) {
			mediaType = Block.MT_IMAGE;
		} else if ("1014162.3512953".equals(formatObj.uid)) {
			mediaType = Block.MT_VIDEO;
		}
		return mediaType;
	}
	
	private static synchronized KrnObject getLangObj(String code, Session ors) throws Exception {
		if (LANG_RU == null) {
			LANG_RU = ors.getObjectByUid("102", 0);
			LANG_KZ = ors.getObjectByUid("103", 0);
		}
		return "ru".equals(code) ? LANG_RU : LANG_KZ;
	}
	
	/**
	 * Получение реального IP рабочей станции при использовании
	 * проксирующего сервлета WLS
	 * @param req
	 * @return
	 */
	private String getIpAddress(HttpServletRequest req) {
		String ip = req.getHeader("wl-proxy-client-ip");
		ip = ip == null ? req.getRemoteAddr() : ip;
		
		if (ip != null) {
			ip = Normalizer.normalize(ip, Form.NFKC);
			ip = ip.replace("'", "''").replace("\"", "").replace("`", "").replace("]", "")
					.replace("[", "").replace("\r", "").replace("\n", "");
			return StringEscapeUtils.escapeHtml(ip);
		}
		return "unknown";
	}
	
	private String createCertificate(HttpSession hs, Session ors) throws Throwable {
		KrnObject obj = (KrnObject)hs.getAttribute("step");
        Context ctx = new Context(new long[0], 0, 0);
        ctx.langId = 0;
        ctx.trId = 0;
        ors.setContext(ctx);
        List<Object> args = new ArrayList<Object>();
        args.add(hs.getAttribute("lang"));
        SrvOrLang orlang = ors.getSrvOrLang();
        String res = (String)orlang.exec(obj, obj, "сформировать сертификат", args, new Stack<String>());
        return res;
	}
	
	private Object createCertificateErk(HttpSession hs, Session ors) throws Throwable {
		try {
			KrnObject obj = (KrnObject)hs.getAttribute("step");
	        Context ctx = new Context(new long[0], 0, 0);
	        ctx.langId = 0;
	        ctx.trId = 0;
	        ors.setContext(ctx);
	        List<Object> args = new ArrayList<Object>();
	        args.add(hs.getAttribute("lang"));
	        SrvOrLang orlang = ors.getSrvOrLang();
	        byte[] res = (byte[])orlang.exec(obj, obj, "сформировать заключение ЕРК 2", args, new Stack<String>());
	        return res;
		} catch (Exception e) {
			final Object iin = hs.getAttribute(HS_IIN);
			log.error(iin + " Ошибка при формировании отчета.", e);
	        return "Ошибка при формировании Заключения! Для распечатки Заключения обратитесь к администратору.";
		}
	}

	public static List<ResultRecord> getResultRecords(HttpSession hs) throws Exception {

		KrnObject stepObj = (KrnObject)hs.getAttribute("step");
		Program prg = getProgram(hs);
		
		Session ors = TestServlet.getOr3Session(hs);
		try {
			AttrRequestBuilder recRb = new AttrRequestBuilder(ors.getClassByName("ек::тест::Зап таб результатов"), ors)
					.add("номер")
					.add("подраздел программы")
					.add("кол-во вопросов")
					.add("порог")
					.add("кол-во прав ответов");
			KrnAttribute recsAttr = ors.getAttributeByName(ors.getClassByName("ек::тест::История заявки"), "зап таб результатов");
			QueryResult qr = ors.getObjectValues(new long[] { stepObj.id }, recsAttr.id, new long[0], 0, recRb.build());
			List<ResultRecord> res = new ArrayList<>(qr.totalRows);
			for (Object[] row : qr.rows) {
				KrnObject subSecObj = recRb.getObjectValue("подраздел программы", row);
				res.add(new ResultRecord(
						recRb.getObject(row),
						stepObj,
						recRb.getIntValue("номер", row),
						null,
						subSecObj != null ? prg.subSections.get(subSecObj.id) : null,
						recRb.getIntValue("кол-во вопросов", row),
						recRb.getIntValue("порог", row),
						recRb.getIntValue("кол-во прав ответов", row),
						null));
			}
			return res;
		} finally {
			ors.release();
		}
	}
	
	private void createViewRecord(Session ors, HttpSession hs, UserAnswer newUawr) throws KrnException {

		UserAnswer currUawr = (UserAnswer)hs.getAttribute(HS_QVR_UAWR);
		if (currUawr == null) {
			hs.setAttribute(HS_QVR_UAWR, newUawr);
			hs.setAttribute(HS_QVR_START_TIME, new Timestamp(System.currentTimeMillis()));
		} else {
			if (currUawr != newUawr) {
				Map<Pair<KrnAttribute, Long>, Object> values = new HashMap<Pair<KrnAttribute,Long>, Object>();
				values.put(new Pair<KrnAttribute, Long>(ATTR_ZQ_UAWR, 0L), currUawr.obj);
				values.put(new Pair<KrnAttribute, Long>(ATTR_ZQ_START_TIME, 0L), hs.getAttribute(HS_QVR_START_TIME));
				values.put(new Pair<KrnAttribute, Long>(ATTR_ZQ_END_TIME, 0L), new Timestamp(System.currentTimeMillis()));
				
				ors.createObject(CLS_ZAP_QVIEW, values, 0);
				ors.commitTransaction();
				
				if (newUawr != null) {
					hs.setAttribute(HS_QVR_UAWR, newUawr);
					hs.setAttribute(HS_QVR_START_TIME, new Timestamp(System.currentTimeMillis()));
				} else {
					hs.removeAttribute(HS_QVR_UAWR);
					hs.removeAttribute(HS_QVR_START_TIME);
				}
			}
		}
	}
	
	private void computeBackwardDepth(Block currBlk, long prevBlkId, HttpSession hs) {
		if (prevBlkId != 0 && currBlk.obj.id != prevBlkId) {
			boolean count = false;
			Section sec = currBlk.subSec.sec;
			List<Block> blocks = sec.joinQuestions ? sec.joinedSubSection.blocks : currBlk.subSec.blocks;
			for (Block block : blocks) {
				if (block.obj.id == currBlk.obj.id) {
					count = true;
				} else if (block.obj.id == prevBlkId) {
					break;
				}
			}
			int bwdDepth = 0;
			if (count) {
				Integer currBwdDepth = (Integer)hs.getAttribute(HS_NAV_BWD_DEPTH);
				bwdDepth = currBwdDepth != null ? currBwdDepth + 1 : 1;
			}
			hs.setAttribute(HS_NAV_BWD_DEPTH, bwdDepth);
		}
	}

	private static synchronized void initMetadata(Session ors) throws KrnException {
		if (CLS_ZAP_QVIEW == null) {
			
			CLS_ZAP_QVIEW = ors.getClassByName("ек::тест::Зап таб просмотра вопроса");
			ATTR_ZQ_UAWR = ors.getAttributeByName(CLS_ZAP_QVIEW, "зап таб ответа");
			ATTR_ZQ_START_TIME = ors.getAttributeByName(CLS_ZAP_QVIEW, "время начало");
			ATTR_ZQ_END_TIME = ors.getAttributeByName(CLS_ZAP_QVIEW, "время конец");

			OBJ_RES_PASSED = ors.getObjectByUid("1014162.3412572", 0);
			OBJ_RES_FAILED = ors.getObjectByUid("1014162.3412573", 0);
			
			OBJ_DIRTYPE_GMA = ors.getObjectByUid("1023003.15617980", 0);
			OBJ_DIRTYPE_COMP = ors.getObjectByUid("1023003.15617978", 0);
		}
	}
}
