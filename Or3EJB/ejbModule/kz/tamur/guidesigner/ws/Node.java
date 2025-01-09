package kz.tamur.guidesigner.ws;

import java.util.ArrayList;
import java.util.List;

class Node {
    private String name;
    private String namespace;
    private String typeName;
    private String typeURI;
    private boolean isComplexType;
    private List<Node> childs = new ArrayList<Node>();
    
    public Node() {}
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeURI() {
        return typeURI;
    }

    public void setTypeURI(String typeURI) {
        this.typeURI = typeURI;
    }

    public boolean isComplexType() {
        return isComplexType;
    }

    public void setComplexType(boolean isComplexType) {
        this.isComplexType = isComplexType;
    }

    public void addChild(Node childNode) {
        childs.add(childNode);
    }
    
    public void removeChild(Node childNode) {
        childs.remove(childNode);
    }
    
    public List<Node> getChilds() {
        return childs;
    }
    
//    public void toString() {
//        System.out.println(name + ":" + namespace);
//        
//    }
}