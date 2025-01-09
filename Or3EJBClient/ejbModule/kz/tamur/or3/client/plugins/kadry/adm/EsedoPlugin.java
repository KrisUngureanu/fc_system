package kz.tamur.or3.client.plugins.kadry.adm;

import java.io.File;
import java.util.Vector;

import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.OrFrame;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.lang.ObjectWrp;
import kz.tamur.rt.orlang.AbstractClientPlugin;
import lotus.domino.Agent;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.View;

public class EsedoPlugin extends AbstractClientPlugin {

	public void sendReport(ObjectWrp report, ObjectWrp lang, String desc) {
		OrFrame frame = getFrame();
		ReportPrinter rp = frame.getReportPrinter(report.getId());
		File file = rp.printToFile(lang.getKrnObject());
		try {
			sendFile(file, desc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendReportTest(ObjectWrp report, ObjectWrp lang, String desc) {
		OrFrame frame = getFrame();
		ReportPrinter rp = frame.getReportPrinter(report.getId());
		File file = rp.printToFile(lang.getKrnObject());

		try {
			NotesThread.sinitThread();

			Session s = NotesFactory.createSession((String) null,
					(String) null, "12345678");
			System.out.println(s.getPlatform());
			System.out.println(s.getUserNameList());
			System.out.println(s.getUserGroupNameList());

			Database db = s.getDatabase("", "test.nsf");

			Document doc = db.createDocument();
			doc.replaceItemValue("Form", "TestDoc");
			RichTextItem item = doc.createRichTextItem("rtfield");
			item.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, file
					.getAbsolutePath(), file.getName());
			doc.replaceItemValue("desc", desc);
			if (doc.computeWithForm(false, false)) {
				doc.save();
			} else {
				System.out.println("Document contains errors");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			NotesThread.stermThread();
		}
	}

	private void sendFile(File file, String desc) throws Exception {
		try {
			NotesThread.sinitThread();

			Session s = NotesFactory.createSession((String) null,
					(String) null, "12345678");
			System.out.println(s.getPlatform());
			System.out.println(s.getUserNameList());
			System.out.println(s.getUserGroupNameList());

			Database db = s.getDatabase("TEST/APRK/APRK",
					"esedo2008/esedo2008_attach.nsf");

			Document pfDoc = db.getProfileDocument("Options", null);

			Database strDb = s.getDatabase("TEST/APRK/APRK", (String) pfDoc
					.getItemValue("StructureDB").get(0));
			Database cntDb = s.getDatabase("TEST/APRK/APRK", (String) pfDoc
					.getItemValue("ContentDB").get(0));
			Database depDb = s.getDatabase("TEST/APRK/APRK", (String) pfDoc
					.getItemValue("DepotOptionsDB").get(0));
			Database dicDb = s.getDatabase("TEST/APRK/APRK", (String) pfDoc
					.getItemValue("DictionaryDB").get(0));

			// Создаем документ
			Document doc = db.createDocument();
			doc.replaceItemValue("Form", "OUT");
			// Вид документа
			doc.replaceItemValue("DocumentTypeCode", "01130");
			doc.replaceItemValue("DocumentTypeNameRU", "Приказ по личному составу");
			// Характер вопроса
			doc.replaceItemValue("DocumentCharacterTypeCode", "01020500");
			doc.replaceItemValue("DocumentCharacterTypeNameRU", "Госдарственная служба, кадровое обеспечение");
		
			doc.replaceItemValue("DocumentSummary", desc);
			
			String uid = doc.getUniversalID();
			doc.replaceItemValue("UID", uid);
			doc.replaceItemValue("DocumentUID", uid);
			doc.replaceItemValue("MainUID", uid);

			// Задаем данные исполнителя
			DocumentCollection docs = strDb
					.search("Form=\"Employee\" & EmployeeLNAddress=\""
							+ s.getUserName() + "\"");
			Document empDoc = docs.getFirstDocument();
			if (empDoc != null) {

				doc.appendItemValue("DocumentWriterCode", getFirstItemValue(
						empDoc, "ElementCode"));
				doc.appendItemValue("DocumentWriterGroup", getFirstItemValue(
						empDoc, "group"));
				doc.appendItemValue("DocumentWriterPGroup", getFirstItemValue(
						empDoc, "PGroup"));
				doc.appendItemValue("DocumentWriterSGroup", getFirstItemValue(
						empDoc, "sgroup"));
				doc.appendItemValue("DocumentWriterNameKZ", getFirstItemValue(
						empDoc, "ElementNameKZ"));
				doc.appendItemValue("DocumentWriterNameRU", getFirstItemValue(
						empDoc, "ElementNameRU"));
				doc.appendItemValue("DocumentWriterOfficeNameKZ",
						getFirstItemValue(empDoc, "OfficeNameKZ"));
				doc.appendItemValue("DocumentWriterOfficeNameRU",
						getFirstItemValue(empDoc, "OfficeNameRU"));
				doc.appendItemValue("DocumentWriterLNAddress",
						getFirstItemValue(empDoc, "EmployeeLNAddress"));
				doc.appendItemValue("DocumentWriterPhone", getFirstItemValue(
						empDoc, "EmployeePhone"));

				Document empDoc1 = null;
				Vector vs = empDoc.getItemValue("NomDivCode");
				if (vs.size() > 0 && !"".equals(vs.get(0))) {
					String code = (String) vs.get(0);
					doc.appendItemValue("DocumentWriterParentCode", code);
					doc.appendItemValue("DocumentWriterParentNameKZ", empDoc
							.getItemValue("NomDivNameKZ").get(0));
					doc.appendItemValue("DocumentWriterParentNameRU", empDoc
							.getItemValue("NomDivNameRU").get(0));
					empDoc1 = strDb.getDocumentByUNID((String) empDoc
							.getItemValue("NomDivUID").get(0));
				} else {
					Document parentDoc = strDb.getDocumentByUNID(empDoc
							.getParentDocumentUNID());
					empDoc1 = parentDoc;
					while (!"1".equals(empDoc1.getItemValue("IsOtdel").get(0))) {
						empDoc1 = strDb.getDocumentByUNID(empDoc1
								.getParentDocumentUNID());
					}
					doc.appendItemValue("DocumentWriterParentCode",
							getFirstItemValue(empDoc1, "ElementCode"));
					doc.appendItemValue("DocumentWriterParentNameKZ",
							getFirstItemValue(empDoc1, "ElementNameKZ"));
					doc.appendItemValue("DocumentWriterParentNameRU",
							getFirstItemValue(empDoc1, "ElementNameRU"));
				}
				doc.replaceItemValue("CountID", getFirstItemValue(empDoc1,
						"CountID"));
				doc.replaceItemValue("NomenclatureIndex", "3.1");
				doc.replaceItemValue("NomenclatureDep", getFirstItemValue(doc,
						"DocumentWriterParentCode"));
			}

			// Создаем вложение
			Document cntDoc = cntDb.createDocument();
			cntDoc.replaceItemValue("Form", "Attachment");
			cntDoc.replaceItemValue("UID", uid);
			cntDoc.replaceItemValue("DocumentUID", uid);
			String attachUid = cntDoc.getUniversalID();
			cntDoc.replaceItemValue("AttachUID", attachUid);
			cntDoc.replaceItemValue("ParentDB", db.getFilePath());
			RichTextItem rtItem = cntDoc.createRichTextItem("AttachmentBody");
			rtItem.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, file
					.getAbsolutePath(), file.getName());
			cntDoc.save(true, true);

			doc.replaceItemValue("AttachmentsUID", cntDoc.getUniversalID());
			doc.replaceItemValue("AttachmentName", file.getName());
			doc.computeWithForm(false, false);
			doc.save(true, true);

			// Создаем карточку в базе "Настройка хранилищ"
			Document depDoc = depDb.createDocument();
			depDoc.replaceItemValue("Form", "AttachInfo");
			depDoc.replaceItemValue("AttachmentName", getFirstItemValue(doc,
					"AttachmentName"));
			String str = cntDb.getFilePath();
			depDoc.replaceItemValue("AttachmentDB", str);
			str = str.substring(1, str.length() - 4);
			str = str.substring(str.length() - 2);
			if (toInt(str) == null) {
				str = "00";
			}
			depDoc.replaceItemValue("AttachmentDBProfile", "ContentDB" + str);
			depDoc.replaceItemValue("OwnerDB", db.getFilePath());
			depDoc.replaceItemValue("OwnerDBProfile", "InboxDB");
			depDoc.replaceItemValue("UID", uid);
			depDoc.replaceItemValue("MainUID", uid);
			depDoc.replaceItemValue("AttachmentUID", attachUid);
			depDoc.replaceItemValue("DocumentUID", depDoc.getUniversalID());
			depDoc.save(true, true);

			// Регистрируем документ
			String number = "";

			if (getFirstItemValue(doc, "NomenclatureDep") == null) {
				doc.replaceItemValue("NomenclatureDep", getFirstItemValue(doc,
						"DocumentWriterParentCode"));
			}

			if (getFirstItemValue(doc, "DocumentOutNumber") == null) {
				Document d = dicDb.getDocumentByUNID(getFirstItemValue(pfDoc,
						"OutboxRegNumberID"));
				for (int i = 0; i <= 7; i++) {
					String x = "Section_" + i;
					Item item = d.getFirstItem(x);
					if (!"".equals(item.getText())) {
						number = number
								+ getRegNumber(item.getText(), doc, db, dicDb,
										s);
					} else {
						break;
					}
				}
				doc.replaceItemValue("DocumentOutNumber", number);
			}
			doc.replaceItemValue("ExecutionStatusCode", "Created");
			doc.replaceItemValue("ExecutionStatusNameRU", "Зарегистрированный");
			doc.replaceItemValue("ExecutionStatusNameKZ", "Тіркелінген");

			doc.save(true, true);
		} finally {
			NotesThread.stermThread();
		}
	}

	private String getRegNumber(String paramName, Document doc, Database db,
			Database dicDb, Session session) throws NotesException {
		String value = null;
		String str = paramName.substring(0, 4);
		if ("Code".equals(str)) {
			value = code(paramName, doc);
		} else if ("Sqnc".equals(str)) {
		} else if ("Sprt".equals(str)) {
			if ("Sprt[.]".equals(paramName)) {
				value = ".";
			} else if ("Sprt[,]".equals(paramName)) {
				value = ",";
			} else if ("Sprt[-]".equals(paramName)) {
				value = "-";
			} else if ("Sprt[/]".equals(paramName)) {
				value = "/";
			}
		} else {
			if (paramName.startsWith("@")) {
				Vector vs = session.evaluate(paramName, doc);
				String v = (String) vs.get(0);
				if ("SqncNomenclature".equals(v)
						|| "SqncDepartmentOutByWriter".equals(v)
						|| "SqncRepeat".equals(v)) {
					value = "" + sequence(v, doc, db, dicDb);
				} else {
					value = v;
				}
			} else {
				value = paramName;
			}
		}
		return value;
	}

	private String code(String paramName, Document doc) throws NotesException {
		if ("CodeNomenklature".equals(paramName)) {
			return getFirstItemValue(doc, "NomenclatureIndex");
		} else if ("CodeDepartmentByWriter".equals(paramName)) {
			return getFirstItemValue(doc, "DocumentWriterParentCode");
		} else if ("CodeBySigner".equals(paramName)) {
			return getFirstItemValue(doc, "DocumentSignerCode");
		}
		return null;
	}

	private int sequence(String paramName, Document doc, Database db,
			Database dicDb) throws NotesException {
		String id;
		Document d = null;
		Item item = null;
		View view = db.getView("Count");
		if ("SqncInbox".equals(paramName)) {
			d = view.getFirstDocument();
			item = d.getFirstItem("InboxCount");
		} else if ("SqncOutbox".equals(paramName)) {
			d = view.getFirstDocument();
			item = d.getFirstItem("OutboxCount");
		} else if ("SqncDepartmentOutByWriter".equals(paramName)) {
			d = view.getFirstDocument();
			if (doc.getFirstItem("NomenclatureDep") != null) {
				id = getFirstItemValue(doc, "NomenclatureDep");
			} else {
				id = getFirstItemValue(doc, "DocumentWriterParentCode");
			}
			while (d != null) {
				if (id.equals(getFirstItemValue(d, "TypeCorrCode"))) {
					break;
				}
				d = view.getNextDocument(d);
			}
			if (d != null) {
				item = d.getFirstItem("OutboxCount");
			} else {
				System.out
						.println("Не создан счетчик для данного подразделения. Обратитесь к администратору.");
			}
		} else if ("SqncNomenclature".equals(paramName)) {
			d = dicDb.getDocumentByID(getFirstItemValue(doc,
					"NomenclatureCardID"));
			item = d.getFirstItem("NomenclatureCount");
			Agent agent = db.getAgent("ChangeNomenclatureCount");
			agent.runOnServer(getFirstItemValue(doc, "NomenclatureCardID"));
		}

		int res = 0;

		if ("SqncRepeat".equals(paramName)) {
			// res = num + 1;
		} else {
			res = Integer.parseInt(item.getText());
		}

		if (!"SqncNomenclature".equals(paramName)
				&& !"SqncRepeat".equals(paramName)) {
			item.setValueString("" + (res + 1));
			d.save(false, false);
		}
		return res;
	}

	private String getFirstItemValue(Document doc, String name)
			throws NotesException {
		Vector vs = doc.getItemValue(name);
		String res = vs.size() > 0 ? (String) vs.get(0) : null;
		System.out.println(res);
		return res;
	}

	private Integer toInt(String s) {
		try {
			return new Integer(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
