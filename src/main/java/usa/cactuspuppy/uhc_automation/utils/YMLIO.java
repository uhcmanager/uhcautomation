package usa.cactuspuppy.uhc_automation.utils;

import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom YML parser supporting #comments and indented key-value pairs
 * @author CactusPuppy
 */
@NoArgsConstructor
public final class YMLIO {
    /**
     * Pattern to match against potential config values
     */
    private static final Pattern KV = Pattern.compile("( *)([^:\\n]+): *([^:\\n]*)");
    /**
     * Key-value pairs from config
     */
    private Map<String, String> values = new HashMap<>();

    private boolean fileUpToDate = false;

    public void readValues(InputStream inputStream) {
        //Initialize variables
        LinkedList<Integer> currIndents = new LinkedList<>();
        currIndents.addLast(0);
        int lineIndex = 0;
        Scanner scan = new Scanner(inputStream);
        LinkedList<String> currPrefixes = new LinkedList<>();
        //Trackers
        String previousKey = "";
        int prevIndent = 0;

        //Read in
        while (scan.hasNext()) {
            String line = scan.nextLine();
            lineIndex += 1;
            int hashIndex = line.indexOf("#");
            if (hashIndex != -1) {
                line = line.substring(0, hashIndex);
            }
            Matcher m = KV.matcher(line);
            if (!m.matches()) {
                Logger.logWarning(this.getClass(), "Unable to parse line " + lineIndex + ": " + line);
                continue;
            }
            String indent = m.group(1);
            String key = m.group(2);
            String value = m.group(3);
            key = key.trim();
            value = value.trim();
            int currentIndent = indent.length();
            if (currentIndent <= currIndents.getLast()) {
                while (currentIndent >= currIndents.peekLast()) { //Pop prefixes off the end until reach appropriate indent level
                    currIndents.removeLast();
                    if (!currPrefixes.isEmpty()) currPrefixes.removeLast();
                    if (currIndents.isEmpty()) {
                        currIndents.addLast(0);
                        break;
                    }
                }
            } else if (currentIndent > currIndents.getLast()) {
                currPrefixes.addLast(previousKey); //Add new indent
                currIndents.addLast(prevIndent);
            }
            StringJoiner prefixJoiner = new StringJoiner(".");
            for (String prefix : currPrefixes) {
                if (prefix != null && !prefix.equals("")) prefixJoiner.add(prefix);
            }
            prefixJoiner.add(key);
            values.put(prefixJoiner.toString(), value);
            previousKey = key;
            prevIndent = currentIndent;
        }
        fileUpToDate = true;
    }

    public String get(String key) {
        return values.get(key);
    }

    /**
     * @return A copy of values currently stored in the cache
     */
    public Map<String, String> getValues() {
        return new HashMap<>(values);
    }

    public void update(String key, String value) {
        values.put(key, value);
        fileUpToDate = false;
    }

    @Override
    public String toString() {
        StringBuilder rv = new StringBuilder();
        Map<String, String> values = getValues();
        for (String s : values.keySet()) {
            rv.append(s).append(" -> ").append(values.get(s)).append("\n");
        }
        return rv.toString();
    }
}
