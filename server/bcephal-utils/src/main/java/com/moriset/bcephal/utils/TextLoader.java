package com.moriset.bcephal.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextLoader {

	protected BufferedWriter writer;

	public TextLoader(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.getParentFile().exists())
				file.getParentFile().mkdir();
			if (!file.exists())
				file.createNewFile();
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String text) {
		try {
			if(writer != null)writer.write(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeln(String text) {
		write(text + "\n");
	}

	public void close() {
		try {
			if (writer != null)
				writer.close();
		} catch (Exception e) {
		}
		writer = null;
	}

	protected void write(BufferedWriter writer, String text) throws IOException {
		writer.write(text);
	}

}
