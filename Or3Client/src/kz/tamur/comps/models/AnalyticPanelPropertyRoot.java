package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.STRING;
import static kz.tamur.comps.models.Types.RSTRING;

import java.awt.GridBagConstraints;

public class AnalyticPanelPropertyRoot extends PropertyRoot {
	public AnalyticPanelPropertyRoot() {
		super();
		PropertyNode title = new PropertyNode(this, "title", RSTRING, null, false, null);
        PropertyReestr.registerDebugProperty(title);
        PropertyReestr.registerProperty(title);
        
        // Позиция
		new CompPosition(this);
	
		PropertyNode analytic = new PropertyNode(this, "analytic", -1, null, false, null);
        new PropertyNode(analytic, "xAxis", EXPR, null, false, null);
        new PropertyNode(analytic, "yAxis", EXPR, null, false, null);
        new PropertyNode(analytic, "zAxis", EXPR, null, false, null);
        new PropertyNode(analytic, "firstXAxis", EXPR, null, false, null);
        new PropertyNode(analytic, "firstYAxis", EXPR, null, false, null);
        new PropertyNode(analytic, "fact", STRING, null, false, null);
        
        EnumValue[] evs = {
            new EnumValue(0, "Столбчатая"),
            new EnumValue(1, "Круговая"),
            new EnumValue(2, "Линейная"),
            new EnumValue(3, "Кольцевая"),
            new EnumValue(4, "Столбчатая 3D")
        };
        new PropertyNode(analytic, "type", Types.ENUM, evs, false, new Integer(0));
        new PropertyNode(analytic, "showLegend", Types.BOOLEAN, null, false, true);
        
        PropertyNode agg = new PropertyNode(analytic, "agg", -1, null, false, null);
        EnumValue[] aggType = {
                new EnumValue(0, "Количество"),
                new EnumValue(1, "Сумма"),
                new EnumValue(2, "Среднее")
        };
        new PropertyNode(agg, "aggType", Types.ENUM, aggType, false, new Integer(0));
        new PropertyNode(agg, "aggField", Types.STRING, null, false, null);
        
        PropertyNode ref = new PropertyNode(this, "ref", -1, null, false, null);
	}
}
