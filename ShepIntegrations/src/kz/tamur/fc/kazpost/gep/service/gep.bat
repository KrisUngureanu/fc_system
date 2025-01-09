rem usage: wsconsume [options] <wsdl-url>
rem options:
rem   -h, --help                  Show this help message
rem   -b, --binding=<file>        One or more JAX-WS or JAXB binding files
rem   -k, --keep                  Keep/Generate Java source
rem   -c  --catalog=<file>        Oasis XML Catalog file for entity resolution
rem   -j  --clientjar=<name>      Create a jar file of the generated artifacts for calling the webservice
rem   -p  --package=<name>        The target package for generated source
rem   -w  --wsdlLocation=<loc>    Value to use for @WebServiceClient.wsdlLocation
rem   -o, --output=<directory>    The directory to put generated artifacts
rem   -s, --source=<directory>    The directory to put Java source
rem   -t, --target=<2.1|2.2>      The JAX-WS specification target
rem   -q, --quiet                 Be somewhat more quiet
rem   -v, --verbose               Show full exception stack traces
rem   -l, --load-consumer         Load the consumer and exit (debug utility)
rem   -e, --extension             Enable SOAP 1.2 binding extension
rem   -a, --additionalHeaders     Enables processing of implicit SOAP headers
rem   -d, --encoding=<charset>    The charset encoding to use for generated sources
rem   -n, --nocompile             Do not compile generated sources

set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_80
D:\workspaces\gbdrn\jboss-eap-6.2\bin\wsconsume.bat -k -e -n -o out -p kz.tamur.fc.kazpost.gep.service -w SyncChannelHttp_Service.wsdl SyncChannelHttp_Service.wsdl