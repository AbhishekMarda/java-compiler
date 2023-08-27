package javacompiler.translator.Helpers;

public class Gensym {
    private static int counter = 0;
    public static String gensym(SparrowIdentifierType id) {
        String ret = id.name + counter;
        counter++;
        return ret;
    }
    public static String getCurrent(SparrowIdentifierType id) {
        return id.name + counter;
    }
}
