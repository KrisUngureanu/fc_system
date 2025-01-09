package kz.tamur.or3.client.props;

public class Expression {

	public final String text;

	public Expression(String text) {
		this.text = text;
	}

    public boolean equals(Object obj) {
        if (this == obj || (obj==null && "".equals(text))) {
            return true;
        }
        if(obj instanceof Expression) {
            return  (text==null||"".equals(text)?((Expression)obj).text==null||"".equals(((Expression)obj).text):text.equals(((Expression)obj).text));
        }
        return false;
    }
    
    public String getExprString() {
    	return text;
    }
}
