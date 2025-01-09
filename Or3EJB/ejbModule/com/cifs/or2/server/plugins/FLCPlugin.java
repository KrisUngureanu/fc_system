package com.cifs.or2.server.plugins;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import oracle.xml.parser.schema.XMLSchema;
import oracle.xml.parser.schema.XSDBuilder;
import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLParser;

import org.w3c.dom.Document;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class FLCPlugin implements SrvPlugin {

    private Session session;

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
	
    public List<String> checkData(Object data, String xsdFilePath) {
        List<String> errors = new ArrayList<String>();
        try {
            String packageName = data.getClass().getPackage().getName();

            // Преобразование объекта data в xml (обязательное условие - объект data должен быть корневым элементом)
            JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder().newDocument();
            jaxbMarshaller.marshal(data, document);

            XSDBuilder xsdBuilder = new XSDBuilder();
            try {
            	xsdBuilder.setXMLProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            	xsdBuilder.setXMLProperty(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            } catch (Exception e) {
            	System.out.println("ACCESS_EXTERNAL_DTD or ACCESS_EXTERNAL_STYLESHEET are not supported");
            }
            URL url = createURL(xsdFilePath);
            XMLSchema schemaDocument = (XMLSchema) xsdBuilder.build(url);

            String result = process(document, schemaDocument);

            if (result.trim().length() > 0) {
                errors = Arrays.asList(result.trim().split("\n"));
            }
        } catch (Exception e) {}
        return errors;
    }
	
    public String process(Document document, XMLSchema schemaDocument) {
        OutputStream output = new OutputStream() {
            private StringBuilder string = new StringBuilder();

            @Override
            public void write(int b) throws IOException {
                this.string.append((char) b);
            }

            public String toString() {
                return string.toString();
            }
        };
        try {
            DOMParser domParser = new DOMParser();
            domParser.setXMLSchema(schemaDocument);
            domParser.setValidationMode(XMLParser.SCHEMA_VALIDATION);
            domParser.setPreserveWhitespace(true);
            
            domParser.setErrorStream(output);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Source xmlSource = new DOMSource(document);
            Result outputTarget = new StreamResult(outputStream);
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
            domParser.parse(is);
            System.out.println("Входной xml-файл соответствует формату!");
        } catch (Exception e) {}
        return output.toString();
    }

    private URL createURL(String fileName) {
        URL url = null;
        try {
            url = new URL(fileName);
        } catch (MalformedURLException ex) {
            File file = new File(fileName);
            try {
                String path = file.getAbsolutePath();
                String fileSeparator = File.separator;
                if (fileSeparator.length() == 1) {
                    char separator = fileSeparator.charAt(0);
                    if (separator != '/')
                        path = path.replace(separator, '/');
                    if (path.charAt(0) != '/')
                        path = '/' + path;
                }
                path = "file://" + path;
                url = new URL(path);
            } catch (MalformedURLException e) {
                System.out.println("Cannot create url for: " + fileName);
            }
        }
        return url;
    }
}