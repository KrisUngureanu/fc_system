package kz.tamur.comps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.util.ResourceBundle;

import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import kz.tamur.comps.models.GradientColor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.SlidingGanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Hour;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.Layer;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Класс реализует начинку компонента {@link kz.tamur.comps.OrChartPanel} В классе сделана реализация всех видев графиков и диаграм что необходимо вывести пользователю
 * 
 * @author Sergey Lebedev
 */
public class OrCharts {
    // заголовок диаграммы
    public String title;
    // левая вертикальная метка
    private String leftVerticlLabel = "";
    // верхняя горизонтальная метка
    private String upHorizontalLabel = "";
    // главная панель с графиком
    private OrChartPanel chartMain;
    // набор данных
    private IntervalCategoryDataset dataSetInterval = null;
    // набор данных
    private GanttCategoryDataset GantDataSet = null;
    private SlidingGanttCategoryDataset dataSetSliding = null;
    // коллекция задач
    private TaskSeriesCollection collection;
    // Компонент, в котором конструируется диаграмма
    private JFreeChart chart = null;
    // участок на диаграмме с категориями
    CategoryPlot plot = null;
    // Панель с диаграммой, именно она является выходным результатом класса
    private ChartPanel chartPanel = null;
    // Режим исполнения диаграммы
    private int mode;

    private GridBagLayout gbl = new GridBagLayout();
    // скроллбар для обновления значений набора данных
    private Dimension preferredSize;
    private int countVisibleTask;
    private ResourceBundle resource = ResourceBundle.getBundle(Constants.NAME_RESOURCES, kz.tamur.rt.Utils.getLocale());
    final private JScrollBar scroller = new JScrollBar(1, 0, 15, 0, 50);

    /**
     * Конструктор класса
     * 
     * @param chartMain
     *            панель на которой будет размещён график/диаграмма
     * @param title
     *            заголовок диаграммы
     * @param mode
     *            тип графика/диаграммы который необходимо построить
     */
    public OrCharts(OrChartPanel chartMain, String title, int mode) {
        this.title = title;
        this.mode = mode;
        this.chartMain = chartMain;
    }

