package kz.tamur.fc.bank.credit.guarantee;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {
 
	@Override
    public String marshal(Date dt) throws Exception {
        return new SimpleDateFormat("yyyy-MM-dd").format(dt);
    }

    @Override
    public Date unmarshal(String s) throws Exception {
        return DatatypeConverter.parseDate((String) s).getTime();
    }
}