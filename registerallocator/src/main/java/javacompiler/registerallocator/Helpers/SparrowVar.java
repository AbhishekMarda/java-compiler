package javacompiler.registerallocator.Helpers;

public class SparrowVar {
    String name;

    public SparrowVar(String name){
        this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        SparrowVar other = (SparrowVar) obj;
        return this.name.equals(other.name);
    }
}
