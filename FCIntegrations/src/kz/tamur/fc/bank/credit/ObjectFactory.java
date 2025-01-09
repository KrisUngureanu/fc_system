
package kz.tamur.fc.bank.credit;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.fc.bank.credit package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _BatchFileDto_QNAME = new QName("http://data.chdb.scb.kz", "batchFileDto");
    private final static QName _BatchRecordDto_QNAME = new QName("http://data.chdb.scb.kz", "batchRecordDto");
    private final static QName _DateFromToParams_QNAME = new QName("http://data.chdb.scb.kz", "dateFromToParams");
    private final static QName _BatchPackageDto_QNAME = new QName("http://data.chdb.scb.kz", "batchPackageDto");
    private final static QName _GetImportInfoResponse_QNAME = new QName("http://data.chdb.scb.kz", "getImportInfoResponse");
    private final static QName _ProcessFile_QNAME = new QName("http://data.chdb.scb.kz", "processFile");
    private final static QName _BatchPackageParams_QNAME = new QName("http://data.chdb.scb.kz", "batchPackageParams");
    private final static QName _GetImportInfo_QNAME = new QName("http://data.chdb.scb.kz", "getImportInfo");
    private final static QName _SaveResult_QNAME = new QName("http://data.chdb.scb.kz", "saveResult");
    private final static QName _VersionedDto_QNAME = new QName("http://data.chdb.scb.kz", "versionedDto");
    private final static QName _DataSourceDto_QNAME = new QName("http://data.chdb.scb.kz", "dataSourceDto");
    private final static QName _GetAvgTimeOneContractImport_QNAME = new QName("http://data.chdb.scb.kz", "getAvgTimeOneContractImport");
    private final static QName _BatchFilesImportInfo_QNAME = new QName("http://data.chdb.scb.kz", "batchFilesImportInfo");
    private final static QName _BatchRecordsImportInfo_QNAME = new QName("http://data.chdb.scb.kz", "batchRecordsImportInfo");
    private final static QName _GetImportDetailInfo_QNAME = new QName("http://data.chdb.scb.kz", "getImportDetailInfo");
    private final static QName _GetImportDetailInfoResponse_QNAME = new QName("http://data.chdb.scb.kz", "getImportDetailInfoResponse");
    private final static QName _ScbBusinessFault_QNAME = new QName("http://data.chdb.scb.kz", "ScbBusinessFault");
    private final static QName _ProcessFileResponse_QNAME = new QName("http://data.chdb.scb.kz", "processFileResponse");
    private final static QName _UploadFile_QNAME = new QName("http://data.chdb.scb.kz", "uploadFile");
    private final static QName _BatchProvidedDto_QNAME = new QName("http://data.chdb.scb.kz", "batchProvidedDto");
    private final static QName _GetImportInfoWithComplexParamsResponse_QNAME = new QName("http://data.chdb.scb.kz", "getImportInfoWithComplexParamsResponse");
    private final static QName _BatchComplexParams_QNAME = new QName("http://data.chdb.scb.kz", "batchComplexParams");
    private final static QName _GetImportInfoWithComplexParams_QNAME = new QName("http://data.chdb.scb.kz", "getImportInfoWithComplexParams");
    private final static QName _BatchImportStatisticResult_QNAME = new QName("http://data.chdb.scb.kz", "batchImportStatisticResult");
    private final static QName _GetAvgTimeOneContractImportResponse_QNAME = new QName("http://data.chdb.scb.kz", "getAvgTimeOneContractImportResponse");
    private final static QName _ValidationFault_QNAME = new QName("http://data.chdb.scb.kz", "ValidationFault");
    private final static QName _BatchFileParams_QNAME = new QName("http://data.chdb.scb.kz", "batchFileParams");
    private final static QName _UploadFileResponse_QNAME = new QName("http://data.chdb.scb.kz", "uploadFileResponse");
    private final static QName _ScbServiceFaultInfo_QNAME = new QName("http://data.chdb.scb.kz", "scbServiceFaultInfo");
    private final static QName _ScbSystemFault_QNAME = new QName("http://data.chdb.scb.kz", "ScbSystemFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.fc.bank.credit
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BatchPackageDto }
     * 
     */
    public BatchPackageDto createBatchPackageDto() {
        return new BatchPackageDto();
    }

    /**
     * Create an instance of {@link GetImportInfoResponse }
     * 
     */
    public GetImportInfoResponse createGetImportInfoResponse() {
        return new GetImportInfoResponse();
    }

    /**
     * Create an instance of {@link ProcessFile }
     * 
     */
    public ProcessFile createProcessFile() {
        return new ProcessFile();
    }

    /**
     * Create an instance of {@link BatchPackageParams }
     * 
     */
    public BatchPackageParams createBatchPackageParams() {
        return new BatchPackageParams();
    }

    /**
     * Create an instance of {@link GetImportInfo }
     * 
     */
    public GetImportInfo createGetImportInfo() {
        return new GetImportInfo();
    }

    /**
     * Create an instance of {@link DateFromToParams }
     * 
     */
    public DateFromToParams createDateFromToParams() {
        return new DateFromToParams();
    }

    /**
     * Create an instance of {@link SaveResult }
     * 
     */
    public SaveResult createSaveResult() {
        return new SaveResult();
    }

    /**
     * Create an instance of {@link BatchRecordDto }
     * 
     */
    public BatchRecordDto createBatchRecordDto() {
        return new BatchRecordDto();
    }

    /**
     * Create an instance of {@link BatchFileDto }
     * 
     */
    public BatchFileDto createBatchFileDto() {
        return new BatchFileDto();
    }

    /**
     * Create an instance of {@link GetAvgTimeOneContractImport }
     * 
     */
    public GetAvgTimeOneContractImport createGetAvgTimeOneContractImport() {
        return new GetAvgTimeOneContractImport();
    }

    /**
     * Create an instance of {@link ScbServiceFaultInfo }
     * 
     */
    public ScbServiceFaultInfo createScbServiceFaultInfo() {
        return new ScbServiceFaultInfo();
    }

    /**
     * Create an instance of {@link ProcessFileResponse }
     * 
     */
    public ProcessFileResponse createProcessFileResponse() {
        return new ProcessFileResponse();
    }

    /**
     * Create an instance of {@link UploadFile }
     * 
     */
    public UploadFile createUploadFile() {
        return new UploadFile();
    }

    /**
     * Create an instance of {@link GetImportInfoWithComplexParamsResponse }
     * 
     */
    public GetImportInfoWithComplexParamsResponse createGetImportInfoWithComplexParamsResponse() {
        return new GetImportInfoWithComplexParamsResponse();
    }

    /**
     * Create an instance of {@link BatchFilesImportInfo }
     * 
     */
    public BatchFilesImportInfo createBatchFilesImportInfo() {
        return new BatchFilesImportInfo();
    }

    /**
     * Create an instance of {@link BatchRecordsImportInfo }
     * 
     */
    public BatchRecordsImportInfo createBatchRecordsImportInfo() {
        return new BatchRecordsImportInfo();
    }

    /**
     * Create an instance of {@link GetImportDetailInfo }
     * 
     */
    public GetImportDetailInfo createGetImportDetailInfo() {
        return new GetImportDetailInfo();
    }

    /**
     * Create an instance of {@link GetImportDetailInfoResponse }
     * 
     */
    public GetImportDetailInfoResponse createGetImportDetailInfoResponse() {
        return new GetImportDetailInfoResponse();
    }

    /**
     * Create an instance of {@link BatchFileParams }
     * 
     */
    public BatchFileParams createBatchFileParams() {
        return new BatchFileParams();
    }

    /**
     * Create an instance of {@link UploadFileResponse }
     * 
     */
    public UploadFileResponse createUploadFileResponse() {
        return new UploadFileResponse();
    }

    /**
     * Create an instance of {@link ValidationFaultBean }
     * 
     */
    public ValidationFaultBean createValidationFaultBean() {
        return new ValidationFaultBean();
    }

    /**
     * Create an instance of {@link BatchComplexParams }
     * 
     */
    public BatchComplexParams createBatchComplexParams() {
        return new BatchComplexParams();
    }

    /**
     * Create an instance of {@link GetImportInfoWithComplexParams }
     * 
     */
    public GetImportInfoWithComplexParams createGetImportInfoWithComplexParams() {
        return new GetImportInfoWithComplexParams();
    }

    /**
     * Create an instance of {@link GetAvgTimeOneContractImportResponse }
     * 
     */
    public GetAvgTimeOneContractImportResponse createGetAvgTimeOneContractImportResponse() {
        return new GetAvgTimeOneContractImportResponse();
    }

    /**
     * Create an instance of {@link BatchImportStatisticResult }
     * 
     */
    public BatchImportStatisticResult createBatchImportStatisticResult() {
        return new BatchImportStatisticResult();
    }

    /**
     * Create an instance of {@link PaginationSupportDto }
     * 
     */
    public PaginationSupportDto createPaginationSupportDto() {
        return new PaginationSupportDto();
    }

    /**
     * Create an instance of {@link ConstraintViolation }
     * 
     */
    public ConstraintViolation createConstraintViolation() {
        return new ConstraintViolation();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchFileDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "batchFileDto")
    public JAXBElement<BatchFileDto> createBatchFileDto(BatchFileDto value) {
        return new JAXBElement<BatchFileDto>(_BatchFileDto_QNAME, BatchFileDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchRecordDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "batchRecordDto")
    public JAXBElement<BatchRecordDto> createBatchRecordDto(BatchRecordDto value) {
        return new JAXBElement<BatchRecordDto>(_BatchRecordDto_QNAME, BatchRecordDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DateFromToParams }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "dateFromToParams")
    public JAXBElement<DateFromToParams> createDateFromToParams(DateFromToParams value) {
        return new JAXBElement<DateFromToParams>(_DateFromToParams_QNAME, DateFromToParams.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchPackageDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "batchPackageDto")
    public JAXBElement<BatchPackageDto> createBatchPackageDto(BatchPackageDto value) {
        return new JAXBElement<BatchPackageDto>(_BatchPackageDto_QNAME, BatchPackageDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetImportInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "getImportInfoResponse")
    public JAXBElement<GetImportInfoResponse> createGetImportInfoResponse(GetImportInfoResponse value) {
        return new JAXBElement<GetImportInfoResponse>(_GetImportInfoResponse_QNAME, GetImportInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "processFile")
    public JAXBElement<ProcessFile> createProcessFile(ProcessFile value) {
        return new JAXBElement<ProcessFile>(_ProcessFile_QNAME, ProcessFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchPackageParams }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "batchPackageParams")
    public JAXBElement<BatchPackageParams> createBatchPackageParams(BatchPackageParams value) {
        return new JAXBElement<BatchPackageParams>(_BatchPackageParams_QNAME, BatchPackageParams.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetImportInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "getImportInfo")
    public JAXBElement<GetImportInfo> createGetImportInfo(GetImportInfo value) {
        return new JAXBElement<GetImportInfo>(_GetImportInfo_QNAME, GetImportInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "saveResult")
    public JAXBElement<SaveResult> createSaveResult(SaveResult value) {
        return new JAXBElement<SaveResult>(_SaveResult_QNAME, SaveResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VersionedDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "versionedDto")
    public JAXBElement<VersionedDto> createVersionedDto(VersionedDto value) {
        return new JAXBElement<VersionedDto>(_VersionedDto_QNAME, VersionedDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataSourceDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "dataSourceDto")
    public JAXBElement<DataSourceDto> createDataSourceDto(DataSourceDto value) {
        return new JAXBElement<DataSourceDto>(_DataSourceDto_QNAME, DataSourceDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvgTimeOneContractImport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "getAvgTimeOneContractImport")
    public JAXBElement<GetAvgTimeOneContractImport> createGetAvgTimeOneContractImport(GetAvgTimeOneContractImport value) {
        return new JAXBElement<GetAvgTimeOneContractImport>(_GetAvgTimeOneContractImport_QNAME, GetAvgTimeOneContractImport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchFilesImportInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "batchFilesImportInfo")
    public JAXBElement<BatchFilesImportInfo> createBatchFilesImportInfo(BatchFilesImportInfo value) {
        return new JAXBElement<BatchFilesImportInfo>(_BatchFilesImportInfo_QNAME, BatchFilesImportInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchRecordsImportInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "batchRecordsImportInfo")
    public JAXBElement<BatchRecordsImportInfo> createBatchRecordsImportInfo(BatchRecordsImportInfo value) {
        return new JAXBElement<BatchRecordsImportInfo>(_BatchRecordsImportInfo_QNAME, BatchRecordsImportInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetImportDetailInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "getImportDetailInfo")
    public JAXBElement<GetImportDetailInfo> createGetImportDetailInfo(GetImportDetailInfo value) {
        return new JAXBElement<GetImportDetailInfo>(_GetImportDetailInfo_QNAME, GetImportDetailInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetImportDetailInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "getImportDetailInfoResponse")
    public JAXBElement<GetImportDetailInfoResponse> createGetImportDetailInfoResponse(GetImportDetailInfoResponse value) {
        return new JAXBElement<GetImportDetailInfoResponse>(_GetImportDetailInfoResponse_QNAME, GetImportDetailInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ScbServiceFaultInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "ScbBusinessFault")
    public JAXBElement<ScbServiceFaultInfo> createScbBusinessFault(ScbServiceFaultInfo value) {
        return new JAXBElement<ScbServiceFaultInfo>(_ScbBusinessFault_QNAME, ScbServiceFaultInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "processFileResponse")
    public JAXBElement<ProcessFileResponse> createProcessFileResponse(ProcessFileResponse value) {
        return new JAXBElement<ProcessFileResponse>(_ProcessFileResponse_QNAME, ProcessFileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UploadFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "uploadFile")
    public JAXBElement<UploadFile> createUploadFile(UploadFile value) {
        return new JAXBElement<UploadFile>(_UploadFile_QNAME, UploadFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchProvidedDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "batchProvidedDto")
    public JAXBElement<BatchProvidedDto> createBatchProvidedDto(BatchProvidedDto value) {
        return new JAXBElement<BatchProvidedDto>(_BatchProvidedDto_QNAME, BatchProvidedDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetImportInfoWithComplexParamsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "getImportInfoWithComplexParamsResponse")
    public JAXBElement<GetImportInfoWithComplexParamsResponse> createGetImportInfoWithComplexParamsResponse(GetImportInfoWithComplexParamsResponse value) {
        return new JAXBElement<GetImportInfoWithComplexParamsResponse>(_GetImportInfoWithComplexParamsResponse_QNAME, GetImportInfoWithComplexParamsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchComplexParams }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "batchComplexParams")
    public JAXBElement<BatchComplexParams> createBatchComplexParams(BatchComplexParams value) {
        return new JAXBElement<BatchComplexParams>(_BatchComplexParams_QNAME, BatchComplexParams.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetImportInfoWithComplexParams }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "getImportInfoWithComplexParams")
    public JAXBElement<GetImportInfoWithComplexParams> createGetImportInfoWithComplexParams(GetImportInfoWithComplexParams value) {
        return new JAXBElement<GetImportInfoWithComplexParams>(_GetImportInfoWithComplexParams_QNAME, GetImportInfoWithComplexParams.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchImportStatisticResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "batchImportStatisticResult")
    public JAXBElement<BatchImportStatisticResult> createBatchImportStatisticResult(BatchImportStatisticResult value) {
        return new JAXBElement<BatchImportStatisticResult>(_BatchImportStatisticResult_QNAME, BatchImportStatisticResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvgTimeOneContractImportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "getAvgTimeOneContractImportResponse")
    public JAXBElement<GetAvgTimeOneContractImportResponse> createGetAvgTimeOneContractImportResponse(GetAvgTimeOneContractImportResponse value) {
        return new JAXBElement<GetAvgTimeOneContractImportResponse>(_GetAvgTimeOneContractImportResponse_QNAME, GetAvgTimeOneContractImportResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidationFaultBean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "ValidationFault")
    public JAXBElement<ValidationFaultBean> createValidationFault(ValidationFaultBean value) {
        return new JAXBElement<ValidationFaultBean>(_ValidationFault_QNAME, ValidationFaultBean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchFileParams }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "batchFileParams")
    public JAXBElement<BatchFileParams> createBatchFileParams(BatchFileParams value) {
        return new JAXBElement<BatchFileParams>(_BatchFileParams_QNAME, BatchFileParams.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UploadFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "uploadFileResponse")
    public JAXBElement<UploadFileResponse> createUploadFileResponse(UploadFileResponse value) {
        return new JAXBElement<UploadFileResponse>(_UploadFileResponse_QNAME, UploadFileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ScbServiceFaultInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "scbServiceFaultInfo")
    public JAXBElement<ScbServiceFaultInfo> createScbServiceFaultInfo(ScbServiceFaultInfo value) {
        return new JAXBElement<ScbServiceFaultInfo>(_ScbServiceFaultInfo_QNAME, ScbServiceFaultInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ScbServiceFaultInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.chdb.scb.kz", name = "ScbSystemFault")
    public JAXBElement<ScbServiceFaultInfo> createScbSystemFault(ScbServiceFaultInfo value) {
        return new JAXBElement<ScbServiceFaultInfo>(_ScbSystemFault_QNAME, ScbServiceFaultInfo.class, null, value);
    }

}
