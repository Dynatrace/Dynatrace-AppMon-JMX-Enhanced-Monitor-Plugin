package com.dynatrace.diagnostics.plugin.jmx.helper;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.diagnostics.sdk.resources.BaseConstants;

public class HelperUtils implements JMXMonitorConstants {
	private static final Logger log = Logger.getLogger(HelperUtils.class.getName());
		
	public static String getExceptionAsString(Exception e) {
		if (log.isLoggable(Level.FINER)) {
			log.finer("Entering getExceptionAsString method");
		}
		String msg;
		if ((msg = e.getMessage()) == null) {
			msg = BaseConstants.DASH;
		}
		return new StringBuilder(e.getClass().getCanonicalName()
				+ " exception occurred. Message = '").append(msg)
				.append("'; Stacktrace is '").append(getStackTraceAsString(e))
				.append("'").toString();
	}

	public static String getStackTraceAsString(Exception e) {
		String returnString = "";
		if (log.isLoggable(Level.FINER)) {
			log.finer("Entering getStackTraceAsString method");
		}
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		try {
			e.printStackTrace(new PrintStream(ba, true, DEFAULT_ENCODING));
			returnString = ba.toString(DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e1) {
			log.finer("getStackTraceAsString method: UnsupportedEncodingException ; message is '" + e1.getMessage() + "'");
		}
		return returnString;
	}
}
