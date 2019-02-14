package usa.cactuspuppy.uhc_automation.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JarResourceTest {
    private File testDir;
    private InputStream inputStream;

    @Test
    public void invalidArgsCheck() {
        assertEquals(-1, JarResource.extractResource(inputStream, null, true));
        assertEquals(-1, JarResource.extractResource(null, testDir.getPath() + "/asdf.txt", true));
    }

    @Test
    public void overwriteCheck() throws Exception {
        if (!(new File(testDir.getPath() + "/config.yml")).createNewFile()) throw new RuntimeException("Test overwrite file not created");
        assertEquals(2, JarResource.extractResource(inputStream, testDir.getPath() + "/config.yml", false));
    }

    @Test
    public void actualFileCopyCheck() throws Exception {
        JarResource.extractResource(inputStream, testDir.getPath() + "/config.yml", true);
        File outFile = new File(testDir.getPath() + "/config.yml");
        assertTrue(outFile.isFile());
        Scanner scan = new Scanner(outFile);
        Scanner scanStream = new Scanner(inputStream);
        while (scan.hasNext() && scanStream.hasNext()) {
            assertEquals(scanStream.nextLine(), scan.nextLine());
        }
        scan.close();
        scanStream.close();
    }

    @Before
    public void setup() throws RuntimeException {
        testDir = new File("junit");
        if (testDir.exists()) {
            try {
                FileUtils.deleteDirectory(testDir);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not remove existing test directory");
            }
        }
        if (!testDir.mkdirs()) throw new RuntimeException("Could not make test directory");
        assertTrue(testDir.isDirectory());
        inputStream = IOUtils.toInputStream("#Spotify Extreme Cool\nasdf: true", Charset.defaultCharset());
    }

    @After
    public void tearDown() throws RuntimeException {
        inputStream = null;
        try {
            FileUtils.deleteDirectory(testDir);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not remove test directory");
        }
    }
}