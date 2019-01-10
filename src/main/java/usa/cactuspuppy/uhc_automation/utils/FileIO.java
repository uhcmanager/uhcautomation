package usa.cactuspuppy.uhc_automation.utils;

import java.io.*;
import java.util.Scanner;

public final class FileIO {
    /**
     * Returns token from the first line
     * <p>
     *     Note: Assumes the token is on the first line, and that the line is formatted as:
     *     "{@literal <word>}: {@literal <token>}"
     * </p>
     * @param iS input stream to read from
     * @return token
     */
    public static String readToken(InputStream iS) {
        Scanner scan = new Scanner(iS);
        String line = scan.nextLine();
        scan.close();
        line = line.substring(line.indexOf(':') + 1);
        return line.trim();
    }

    /**
     * Saves an input stream to a file
     * @param path path to file
     * @param fileName name of file to save to
     * @param stream Data stream to read from
     * @param append Whether to append to the file, if it exists
     */
    public static void saveToFile(String path, String fileName, InputStream stream, boolean append) {
        File file = new File(path, fileName);
        try {
            FileWriter w = new FileWriter(file, append);
            BufferedWriter bw = new BufferedWriter(w);
            Scanner scanner = new Scanner(stream);
            while (scanner.hasNext()) {
                bw.write(scanner.nextLine());
            }
            bw.close();
        } catch (IOException e) {
            Logger.logWarning(FileIO.class, "Issue saving to " + file.getPath(), e);
        }
    }
}
