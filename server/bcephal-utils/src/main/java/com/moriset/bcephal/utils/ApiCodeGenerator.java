package com.moriset.bcephal.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ApiCodeGenerator {
	
	public static final String FILE_LOADER_PREFIX = "FL_";
	public static final String SCHEDULER_PREFIX = "SC_";
	public static final String USER_LOAD_PREFIX = "UL_";

	public static String generate() {
		return new SimpleDateFormat("yyyyMMddhhmmss").format(Date.from(Instant.now()));
	}
	
	public static String generate(String prefix) {
		return String.format("%s%s", prefix != null ? prefix : "", generate());
	}
	
}
