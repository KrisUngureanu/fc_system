package kz.tamur.rt;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.models.ColorAct;
import kz.tamur.guidesigner.ConfigEditor;

import static kz.tamur.rt.Utils.*;
import static kz.tamur.comps.Constants.*;

/**
 * Класс, реализующий механизм настройки пользовательской конфигурации
 */
public class LocalEditor extends ConfigEditor {

    private static Kernel krn = Kernel.instance();

    /** KRN объект - текущий пользователь. */
    protected KrnObject krnObj;

    /** KRN объект класс локальных настроек */
    private KrnObject configObj;

    /**
     * Конструктор класса
     */
    public LocalEditor() {
        // вызвать конструктор суперкласса с конфигурацией текущего пользователя
        super(krn.getUser().config, false);
        // скрыть не нужные настройки от пользователя
        leftPanel.setVisible(false);
        gradientMenuPanelPane.setVisible(false);
        transparentMainCheck.setVisible(false);
        transparentDialogCheck.setVisible(false);
        transparentCellTablePane.setVisible(false);
        transparentBackTabTitlePane.setVisible(false);
        transparentSelectedTabTitlePane.setVisible(false);
        colorMainPane.setVisible(false);
        colorHeaderTablePane.setVisible(false);
        colorTabTitlePane.setVisible(false);
        colorBackTabTitlePane.setVisible(false);
        colorFontTabTitlePane.setVisible(false);
        colorFontBackTabTitlePane.setVisible(false);
        classControlPane.setVisible(false);
        // получить KRN объект текущего пользователя.
        krnObj = krn.getUser().object;
        configObj = krn.getUser().getConfigObj();
    }

