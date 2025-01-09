package kz.tamur.admin;

import static kz.tamur.rt.Utils.getMidSysColor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;

public class FiltersDialogPanel extends JPanel {
	private Kernel krn = Kernel.instance();

	public FiltersDialogPanel(List<Long> filtersIds, String name) throws KrnException {
        Border border = kz.tamur.rt.Utils.createTitledBorder(BorderFactory.createLineBorder(getMidSysColor()), "Фильтры, содержащие изменяемый атрибут");
        setBorder(border);
		Utils.setAllSize(this, new Dimension(600, 600));
		String[] data = new String[filtersIds.size()];
		KrnClass filterCls = krn.getClassByName("Filter");
		KrnAttribute titleAttr = krn.getAttributeByName(filterCls, "title");
		for (int i = 0; i < filtersIds.size(); i++) {
			try {
				String title  = krn.getStringsSingular(filtersIds.get(i), titleAttr.id, 0, false, false);
				data[i] = title;
			} catch (KrnException e) {
				e.printStackTrace();
			}
		}
		JList list = new JList(data) {
			public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
				int row;
				if (orientation == SwingConstants.VERTICAL && direction < 0 && (row = getFirstVisibleIndex()) != -1) {
					Rectangle r = getCellBounds(row, row);
					if ((r.y == visibleRect.y) && (row != 0)) {
						Point loc = r.getLocation();
						loc.y--;
						int prevIndex = locationToIndex(loc);
						Rectangle prevR = getCellBounds(prevIndex, prevIndex);
						if (prevR == null || prevR.y >= r.y) {
							return 0;
						}
						return prevR.height;
					}
				}
				return super.getScrollableUnitIncrement(visibleRect, orientation, direction);
			}
		};
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		Font ff0 = list.getFont();
		list.setFont(new Font(ff0.getName(), Font.PLAIN, 12));
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(600, 580));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);		
		add(listScroller, BorderLayout.CENTER);
	}
}