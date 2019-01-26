package usa.cactuspuppy.uhc_automation.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public static CountdownList getList(String csl) {
        List<Long> titles = new ArrayList<>();
        List<Long> others = new ArrayList<>();
        csl = csl.trim();
        String[] items = csl.split("\\s*,\\s*");
        Pattern valid = Pattern.compile("(\\+?)(\\d+)");
        for (String s : items) {
            Matcher m = valid.matcher(s);
            if (!m.matches()) continue;
            Long value;
            try {
                value = Long.valueOf(m.group(2));
            } catch (NumberFormatException e) {
                continue;
            }
            if (m.group(1).equals("")) others.add(value);
            else titles.add(value);
        }
        titles = titles.stream().sorted().collect(Collectors.toList());
        others = others.stream().sorted().collect(Collectors.toList());
        return new CountdownList(titles, others);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class CountdownList {
        private List<Long> titles;
        private List<Long> other;
    }
}