    /**
     * Сохранение всех настроек пользователя
     */
    public void setAllConfig() {
        try {
            // если в конфигурации пользователя были измнения - сохранить данные в класс пользователя
            if (isChangeGradientMainFrame || isChangeGradientControlPanel || isChangeGradientMenuPanel || isChangeTransparentMain
                    || isChangeTransparentDialog || isChangeColorMain || isTransparentCellTable || isColorHeaderTable
                    || isColorTabTitle || isColorBackTabTitle || isColorFontTabTitle || isColorFontBackTabTitle
                    || isTransparentBackTabTitle || isTransparentSelectedTabTitle || isGradientFieldNOFLC || isBlueSysColor
                    || isDarkShadowSysColor || isMidSysColor || isLightYellowColor || isRedColor || isLightRedColor
                    || isLightGreenColor || isShadowYellowColor || isSysColor || isLightSysColor || isDefaultFontColor
                    || isSilverColor || isShadowsGreyColor || isKeywordColor || isVariableColor || isClientVariableColor
                    || isCommentColor) {
                if (isChangeGradientMainFrame) {
                    isChangeGradientMainFrame = false;
                    conf.setGradientMainFrame(gradientMainFrame);
                    krn.setString(configObj.id, configObj.classId, ATTR_GRADIENT_MAIN_FRAME, 0, 0,
                            gradientMainFrame == null ? null : gradientMainFrame.toString(), 0);
                }
                if (isChangeGradientControlPanel) {
                    isChangeGradientControlPanel = false;
                    conf.setGradientControlPanel(gradientControlPanel);
                    krn.setString(configObj.id, configObj.classId, ATTR_GRADIENT_CONTROL_PANEL, 0, 0,
                            gradientControlPanel == null ? null : gradientControlPanel.toString(), 0);
                }
                if (isChangeGradientMenuPanel) {
                    isChangeGradientMenuPanel = false;
                    conf.setGradientMenuPanel(gradientMenuPanel);
                    krn.setString(configObj.id, configObj.classId, ATTR_GRADIENT_MENU_PANEL, 0, 0,
                            gradientMenuPanel == null ? null : gradientMenuPanel.toString(), 0);
                }
                if (isChangeTransparentMain) {
                    isChangeTransparentMain = false;
                    conf.setTransparentMain(transparentMain);
                    krn.setLong(configObj.id, configObj.classId, ATTR_TRANSPARENT_MAIN, 0, Utils.toLong(transparentMain), 0);
                }
                if (isChangeTransparentDialog) {
                    isChangeTransparentDialog = false;
                    conf.setTransparentDialog(transparentDialog);
                    krn.setLong(configObj.id, configObj.classId, ATTR_TRANSPARENT_DIALOG, 0, Utils.toLong(transparentDialog), 0);
                }
                if (isTransparentCellTable) {
                    isTransparentCellTable = false;
                    conf.setTransparentCellTable(transparentCellTable);
                    krn.setLong(configObj.id, configObj.classId, ATTR_TRANSPARENT_CELL_TABLE, 0, transparentCellTable, 0);
                }
                if (isColorHeaderTable) {
                    isColorHeaderTable = false;
                    conf.setColorHeaderTable(colorHeaderTable);
                    krn.setString(configObj.id, configObj.classId, ATTR_COLOR_HEADER_TABLE, 0, 0, colorHeaderTable == null ? null
                            : colorHeaderTable.getRGB() + "", 0);
                }
                if (isColorTabTitle) {
                    isColorTabTitle = false;
                    conf.setColorTabTitle(colorTabTitle);
                    krn.setString(configObj.id, configObj.classId, ATTR_COLOR_TAB_TITLE, 0, 0, colorTabTitle == null ? null
                            : colorTabTitle.getRGB() + "", 0);
                }
                if (isColorBackTabTitle) {
                    isColorBackTabTitle = false;
                    conf.setColorBackTabTitle(colorBackTabTitle);
                    krn.setString(configObj.id, configObj.classId, ATTR_COLOR_BACK_TAB_TITLE, 0, 0,
                            colorBackTabTitle == null ? null : colorBackTabTitle.getRGB() + "", 0);
                }
                if (isColorFontTabTitle) {
                    isColorFontTabTitle = false;
                    conf.setColorFontTabTitle(colorFontTabTitle);
                    krn.setString(configObj.id, configObj.classId, ATTR_COLOR_FONT_TAB_TITLE, 0, 0,
                            colorFontTabTitle == null ? null : colorFontTabTitle.getRGB() + "", 0);
                }
                if (isColorFontBackTabTitle) {
                    isColorFontBackTabTitle = false;
                    conf.setColorFontBackTabTitle(colorFontBackTabTitle);
                    krn.setString(configObj.id, configObj.classId, ATTR_COLOR_FONT_BACK_TAB_TITLE, 0, 0,
                            colorFontBackTabTitle == null ? null : colorFontBackTabTitle.getRGB() + "", 0);
                }
                if (isTransparentBackTabTitle) {
                    isTransparentBackTabTitle = false;
                    conf.setTransparentBackTabTitle(transparentBackTabTitle);
                    krn.setLong(configObj.id, configObj.classId, ATTR_TRANSPARENT_BACK_TAB_TITLE, 0, transparentBackTabTitle, 0);
                }
                if (isTransparentSelectedTabTitle) {
                    isTransparentSelectedTabTitle = false;
                    conf.setTransparentSelectedTabTitle(transparentSelectedTabTitle);
                    krn.setLong(configObj.id, configObj.classId, ATTR_TRANSPARENT_SELECTED_TAB_TITLE, 0,
                            transparentSelectedTabTitle, 0);
                }
                if (isGradientFieldNOFLC) {
                    isGradientFieldNOFLC = false;
                    conf.setGradientFieldNOFLC(gradientFieldNOFLC);
                    krn.setString(configObj.id, configObj.classId, ATTR_GRADIENT_FIELD_NO_FLC, 0, 0,
                            gradientFieldNOFLC == null ? null : gradientFieldNOFLC.toString(), 0);
                }
                if (isChangeColorMain) {
                    isChangeColorMain = false;
                    conf.setColorMain(new ColorAct(mainColor, colorMainCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_COLOR_MAIN, 0, 0, conf.getColorMain() == null ? null : conf.getColorMain().getRGBAct(), 0);
                    setMainColor(mainColor);
                }
                if (isBlueSysColor) {
                    isBlueSysColor = false;
                    conf.setBlueSysColor(new ColorAct(blueSysColor, blueSysColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_BLUE_SYS_COLOR, 0, 0,
                            conf.getBlueSysColor() == null ? null : conf.getBlueSysColor().getRGBAct(), 0);
                    setBlueSysColor(blueSysColor);
                }
                if (isDarkShadowSysColor) {
                    isDarkShadowSysColor = false;
                    conf.setDarkShadowSysColor(new ColorAct(darkShadowSysColor, darkShadowSysColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_DARK_SHADOW_SYS_COLOR, 0, 0,
                            conf.getDarkShadowSysColor() == null ? null : conf.getDarkShadowSysColor().getRGBAct(), 0);
                    setDarkShadowSysColor(darkShadowSysColor);
                }
                if (isMidSysColor) {
                    isMidSysColor = false;
                    conf.setMidSysColor(new ColorAct(midSysColor, midSysColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_MID_SYS_COLOR, 0, 0, conf.getMidSysColor() == null ? null
                            : conf.getMidSysColor().getRGBAct(), 0);
                    setMidSysColor(midSysColor);
                }
                if (isLightYellowColor) {
                    isLightYellowColor = false;
                    conf.setLightYellowColor(new ColorAct(lightYellowColor, lightYellowColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_LIGHT_YELLOW_COLOR, 0, 0,
                            conf.getLightYellowColor() == null ? null : conf.getLightYellowColor().getRGBAct(), 0);
                    setLightYellowColor(lightYellowColor);
                }
                if (isRedColor) {
                    isRedColor = false;
                    conf.setRedColor(new ColorAct(redColor, redColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_RED_COLOR, 0, 0, conf.getRedColor() == null ? null : conf
                            .getRedColor().getRGBAct(), 0);
                    setRedColor(redColor);
                }
                if (isLightRedColor) {
                    isLightRedColor = false;
                    conf.setLightRedColor(new ColorAct(lightRedColor, lightRedColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_LIGHT_RED_COLOR, 0, 0,
                            conf.getLightRedColor() == null ? null : conf.getLightRedColor().getRGBAct(), 0);
                    setLightRedColor(lightRedColor);
                }
                if (isLightGreenColor) {
                    isLightGreenColor = false;
                    conf.setLightGreenColor(new ColorAct(lightGreenColor, lightGreenColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_LIGHT_GREEN_COLOR, 0, 0,
                            conf.getLightGreenColor() == null ? null : conf.getLightGreenColor().getRGBAct(), 0);
                    setLightGreenColor(lightGreenColor);
                }
                if (isShadowYellowColor) {
                    isShadowYellowColor = false;
                    conf.setShadowYellowColor(new ColorAct(shadowYellowColor, shadowYellowColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_SHADOW_YELLOW_COLOR, 0, 0,
                            conf.getShadowYellowColor() == null ? null : conf.getShadowYellowColor().getRGBAct(), 0);
                    setShadowYellowColor(shadowYellowColor);
                }
                if (isSysColor) {
                    isSysColor = false;
                    conf.setSysColor(new ColorAct(sysColor, sysColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_SYS_COLOR, 0, 0, conf.getSysColor() == null ? null : conf
                            .getSysColor().getRGBAct(), 0);
                    setSysColor(sysColor);
                }
                if (isLightSysColor) {
                    isLightSysColor = false;
                    conf.setLightSysColor(new ColorAct(lightSysColor, lightSysColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_LIGHT_SYS_COLOR, 0, 0,
                            conf.getLightSysColor() == null ? null : conf.getLightSysColor().getRGBAct(), 0);
                    setLightSysColor(lightSysColor);
                }
                if (isDefaultFontColor) {
                    isDefaultFontColor = false;
                    conf.setDefaultFontColor(new ColorAct(defaultFontColor, defaultFontColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_DEFAULT_FONT_COLOR, 0, 0,
                            conf.getDefaultFontColor() == null ? null : conf.getDefaultFontColor().getRGBAct(), 0);
                    setDefaultFontColor(defaultFontColor);
                }
                if (isSilverColor) {
                    isSilverColor = false;
                    conf.setSilverColor(new ColorAct(silverColor, silverColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_SILVER_COLOR, 0, 0, conf.getSilverColor() == null ? null
                            : conf.getSilverColor().getRGBAct(), 0);
                    setSilverColor(silverColor);
                }
                if (isShadowsGreyColor) {
                    isShadowsGreyColor = false;
                    conf.setShadowsGreyColor(new ColorAct(shadowsGreyColor, shadowsGreyColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_SHADOWS_GREY_COLOR, 0, 0,
                            conf.getShadowsGreyColor() == null ? null : conf.getShadowsGreyColor().getRGBAct(), 0);
                    setShadowsGreyColor(shadowsGreyColor);
                }
                if (isKeywordColor) {
                    isKeywordColor = false;
                    conf.setKeywordColor(new ColorAct(keywordColor, keywordColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_KEYWORD_COLOR, 0, 0,
                            conf.getKeywordColor() == null ? null : conf.getKeywordColor().getRGBAct(), 0);
                    setKeywordColor(keywordColor);
                }
                if (isVariableColor) {
                    isVariableColor = false;
                    conf.setVariableColor(new ColorAct(variableColor, variableColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_VARIABLE_COLOR, 0, 0,
                            conf.getVariableColor() == null ? null : conf.getVariableColor().getRGBAct(), 0);
                    setVariableColor(variableColor);
                }
                if (isClientVariableColor) {
                    isClientVariableColor = false;
                    conf.setClientVariableColor(new ColorAct(clientVariableColor, clientVariableColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_CLIENT_VARIABLE_COLOR, 0, 0,
                            conf.getClientVariableColor() == null ? null : conf.getClientVariableColor().getRGBAct(), 0);
                    setClientVariableColor(clientVariableColor);
                }
                if (isCommentColor) {
                    isCommentColor = false;
                    conf.setCommentColor(new ColorAct(commentColor, commentColorCheck.isSelected()));
                    krn.setString(configObj.id, configObj.classId, ATTR_COMMENT_COLOR, 0, 0,
                            conf.getCommentColor() == null ? null : conf.getCommentColor().getRGBAct(), 0);
                    setCommentColor(commentColor);
                }

                krn.setObject(krnObj.id, krnObj.classId, "config", 0, configObj.id, 0, false);
                krn.updateUser(krnObj, toString());
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

}
