package kz.tamur.shep.common;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Node;

public class DataConverter {

    public static Object convertFromString(String data, String packageName) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(packageName);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader((String) data);
        Object result = unmarshaller.unmarshal(reader);
        if (result.getClass().getName().equals("javax.xml.bind.JAXBElement")) {
            return ((JAXBElement<?>) result).getValue();
        }
        return result;
    }

    public static Object convertFromString(Object data, String packageName) throws JAXBException {
        Node requestNode = ((Node) data).getFirstChild();
        JAXBContext context = JAXBContext.newInstance(packageName);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object result = unmarshaller.unmarshal(requestNode);
        if (result.getClass().getName().equals("javax.xml.bind.JAXBElement")) {
            return ((JAXBElement<?>) result).getValue();
        }
        return result;
    }
}