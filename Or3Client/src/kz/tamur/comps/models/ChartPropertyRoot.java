package kz.tamur.comps.models;

import java.awt.Color;

import javax.swing.*;

import static kz.tamur.comps.Constants.*;
import static kz.tamur.comps.models.Types.*;

/**
 * Класс свойств для компонента вывода диаграмм
 * 
 * @author Sergey Lebedev
 * 
 */
public class ChartPropertyRoot extends PropertyRoot {
    public ChartPropertyRoot() {
        super();
        PropertyNode title = new PropertyNode(this, "title", RSTRING, null, false, null);
        PropertyReestr.registerProperty(title);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        // Позиция
        new CompPosition(this);
        // Данные
        PropertyNode ref = new PropertyNode(this, "ref", -1, null, false, null);
        PropertyNode data = new PropertyNode(ref, "data", REF, null, false, null);
        PropertyReestr.registerProperty(data);
        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("Panel.background"));
        new CompBorder(view, UIManager.getBorder("Panel.border"));
        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        PropertyNode act = new ActivProperty(pov);
        act.removeChild("editable");
        new PropertyNode(act.getChildCount() - 2, act, "enabled", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(pov, "beforeOpen", EXPR, null, false, null);
        new PropertyNode(pov, "afterOpen", EXPR, null, false, null);
        new PropertyNode(pov, "beforeClose", EXPR, null, false, null);
        new PropertyNode(pov, "afterClose", EXPR, null, false, null);
      //  new PropertyNode(pov, "isVisible", EXPR, null, false, null);
        new PropertyNode(pov, "createXml", EXPR, null, false, null);

        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);

