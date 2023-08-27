package javacompiler.translator.Helpers;

/*
 * There are 3 possible types: 
 * 1. Integer
 * 2. Address
 * 3. Funtion
 */
public class SparrowType {
    private SparrowType(String name) {
        this.name = name;
    }
    private String name;

    public static SparrowType INTEGER_TYPE = new SparrowType("integer");
    public static SparrowType ADDRESS_TYPE = new SparrowType("address");
    public static SparrowType FUNCTION_TYPE = new SparrowType("function");

    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        SparrowType other = (SparrowType) obj;
        return name.equals(other.name);
    }

}
