/**
 * StructureAlignment.java
 *
 * このファイルはWSDLから自動生成されました / [en]-(This file was auto-generated from WSDL)
 * Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java生成器によって / [en]-(by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.)
 */

package keggapi;

public class StructureAlignment  implements java.io.Serializable {
    private java.lang.String target_id;

    private float score;

    private int[] query_nodes;

    private int[] target_nodes;

    public StructureAlignment() {
    }

    public StructureAlignment(
           java.lang.String target_id,
           float score,
           int[] query_nodes,
           int[] target_nodes) {
           this.target_id = target_id;
           this.score = score;
           this.query_nodes = query_nodes;
           this.target_nodes = target_nodes;
    }


    /**
     * Gets the target_id value for this StructureAlignment.
     * 
     * @return target_id
     */
    public java.lang.String getTarget_id() {
        return target_id;
    }


    /**
     * Sets the target_id value for this StructureAlignment.
     * 
     * @param target_id
     */
    public void setTarget_id(java.lang.String target_id) {
        this.target_id = target_id;
    }


    /**
     * Gets the score value for this StructureAlignment.
     * 
     * @return score
     */
    public float getScore() {
        return score;
    }


    /**
     * Sets the score value for this StructureAlignment.
     * 
     * @param score
     */
    public void setScore(float score) {
        this.score = score;
    }


    /**
     * Gets the query_nodes value for this StructureAlignment.
     * 
     * @return query_nodes
     */
    public int[] getQuery_nodes() {
        return query_nodes;
    }


    /**
     * Sets the query_nodes value for this StructureAlignment.
     * 
     * @param query_nodes
     */
    public void setQuery_nodes(int[] query_nodes) {
        this.query_nodes = query_nodes;
    }


    /**
     * Gets the target_nodes value for this StructureAlignment.
     * 
     * @return target_nodes
     */
    public int[] getTarget_nodes() {
        return target_nodes;
    }


    /**
     * Sets the target_nodes value for this StructureAlignment.
     * 
     * @param target_nodes
     */
    public void setTarget_nodes(int[] target_nodes) {
        this.target_nodes = target_nodes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StructureAlignment)) return false;
        StructureAlignment other = (StructureAlignment) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.target_id==null && other.getTarget_id()==null) || 
             (this.target_id!=null &&
              this.target_id.equals(other.getTarget_id()))) &&
            this.score == other.getScore() &&
            ((this.query_nodes==null && other.getQuery_nodes()==null) || 
             (this.query_nodes!=null &&
              java.util.Arrays.equals(this.query_nodes, other.getQuery_nodes()))) &&
            ((this.target_nodes==null && other.getTarget_nodes()==null) || 
             (this.target_nodes!=null &&
              java.util.Arrays.equals(this.target_nodes, other.getTarget_nodes())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getTarget_id() != null) {
            _hashCode += getTarget_id().hashCode();
        }
        _hashCode += new Float(getScore()).hashCode();
        if (getQuery_nodes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getQuery_nodes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getQuery_nodes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTarget_nodes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTarget_nodes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTarget_nodes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // メタデータ型 / [en]-(Type metadata)
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StructureAlignment.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("SOAP/KEGG", "StructureAlignment"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("target_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "target_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("score");
        elemField.setXmlName(new javax.xml.namespace.QName("", "score"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("query_nodes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "query_nodes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("target_nodes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "target_nodes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * メタデータオブジェクトの型を返却 / [en]-(Return type metadata object)
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
