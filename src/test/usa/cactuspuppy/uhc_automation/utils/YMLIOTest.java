package usa.cactuspuppy.uhc_automation.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Map;

import static org.junit.Assert.*;

public class YMLIOTest {
    private String input;

    @Before
    public void setup() {
        input = "label:\n  label1:  asdf";
    }

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
        assertEquals("true", testReader.get("label2"));
        System.out.println(testReader.toString());
    }

    @Test
    public void readValuesComplicated() {
        input = "label:\n" +
                "  label1:  asdf\n" +
                "  label1-1: false\n" +
                "    oceanman:   unit\n" +
                "   yml: uiop\n" +
                "label2: true";
        YMLIO testReader = new YMLIO();
        testReader.readValues(new ByteArrayInputStream(input.getBytes()));
        String test = testReader.get("label").trim();
        assertEquals("asdf", testReader.get("label.label1"));
        assertEquals("false", testReader.get("label.label1-1"));
        assertEquals("unit", testReader.get("label.label1-1.oceanman"));
        assertEquals("uiop", testReader.get("label.label1-1.yml"));
        assertEquals("true", testReader.get("label2"));
        System.out.println(testReader.toString());
    }
}