    /**
     * Инициализация диаграммы Ганта
     * Перед вызовом должен быть собран набор данных
     * 
     * @param type
     *            тип графика
     */
    public void initGantt(int type) {
        if (mode != Mode.RUNTIME) {
            switch (type) {
            case Constants.GANTT_0:// Простая диаграмма Гантта
            case Constants.GANTT_1: // Диаграмма Гантта с несколькими сериями
            case Constants.SLIDING_GANTT_DATASET_1: // Гант с прокруткой
                initGanttTest(type);
                break;
            }
        } else {
            CategoryItemRenderer renderer;
            switch (type) {
            case Constants.GANTT_0:// Простая диаграмма Гантта
                // создание диаграммы
                createGanttChart(GantDataSet, false);
                // установка синего цвета для выводимых на диаграмме графиков
                plot = (CategoryPlot) chart.getPlot();
                plot.setNoDataMessage(resource.getString("notData"));
                renderer = plot.getRenderer();
                renderer.setSeriesPaint(0, Color.blue);
                chartPanel = new ChartPanel(chart, true);
                initChartPanel();
                break;
            case Constants.GANTT_1: // Диаграмма Гантта с несколькими сериями
                // создание диаграммы
                createGanttChart(GantDataSet, true);
                // установка синего цвета для выводимых на диаграмме графиков
                plot = (CategoryPlot) chart.getPlot();
                plot.setNoDataMessage(resource.getString("notData"));
                renderer = plot.getRenderer();
                chartPanel = new ChartPanel(chart, true);
                initChartPanel();
                break;
            case Constants.SLIDING_GANTT_DATASET_1: // Гант с прокруткой
                if (GantDataSet==null) {
                    return;
                }
                dataSetSliding = new SlidingGanttCategoryDataset(GantDataSet, 0, countVisibleTask);
                createSlidingGanttChart(dataSetSliding, true);
                chartPanel = new ChartPanel(chart, true);
                scroller.getModel().addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        if (dataSetSliding.getColumnCount() != 0 && scroller.getValue()<=dataSetSliding.getColumnCount()-1) {
                            dataSetSliding.setFirstCategoryIndex(scroller.getValue());
                        }
                    }
                });
                initChartPanel();
                break;
            }

        }
    }

    /**
     * Создание диаграммы Гантта с предопределёным набором данных
     * Необходимо для предварительного отображения диаграммы в конструкторе
     * 
     * @param type
     *            тип диаграммы
     */
    public void initGanttTest(int type) {
        createGanttChart(createDatasetTest(type), Constants.GANTT_1 == type);
        chartPanel = new ChartPanel(chart);
        plot = (CategoryPlot) chart.getPlot();
        initChartPanel();
    }

    /**
     * Инициализация панели с графиком
     */
    public void initChartPanel() {
        GridBagConstraints c = new GridBagConstraints();
        // инициализация параметров
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 0;
        c.gridwidth = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = Constants.INSETS_0;
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        gbl.setConstraints(chartPanel, c);
    }

    public void setConstraints(GridBagConstraints c) {
        gbl.setConstraints(chartPanel, c);
    }

    /**
     * Создание набора данных
     * 
     * Используется в @see kz.tamur.comps.OrCharts#initGantt
     * 
     * @param nameTaskSeries
     *            наименование серии задач
     * 
     * @param IDs
     *            список KrnObject, в него передаётся набор ID объектов из интерфейса по атрибутам которых необходимо строить диаграмму
     * @param taskPath
     *            путь к атрибуту обекта из <code>IDs</code> из которого будет дёргаться название задачи
     * @param startPath
     *            путь к атрибуту обекта из <code>IDs</code> из которого будет дёргаться дата начала задачи
     * @param endPath
     *            путь к атрибуту обекта из <code>IDs</code> из которого будет дёргаться дата завершения задачи
     */
    public void createGanttDataset(String nameTaskSeries, KrnObject[] IDs, String taskPath, String startPath, String endPath) {
        if (mode != Mode.RUNTIME || IDs == null || taskPath.isEmpty() || startPath.isEmpty() || endPath.isEmpty()) {
            return;
        }
        TaskSeries s = new TaskSeries(nameTaskSeries);
        collection = new TaskSeriesCollection();
        String task = null;
        Date start = null;
        Date end = null;
        int i = 0;
        for (KrnObject obj : IDs) {
            try {
                task = (i) + ". " + obj.getAttr(taskPath);
                start = (Date) obj.getAttr(startPath);
                end = (Date) obj.getAttr(endPath);
                // добавить один день к дате
                end = addDays(end, 1);
            } catch (KrnException e) {
                e.printStackTrace();
            }
            if (task != null && start != null && end != null) {
                s.add(new Task(task, new SimpleTimePeriod(start, end)));
                ++i;
            }
        }
        collection.add(s);

        TaskSeriesCollection taskseriescollection = new TaskSeriesCollection();
        taskseriescollection.add(s);
        GantDataSet = taskseriescollection;
    }

    /**
     * Создание набора данных для диаграммы Гантта с несколькими сериями задач
     * 
     * @param IDs
     *            идентификаторы объектов для вывода на диаграмме
     * @param taskPath
     *            путь к атрибуту обекта из <code>IDs</code> из которого будет дёргаться название задачи
     * @param startPath
     *            путь к атрибуту обекта из <code>IDs</code> из которого будет дёргаться дата начала задачи
     * @param endPath
     *            путь к атрибуту обекта из <code>IDs</code> из которого будет дёргаться дата завершения задачи
     * @param nameAttrSeries601
     *            имя (или имена, через запятую) атрибута объекта, которое будет использоваться для извлечения наименования серии задач
     * @param count
     *            количество серий в диаграмме
     * @param taskSeries
     *            путь к атрибуту обекта из <code>IDs</code> из которого будет дёргаться объект для анализа на соотеветствие той или иной серии задач
     * @throws KrnException
     */
    public void createGantt1Dataset(KrnObject[] IDs, String taskPath, String startPath, String endPath, String nameAttrSeries601,
            int count, String taskSeries) throws KrnException {
        if (mode != Mode.RUNTIME || IDs == null || taskPath.isEmpty() || startPath.isEmpty() || endPath.isEmpty()) {
            return;
        }
        // язык интерфейса
        final long lid = com.cifs.or2.client.Utils.getInterfaceLangId();
        // идентификаторы объектов для идентификации серий
        String[][] nameSeries = new String[count][];
        // массив максимальных индексов для каждой серии (используется принумерации задач внутри каждой серии)
        int[] indx = new int[count];
        // серии задач для диаграммы
        TaskSeries[] series = new TaskSeries[count];
        // временное хранение значений PropertyValue
        PropertyValue pv;
        // временное хранение заголовков серий
        String titleSeries;
        Kernel krn = Kernel.instance();

        for (int i = 0, j = 1; i < count; ++i, ++j) {
            // получение объектов для идентификации данной серии задач
            pv = chartMain.getPropertyValue(chartMain.getNodeChart().getChild("attr").getChild("series")
                    .getChild("series" + j + "-601"));
            String ids = (!pv.isNull()) ? pv.stringValue() : "";
            ids = ids.replaceAll("\\d+\\.", "");
            nameSeries[i] = (ids.contains(",")) ? ids.split(",") : new String[] { ids };
            titleSeries = "";
            // получение заголовка серии
            for (String s : nameSeries[i]) {
                KrnObject obj = krn.getObjectById(Long.parseLong(s), 0);
                // множественное задание
                if (nameAttrSeries601.contains(",")) {
                    String[] attrS = nameAttrSeries601.split(",");
                    for (String attr : attrS) {
                        try {
                            titleSeries = krn.getStrings(obj, attr, lid, 0)[0];
                            break;
                        } catch (Exception e) {
                            titleSeries = "Не определено";
                        }
                    }
                } else {
                    try {
                        titleSeries = krn.getStrings(obj, nameAttrSeries601, lid, 0)[0];
                    } catch (Exception e) {
                        titleSeries = "Не определено";
                    }

                }
            }
            // создание новой серии задач
            series[i] = new TaskSeries((i + 1) + " " + titleSeries);
            // попутно обнулить элемент массива с индексами
            indx[i] = 0;
        }

        // создание коллекции задач
        collection = new TaskSeriesCollection();
        String task = null;
        KrnObject objSeries = null;
        Date start = null;
        Date end = null;

        // сравнение атрибутов выбранных фильтром объектов, проверка их на соответствие критериями добавление в соответствущую серию
        for (KrnObject obj : IDs) {
            // объект который необходимо сравнить на совпадение с ID заданными в атрибутах
            objSeries = (KrnObject) obj.getAttr(taskSeries);
            // количество объектов, соответствие которым позволяет отнести обект к некторой серии задач
            int inCount;
            // перебор всех атрибутов задач
            for (int j = 0; j < count; ++j) {
                inCount = nameSeries[j].length;
                for (int i = 0; i < inCount; ++i) {
                    // если атрибут серии совпадает с атрибутом объекта, то создать новую задачу к текущей серии задач
                    if (nameSeries[j][i].equals(objSeries.id + "")) {
                        task = ++indx[j] + ". " + obj.getAttr(taskPath);
                        start = (Date) obj.getAttr(startPath);
                        end = (Date) obj.getAttr(endPath);
                        // добавить один день к дате
                        end = addDays(end, 1);
                        if (task != null && start != null && end != null && (start.getTime() <= end.getTime())) {
                            series[j].add(new Task(task, new SimpleTimePeriod(start, end)));
                        }
                        break;
                    }
                }
            }
        }
        for (TaskSeries s : series) {
            collection.add(s);
        }
        GantDataSet = collection;
    }

    public void createSlidingGanttDataset(KrnObject[] IDs, String taskPath, String startPath, String endPath,
            String nameAttrSeries601, int count, String taskSeries, int countVisibleTask) throws KrnException {
        this.countVisibleTask = countVisibleTask;
        createGantt1Dataset(IDs, taskPath, startPath, endPath, nameAttrSeries601, count, taskSeries);

    }

    /**
     * Создание тестового набора данных
     * 
     * @param type
     *            тип диаграммы
     * @return Набор данных
     */
    public static IntervalCategoryDataset createDatasetTest(int type) {
        final TaskSeries s1 = new TaskSeries("Запланированные");
        final TaskSeries s2 = new TaskSeries("Фактические");
        final TaskSeriesCollection collection = new TaskSeriesCollection();
        s1.add(new Task("Описание предложений",
                new SimpleTimePeriod(date(1, Calendar.APRIL, 2001), date(5, Calendar.APRIL, 2001))));
        s1.add(new Task("Получени требований", new SimpleTimePeriod(date(9, Calendar.APRIL, 2001), date(9, Calendar.APRIL, 2001))));
        s1.add(new Task("Анализ требований", new SimpleTimePeriod(date(10, Calendar.APRIL, 2001), date(5, Calendar.MAY, 2001))));
        s1.add(new Task("Проектирование классов", new SimpleTimePeriod(date(6, Calendar.MAY, 2001), date(30, Calendar.MAY, 2001))));
        s1.add(new Task("Проектирование интерфейсов", new SimpleTimePeriod(date(2, Calendar.JUNE, 2001), date(2, Calendar.JUNE,
                2001))));
        s1.add(new Task("Разработка(Альфа)", new SimpleTimePeriod(date(3, Calendar.JUNE, 2001), date(31, Calendar.JULY, 2001))));
        s1.add(new Task("Репроектирование", new SimpleTimePeriod(date(1, Calendar.AUGUST, 2001), date(8, Calendar.AUGUST, 2001))));
        s1.add(new Task("Пересмотр проектирования интерфейсов", new SimpleTimePeriod(date(10, Calendar.AUGUST, 2001), date(10,
                Calendar.AUGUST, 2001))));
        s1.add(new Task("Разработка(Бета)", new SimpleTimePeriod(date(12, Calendar.AUGUST, 2001), date(12, Calendar.SEPTEMBER,
                2001))));
        s1.add(new Task("Тестирование",
                new SimpleTimePeriod(date(13, Calendar.SEPTEMBER, 2001), date(31, Calendar.OCTOBER, 2001))));
        s1.add(new Task("Окончательная реализация", new SimpleTimePeriod(date(1, Calendar.NOVEMBER, 2001), date(15,
                Calendar.NOVEMBER, 2001))));
        s1.add(new Task("Закрытие", new SimpleTimePeriod(date(28, Calendar.NOVEMBER, 2001), date(30, Calendar.NOVEMBER, 2001))));
        collection.add(s1);
        switch (type) {
        case Constants.GANTT_1: // Диаграмма Гантта с несколькими сериями
            s2.add(new Task("Описание предложений", new SimpleTimePeriod(date(1, Calendar.APRIL, 2001), date(5, Calendar.APRIL,
                    2001))));
            s2.add(new Task("Получени требований", new SimpleTimePeriod(date(9, Calendar.APRIL, 2001), date(9, Calendar.APRIL,
                    2001))));
            s2.add(new Task("Анализ требований", new SimpleTimePeriod(date(10, Calendar.APRIL, 2001),
                    date(15, Calendar.MAY, 2001))));
            s2.add(new Task("Проектирование классов", new SimpleTimePeriod(date(15, Calendar.MAY, 2001), date(17, Calendar.JUNE,
                    2001))));
            s2.add(new Task("Проектирование интерфейсов", new SimpleTimePeriod(date(30, Calendar.JUNE, 2001), date(30,
                    Calendar.JUNE, 2001))));
            s2.add(new Task("Разработка(Альфа)", new SimpleTimePeriod(date(1, Calendar.JULY, 2001), date(12, Calendar.SEPTEMBER,
                    2001))));
            s2.add(new Task("Репроектирование", new SimpleTimePeriod(date(12, Calendar.SEPTEMBER, 2001), date(22,
                    Calendar.SEPTEMBER, 2001))));
            s2.add(new Task("Пересмотр проектирования интерфейсов", new SimpleTimePeriod(date(25, Calendar.SEPTEMBER, 2001),
                    date(27, Calendar.SEPTEMBER, 2001))));
            s2.add(new Task("Разработка(Бета)", new SimpleTimePeriod(date(27, Calendar.SEPTEMBER, 2001), date(30,
                    Calendar.OCTOBER, 2001))));
            s2.add(new Task("Тестирование", new SimpleTimePeriod(date(31, Calendar.OCTOBER, 2001), date(17, Calendar.NOVEMBER,
                    2001))));
            s2.add(new Task("Окончательная реализация", new SimpleTimePeriod(date(18, Calendar.NOVEMBER, 2001), date(5,
                    Calendar.DECEMBER, 2001))));
            s2.add(new Task("Закрытие",
                    new SimpleTimePeriod(date(10, Calendar.DECEMBER, 2001), date(11, Calendar.DECEMBER, 2001))));
            collection.add(s2);
            break;
        }

        return collection;
    }

    /**
     * Создание диаграммы Гантта на основе <code>IntervalCategoryDataset</code>
     * 
     * @param dataset
     *            набор данных
     * @param legend
     *            отображать легенду?
     * @return
     */
    private void createGanttChart(final IntervalCategoryDataset dataset, boolean legend) {
        chart = ChartFactory.createGanttChart(title, leftVerticlLabel, upHorizontalLabel, dataset, legend, true, false);
    }

    /**
     * Создание диаграммы Гантта на основе <code>GanttCategoryDataset</code>
     * 
     * @param dataset
     *            набор данных
     * @param legend
     *            отображать легенду?
     * @return
     */
    private void createGanttChart(final GanttCategoryDataset dataSet, boolean legend) {
        chart = ChartFactory.createGanttChart(title, leftVerticlLabel, upHorizontalLabel, dataSet, legend, true, false);

        if (plot == null) {
            plot = (CategoryPlot) chart.getPlot();
        }

        Hour h = new Hour(1, 14, 5, 2008);
        for (int i = 0; i < 12; i++) {
            Marker marker = new IntervalMarker(h.getFirstMillisecond(), h.getLastMillisecond(), Color.lightGray);
            plot.addRangeMarker(marker, Layer.BACKGROUND);
            // прыжок на два часа вперёд
            h = (Hour) h.next().next();
        }

        GanttRenderer renderer = (GanttRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

    }

    /**
     * Создание диаграммы Гантта на основе <code>SlidingGanttCategoryDataset</code>
     * 
     * @param dataset
     *            набор данных
     * @param legend
     *            отображать легенду?
     * @return
     */
    private void createSlidingGanttChart(SlidingGanttCategoryDataset dataSet, boolean legend) {
        chart = ChartFactory.createGanttChart(title, leftVerticlLabel, upHorizontalLabel, dataSet, legend, true, false);

        if (plot == null) {
            plot = (CategoryPlot) chart.getPlot();
        }

        Hour h = new Hour(1, 14, 5, 2008);
        for (int i = 0; i < 12; i++) {
            Marker marker = new IntervalMarker(h.getFirstMillisecond(), h.getLastMillisecond(), Color.lightGray);
            plot.addRangeMarker(marker, Layer.BACKGROUND);
            // прыжок на два часа вперёд
            h = (Hour) h.next().next();
        }

        plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(10F);
        DateAxis dateaxis = (DateAxis) plot.getRangeAxis();
        if (dataSet != null) {
            Range range = DatasetUtilities.findRangeBounds(dataSet.getUnderlyingDataset(), true);
            if (range != null) {    
                dateaxis.setRange(range);
            }
        }
        GanttRenderer ganttrenderer = (GanttRenderer) plot.getRenderer();
        ganttrenderer.setDrawBarOutline(false);
        ganttrenderer.setShadowVisible(false);
    }

    /**
     * дополнительный метод создающий объект типа <code>Date</code>
     * 
     * @param day
     *            день.
     * @param month
     *            месяц.
     * @param year
     *            год.
     * 
     * @return дата.
     */
    private static Date date(final int day, final int month, final int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    /**
     * Дабавление дней к дате
     * 
     * @param date
     *            дата которую нужно увеличить
     * @param days
     *            количество дней, которое нужно прибавить к дате(отрицательное число удаляет дни)
     * @return the date изменённая дата
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); // minus number would decrement the days
        return cal.getTime();
    }

    /**
     * Сохранение графика(глобальная переменная) <code>chart</code> в файл с картинкой JPEG
     * 
     * @param fileLocation
     *            полное имя сохраняемого файла
     * @param resolution
     *            разрешение в котором необходимо сохранить картинку
     */
    public void saveChart(String fileLocation, Dimension resolution) {
        try {
            ChartUtilities.saveChartAsJPEG(new File(fileLocation), chart, resolution.width, resolution.height);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения файла: " + fileLocation);
            e.printStackTrace();
        }
    }

    /**
     * Задание левой вертикальной подписи с диаграмме
     * 
     * @param upHorizontalLabel
     *            строка, используемая в качестве подписи
     */
    public void setLeftVerticlLabel(String leftVerticlLabel) {
        this.leftVerticlLabel = leftVerticlLabel;
    }

    /**
     * Задание верхней горизонтальной подписи с диаграмме
     * 
     * @param upHorizontalLabel
     *            строка, используемая в качестве подписи
     */
    public void setUpHorizontalLabel(String upHorizontalLabel) {
        this.upHorizontalLabel = upHorizontalLabel;
    }

    /**
     * Установка цвета для фонового цвета самого графика
     * 
     * @param color
     */
    public void setColorChart(Color color) {
        if (plot != null && color != null) {
            plot.setBackgroundPaint(color);
        }
    }

    /**
     * Установка цвета для окружающего график фона
     * 
     * @param color
     *            цвет
     */
    public void setColorCanvasChart(Color color) {
        if (chart != null) {
            chart.setBackgroundPaint(color);
        }
    }

    public void setGradientChart(GradientColor gradient) {
        if (plot != null) {
            plot.setBackgroundPaint(getGradientPaint(gradient, chartPanel.getHeight(), chartPanel.getWidth()));
        }

    }

    public void setGradientCanvasChart(GradientColor gradient) {
        if (chart != null) {
            chart.setBackgroundPaint(getGradientPaint(gradient, preferredSize.height, preferredSize.width));
        }
    }

    private GradientPaint getGradientPaint(GradientColor gradient, int height, int wigth) {
            final int startH = (int) (wigth / 100f * gradient.getPositionStartColor());
            final int endH = (int) (wigth / 100f * gradient.getPositionEndColor());
            final int startV = (int) (height / 100f * gradient.getPositionStartColor());
            final int endV = (int) (height / 100f * gradient.getPositionEndColor());
            switch (gradient.getOrientation()) {
            case Constants.HORIZONTAL:
                return new GradientPaint(startH, 0, gradient.getStartColor(), endH, 0, gradient.getEndColor(), gradient.isCycle());
            case Constants.VERTICAL:
                return new GradientPaint(0, startV, gradient.getStartColor(), 0, endV, gradient.getEndColor(), gradient.isCycle());
            case Constants.DIAGONAL:
                return new GradientPaint(startH, height - startV, gradient.getStartColor(), endH, height - endV, gradient.getEndColor(), gradient.isCycle());
            case Constants.DIAGONAL2:
                return new GradientPaint(startH, startV, gradient.getStartColor(), endH, endV, gradient.getEndColor(), gradient.isCycle());
            default:
                return new GradientPaint(startH, 0, gradient.getStartColor(), endH, 0, gradient.getEndColor(), gradient.isCycle());
            }
    }

    /**
     * Установка прозрачности для полос графика
     * 
     * @param idPercent
     *            коофициент прозрачности
     */
    public void setTransparencyChartBar(int idPercent) {
        if (plot != null) {
            plot.setBackgroundAlpha(idPercent / 10f);
        }
    }

    /**
     * Получить конечную панель с собранной диаграммой
     * 
     * @return объект <code>ChartPanel</code> с собранным не нём графиком
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public void setAdditionalSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    public JScrollBar getScroller() {
        return scroller;
    }

}
