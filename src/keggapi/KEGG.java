/**
 * KEGG.java
 *
 * このファイルはWSDLから自動生成されました / [en]-(This file was auto-generated from WSDL)
 * Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java生成器によって / [en]-(by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.)
 */

package keggapi;

public interface KEGG extends javax.xml.rpc.Service {
    public java.lang.String getKEGGPortAddress();

    public keggapi.KEGGPortType getKEGGPort() throws javax.xml.rpc.ServiceException;

    public keggapi.KEGGPortType getKEGGPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
