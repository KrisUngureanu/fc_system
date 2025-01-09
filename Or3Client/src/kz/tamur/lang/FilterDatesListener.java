package kz.tamur.lang;

import java.util.Map;

import com.cifs.or2.kernel.Date;

public interface FilterDatesListener {

	void changed(Map<Integer, Date> filterDates);
}
