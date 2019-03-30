package usa.cactuspuppy.uhc_automation.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class JarResource {
    /**
     * Extracts a jar resource to the specified relative output path.
     * @param inputStream inputStream to copy
     * @param relativeOutput file path to extract to
     * @param overwrite whether to overwrite the output file if it exists
     * @return update code for success or failure
     * -1 - Illegal args
     * 0 - OK/Success
     * 1 - I/O Problem
     * 2 - Output file exists (overwrite false only)
     */
    public static int extractResource(InputStream inputStream, String relativeOutput, boolean overwrite) {
        if (inputStream == null || relativeOutput == null) return -1;
        File outFile = new File(relativeOutput);
        if (!overwrite && outFile.isFile()) return 2;
        try {
            FileUtils.copyInputStreamToFile(inputStream, outFile);
        } catch (IOException e) {
            Logger.logSevere(JarResource.class, "Could not write resource to specified outfile", e);
            return 1;
        }
        return 0;
    }
}
