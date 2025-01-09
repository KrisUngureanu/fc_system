<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:h="http://java.sun.com/jsf/html" xmlns:f="http://java.sun.com/jsf/core" xmlns:a4j="http://richfaces.org/a4j" xmlns:rich="http://richfaces.org/rich">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Анкета политического служащего</title>
</head>
<body>
<f:view>
    <h:form id="tabs_form">
    <rich:tabPanel switchType="ajax">
        <rich:tab>
            <f:facet name="label">
                <h:panelGroup>
                    <h:outputText value="Ajax with RichFaces" />
                </h:panelGroup>
            </f:facet>
            <p>
                The framework is implemented using a component library. 
                The library set Ajax functionality into existing pages, so 
                there is no need to write any JavaScript code or to replace 
                existing components with new Ajax one. Ajax4jsf enables page-wide 
                Ajax support instead of the traditional component-wide support and 
                it gives the opportunity to define the event on the page. An event 
                invokes an Ajax request and areas of the page which are synchronized
                 with the JSF Component Tree after changing the data on the server 
                 by Ajax request in accordance with events fired on the client.         
            </p>
                    <h:panelGrid columns="2" columnClasses="top,top">
            <rich:fileUpload fileUploadListener="#{fotoUploadBean.listener}"
                maxFilesQuantity="#{fotoUploadBean.uploadsAvailable}"
                id="upload"
                immediateUpload="#{fotoUploadBean.autoUpload}"
                acceptedTypes="jpg, gif, png, bmp">
                <a4j:support event="onuploadcomplete" reRender="info" />
            </rich:fileUpload>
            <h:panelGroup id="info">
                <rich:panel bodyClass="info">
                    <f:facet name="header">
                        <h:outputText value="Uploaded Files Info" />
                    </f:facet>
                    <h:outputText value="No files currently uploaded"
                        rendered="#{fotoUploadBean.size==0}" />
                    <rich:dataGrid columns="1" value="#{fotoUploadBean.files}"
                        var="file" rowKeyVar="row">
                        <rich:panel bodyClass="rich-laguna-panel-no-header">
                            <h:panelGrid columns="2">
                                <a4j:mediaOutput element="img" mimeType="#{file.mime}"
                                    createContent="#{fotoUploadBean.paint}" value="#{row}"
                                    style="width:100px; height:100px;" cacheable="false">
                                    <f:param value="#{fotoUploadBean.timeStamp}" name="time"/>  
                                </a4j:mediaOutput>
                                <h:panelGrid columns="2">
                                    <h:outputText value="File Name:" />
                                    <h:outputText value="#{file.name}" />
                                    <h:outputText value="File Length(bytes):" />
                                    <h:outputText value="#{file.length}" />
                                </h:panelGrid>
                            </h:panelGrid>
                        </rich:panel>
                    </rich:dataGrid>
                </rich:panel>
                <rich:spacer height="3"/>
                <br />
                <a4j:commandButton action="#{fotoUploadBean.clearUploadData}"
                    reRender="info, upload" value="Clear Uploaded Data"
                    rendered="#{fotoUploadBean.size>0}" />
            </h:panelGroup>
        </h:panelGrid>
        </rich:tab>
    </rich:tabPanel>
    </h:form>
    <rich:messages></rich:messages>
</f:view>
</body>
</html>