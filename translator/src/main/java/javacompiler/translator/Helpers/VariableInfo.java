package javacompiler.translator.Helpers;

public class VariableInfo {
    public VariableInfo(String name, String className, MiniJavaType type) {
        this.name = name;
        this.assocClass = className;
        this.type = type;
    }
    public String name;
    public String assocClass;
    public MiniJavaType type;

    public void setAssocClass(String assocClass) {
        this.assocClass = assocClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        VariableInfo other = (VariableInfo) obj;
        return name != null ? name.equals(other.name) : other.name == null;
    }
}
