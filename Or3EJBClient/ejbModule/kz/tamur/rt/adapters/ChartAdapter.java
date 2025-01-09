package kz.tamur.rt.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import kz.tamur.comps.OrChartPanel;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.orlang.ClientOrLang;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * @author Sergey Lebedev
 * 
 *         Адаптер под компонент вывода диаграмм {@link OrChartPanel}
 */
public class ChartAdapter extends ComponentAdapter {

    private OrChartPanel chart;
    private boolean isEn = true;
    private KrnObject data[] = null;
    private ASTStart dataEvaluate;

    /**
     * Конструктор класса
     * 
     * @param frame
     * @param chart
     * @param isEditor
     * @throws KrnException
     */
    public ChartAdapter(OrFrame frame, OrChartPanel chart, boolean isEditor) throws KrnException {
        super(frame, chart, isEditor);
        this.chart = chart;
        loadReports();
        PropertyNode pov = chart.getProperties().getChild("pov");
        PropertyNode pn = pov.getChild("activity").getChild("enabled");
        PropertyValue pv = chart.getPropertyValue(pn);
        isEn = (pv.isNull()) ? ((Boolean) pn.getDefaultValue()).booleanValue() : pv.booleanValue();
        setEnabled(isEn);
    }

    /**
     * Загрузка отчётов ??
     */
    private void loadReports() {
        PropertyNode prop = chart.getProperties();
        PropertyNode rprop = prop.getChild("reports");
        PropertyValue pv = chart.getPropertyValue(rprop);
        if (!pv.isNull()) {
            Object value = pv.objectValue();
            if (value instanceof ReportRecord) {
                frame.setRootReport((ReportRecord) value);
                // ReportPrinter rp = new ReportPrinterAdapter(frame, panel,
                // (ReportRecord) value);
                // frame.addReport(rp);
            } else if (value instanceof ReportRecord[]) {
                ReportRecord[] reports = (ReportRecord[]) value;
                for (int i = 0; i < reports.length; i++) {
                    ReportPrinter rp = new ReportPrinterAdapter(frame, chart, reports[i]);
                    frame.addReport(rp);
                }
            }
        }
    }

    public void clear() {
    }

    public void setData(KrnObject[] data) {
        this.data = data;
    }

    /**
     * Метод Возвращает {@link OrChartPanel} для данного адаптера
     * 
     * @return
     */
    public OrChartPanel getChart() {
        return chart;
    }

    /**
     * Получает данные из указанного атрибута
     * 
     * @param nameAttr
     *            имя атрибута
     * @param isInit
     *            инициализация?
     * @return
     */
    public KrnObject[] getData(String nameAttr, boolean isInit) {
        PropertyValue pv = null;
        if (isInit) {
            try {
                pv = chart.getPropertyValue(chart.getNodeChart().getChild(nameAttr));
            } catch (NullPointerException e) {
                System.out.println(e);
            }
            String expr = pv.isNull() ? "" : pv.stringValue().trim();
            if (expr.length() > 0) {
                dataEvaluate = OrLang.createStaticTemplate(expr);
            }
        } else if (dataEvaluate != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            try {
                Map<String, Object> vc = new HashMap<String, Object>();
                boolean calcOwner = OrCalcRef.setCalculations();
                orlang.evaluate(dataEvaluate, vc, this, new Stack<String>());
                if (calcOwner) {
                    OrCalcRef.makeCalculations();
                }
                Object rez = vc.get("RETURN");
                if (rez instanceof KrnObject[]) {
                    data = (KrnObject[]) rez;
                } else if (rez instanceof KrnObject) {
                    data = new KrnObject[] { (KrnObject) rez };
                } else if (rez instanceof ArrayList<?>) {
                    ArrayList<KrnObject> list = (ArrayList<KrnObject>)rez; 
                    data = new KrnObject[list.size()];
                    int i = 0;
                    for (KrnObject elem : list){
                        data[i++] = elem;   
                    }
                }

            } catch (Exception e) {
                Util.showErrorMessage(chart, e.getMessage(), "Невозможно получить значение");
            }
        }

        return data;
    }

    /**
     * Метод Возвращает систояние активности компонента {@link OrChartPanel}
     * 
     * @return
     */
    public boolean isEnabled() {
        return isEn;
    }
}
