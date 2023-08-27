package javacompiler.translator.Helpers;

public class MethodInfo {
    public MethodInfo(String name, String className, MiniJavaType returnType) {
        this.name = name;
        this.assocClass = className;
        this.returnType = returnType;
    }

    public String name;
    public String assocClass;
    public MiniJavaType returnType;

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
        
        MethodInfo other = (MethodInfo) obj;
        return name != null ? name.equals(other.name) : other.name == null;
    }
}
