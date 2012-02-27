package com.splunk.shep.testutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.AssertJUnit;

/**
 * All the utils regarding files goes in here. If there are exceptions while
 * doing any operations the test will fail with appropriate message.
 */
public class UtilsFile {

    /**
     * @return a temporary file with random content.
     * 
     * @see UtilsFile#createTestFile()
     * @see UtilsFile#createTestFileWithRandomContent()
     */
    public static File createTestFileWithRandomContent() {
	File testFile = createTestFile();
	populateFileWithRandomContent(testFile);
	return testFile;
    }

    /**
     * @return a temporary, existing, empty file that will be deleted when the
     *         VM terminates.
     * 
     * @see #createTestFilePath()
     */
    public static File createTestFile() {
	File testFile = null;
	try {
	    testFile = File.createTempFile(UtilsFile.class.getSimpleName(), "");
	    testFile.deleteOnExit();
	} catch (IOException e) {
	    UtilsTestNG.failForException("Couldn't create a test file.", e);
	}
	return testFile;
    }

    /**
     * @return a temporary file path. The returned file doesn't exist, but the
     *         the path to the file was valid at the time of this method call.
     * 
     * @see #createTestFile()
     */
    public static File createTestFilePath() {
	File testFile = createTestFile();
	AssertJUnit.assertTrue(testFile.delete());
	return testFile;
    }

    /**
     * Writes random alphanumeric content to specified file.
     * 
     * @param file
     */
    public static void populateFileWithRandomContent(File file) {
	PrintStream printStream = null;
	try {
	    printStream = new PrintStream(file);
	    printStream.println(RandomStringUtils.randomAlphanumeric(1000));
	    printStream.flush();
	} catch (FileNotFoundException e) {
	    UtilsTestNG.failForException("Failed to write random content", e);
	} finally {
	    IOUtils.closeQuietly(printStream);
	}
    }

}
