package kz.tamur.or3.server.plugins.ekyzmet;
/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2009 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;

import net.sf.jasperreports.components.charts.AbstractChartCustomizer;
import net.sf.jasperreports.components.charts.ChartComponent;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: SpiderChartCustomizer.java,v 1.1 2017/01/30 10:57:09 berik Exp $
 */
public class SpiderChartCustomizer extends AbstractChartCustomizer
{

	public void customize(JFreeChart chart, ChartComponent chartComponent)
	{
		SpiderWebPlot plot = (SpiderWebPlot)chart.getPlot();
		plot.setLabelFont(new Font("Arial", Font.PLAIN, 8));
		plot.setAxistTickLabelFont(new Font("Arial", Font.PLAIN, 6));
	}
}