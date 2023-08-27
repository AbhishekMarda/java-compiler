package javacompiler.riscvtranslator.Helpers;

public class Gensym {
    private static int counter = 0;
    public static String gensym(String id) {
        String ret = id + counter;
        counter++;
        return ret;
    }
    public static String getCurrent(String id) {
        return id + counter;
    }

    public static String NULL = "null";
    public static String LABEL = "label";
}