        new PropertyNode(this, "reports", REPORT, null, true, null);
        new PropertyNode(this, "pmenu", PMENUITEM, null, true, null);
        // тип Диаграммы
        PropertyNode chart = new PropertyNode(this, "chart", -1, null, false, null);
        final EnumValueToolTip defaultEnum = new EnumValueToolTip(GANTT_1, "Гантт - с сериями", "GANTT_1.png");
        final EnumValueToolTip[] type = {
                new EnumValueToolTip(AREA_CHARTS, "1. Area Charts", null),
                new EnumValueToolTip(AREA_CHART_1, "1.1. AreaChart 1", "AREA_CHART_1.png"),
                new EnumValueToolTip(STACKED_AREA_CHART_1, "1.2. StackedAreaChart 1", "STACKED_AREA_CHART_1.png"),
                new EnumValueToolTip(STACKED_XY_AREA_CHART_1, "1.3. StackedXYAreaChart 1", "STACKED_XY_AREA_CHART_1.png"),
                new EnumValueToolTip(STACKED_XY_AREA_CHART_2, "1.4. StackedXYAreaChart 2", "STACKED_XY_AREA_CHART_2.png"),
                new EnumValueToolTip(STACKED_XY_AREA_RENDERER_1, "1.5. StackedXYAreaRenderer 1", "STACKED_XY_AREA_RENDERER_1.png"),
                new EnumValueToolTip(XY_AREA_CHART_1, "1.6. XYAreaChart 1", "XY_AREA_CHART_1.png"),
                new EnumValueToolTip(XY_AREA_CHART_2, "1.7. XYAreaChart 2", "XY_AREA_CHART_2.png"),
                new EnumValueToolTip(XY_AREA_RENDERER_2_1, "1.8. XYAreaRenderer2 1", "XY_AREA_RENDERER_2_1.png"),
                new EnumValueToolTip(XY_STEP_AREA_RENDERER_1, "1.9. XYStepAreaRenderer 1", "XY_STEP_AREA_RENDERER_1.png"),
                new EnumValueToolTip(BAR_CHARTS, "2. Bar Charts", null),
                new EnumValueToolTip(CATEGORY_PLOT, "2.1. CategoryPlot", null),
                new EnumValueToolTip(BAR_CHART_1, "2.1.1. BarChart 1", "BAR_CHART_1.png"),
                new EnumValueToolTip(BAR_CHART_2, "2.1.2. BarChart 2", "BAR_CHART_2.png"),
                new EnumValueToolTip(BAR_CHART_3, "2.1.3. BarChart 3", "BAR_CHART_3.png"),
                new EnumValueToolTip(BAR_CHART_4, "2.1.4. BarChart 4", "BAR_CHART_4.png"),
                new EnumValueToolTip(BAR_CHART_5, "2.1.5. BarChart 5", "BAR_CHART_5.png"),
                new EnumValueToolTip(BAR_CHART_6, "2.1.6. BarChart 6", "BAR_CHART_6.png"),
                new EnumValueToolTip(BAR_CHART_7, "2.1.7. BarChart 7", "BAR_CHART_7.png"),
                new EnumValueToolTip(BAR_CHART_8, "2.1.8. BarChart 8", "BAR_CHART_8.png"),
                new EnumValueToolTip(BAR_CHART_9, "2.1.9. BarChart 9", "BAR_CHART_9.png"),
                new EnumValueToolTip(BAR_CHART_10, "2.1.10. BarChart 10", "BAR_CHART_10.png"),
                new EnumValueToolTip(BAR_CHART_11, "2.1.11. BarChart 11", "BAR_CHART_11.png"),
                new EnumValueToolTip(BAR_CHART_3D_1, "2.1.12. BarChart3D 1", "BAR_CHART_3D_1.png"),
                new EnumValueToolTip(BAR_CHART_3D_2, "2.1.13. BarChart3D 2", "BAR_CHART_3D_2.png"),
                new EnumValueToolTip(BAR_CHART_3D_3, "2.1.14. BarChart3D 3", "BAR_CHART_3D_3.png"),
                new EnumValueToolTip(BAR_CHART_3D_4, "2.1.15. BarChart3D 4", "BAR_CHART_3D_4.png"),
                new EnumValueToolTip(CYLINDER_CHART_1, "2.1.16. CylinderChart 1", "CYLINDER_CHART_1.png"),
                new EnumValueToolTip(CYLINDER_CHART_2, "2.1.17. CylinderChart 2", "CYLINDER_CHART_2.png"),
                new EnumValueToolTip(INTERVAL_BAR_CHART_1, "2.1.18. IntervalBarChart 1", "INTERVAL_BAR_CHART_1.png"),
                new EnumValueToolTip(LAYERED_BAR_CHART_1, "2.1.19. LayeredBarChart 1", "LAYERED_BAR_CHART_1.png"),
                new EnumValueToolTip(LAYERED_BAR_CHART_2, "2.1.20. LayeredBarChart 2", "LAYERED_BAR_CHART_2.png"),
                new EnumValueToolTip(SLIDING_CATEGORY_DATASET_1, "2.1.21. SlidingCategoryDataset 1",
                        "SLIDING_CATEGORY_DATASET_1.png"),
                new EnumValueToolTip(SLIDING_CATEGORY_DATASET_2, "2.1.22. SlidingCategoryDataset 2",
                        "SLIDING_CATEGORY_DATASET_2.png"),
                new EnumValueToolTip(STATISTICAL_BAR_CHART_1, "2.1.23. StatisticalBarChart 1", "STATISTICAL_BAR_CHART_1.png"),
                new EnumValueToolTip(SURVEY_RESULTS_1, "2.1.24. SurveyResults 1", "SURVEY_RESULTS_1.png"),
                new EnumValueToolTip(SURVEY_RESULTS_2, "2.1.25. SurveyResults 2", "SURVEY_RESULTS_2.png"),
                new EnumValueToolTip(SURVEY_RESULTS_3, "2.1.26. SurveyResults 3", "SURVEY_RESULTS_3.png"),
                new EnumValueToolTip(WATERFALL_CHART_1, "2.1.27. WaterfallChart 1", "WATERFALL_CHART_1.png"),
                new EnumValueToolTip(XY_PLOT, "2.2. XYPlot", null),
                new EnumValueToolTip(XY_BAR_CHART_1, "2.2.1. XYBarChart 1", "XY_BAR_CHART_1.png"),
                new EnumValueToolTip(XY_BAR_CHART_2, "2.2.2. XYBarChart 2", "XY_BAR_CHART_2.png"),
                new EnumValueToolTip(XY_BAR_CHART_3, "2.2.3. XYBarChart 3", "XY_BAR_CHART_3.png"),
                new EnumValueToolTip(XY_BAR_CHART_4, "2.2.4. XYBarChart 4", "XY_BAR_CHART_4.png"),
                new EnumValueToolTip(XY_BAR_CHART_5, "2.2.5. XYBarChart 5", "XY_BAR_CHART_5.png"),
                new EnumValueToolTip(XY_BAR_CHART_6, "2.2.6. XYBarChart 6", "XY_BAR_CHART_6.png"),
                new EnumValueToolTip(XY_BAR_CHART_7, "2.2.7. XYBarChart 7", "XY_BAR_CHART_7.png"),
                new EnumValueToolTip(CLUSTERED_XY_BAR_RENDERER_1, "2.2.8. ClusteredXYBarRenderer 1",
                        "CLUSTERED_XY_BAR_RENDERER_1.png"),
                new EnumValueToolTip(STACKED_XY_BAR_CHART_1, "2.2.9. StackedXYBarChart 1", "STACKED_XY_BAR_CHART_1.png"),
                new EnumValueToolTip(STACKED_XY_BAR_CHART_2, "2.2.10. StackedXYBarChart 2", "STACKED_XY_BAR_CHART_2.png"),
                new EnumValueToolTip(STACKED_XY_BAR_CHART_3, "2.2.11. StackedXYBarChart 3", "STACKED_XY_BAR_CHART_3.png"),
                new EnumValueToolTip(XY_RELATIVE_DATE_FORMAT_1, "2.2.12. RelativeDateFormat 1", "XY_RELATIVE_DATE_FORMAT_1.png"),
                new EnumValueToolTip(XY_RELATIVE_DATE_FORMAT_2, "2.2.13. RelativeDateFormat 2", "XY_RELATIVE_DATE_FORMAT_2.png"),
                new EnumValueToolTip(BAR_CHARTS_STACKED, "3. Bar Charts - Stacked", null),
                new EnumValueToolTip(POPULATION_CHART_1, "3.1. PopulationChart 1", "POPULATION_CHART_1.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_1, "3.2. StackedBarChart 1", "STACKED_BAR_CHART_1.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_2, "3.3. StackedBarChart 2", "STACKED_BAR_CHART_2.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_3, "3.4. StackedBarChart 3", "STACKED_BAR_CHART_3.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_4, "3.5. StackedBarChart 4", "STACKED_BAR_CHART_4.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_5, "3.6. StackedBarChart 5", "STACKED_BAR_CHART_5.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_6, "3.7. StackedBarChart 6", "STACKED_BAR_CHART_6.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_7, "3.8. StackedBarChart 7", "STACKED_BAR_CHART_7.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_3D_1, "3.9. StackedBarChart3D 1", "STACKED_BAR_CHART_3D_1.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_3D_2, "3.10. StackedBarChart3D 2", "STACKED_BAR_CHART_3D_2.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_3D_3, "3.11. StackedBarChart3D 3", "STACKED_BAR_CHART_3D_3.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_3D_4, "3.12. StackedBarChart3D 4", "STACKED_BAR_CHART_3D_4.png"),
                new EnumValueToolTip(STACKED_BAR_CHART_3D_5, "3.13. StackedBarChart3D 5", "STACKED_BAR_CHART_3D_5.png"),
                new EnumValueToolTip(COMBINED_AXIS_CHARTS, "4. Combined Axis Charts", null),
                new EnumValueToolTip(COMBINED_CATEGORY_PLOT_1, "4.1. CombinedCategoryPlot 1", "COMBINED_CATEGORY_PLOT_1.png"),
                new EnumValueToolTip(COMBINED_CATEGORY_PLOT_2, "4.2. CombinedCategoryPlot 2", "COMBINED_CATEGORY_PLOT_2.png"),
                new EnumValueToolTip(COMBINED_TIME_SERIES_1, "4.3. CombinedTimeSeries 1", "COMBINED_TIME_SERIES_1.png"),
                new EnumValueToolTip(COMBINED_XY_PLOT_1, "4.4. CombinedXYPlot 1", "COMBINED_XY_PLOT_1.png"),
                new EnumValueToolTip(COMBINED_XY_PLOT_2, "4.5. CombinedXYPlot 2", "COMBINED_XY_PLOT_2.png"),
                new EnumValueToolTip(COMBINED_XY_PLOT_3, "4.6. CombinedXYPlot 3", "COMBINED_XY_PLOT_3.png"),
                new EnumValueToolTip(COMBINED_XY_PLOT_4, "4.7. CombinedXYPlot 4", "COMBINED_XY_PLOT_4.png"),
                new EnumValueToolTip(FINANCIAL_CHARTS, "5. Financial Charts", null),
                new EnumValueToolTip(CANDLESTICK_CHART_1, "5.1. CandlestickChart 1", "CANDLESTICK_CHART_1.png"),
                new EnumValueToolTip(HIGHLOW_CHART_1, "5.2. HighLowChart 1", "HIGHLOW_CHART_1.png"),
                new EnumValueToolTip(HIGHLOW_CHART_2, "5.3. HighLowChart 2", "HIGHLOW_CHART_2.png"),
                new EnumValueToolTip(HIGHLOW_CHART_3, "5.4. HighLowChart 3", "HIGHLOW_CHART_3.png"),
                new EnumValueToolTip(MOVING_AVERAGE_1, "5.5. MovingAverage 1", "MOVING_AVERAGE_1.png"),
                new EnumValueToolTip(PRICE_VOLUME_1, "5.6. PriceVolume 1", "PRICE_VOLUME_1.png"),
                new EnumValueToolTip(PRICE_VOLUME_2, "5.7. PriceVolume 2", "PRICE_VOLUME_2.png"),
                new EnumValueToolTip(YIELD_CURVE_1, "5.8. YieldCurve 1", "YIELD_CURVE_1.png"),
                new EnumValueToolTip(GANTT_CHARTS, "Диаграммы Гантта", null),
                new EnumValueToolTip(GANTT_0, "Гантт - основная", "GANTT_0.png"),
                defaultEnum,
                new EnumValueToolTip(GANTT_2, "Гант с % выполнения", "GANTT_2.png"),
                new EnumValueToolTip(SLIDING_GANTT_DATASET_1, "Гант с прокруткой", "SLIDING_GANTT_DATASET_1.png"),
                new EnumValueToolTip(XY_TASK_DATASET_1, "6.4. XYTaskDataset 1", "XY_TASK_DATASET_1.png"),
                new EnumValueToolTip(XY_TASK_DATASET_2, "6.5. XYTaskDataset 2", "XY_TASK_DATASET_2.png"),
                new EnumValueToolTip(LINE_CHARTS, "7. Line Charts", null),
                new EnumValueToolTip(ANNOTATION_1, "7.1. Annotation 1", "ANNOTATION_1.png"),
                new EnumValueToolTip(LINE_CHART_1, "7.2. LineChart 1", "LINE_CHART_1.png"),
                new EnumValueToolTip(LINE_CHART_2, "7.3. LineChart 2", "LINE_CHART_2.png"),
                new EnumValueToolTip(LINE_CHART_3, "7.4. LineChart 3", "LINE_CHART_3.png"),
                new EnumValueToolTip(LINE_CHART_4, "7.5. LineChart 4", "LINE_CHART_4.png"),
                new EnumValueToolTip(LINE_CHART_5, "7.6. LineChart 5", "LINE_CHART_5.png"),
                new EnumValueToolTip(LINE_CHART_6, "7.7. LineChart 6", "LINE_CHART_6.png"),
                new EnumValueToolTip(LINE_CHART_7, "7.8. LineChart 7", "LINE_CHART_7.png"),
                new EnumValueToolTip(LINE_CHART_8, "7.9. LineChart 8", "LINE_CHART_8.png"),
                new EnumValueToolTip(LINE_CHART_3D_1, "7.10. LineChart3D 1", "LINE_CHART_3D_1.png"),
                new EnumValueToolTip(STATISTICAL_LINE_CHART_1, "7.11. StatisticalLineChart 1", "STATISTICAL_LINE_CHART_1.png"),
                new EnumValueToolTip(XY_SPLINE_RENDERER_1, "7.12. XYSplineRenderer 1", "XY_SPLINE_RENDERER_1.png"),
                new EnumValueToolTip(XY_STEP_RENDERER_1, "7.13. XYStepRenderer 1", "XY_STEP_RENDERER_1.png"),
                new EnumValueToolTip(XY_STEP_RENDERER_2, "7.14. XYStepRenderer 2", "XY_STEP_RENDERER_2.png"),
                new EnumValueToolTip(DIAL_METER_CHARTS, "8. Dial / Meter Charts", null),
                new EnumValueToolTip(DIAL_1, "8.1. Dial 1", "DIAL_1.png"),
                new EnumValueToolTip(DIAL_2, "8.2. Dial 2", "DIAL_2.png"),
                new EnumValueToolTip(DIAL_2A, "8.3. Dial 2a", "DIAL_2A.png"),
                new EnumValueToolTip(DIAL_3, "8.4. Dial 3", "DIAL_3.png"),
                new EnumValueToolTip(DIAL_4, "8.5. Dial 4", "DIAL_4.png"),
                new EnumValueToolTip(DIAL_5, "8.6. Dial 5", "DIAL_5.png"),
                new EnumValueToolTip(METER_CHART_1, "8.7. MeterChart 1", "METER_CHART_1.png"),
                new EnumValueToolTip(METER_CHART_2, "8.8. MeterChart 2", "METER_CHART_2.png"),
                new EnumValueToolTip(METER_CHART_3, "8.9. MeterChart 3", "METER_CHART_3.png"),
                new EnumValueToolTip(THERMOMETER_1, "8.10. Thermometer 1", "THERMOMETER_1.png"),
                new EnumValueToolTip(MULTIPLE_AXIS_CHARTS, "9. Multiple Axis Charts", null),
                new EnumValueToolTip(DUAL_AXIS_1, "9.1. DualAxis 1", "DUAL_AXIS_1.png"),
                new EnumValueToolTip(DUAL_AXIS_2, "9.2. DualAxis 2", "DUAL_AXIS_2.png"),
                new EnumValueToolTip(DUAL_AXIS_3, "9.3. DualAxis 3", "DUAL_AXIS_3.png"),
                new EnumValueToolTip(DUAL_AXIS_4, "9.4. DualAxis 4", "DUAL_AXIS_4.png"),
                new EnumValueToolTip(DUAL_AXIS_5, "9.5. DualAxis 5", "DUAL_AXIS_5.png"),
                new EnumValueToolTip(MULTIPLE_AXIS_1, "9.6. MultipleAxis 1", "MULTIPLE_AXIS_1.png"),
                new EnumValueToolTip(MULTIPLE_AXIS_2, "9.7. MultipleAxis 2", "MULTIPLE_AXIS_2.png"),
                new EnumValueToolTip(MULTIPLE_AXIS_3, "9.8. MultipleAxis 3", "MULTIPLE_AXIS_3.png"),
                new EnumValueToolTip(PARETO_CHART_1, "9.9. ParetoChart 1", "PARETO_CHART_1.png"),
                new EnumValueToolTip(OVERLAID_CHARTS, "10. Overlaid Charts", null),
                new EnumValueToolTip(OVERLAID_BAR_CHART_1, "10.1. OverlaidBarChart 1", "OVERLAID_BAR_CHART_1.png"),
                new EnumValueToolTip(OVERLAID_BAR_CHART_2, "10.2. OverlaidBarChart 2", "OVERLAID_BAR_CHART_2.png"),
                new EnumValueToolTip(OVERLAID_XY_PLOT_1, "10.3. OverlaidXYPlot 1", "OVERLAID_XY_PLOT_1.png"),
                new EnumValueToolTip(OVERLAID_XY_PLOT_2, "10.4. OverlaidXYPlot 2", "OVERLAID_XY_PLOT_2.png"),
                new EnumValueToolTip(PIE_CHARTS, "11. Pie Charts", null),
                new EnumValueToolTip(PIE_CHART_1, "11.1. PieChart 1", "PIE_CHART_1.png"),
                new EnumValueToolTip(PIE_CHART_2, "11.2. PieChart 2", "PIE_CHART_2.png"),
                new EnumValueToolTip(PIE_CHART_3, "11.3. PieChart 3", "PIE_CHART_3.png"),
                new EnumValueToolTip(PIE_CHART_4, "11.4. PieChart 4", "PIE_CHART_4.png"),
                new EnumValueToolTip(PIE_CHART_5, "11.5. PieChart 5", "PIE_CHART_5.png"),
                new EnumValueToolTip(PIE_CHART_6, "11.6. PieChart 6", "PIE_CHART_6.png"),
                new EnumValueToolTip(PIE_CHART_7, "11.7. PieChart 7", "PIE_CHART_7.png"),
                new EnumValueToolTip(PIE_CHART_8, "11.8. PieChart 8", "PIE_CHART_8.png"),
                new EnumValueToolTip(PIE_CHART_3D_1, "11.9. PieChart3D 1", "PIE_CHART_3D_1.png"),
                new EnumValueToolTip(PIE_CHART_3D_2, "11.10. PieChart3D 2", "PIE_CHART_3D_2.png"),
                new EnumValueToolTip(PIE_CHART_3D_3, "11.11. PieChart3D 3", "PIE_CHART_3D_3.png"),
                new EnumValueToolTip(MULTIPLE_PIE_CHART_1, "11.12. MultiplePieChart 1", "MULTIPLE_PIE_CHART_1.png"),
                new EnumValueToolTip(MULTIPLE_PIE_CHART_2, "11.13. MultiplePieChart 2", "MULTIPLE_PIE_CHART_2.png"),
                new EnumValueToolTip(MULTIPLE_PIE_CHART_3, "11.14. MultiplePieChart 3", "MULTIPLE_PIE_CHART_3.png"),
                new EnumValueToolTip(MULTIPLE_PIE_CHART_4, "11.15. MultiplePieChart 4", "MULTIPLE_PIE_CHART_4.png"),
                new EnumValueToolTip(RING_CHART_1, "11.16. RingChart 1", "RING_CHART_1.png"),
                new EnumValueToolTip(STATISTICAL_CHARTS, "12. Statistical Charts", null),
                new EnumValueToolTip(BOX_AND_WHISKER_CHART_1, "12.1. BoxAndWhiskerChart 1", "BOX_AND_WHISKER_CHART_1.png"),
                new EnumValueToolTip(BOX_AND_WHISKER_CHART_2, "12.2. BoxAndWhiskerChart 2", "BOX_AND_WHISKER_CHART_2.png"),
                new EnumValueToolTip(HISTOGRAM_1, "12.3. Histogram 1", "HISTOGRAM_1.png"),
                new EnumValueToolTip(HISTOGRAM_2, "12.4. Histogram 2", "HISTOGRAM_2.png"),
                new EnumValueToolTip(MIN_MAX_CATEGORYPLOT_1, "12.5. MinMaxCategoryPlot 1", "MIN_MAX_CATEGORYPLOT_1.png"),
                new EnumValueToolTip(NORMAL_DISTRIBUTION_1, "12.6. NormalDistribution 1", "NORMAL_DISTRIBUTION_1.png"),
                new EnumValueToolTip(NORMAL_DISTRIBUTION_2, "12.7. NormalDistribution 2", "NORMAL_DISTRIBUTION_2.png"),
                new EnumValueToolTip(REGRESSION_1, "12.8. Regression 1", "REGRESSION_1.png"),
                new EnumValueToolTip(SCATTER_PLOT_1, "12.9. ScatterPlot 1", "SCATTER_PLOT_1.png"),
                new EnumValueToolTip(SCATTER_PLOT_2, "12.10. ScatterPlot 2", "SCATTER_PLOT_2.png"),
                new EnumValueToolTip(SCATTER_PLOT_3, "12.11. ScatterPlot 3", "SCATTER_PLOT_3.png"),
                new EnumValueToolTip(SCATTER_PLOT_4, "12.12. ScatterPlot 4", "SCATTER_PLOT_4.png"),
                new EnumValueToolTip(XY_ERROR_RENDERER_1, "12.13. XYErrorRenderer 1", "XY_ERROR_RENDERER_1.png"),
                new EnumValueToolTip(XY_ERROR_RENDERER_2, "12.14. XYErrorRenderer 2", "XY_ERROR_RENDERER_2.png"),
                new EnumValueToolTip(TIME_SERIES_CHARTS, "13. Time Series Charts", null),
                new EnumValueToolTip(TIME_RENDERER_1, "13.1. TimeSeries 1", "TIME_RENDERER_1.png"),
                new EnumValueToolTip(TIME_SERIES_2, "13.2. TimeSeries 2", "TIME_SERIES_2.png"),
                new EnumValueToolTip(TIME_SERIES_3, "13.3. TimeSeries 3", "TIME_SERIES_3.png"),
                new EnumValueToolTip(TIME_SERIES_4, "13.4. TimeSeries 4", "TIME_SERIES_4.png"),
                new EnumValueToolTip(TIME_SERIES_5, "13.5. TimeSeries 5", "TIME_SERIES_5.png"),
                new EnumValueToolTip(TIME_SERIES_6, "13.6. TimeSeries 6", "TIME_SERIES_6.png"),
                new EnumValueToolTip(TIME_SERIES_7, "13.7. TimeSeries 7", "TIME_SERIES_7.png"),
                new EnumValueToolTip(TIME_SERIES_8, "13.8. TimeSeries 8", "TIME_SERIES_8.png"),
                new EnumValueToolTip(TIME_SERIES_9, "13.9. TimeSeries 9", "TIME_SERIES_9.png"),
                new EnumValueToolTip(TIME_SERIES_10, "13.10. TimeSeries 10", "TIME_SERIES_11.png"),
                new EnumValueToolTip(TIME_SERIES_11, "13.11. TimeSeries 11", "TIME_SERIES_11.png"),
                new EnumValueToolTip(TIME_SERIES_12, "13.12. TimeSeries 12", "TIME_SERIES_12.png"),
                new EnumValueToolTip(TIME_SERIES_13, "13.13. TimeSeries 13", "TIME_SERIES_13.png"),
                new EnumValueToolTip(PERIOD_AXIS_1, "13.14. PeriodAxis 1", "PERIOD_AXIS_1.png"),
                new EnumValueToolTip(PERIOD_AXIS_2, "13.15. PeriodAxis 2", "PERIOD_AXIS_2.png"),
                new EnumValueToolTip(PERIOD_AXIS_3, "13.16. PeriodAxis 3", "PERIOD_AXIS_3.png"),
                new EnumValueToolTip(RELATIVE_DATE_FORMAT_1, "13.17. RelativeDateFormat 1", "RELATIVE_DATE_FORMAT_1.png"),
                new EnumValueToolTip(DEVIATION_RENDERER_1, "13.18. DeviationRenderer 1", "DEVIATION_RENDERER_1.png"),
                new EnumValueToolTip(DEVIATION_RENDERER_2, "13.19. DeviationRenderer 2", "DEVIATION_RENDERER_2.png"),
                new EnumValueToolTip(DIFFERENCE_CHART_1, "13.20. DifferenceChart 1", "DIFFERENCE_CHART_1.png"),
                new EnumValueToolTip(DIFFERENCE_CHART_2, "13.21. DifferenceChart 2", "DIFFERENCE_CHART_2.png"),
                new EnumValueToolTip(COMPARE_TO_PREVIOUS_YEAR, "13.22. CompareToPreviousYear", "COMPARE_TO_PREVIOUS_YEAR.png"),
                new EnumValueToolTip(XY_CHARTS, "14. XY Charts", null),
                new EnumValueToolTip(XY_SCATTER_PLOT_1, "14.1. ScatterPlot 1", "XY_SCATTER_PLOT_1.png"),
                new EnumValueToolTip(XY_SCATTER_PLOT_2, "14.2. ScatterPlot 2", "XY_SCATTER_PLOT_2.png"),
                new EnumValueToolTip(XY_SCATTER_PLOT_3, "14.3. ScatterPlot 3", "XY_SCATTER_PLOT_3.png"),
                new EnumValueToolTip(LOG_AXIS_1, "14.4. LogAxis 1", "LOG_AXIS_1.png"),
                new EnumValueToolTip(FUNCTION_2D_1, "14.5. Function2D 1", "FUNCTION_2D_1.png"),
                new EnumValueToolTip(XY_BLOCK_CHART_1, "14.6. XYBlockChart 1", "XY_BLOCK_CHART_1.png"),
                new EnumValueToolTip(XY_BLOCK_CHART_2, "14.7. XYBlockChart 2", "XY_BLOCK_CHART_2.png"),
                new EnumValueToolTip(XY_BLOCK_CHART_3, "14.8. XYBlockChart 3", "XY_BLOCK_CHART_3.png"),
                new EnumValueToolTip(XY_LINE_AND_SHAPE_RENDERER_1, "14.9. XYLineAndShapeRenderer 1",
                        "XY_LINE_AND_SHAPE_RENDERER_1.png"),
                new EnumValueToolTip(XY_LINE_AND_SHAPE_RENDERER_2, "14.10. XYLineAndShapeRenderer 2",
                        "XY_LINE_AND_SHAPE_RENDERER_2.png"),
                new EnumValueToolTip(XY_SERIES_1, "14.11. XYSeries 1", "XY_SERIES_1.png"),
                new EnumValueToolTip(XY_SERIES_2, "14.12. XYSeries 2", "XY_SERIES_2.png"),
                new EnumValueToolTip(XY_SERIES_3, "14.13. XYSeries 3", "XY_SERIES_3.png"),
                new EnumValueToolTip(XY_SHAPE_RENDERER_1, "14.14. XYShapeRenderer 1", "XY_SHAPE_RENDERER_1.png"),
                new EnumValueToolTip(VECTOR_PLOT_1, "14.15. VectorPlot 1", "VECTOR_PLOT_1.png") };
        new PropertyNode(chart, "typeChart", ENUM_TOOL_TIP, type, false, defaultEnum);
        // атрибуты для всех видов диаграм
        // цифры в конце атрибута поясняют к какому типу диаграммы он относится (смотреть класс Constants)
        // если цифры отсутствуют - атрибут общий
        PropertyNode attr = new PropertyNode(chart, "attr", -1, null, false, null);
        new PropertyNode(chart, "dataChart", EXPR, null, false, null);
        new PropertyNode(chart, "isBtnSaveJPEG", BOOLEAN, null, false, true);

        EnumValue resolutionDef = new EnumValue(R1024_768, "1024x768");
        EnumValue[] resolutions = { new EnumValue(R1920_1200, "1920x1200"), new EnumValue(R1920_1080, "1920x1080"),
                new EnumValue(R1680_1050, "1680x1050"), new EnumValue(R1600_900, "1600x900"),
                new EnumValue(R1440_900, "1440x900"), new EnumValue(R1366_768, "1366x768"), new EnumValue(R1360_768, "1360x768"),
                new EnumValue(R1280_1024, "1280x1024"), new EnumValue(R1280_800, "1280x800"),
                new EnumValue(R1280_768, "1280x768"), resolutionDef, new EnumValue(R800_600, "800x600"),
                new EnumValue(R640_480, "640x480") };

        new PropertyNode(chart, "resolutionJPEG", ENUM, resolutions, false, resolutionDef);

        new PropertyNode(attr, "nameTaskSeries", STRING, null, false, null);
        new PropertyNode(attr, "leftVerticlLabel", STRING, null, false, null);
        new PropertyNode(attr, "upHorizontalLabel", STRING, null, false, null);
        new PropertyNode(attr, "taskPath601", REF, null, false, null);
        new PropertyNode(attr, "startPath601", REF, null, false, null);
        new PropertyNode(attr, "endPath601", REF, null, false, null);
        new PropertyNode(attr, "bla-bla-bla1", REF, null, false, null);
        new PropertyNode(attr, "bla-bla-bla2", REF, null, false, null);
        new PropertyNode(attr, "bla-bla-bla3", REF, null, false, null);
        new PropertyNode(attr, "countVisibleTask", INTEGER, null, false, 10);
        new PropertyNode(attr, "taskSeries", REF, null, false, null);
        EnumValue[] countSeries = { new EnumValue(1, "1"), new EnumValue(2, "2"), new EnumValue(3, "3"), new EnumValue(4, "4"),
                new EnumValue(5, "5"), new EnumValue(6, "6"), new EnumValue(7, "7"), new EnumValue(8, "8"),
                new EnumValue(9, "9"), new EnumValue(10, "10") };
        // Имя атрибута(атрибутов) для получения имени серии
        new PropertyNode(attr, "nameAttrSeries601", STRING, null, true, "наименование");
        // количество серий задач для графика
        new PropertyNode(attr, "countSeries601", ENUM, countSeries, false, 3);
        // раздел с сериями задач
        PropertyNode series = new PropertyNode(attr, "series", -1, null, false, null);
        new PropertyNode(series, "series1-601", KRNOBJECT_ID, null, true, null);
        new PropertyNode(series, "series2-601", KRNOBJECT_ID, null, true, null);
        new PropertyNode(series, "series3-601", KRNOBJECT_ID, null, true, null);
        new PropertyNode(series, "series4-601", KRNOBJECT_ID, null, true, null);
        new PropertyNode(series, "series5-601", KRNOBJECT_ID, null, true, null);
        new PropertyNode(series, "series6-601", KRNOBJECT_ID, null, true, null);
        new PropertyNode(series, "series7-601", KRNOBJECT_ID, null, true, null);
        new PropertyNode(series, "series8-601", KRNOBJECT_ID, null, true, null);
        new PropertyNode(series, "series9-601", KRNOBJECT_ID, null, true, null);
        new PropertyNode(series, "series10-601", KRNOBJECT_ID, null, true, null);
        PropertyNode colors = new PropertyNode(chart, "colors", -1, null, false, null);
        //Object[] value = new Object[] { new Color(184, 207, 238), new Color(184, 207, 238), Constants.HORIZONTAL, true, 0, 50 };
        GradientColor value =  new GradientColor("-4209202, -1775379, 3, 1, 0, 50, 1");
        new PropertyNode(colors, "colorControlPane", GRADIENT_COLOR, null, false, value);
        new PropertyNode(colors, "colorChartPane", GRADIENT_COLOR, null, false, value);
        new PropertyNode(colors, "colorChart", COLOR, null, false, new Color(192, 192, 192));
        //value = new Object[] { Color.white, Color.white, Constants.HORIZONTAL, true, 0, 50 };
        value = new GradientColor("-4209202, -1775379, 3, 1, 0, 50, 1");
        
        new PropertyNode(colors, "colorCanvasChart", GRADIENT_COLOR, null, false, value);
        EnumValue[] trChartBar = { new EnumValue(10, "0%"), new EnumValue(9, "10%"), new EnumValue(8, "20%"),
                new EnumValue(7, "30%"), new EnumValue(6, "40%"), new EnumValue(5, "50%"), new EnumValue(4, "60%"),
                new EnumValue(3, "70%"), new EnumValue(2, "80%"), new EnumValue(1, "90%"), new EnumValue(0, "100%") };

        new PropertyNode(chart, "transparencyChartBar", ENUM, trChartBar, false, 5);
        // размеры
        PropertyNode size = new PropertyNode(chart, "sizeChart", -1, null, false, null);
        new CompPosition(size);
    }
}
