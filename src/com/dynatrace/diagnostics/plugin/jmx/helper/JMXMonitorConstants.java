package com.dynatrace.diagnostics.plugin.jmx.helper;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.dynatrace.diagnostics.pdk.Status;

public interface JMXMonitorConstants {
	// plugin parameters
	public static final String CONFIG_IS_TASK = "isTask";
	public static final String CONFIG_PLUGIN_ID = "pluginId";
	public static final String CONFIG_IS_CUSTOM_JMX_URL = "isCustomJmxUrlInd";
	public static final String CONFIG_JMX_HOST = "jmxHost";
	public static final String CONFIG_JMX_PORT = "jmxPort";
	public static final String CONFIG_IS_AUTHENTICATION_ON = "isAuthenticationOn";
	public static final String CONFIG_USER = "user";
	public static final String CONFIG_PASSWORD = "password";
	public static final String CONFIG_URL_PATH = "urlPath";
	public static final String CONFIG_CUSTOM_JMX_SERVICE_URL = "customJmxUrl";
	public static final String CONFIG_JMX_SERVER_TYPE = "jmxServerType";
	public static final String CONFIG_INCLUDE_MBEAN_PATTERN = "includeMBeansPatterns";
	public static final String CONFIG_EXCLUDE_MBEAN_PATTERN = "excludeMBeansPatterns";
	public static final String CONFIG_DT_SERVER = "dtServer";
	public static final String CONFIG_DT_PORT = "dtPort";
	public static final String CONFIG_DT_PROTOCOL = "dtProtocol";
	public static final String CONFIG_IS_DT_AUTH = "isDtAuth";
	public static final String CONFIG_DT_USER = "dtUser";
	public static final String CONFIG_DT_PASSWORD = "dtPassword";
	public static final String CONFIG_DIR_JAR = "dirJar";
	public static final String CONFIG_JAR_VERSION = "version";
	
	// miscellaneous
	public static final String JMX_SERVICE_URL_1 = "service:jmx:rmi://";
	public static final String JMX_SERVICE_URL_2 = "/jndi/rmi://";
	public static final String JMX_SERVICE_URL_3 = "/jmxrmi";
	public static final String JMX_REMOTE_CREDENTIALS = "jmx.remote.credentials";
	public static final String DEFAULT_ENCODING = System.getProperty("file.encoding","UTF-8");
	public static final String NEW_LINE = System.getProperty("line.separator");
	public static final Status STATUS_SUCCESS = new Status(Status.StatusCode.Success);
	public static final String JAR_FILE_NAME = "com.dynatrace.diagnostics.plugin.jmx.metricgroup.";
	public static final String JAR_FILE_NAME_SUFFIX = ".jar ";
	public static final String PLUGIN_XML_FILE_NAME = "plugin.xml";
	public static final String UNIT_SECOND = "s";
	public static final String UNIT_MILLISECOND = "ms";
	public static final String UNIT_NANOSECOND = "ns";
	public static final String UNIT_MINUTE = "min";
	public static final int MANIFEST_FILE_RECORD_LENGTH = 70;
	public static final int MAX_SIZE_ARCHIVE_DATA = 100;
	
