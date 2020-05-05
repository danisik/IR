package cz.zcu.kiv.nlp.ir.trec.utils;

import cz.zcu.kiv.nlp.ir.trec.data.Constants;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

public class Utils {

    public static double roundDoubleNumber(double number) {
        return Math.floor(number * Constants.ROUNDING_NUMBER) / Constants.ROUNDING_NUMBER;
    }

    public static final java.text.DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd_HH_mm_SS");

    /**
     * Saves text to given file.
     *
     * @param file file to save
     * @param text text to save
     */
    public static void saveFile(File file, String text) {
        try {
            PrintStream printStream = new PrintStream(new FileOutputStream(file));
            printStream.print(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file file to save
     * @param list lines of text to save
     */
    public static void saveFile(File file, Collection<String> list) {
        try {

            PrintStream printStream = new PrintStream(new FileOutputStream(file));
            for (String text : list) {
                printStream.println(text);
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
    }

    public static ArrayList<String> readFromFile(String file) {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ( (line = br.readLine()) != null ) {
                lines.add(line);
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }
}
