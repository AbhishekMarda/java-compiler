package javacompiler.translator.Helpers;

public class MiniJavaType {
    public MiniJavaType(String typeName) {
        this.name = typeName;
    }
    private String name;
    public String getName() {
        return name;
    }

    public static boolean isPrimitiveType(MiniJavaType type) {
        return type.equals(MiniJavaType.INTEGER) || type.equals(MiniJavaType.BOOLEAN) || type.equals(MiniJavaType.INTEGER_ARRAY);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        MiniJavaType other = (MiniJavaType) obj;
        return name.equals(other.name);
    }

    public static MiniJavaType INTEGER = new MiniJavaType("int");
    public static MiniJavaType BOOLEAN = new MiniJavaType("boolean");
    public static MiniJavaType INTEGER_ARRAY = new MiniJavaType("int[]");
}
