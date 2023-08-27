package javacompiler.translator.Helpers;

public class SparrowIdentifierType {

    private SparrowIdentifierType(String id) {
        this.name = id;
    }
    public final String name;
    public static SparrowIdentifierType VARTYPE = new SparrowIdentifierType("v");
    public static SparrowIdentifierType LABELTYPE = new SparrowIdentifierType("l");
    public static SparrowIdentifierType NULLTYPE = new SparrowIdentifierType("null");
    public static SparrowIdentifierType VTABLE_TYPE = new SparrowIdentifierType("vmt");
}
