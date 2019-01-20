package usa.cactuspuppy.uhc_automation.utils;

import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom YML parser supporting #comments and indented key-value pairs
 * @author CactusPuppy
 */
@NoArgsConstructor
public final class YMLIO {
    private static final Pattern KV = Pattern.compile("( *)([^:\\n]+): *([^:\\n]*)");
    private Map<String, String> values = new HashMap<>();
    private boolean changed = false;

    private void readValues(InputStream inputStream) {
        Scanner scan = new Scanner(inputStream);
        LinkedList<String> currentPrefix = new LinkedList<>();
        int prevDepth = 0;
        String previousKey;
        while (scan.hasNext()) {
            String line = scan.nextLine();
            int hashIndex = line.indexOf("#");
            if (hashIndex == -1) hashIndex = line.length();
            line = line.substring(0, hashIndex);
            Matcher m = KV.matcher(line);
            if (m.find()) {
                int depth = m.group(1).length() / 2;
                String currentKey = m.group(2);
                String currentValue = m.group(3);
                //TODO: Implement level tracking
            }
        }
    }

    public String get(String s) {
        return values.get(s);
    }
}
