package kz.tamur.web.common.webgui;

import java.awt.Dimension;
import java.util.ResourceBundle;

import org.jdom.Element;

import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.web.common.CommonHelper;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.controller.WebController;

/**
 * Класс реализует простой компонент-переключатель целочисленных значений
 */
public class WebSpinner extends WebToolbar implements JSONComponent {

    /** Ресурс */
    ResourceBundle res = CommonHelper.RESOURCE_RU;

    /** Метка отображения значения компонента. */
    private WebLabel valueView = new WebLabel(null, Mode.RUNTIME, null, null);

    /** Кнопка увеличения значения. */
    private WebButton upBtn = new WebButton(WebController.APP_PATH + "/images/up.png", res.getString("increase"), null, Mode.RUNTIME, null, null);

    /** Кнопка уменьшения значения. */
    private WebButton downBtn = new WebButton(WebController.APP_PATH + "/images/down.png", res.getString("reduce"), null, Mode.RUNTIME, null, null);

    /** Максимальное значение компонента. */
    private int maxValue;

    /** Минимальное значение компонента. */
    private int minValue;

    /** Шаг увеличения/уменьшения значения. */
    private int step;

    /** Стартовое значение компонента. */
    private int initValue;

    /** Текущее значение компонента. */
    private int value = -1;

    /** Родительский компонент, которому будет послано событие о смене значения. */
    WebComponent comp;

    /**
     * Создание нового WebSpinner.
     * 
     * @param comp
     *            родиельский компонент
     * @param maxValue
     *            Максимальное значение компонента.
     * @param minValue
     *            Минимальное значение компонента.
     * @param step
     *            Шаг увеличения/уменьшения значения.
     * @param initValue
     *            Стартовое значение компонента.
     */
    public WebSpinner(WebComponent comp, int maxValue, int minValue, int step, int initValue, Element xml, int mode, OrFrame frame, String id) {
        super(xml, mode, frame, id);
        this.comp = comp;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.step = step;
        this.initValue = initValue;
        initialize();
    }

    /**
     * Создание нового WebSpinner.
     * 
     * @param comp
     *            родиельский компонент
     */
    public WebSpinner(WebComponent comp, Element xml, int mode, OrFrame frame, String id) {
        this(comp, 100, 0, 1, 0, xml, mode, frame, id);
    }

    /**
     * Инициализация компонента
     * 
     * @param comp
     *            the comp
     * @param maxValue
     *            the max value
     * @param minValue
     *            the min value
     * @param step
     *            the step
     * @param initValue
     *            the init value
     */
    private void initialize() {
        valueView.setText(initValue + "");
        kz.tamur.comps.Utils.setAllSize(upBtn, new Dimension(12, 12));
        kz.tamur.comps.Utils.setAllSize(downBtn, new Dimension(12, 12));

        upBtn.setPadding(new Dimension(1, 1));
        downBtn.setPadding(new Dimension(1, 1));
        upBtn.setConstraintsIndent(Constants.INSETS_0);
        downBtn.setConstraintsIndent(Constants.INSETS_0);

        add(valueView);
        add(upBtn);
        add(downBtn);
    }

    /**
     * Получить value.
     * 
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Установить value.
     * 
     * @param value
     *            the new value
     */
    public void setValue(Object value) {
    	int val = (value instanceof Number) ? ((Number)value).intValue() : 0;
        this.value = val;
        valueView.setText(String.valueOf(val));
        sendChangeProperty("value", val);
    }

    /**
     * Получить max value.
     * 
     * @return the max value
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Установить max value.
     * 
     * @param maxValue
     *            the new max value
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        sendChangeProperty("maxValue", maxValue);
    }

    /**
     * Получить min value.
     * 
     * @return the min value
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Установить min value.
     * 
     * @param minValue
     *            the new min value
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
        sendChangeProperty("minValue", minValue);
    }

    /**
     * Получить step.
     * 
     * @return the step
     */
    public int getStep() {
        return step;
    }

    /**
     * Установить шаг.
     * 
     * @param step
     *            новый шаг смены значений
     */
    public void setStep(int step) {
        this.step = step;
        sendChangeProperty("step", step);
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        obj.add("pr", property);
        property.add("valueView", valueView.putJSON(false));
        property.add("upBtn", upBtn.putJSON(false));
        property.add("downBtn", downBtn.putJSON(false));
        sendChange(obj, isSend);
        return obj;
    }
}
