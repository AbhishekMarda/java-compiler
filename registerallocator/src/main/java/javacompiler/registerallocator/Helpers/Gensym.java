package javacompiler.registerallocator.Helpers;

public class Gensym {
    private static int counter = 0;
    public static String gensym(SVVarType id) {
        String ret = id.toString() + counter;
        counter++;
        return ret;
    }
    public static String getCurrent(SVVarType id) {
        return id.toString() + counter;
    }
}
