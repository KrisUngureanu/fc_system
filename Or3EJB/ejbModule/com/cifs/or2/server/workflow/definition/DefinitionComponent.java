package com.cifs.or2.server.workflow.definition;

import java.io.InputStream;
import java.util.Collection;

import com.cifs.or2.server.Session;

public interface DefinitionComponent {
    ProcessDefinition deployProcess(Long processId, InputStream processStream, InputStream[] messageStreams, Session s);
    ProcessDefinition getProcessDefinition(Long processId);
    ProcessDefinition getProcessDefinition(String name);
    Collection getProcessDefinition();
}
