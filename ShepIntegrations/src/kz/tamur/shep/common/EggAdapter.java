package kz.tamur.shep.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EggAdapter {
    private static final Class[] EXAMPLE_CLASSES = new Class[0];

    private static Class[] prepareClasses(Class[]... classArrays) {
        List<Class> classList = new ArrayList<Class>();
        
        if (classArrays != null) {
            for (Class[] classArray: classArrays) {
                if (classArray != null) {
                    for (Class clazz: classArray) {
                        if (clazz != null) {
                            classList.add(clazz);
                        }
                    }
                }
            }
        }
        
        if (classList.isEmpty()) {
            return EXAMPLE_CLASSES;
        } else {
            return classList.toArray(EXAMPLE_CLASSES);
        }
    }

    public static XMLGregorianCalendar generateXMLGC(Date date) {
        if (date == null) {
            return null;
        } else {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            try {
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            } catch (DatatypeConfigurationException exception) {
                exception.printStackTrace(System.err);
                return null;
            }
        }
    }

    public static XMLGregorianCalendar currentTimeXMLGC() {
        return generateXMLGC(new Date());
    }

    public static <T> T fromAnyType(Object anyObject, Class<T> resultClass, Class... additionalClasses)
            throws EggAdapterException {
        if (anyObject == null) {
            return null;
        }
        if (!Element.class.isAssignableFrom(anyObject.getClass())) {
            throw new EggAdapterException("unknown class of anyObject: " + anyObject.getClass().getName());
        }

        if (resultClass == null) {
            throw new EggAdapterException("resultClass is null");
        }

        Node node = ((Element) anyObject).getFirstChild();
        if (node == null) {
            return null;
        }

        Class[] classes = prepareClasses(new Class[] { JAXBElement.class, resultClass }, additionalClasses);

        try {
            JAXBElement<T> jaxbElement = (JAXBElement<T>) JAXBContext.newInstance(classes).createUnmarshaller().unmarshal(node, resultClass);
            return jaxbElement.getValue();
        } catch (JAXBException exception) {
            throw new EggAdapterException("error while parsing " + node.getLocalName() + " as " + resultClass.getName(), exception);
        }
    }
    
    public static <T> T fromAnyType(Object anyObject, Class<T> resultClass, String packageName) throws EggAdapterException {
        if (anyObject == null) {
            return null;
        }
        if (!Element.class.isAssignableFrom(anyObject.getClass())) {
            throw new EggAdapterException("unknown class of anyObject: " + anyObject.getClass().getName());
        }

        if (resultClass == null) {
            throw new EggAdapterException("resultClass is null");
        }

        Node node = ((Element) anyObject).getFirstChild();
        if (node == null) {
            return null;
        }

        try {
            JAXBElement<T> jaxbElement = (JAXBElement<T>) JAXBContext.newInstance(packageName).createUnmarshaller().unmarshal(node, resultClass);
            return jaxbElement.getValue();
        } catch (JAXBException exception) {
            throw new EggAdapterException("error while parsing " + node.getLocalName() + " as " + resultClass.getName(), exception);
        }
    }

    public static Element toAnyType(JAXBElement object, Class... additionalClasses) throws EggAdapterException {
        if (object == null) {
            return null;
        }
        
        Class[] classes = prepareClasses(new Class[] {object.getClass()}, additionalClasses);
        
        Element element;
        try {
            element = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("tempElement");
            JAXBContext.newInstance(classes).createMarshaller().marshal(object, element);
        } catch (DOMException exception) {
            throw new EggAdapterException("error while preparing temporary element", exception);
        } catch (JAXBException exception) {
            throw new EggAdapterException("error while preparing temporary element", exception);
        } catch (ParserConfigurationException exception) {
            throw new EggAdapterException("error while preparing temporary element", exception);
        }
        return element;
    }
    
    public static Element toAnyType(JAXBElement object, String packageName) throws EggAdapterException {
        if (object == null) {
            return null;
        }
        
        Element element;
        try {
            element = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("tempElement");
            JAXBContext.newInstance(packageName).createMarshaller().marshal(object, element);
        } catch (DOMException exception) {
            throw new EggAdapterException("error while preparing temporary element", exception);
        } catch (JAXBException exception) {
            throw new EggAdapterException("error while preparing temporary element", exception);
        } catch (ParserConfigurationException exception) {
            throw new EggAdapterException("error while preparing temporary element", exception);
        }
        return element;
    }
}