package javacompiler.riscvtranslator.RiscV;

public class Prologue {
    private String code =   "  .equiv @sbrk, 9\n" +
                            "  .equiv @print_string, 4\n" +
                            "  .equiv @print_char, 11\n" +
                            "  .equiv @print_int, 1\n" +
                            "  .equiv @exit 10\n" +
                            "  .equiv @exit2, 17\n" +
                            "\n" +
                            ".text\n" +
                            "\n" +
                            "  jal Main\n" +
                            "  li a0, @exit\n" +
                            "  ecall\n";

    @Override
    public String toString() {
        return code;
    }    
}
