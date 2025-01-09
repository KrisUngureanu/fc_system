package kz.tamur.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

public class ThreadLocalNumberFormat extends ThreadLocal<NumberFormat> {
	
	private String pattern;
	private char decimalSeparator;
	private char groupingSeparator;
	private boolean grouping;
	private int maxFractDigits;
	private int groupingSize;

	public ThreadLocalNumberFormat(String pattern) {
		this.pattern = pattern;
		this.decimalSeparator = 0;
		this.groupingSeparator = 0;
		this.grouping = true;
		this.maxFractDigits = -1;
		this.groupingSize = 0;
	}

	public ThreadLocalNumberFormat(String pattern, char decimalSeparator, char groupingSeparator, boolean grouping, int maxFractDigits, int groupingSize) {
		this.pattern = pattern;
		this.decimalSeparator = decimalSeparator;
		this.groupingSeparator = groupingSeparator;
		this.grouping = grouping;
		this.maxFractDigits = maxFractDigits;
		this.groupingSize = groupingSize;
	}

	@Override
	protected NumberFormat initialValue() {
		DecimalFormat fmt = pattern != null ? new DecimalFormat(pattern) : new DecimalFormat();
		
        fmt.setGroupingUsed(grouping);
        if (maxFractDigits > -1)
        	fmt.setMaximumFractionDigits(maxFractDigits);
        if (groupingSize > -1)
        	fmt.setGroupingSize(groupingSize);

        if (decimalSeparator > 0) {
			DecimalFormatSymbols syms = fmt.getDecimalFormatSymbols();
	        syms.setDecimalSeparator(decimalSeparator);
	        
	        if (groupingSeparator > 0)
	        	syms.setGroupingSeparator(groupingSeparator);

	        fmt.setDecimalFormatSymbols(syms);
		}

		return fmt;
	}

	public String format(Object num) {
		return get().format(num);
	}

	public Number parse(String str) throws ParseException {
		return get().parse(str);
	}
}
