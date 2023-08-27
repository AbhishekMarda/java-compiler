
package javacompiler.compiler;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import static javacompiler.typechecker.Typecheck.typecheck;
import static javacompiler.translator.J2S.translateToIR;
import static javacompiler.registerallocator.S2SV.allocateRegisters;
import static javacompiler.riscvtranslator.SV2V.IRtoRiscV;

import cs132.minijava.syntaxtree.Node;

public class Compiler {
    public static void main(String[] args) {
        Options options = new Options();
        Option inputOption = Option.builder("f")
                .required()
                .hasArg()
                .desc("Input file")
                .build();
        options.addOption(inputOption);

        // Create a CommandLineParser
        CommandLineParser parser = new DefaultParser();

        try {
            // Parse the command line arguments
            CommandLine cmd = parser.parse(options, args);

            // Access the values of the options
            String inputFile = cmd.getOptionValue("f");

            // Perform actions based on the parsed options

            File file = new File(inputFile);
            if (!file.exists())
                throw new FileNotFoundException("Could not find " + inputFile);

            // Main logic
            Node miniJavaRoot = typecheck(inputFile);
            if (miniJavaRoot == null)
                throw new Exception("Code did not type check successfully.");
            String output = translateToIR(miniJavaRoot);
            output = allocateRegisters(output);
            output = IRtoRiscV(output);
            // Get the parent directory of the existing file
            String parentDirectory = file.getParent();
            // Specify the name of the new file
            String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
            String newFileName = fileNameWithOutExt + ".v";

            // Create a File object representing the new file in the same directory
            File newFile = new File(parentDirectory, newFileName);
            if (parentDirectory == null) {
                newFile = new File(newFileName);
            }

            FileWriter fileWriter = new FileWriter(newFile);
            fileWriter.write(output);
            fileWriter.close();

        } catch (ParseException e) {
            // Handle parsing exceptions
            System.err.println("Error: " + e.getMessage());
            printHelp(options);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Compiler", options);
    }
}
