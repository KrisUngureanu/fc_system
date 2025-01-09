<%--
  Created by IntelliJ IDEA.
  User: erik-b
  Date: 28.09.2009
  Time: 11:26:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns:h="http://java.sun.com/jsf/html" xmlns:f="http://java.sun.com/jsf/core" xmlns:rich="http://richfaces.org/rich">
<head><title>Simple jsp page</title></head>
<body>
<f:view>
    <p>
        <h:message for="test" id="messageList" tooltip="true"/>
    </p>
    <h:panelGrid>
        <h:outputText>"Title"</h:outputText>
    </h:panelGrid>
    <h:form>
        <test:testInput id="test" value="#{page.testValue}" size="25"/>
        <br/>
        <h:commandButton value="Save"
                         action="#{page.saveValue}"/>

        <div style="width:100%; height:300px; overflow: auto; border: 1">
            <rich:dataTable id="taskList" rows="10" columnClasses="col"
                            value="#{page.users}" var="user" width="100%">

                <f:facet name="header">
                    <rich:columnGroup>
                        <h:column>
                            <h:outputText styleClass="headerText" value="First Name"/>
                        </h:column>
                        <h:column>
                            <h:outputText styleClass="headerText" value="Last Name"/>
                        </h:column>
                    </rich:columnGroup>
                </f:facet>
                <h:column>
                    <h:outputText value="#{user.firstName}"/>
                </h:column>
                <h:column>
                    <h:outputText value="#{user.lastName}"/>
                </h:column>
            </rich:dataTable>

            <rich:datascroller for="taskList" pageIndexVar="pageIndex" pagesVar="pages">
                <f:facet name="pages">
                    <h:outputText value=" #{pageIndex} / #{pages} "></h:outputText>
                </f:facet>
            </rich:datascroller>
        </div>
    </h:form>

</f:view>
</body>
</html>