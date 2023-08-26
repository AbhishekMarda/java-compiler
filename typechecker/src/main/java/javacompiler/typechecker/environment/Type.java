package javacompiler.typechecker.environment;;

public class Type {
    public Type(String typeName) {
        this.name = typeName;
    }
    public String name;
    
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        
        if (other == null) 
            return false;

        if (other.getClass() != this.getClass())
            return false;
        
        Type otherType = (Type) other;
        return otherType.name.equals(this.name);
    }
}
