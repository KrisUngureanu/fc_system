/**
 * 
 */
package com.cifs.or2.client.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class CnrBuilder {
	
    private GridBagConstraints cnr = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0);
	
	public GridBagConstraints build() {
		return cnr;
	}
	
	public CnrBuilder x(int x) {
		cnr.gridx = x;
		return this;
	}

	public CnrBuilder y(int y) {
		cnr.gridy = y;
		return this;
	}

	public CnrBuilder w(int w) {
		cnr.gridwidth = w;
		return this;
	}

	public CnrBuilder h(int h) {
		cnr.gridheight = h;
		return this;
	}
	
	public CnrBuilder wtx(double wtx) {
		cnr.weightx = wtx;
		return this;
	}

	public CnrBuilder wty(double wty) {
		cnr.weighty = wty;
		return this;
	}

	public CnrBuilder anchor(int anchor) {
		cnr.anchor = anchor;
		return this;
	}

	public CnrBuilder fill(int fill) {
		cnr.fill = fill;
		return this;
	}

	public CnrBuilder insLeft(int left) {
		cnr.insets.left = left;
		return this;
	}

	public CnrBuilder insRight(int right) {
		cnr.insets.right = right;
		return this;
	}

	public CnrBuilder insTop(int top) {
		cnr.insets.top = top;
		return this;
	}
	
	public CnrBuilder insBottom(int bottom) {
		cnr.insets.bottom = bottom;
		return this;
	}

	public CnrBuilder ins(int top, int left, int bottom, int right) {
		cnr.insets.top = top;
		cnr.insets.left = left;
		cnr.insets.bottom = bottom;
		cnr.insets.right = right;
		return this;
	}
}