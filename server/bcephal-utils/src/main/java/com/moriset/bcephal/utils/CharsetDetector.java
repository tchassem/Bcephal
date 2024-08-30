package com.moriset.bcephal.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class CharsetDetector {

	public Charset detectCharset(File f) {
		String[] charsets = {StandardCharsets.UTF_8.name(), StandardCharsets.ISO_8859_1.name(), 
				StandardCharsets.US_ASCII.name(), StandardCharsets.UTF_16.name(), StandardCharsets.UTF_16BE.name(),
				StandardCharsets.UTF_16LE.name(), "windows-1253"};
        Charset charset = null;
        for (String charsetName : charsets) {
            charset = detectCharset(f, Charset.forName(charsetName));
            if (charset != null) {
                break;
            }
        }
        if(charset == null) {
        	charset = StandardCharsets.UTF_8;
        }
        return charset;
    }

    private Charset detectCharset(File f, Charset charset) {
        try {
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(f));
            CharsetDecoder decoder = charset.newDecoder();
            decoder.reset();
            byte[] buffer = new byte[512];
            boolean identified = false;
            while ((input.read(buffer) != -1) && (!identified)) {
                identified = identify(buffer, decoder);
            }
            input.close();
            if (identified) {
                return charset;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private boolean identify(byte[] bytes, CharsetDecoder decoder) {
        try {
            decoder.decode(ByteBuffer.wrap(bytes));
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }

}
