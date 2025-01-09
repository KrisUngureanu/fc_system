/**
 * 
 */
package kz.tamur.web.common;

import static kz.tamur.rt.Utils.getHash;
import static kz.tamur.web.controller.WebController.DIR_JS_CSS;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

/**
 * Класс реализует подсчёт хэш-сумм заданных файлов.
 * Используется для автоматического обновления скриптов и стилей WEB интерфейса.
 * 
 * @author Sergey Lebedev
 * 
 */
public class UpdateContent {
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + UpdateContent.class);

    public static String[] archiveHash;
    public static String[] bootboxMinHash;
    public static String[] bootstrapDatepickerHash;
    public static String[] bootstrapDatepickerRuHash;
    public static String[] bootstrapMinHash;
    public static String[] buttonsHash;
    public static String[] closeHash;
    public static String[] commitHash;
    public static String[] datesHash;
    public static String[] fileuploaderMinHash;
    public static String[] getElementByIDHash;
    //public static String[] jqueryMinHash;
    public static String[] jqueryBlockUIHash;
    public static String[] jqueryMaskedinputHash;
    public static String[] jqueryTextChangeHash;
    public static String[] jqueryCaretHash;
    public static String[] langsHash;
    public static String[] myDragHash;
    public static String[] onloadHash;
    public static String[] operationsHash;
    public static String[] pdHash;
    public static String[] processesHash;
    public static String[] tabsHash;
    public static String[] tasksHash;
    public static String[] usersHash;
    public static String[] nicEditHash;
    
    public static String[] bootstrapHash;
    public static String[] tocHash;
    public static String[] datepickerHash;
    public static String[] fileuploaderHash;
    public static String[] mainHash;
    public static String[] bootstrapResponsiveHash;
    public static String[] bsMultilevelmenuHash;
    public static String[] leftHash;
    public static String[] loginHash;

    private final static String archivePathFile = "script/archive.js";
    private final static String bootboxMinPathFile = "script/bootbox.min.js";
    private final static String bootstrapDatepickerPathFile = "script/bootstrap-datepicker.js";
    private final static String bootstrapDatepickerRuPathFile = "script/bootstrap-datepicker.ru.js";
    private final static String bootstrapMinPathFile = "script/bootstrap.min.js";
    private final static String buttonsPathFile = "script/buttons.js";
    private final static String closePathFile = "script/close.js";
    private final static String commitPathFile = "script/commit.js";
    private final static String datesPathFile = "script/dates.js";
    private final static String fileuploaderMinPathFile = "script/fileuploader.min.js";
    private final static String getElementByIDPathFile = "script/getElementByID.js";
    //private final static String jqueryMinPathFile = "script/jquery-1.8.3.min.js";
    private final static String jqueryBlockUIPathFile = "script/jquery.blockUI.js";
    private final static String jqueryMaskedinputPathFile = "script/jquery.maskedinput-1.2.2.js";
    private final static String jqueryTextChangePathFile = "script/jquery.caret.min.js";
    private final static String jqueryCaretPathFile = "script/jquery.textchange.min.js";
    private final static String langsPathFile = "script/langs.js";
    private final static String myDragPathFile = "script/myDrag.js";
    private final static String onloadPathFile = "script/onload.js";
    private final static String operationsPathFile = "script/operations.js";
    private final static String pdPathFile = "script/password.js";
    private final static String processesPathFile = "script/processes.js";
    private final static String tabsPathFile = "script/tabs.js";
    private final static String tasksPathFile = "script/tasks.js";
    private final static String usersPathFile = "script/users.js";
    public final static String NIC_EDIT_PATH_FILE = "script/nicEdit.min.js";

    private final static String bootstrapPathFile = "Styles/bootstrap.min.css";
    private final static String tocPathFile = "Styles/toc.css";
    private final static String datepickerPathFile = "Styles/datepicker.css";
    private final static String fileuploaderPathFile = "Styles/fileuploader.css";
    private final static String mainPathFile = "Styles/main.css";
    private final static String bootstrapResponsivePathFile = "Styles/bootstrap-responsive.min.css";
    private final static String bsMultilevelmenuPathFile = "Styles/bs_multilevelmenu.css";
    private final static String leftPathFile = "Styles/left.css";
    private final static String loginPathFile = "Styles/login.css";

    /**
     * Обновление хэш сумм файлов.
     */
    public static void update() {
        log.info("Обновление хэш сумм файлов контента");
        if (DIR_JS_CSS != null) {
            int count = DIR_JS_CSS.length;
            archiveHash = new String[count];
            bootboxMinHash = new String[count];
            bootstrapDatepickerHash = new String[count];
            bootstrapDatepickerRuHash = new String[count];
            bootstrapMinHash = new String[count];
            buttonsHash = new String[count];
            closeHash = new String[count];
            commitHash = new String[count];
            datesHash = new String[count];
            fileuploaderMinHash = new String[count];
            getElementByIDHash = new String[count];
            jqueryBlockUIHash = new String[count];
            jqueryMaskedinputHash = new String[count];
            jqueryTextChangeHash = new String[count];
            jqueryCaretHash = new String[count];
            langsHash = new String[count];
            myDragHash = new String[count];
            onloadHash = new String[count];
            operationsHash = new String[count];
            pdHash = new String[count];
            processesHash = new String[count];
            tabsHash = new String[count];
            tasksHash = new String[count];
            usersHash = new String[count];
            nicEditHash = new String[count];

            bootstrapHash = new String[count];
            tocHash = new String[count];
            datepickerHash = new String[count];
            fileuploaderHash = new String[count];
            mainHash = new String[count];
            bootstrapResponsiveHash = new String[count];
            bsMultilevelmenuHash = new String[count];
            leftHash = new String[count];
            loginHash = new String[count];

            for (int i = 0; i < count; ++i) {
                String dir = DIR_JS_CSS[i];
                archiveHash[i] = getHashFile(dir + archivePathFile);
                bootboxMinHash[i] = getHashFile(dir + bootboxMinPathFile);
                bootstrapDatepickerHash[i] = getHashFile(dir + bootstrapDatepickerPathFile);
                bootstrapDatepickerRuHash[i] = getHashFile(dir + bootstrapDatepickerRuPathFile);
                bootstrapMinHash[i] = getHashFile(dir + bootstrapMinPathFile);
                buttonsHash[i] = getHashFile(dir + buttonsPathFile);
                closeHash[i] = getHashFile(dir + closePathFile);
                commitHash[i] = getHashFile(dir + commitPathFile);
                datesHash[i] = getHashFile(dir + datesPathFile);
                fileuploaderMinHash[i] = getHashFile(dir + fileuploaderMinPathFile);
                getElementByIDHash[i] = getHashFile(dir + getElementByIDPathFile);
                //jqueryMinHash[i] = getHashFile(dir + jqueryMinPathFile);
                jqueryBlockUIHash[i] = getHashFile(dir + jqueryBlockUIPathFile);
                jqueryMaskedinputHash[i] = getHashFile(dir + jqueryMaskedinputPathFile);
                jqueryTextChangeHash[i] = getHashFile(dir + jqueryTextChangePathFile);
                jqueryCaretHash[i] = getHashFile(dir + jqueryCaretPathFile);
                langsHash[i] = getHashFile(dir + langsPathFile);
                myDragHash[i] = getHashFile(dir + myDragPathFile);
                onloadHash[i] = getHashFile(dir + onloadPathFile);
                operationsHash[i] = getHashFile(dir + operationsPathFile);
                pdHash[i] = getHashFile(dir + pdPathFile);
                processesHash[i] = getHashFile(dir + processesPathFile);
                tabsHash[i] = getHashFile(dir + tabsPathFile);
                tasksHash[i] = getHashFile(dir + tasksPathFile);
                usersHash[i] = getHashFile(dir + usersPathFile);
                nicEditHash[i] = getHashFile(dir + NIC_EDIT_PATH_FILE);

                bootstrapHash[i] = getHashFile(dir + bootstrapPathFile);
                tocHash[i] = getHashFile(dir + tocPathFile);
                datepickerHash[i] = getHashFile(dir + datepickerPathFile);
                fileuploaderHash[i] = getHashFile(dir + fileuploaderPathFile);
                mainHash[i] = getHashFile(dir + mainPathFile);
                bootstrapResponsiveHash[i] = getHashFile(dir + bootstrapResponsivePathFile);
                bsMultilevelmenuHash[i] = getHashFile(dir + bsMultilevelmenuPathFile);
                leftHash[i] = getHashFile(dir + leftPathFile);
                loginHash[i] = getHashFile(dir + loginPathFile);
            }
        }
        log.info("Обновление хэш сумм завершено!");
    }

    /**
     * Получить хэш сумму файла.
     *
     * @param name имя файла.
     * @return хэш сумма.
     */
    private static String getHashFile(String name) {
    	try {
            byte[] fileBArray = Funcs.read(name);
            if (fileBArray == null) throw new NullPointerException();
            return getHash(fileBArray);
        } catch (NullPointerException e) {
            log.info("Не найден файл: " + name);
        } catch (IOException e) {
            log.info("Не найден файл: " + name);
        }
        return null;
    }

}
