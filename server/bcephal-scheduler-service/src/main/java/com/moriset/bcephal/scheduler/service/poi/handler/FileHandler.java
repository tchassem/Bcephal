package com.moriset.bcephal.scheduler.service.poi.handler;

import java.io.File;

public interface FileHandler {
	/**
     * The FileHandler receives a stream ready for reading the
     * file and should handle the content that is provided and
     * try to read and interpret the data.
     *
     * Closing is handled by the framework outside this call.
     *
     * @param stream The input stream to read the file from.
     * @param path the relative path to the file
     * @throws Exception If an error happens in the file-specific handler
     */
    void handleFile(File stream, String path) throws Exception;

    /**
     * Ensures that extracting text from the given file
     * is returning some text.
     */
    void handleExtracting(File file) throws Exception;

    /**
     * Allows to perform some additional work, e.g. run
     * some of the example applications
     */
    void handleAdditional(File file) throws Exception;
}
