package usa.cactuspuppy.uhc_automation.utils;

import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@NoArgsConstructor
public final class CustomYAMLIO {
    private Map<String, String> values = new HashMap<>();

    private void readValuesFromFile(InputStream inputStream) {
        Scanner scan = new Scanner(inputStream);
        while (scan.hasNext()) {
            String line = scan.nextLine();
        }
    }
}
