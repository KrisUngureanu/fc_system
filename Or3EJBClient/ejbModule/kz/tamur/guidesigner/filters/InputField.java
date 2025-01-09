package kz.tamur.guidesigner.filters;

import javax.swing.JTextField;

import kz.tamur.util.DateField;

public class InputField {

    JTextField field = new JTextField();
    String paramName;
    public  InputField(JTextField field)
    {
        this.field = field;
    }
    void setText(String text)
    {field.setText(text);}
    String getText()
    {
        return field.getText();
    }
    public int getDateFormat()
    {
        if(field instanceof DateField)
        { 
           return ((DateField)field).getDateFormat();
        }
        return -1;
    }
}