	// metric group template records
	public static final String START_OF_PLUGIN_XML_FILE = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(NEW_LINE)
																.append("<?eclipse version=\"3.2\"?>").append(NEW_LINE)
																.append("<!-- plugin.xml file written by dynaTrace Client 6.2.0 -->").append(NEW_LINE)
																.append("<plugin>").append(NEW_LINE).toString();
	public static final String MG_LINE_1_0 = "  <extension point=\"com.dynatrace.diagnostics.pdk.monitormetricgroup\" id=\"com.dynatrace.diagnostics.plugin.jmx.monitor."; 	// add metric-group-name here in all small letters without separator chars between words
	public static final String MG_LINE_1_1 = "\" name=\""; 	// add readable metric group name here as it will be used in the UI
	public static final String MG_LINE_1_2 = "\">";
	public static final String MG_LINE_2 = "    <metricgroup monitorid=\"com.dynatrace.diagnostics.plugin.jmx.monitor\">";
	public static final String MG_LINE_3_0 = "    <metric name=\""; // add metric name here
	public static final String MG_LINE_3_1 = "\" unit=\""; // add unit here
	public static final String MG_LINE_3_2 = "\" description=\"My metric description ...\" />";
	public static final String MG_LINE_4 = "    </metricgroup>";
	public static final String MG_LINE_5 = "    <information>";
	public static final String MG_LINE_6 = "      <description value=\"Describes metric groups and metrics which will be captured by the JMX Enhanced Monitor plugin\" />";
	public static final String MG_LINE_7 = "    </information>";
	public static final String MG_LINE_8 = "  </extension>";
	public static final String REST_OF_MG_LINES = new StringBuilder(MG_LINE_4).append(NEW_LINE).append(MG_LINE_5).append(NEW_LINE)
														.append(MG_LINE_6).append(NEW_LINE).append(MG_LINE_7).append(NEW_LINE)
														.append(MG_LINE_8).append(NEW_LINE).toString();
	public static final String END_OF_PLUGIN_XML_FILE = "</plugin>";
	
	// MANIFEST.MF file
	public static final String BUNDLE_SYMBOLIC_NAME = "com.dynatrace.diagnostics.plugin.jmx.metricgroup.";
	public static final String MANIFEST_MF_1 = new StringBuilder("Manifest-Version: 1.0").append(NEW_LINE)
												.append("Bundle-Vendor: Dynatrace").append(NEW_LINE)
												.append("Bundle-ClassPath: .").append(NEW_LINE)
												.append("Bundle-Version: ").toString(); // <== add plugin version here
	public static final String MANIFEST_MF_2 = new StringBuilder(NEW_LINE).append("Bundle-Name: CF Metric Group Plugin ").toString(); // <== add Plugin Instance Id here
	public static final String MANIFEST_MF_3 = new StringBuilder(NEW_LINE).append("Bundle-ManifestVersion: 2").append(NEW_LINE)
												.append("Bundle-SymbolicName: ").toString(); // <== add com.dynatrace.diagnostics.plugin.jmx.metricgroup. + Plugin Instance Id here
	public static final String MANIFEST_MF_4 = new StringBuilder(";").append("singleton:=true").append(NEW_LINE).toString();
	public static final String JAR_COMMAND_0 = "jar -cvfm "; // <== add directory-path here
	
	// unit recognition
	static final Map<String, Pattern> UNIT_PATTERNS_MAP = Collections.unmodifiableMap(
		    new HashMap<String, Pattern>() {
				private static final long serialVersionUID = -3495315942841640533L;
			{
				put(UNIT_SECOND, Pattern.compile(".*uptime.*|.*second.*", Pattern.CASE_INSENSITIVE));
		        put("bytes", Pattern.compile(".*byte.*", Pattern.CASE_INSENSITIVE));
		        put("percent", Pattern.compile(".*ratio.*|.*percent.*|.*[^a-zA-Z]avg[^a-zA-Z].*|.*[^a-zA-Z]avg|avg[^a-zA-Z].*|avg", Pattern.CASE_INSENSITIVE));
		        put(UNIT_MILLISECOND, Pattern.compile(".*_ms.*|.*millisecond.*|.*[^a-zA-Z]1m[^a-zA-Z].*|.*[^a-zA-Z]1m|1m[^a-zA-Z].*|1m", Pattern.CASE_INSENSITIVE));
		        put(UNIT_NANOSECOND, Pattern.compile(".*nanosecond.*|.*timens.*", Pattern.CASE_INSENSITIVE));
		        put(UNIT_MINUTE, Pattern.compile(".*minute.*", Pattern.CASE_INSENSITIVE));
		    }});
}
