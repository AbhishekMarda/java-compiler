package javacompiler.riscvtranslator.RiscV;

public class Epilogue {
    private String code =    ".globl error\n" +
                                    "error:    \n" +
                                    "  mv a1, a0          \n" +
                                    "  li a0, @print_string     \n" +
                                    "  ecall    \n" +
                                    "  li a1, 10     \n" +
                                    "  li a0, @print_char   \n" +
                                    "  ecall   \n" +
                                    "  li a0, @exit   \n" +
                                    "  ecall   \n" +
                                    "abort_17:   \n" +
                                    "  j abort_17   \n" +
                                    "\n" +
                                    ".globl alloc    \n" +
                                    "alloc:    \n" +
                                    "  mv a1, a0    \n" +
                                    "  li a0, @sbrk   \n" +
                                    "  ecall   \n" +
                                    "  jr ra   \n" +
                                    "\n" +
                                    ".data   \n" +
                                    "\n" +
                                    ".globl msg_0   \n" +
                                    "msg_0:   \n" +
                                    "  .asciiz \"null pointer\"   \n" +
                                    "  .align 2\n" +
                                    "\n" +
                                    ".globl msg_1   \n" +
                                    "msg_1:   \n" +
                                    "  .asciiz \"array index out of bounds\"   \n" +
                                    "  .align 2\n";

    @Override
    public String toString() {
        return code;
    }
}
