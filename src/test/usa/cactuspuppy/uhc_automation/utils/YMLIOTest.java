package usa.cactuspuppy.uhc_automation.utils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Map;

import static org.junit.Assert.*;

public class YMLIOTest {
    private String input = "label:\n  label1:  asdf";

    @Test
    public void readValuesSimple() {
        YMLIO testReader = new YMLIO();
        testReader.readValues(new ByteArrayInputStream(input.getBytes()));
        assertEquals("asdf", testReader.get("label.label1"));
        System.out.println(testReader.toString());
    }

    @Test
    public void readValuesBackIndent() {
        input = "label:\n" +
                "  label1:  asdf\n" +
                "label2: true";
        YMLIO testReader = new YMLIO();
        testReader.readValues(new ByteArrayInputStream(input.getBytes()));
        System.out.println(testReader.toString());
    }
}