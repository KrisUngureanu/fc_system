package kz.tamur.fc.bank.record;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.4.6
 * 2014-01-29T09:20:21.298+06:00
 * Generated source version: 2.4.6
 * 
 */
@WebService(targetNamespace = "http://record.bank.fc.tamur.kz", name = "FCBANKServiceSoap")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface FCBANKServiceSoap {

    @WebResult(name = "transmitFromResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:transmitFromRecord")
    public ResponseType transmitFromRecord(
        @WebParam(partName = "parameters", name = "transmitFromRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        TransmitFromRequestType parameters
    );

    @WebResult(name = "educationOrgResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:educationOrg")
    public EducationOrgResponseType educationOrg(
        @WebParam(partName = "parameters", name = "educationOrgRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        EducationOrgRequestType parameters
    );

    @WebResult(name = "transmitResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:transmitRecord")
    public ResponseType transmitRecord(
        @WebParam(partName = "parameters", name = "transmitRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        TransmitRequestType parameters
    );

    @WebResult(name = "getFilePropertyResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:getFileProperty")
    public FilePropertyResponseType getFileProperty(
        @WebParam(partName = "parameters", name = "getFilePropertyRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        FilePropertyRequestType parameters
    );

    @WebResult(name = "getTransmitResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:getTransmit")
    public GetTransmitResponseType getTransmit(
        @WebParam(partName = "parameters", name = "getTransmitRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        GetTransmitRequestType parameters
    );

    @WebResult(name = "closeRecordResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:closeRecord")
    public CloseRecordResponseType closeRecord(
        @WebParam(partName = "parameters", name = "closeRecordRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        CloseRecordRequestType parameters
    );

    @WebResult(name = "checkFlResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:checkFl")
    public ResponseType checkFl(
        @WebParam(partName = "parameters", name = "checkFlRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        CheckFlRequestType parameters
    );

    @WebResult(name = "getPermitTransmitResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:getPermitTransmit")
    public ResponsePermitType getPermitTransmit(
        @WebParam(partName = "parameters", name = "getPermitTransmitRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        GetPermitTransmitType parameters
    );

    @WebResult(name = "summResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:summBonus")
    public SummResponseType summBonus(
        @WebParam(partName = "parameters", name = "summRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        SummRequestType parameters
    );

    @WebResult(name = "createRecordResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:createRecord")
    public CreateRecordResponseType createRecord(
        @WebParam(partName = "parameters", name = "createRecordRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        CreateRecordRequestType parameters
    );

    @WebResult(name = "filePropertyResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:fileProperty")
    public ResponseType fileProperty(
        @WebParam(partName = "parameters", name = "filePropertyRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        FilePropertyType parameters
    );

    @WebResult(name = "transferReestrResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:transferReestr")
    public TransferReestrResponseType transferReestr(
        @WebParam(partName = "parameters", name = "transferReestrRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        TransferReestrRequestType parameters
    );

    @WebResult(name = "getStatusResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:getStatus")
    public StatusResponseType getStatus(
        @WebParam(partName = "parameters", name = "getStatusRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        GetStatusRequestType parameters
    );

    @WebResult(name = "summReestrResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:summReestr")
    public SummReestrResponseType summReestr(
        @WebParam(partName = "parameters", name = "summReestrRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        SummReestrRequestType parameters
    );

    @WebResult(name = "getTransmitRecordResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:getTransmitRecord")
    public ResponseType getTransmitRecord(
        @WebParam(partName = "parameters", name = "getTransmitRecordRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        GetTransmitRecordType parameters
    );

    @WebResult(name = "getConfirmTransmitRecordResponse", targetNamespace = "http://record.bank.fc.tamur.kz", partName = "parameters")
    @WebMethod(action = "tns:getConfirmTransmitRecord")
    public ResponseType getConfirmTransmitRecord(
        @WebParam(partName = "parameters", name = "getConfirmTransmitRecordRequest", targetNamespace = "http://record.bank.fc.tamur.kz")
        GetConfirmTransmitRecordType parameters
    );
}
