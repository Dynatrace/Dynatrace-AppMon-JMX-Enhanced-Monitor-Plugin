package com.dynatrace.diagnostics.plugin.jmx;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;

import com.dynatrace.diagnostics.pdk.MonitorEnvironment;
import com.dynatrace.diagnostics.pdk.MonitorMeasure;
import com.dynatrace.diagnostics.pdk.PluginEnvironment;
import com.dynatrace.diagnostics.pdk.Status;
import com.dynatrace.diagnostics.pdk.TaskEnvironment;
import com.dynatrace.diagnostics.plugin.jmx.helper.HelperUtils;
import com.dynatrace.diagnostics.plugin.jmx.helper.JMXMonitorConstants;
import com.dynatrace.diagnostics.plugin.jmx.helper.JMXMonitorProperties;
import com.dynatrace.diagnostics.plugin.jmx.helper.JMX_SERVER_TYPE;
import com.dynatrace.diagnostics.sdk.resources.BaseConstants;

public class JMXExecutor implements JMXMonitorConstants {
	JMXMonitorProperties pp;
	private static final Logger log = Logger.getLogger(JMXExecutor.class.getName());
	
	public Status setup(PluginEnvironment env) throws Exception {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering setup method");
  		}
		try {
			pp = setConfiguration(env);

			if (log.isLoggable(Level.FINER)) {
				log.finer("setup method: the configuration property file is " + pp.toString());
			}
		} catch (Exception e) {
			String msg = "setConfiguration method: '" + HelperUtils.getExceptionAsString(e) + "'";
			log.severe(msg);
			return new Status(Status.StatusCode.ErrorInfrastructure, msg,msg,e);
		}

		return STATUS_SUCCESS;
	}

	public Status execute(PluginEnvironment env) throws Exception {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering execute method");
  		}
		
		JMXConnector jmxc;
		MBeanServerConnection con;
		try {
			if ((con = pp.getJmxConnection()) == null) {
				if ((jmxc = pp.getJmxConnector()) != null) {
					jmxc.close();
				}
				jmxc = JMXConnectorFactory.connect(pp.getJmxServiceUrl(), pp.getCredentialsMap());
				con = jmxc.getMBeanServerConnection();
			}
		} catch (Exception e) {
			String msg = "execute method: exception was thrown '" + HelperUtils.getExceptionAsString(e) + "'";
			log.severe(msg);
			pp.setJmxConnection(null);
			if ((jmxc = pp.getJmxConnector()) != null) {
				jmxc.close();
			}
			
			return new Status(Status.StatusCode.ErrorInfrastructure, msg,msg, e);
		}
		
		if (env instanceof TaskEnvironment) {
			processJMXTask(env, pp);
			return STATUS_SUCCESS;
		}
		
		try {
			processMBeans(env, con, pp);
		} catch (Exception e) {
			String msg = "execute method: exception was thrown '" + HelperUtils.getExceptionAsString(e) + "'";
			log.severe(msg);
			pp.setJmxConnection(null);
			if ((jmxc = pp.getJmxConnector()) != null) {
				jmxc.close();
			}
			
			return new Status(Status.StatusCode.ErrorInfrastructure, msg,msg, e);
		}
		
		return STATUS_SUCCESS;
	}

	public void teardown(PluginEnvironment env) throws Exception {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering teardown method");
  		}
		
		JMXConnector jmxc;
		if ((jmxc = pp.getJmxConnector()) != null) {
			jmxc.close();
		}
	}
	
	public void processJMXTask(PluginEnvironment env, JMXMonitorProperties props) {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering processJMXTask method");
  		}
		JMXConnector jmxc;
		MBeanServerConnection con;
		try {
			if ((con = pp.getJmxConnection()) == null) {
				if ((jmxc = pp.getJmxConnector()) != null) {
					jmxc.close();
				}
				jmxc = JMXConnectorFactory.connect(pp.getJmxServiceUrl(), pp.getCredentialsMap());
				con = jmxc.getMBeanServerConnection();
			}
			processMBeans(env, con, pp);
		} catch (Exception e) {
			String msg = "processJMXTask method: exception was thrown '" + HelperUtils.getExceptionAsString(e) + "'";
			log.severe(msg);
			throw new RuntimeException(msg, e);
		}
	}
	
	public void processMBeans(PluginEnvironment env, MBeanServerConnection con, JMXMonitorProperties props) throws Exception {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering processMBeans method");
  		}
		// inclusion list
		List<Set<ObjectName>> list =  new ArrayList<Set<ObjectName>>();
		if (!props.getIncludeMBeansPatterns().isEmpty()) {
			for (String line : props.getIncludeMBeansPatterns()) {
				list.add(con.queryNames(new ObjectName(line), null));
			}
		} else {
			list.add(con.queryNames(null, null));
		}
		// exclusion list
		List<Set<ObjectName>> elist =  new ArrayList<Set<ObjectName>>();
		for (String line : props.getExcludeMBeansPatterns()) {
			elist.add(con.queryNames(new ObjectName(line), null));
		}
		StringBuilder sb = null;
		if (props.isTask()) {
			sb = new StringBuilder(START_OF_PLUGIN_XML_FILE);
		}
		for (Set<ObjectName> set : list) {
			nextObjectName:
			for (ObjectName name : set) {
				for (Set<ObjectName> eset : elist) {
					for (ObjectName ename : eset) {
						if (ename.apply(name)) {
							// skip the name object
							if (log.isLoggable(Level.FINER)) {
								log.finer("processMBeans method: object '" + name.getCanonicalName() 
									+ "' was skipped because it matches an exclusion object '" + ename.getCanonicalName() + "'");
							}
							continue nextObjectName;
						}
					}
				}
				// if is a task ==> add metric group entry to the plugin.xml file
				if (props.isTask()) {
					sb.append(buildMGEntry(name, props.getPluginId()));
				}
				MBeanInfo info = con.getMBeanInfo(name);
				MBeanAttributeInfo[] mbas = info.getAttributes();
				String[] aNames = new String[mbas.length];
				int i = 0;
				for (MBeanAttributeInfo mba : mbas) {
					aNames[i++] = mba.getName();
				}
				AttributeList aList = con.getAttributes(name, aNames);
				for (Attribute att : aList.asList()) {
					if (!props.isTask()) {
						buildMeasure((MonitorEnvironment) env, name, att, props.getPluginId()); 
					} else {
						// build metric entry in the plugin.xml file
						sb.append(buildAttrEntry(name, att, props.getPluginId()));
					}
				}
				if (props.isTask()){
					// add the rest of the entries to close MG tags in the plugin.xml file 
					sb.append(REST_OF_MG_LINES);
					
				}
			}
		}
		// for task write sb buffer to the filesystem
		if (props.isTask()) {
			zipMGDir(props);
			buildDir(sb.append(END_OF_PLUGIN_XML_FILE).toString(), props);
			executeScript(props);
			cleanupArchiveDir(props);
		}
	}
	
	public void buildDir(String pluginXml, JMXMonitorProperties props) throws Exception {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering buildDir method");
  		}

		// mg subdirectory
		File file = new File(props.getDirJar());
		// remove all files and sub-directories from the dirJar directory
		FileUtils.deleteQuietly(file);
		file = new File(props.getPluginXml());
		FileUtils.writeStringToFile(file, pluginXml.toString());
		file = new File(props.getJarCommand()[0]);
		FileUtils.deleteQuietly(file);
		FileUtils.writeStringToFile(file, props.getShellScriptContent());
        file.setExecutable(true);
        file.setReadable(true);
        file.setWritable(false);
        
		// create sub-directories
		String dir = props.getDirJar() + "build";
		file = new File(dir);
		file.mkdirs();
		FileUtils.writeStringToFile(new File(new StringBuilder(props.getDirJar()).append("src")
				.append(File.separator).append("dummy").toString()), BaseConstants.EMPTY_STRING);
		FileUtils.writeStringToFile(new File(new StringBuilder(props.getDirJar()).append("lib")
				.append(File.separator).append("dummy").toString()), BaseConstants.EMPTY_STRING);
		file = new File(props.getManifestAbsFileName());
		FileUtils.writeStringToFile(file, props.getManifest());
	}
	
	public void executeScript(JMXMonitorProperties props) {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering executeScript method");
  		}
		ProcessBuilder pb = new ProcessBuilder(props.getJarCommand());
		try {
			Process process = pb.start();
			// TODO change on class PIPE (see GE plugin)
	        InputStream is = process.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        StringBuilder sb = new StringBuilder();
	        while ((line = br.readLine()) != null) {
	        	sb.append(line);
	        }
	        if (log.isLoggable(Level.FINER)) {
        		log.finer("executeJar method: output from the jar command is '" + sb.toString() + "'");
        	}

	        int rc = process.waitFor();
	        if (log.isLoggable(Level.FINER)) {
        		log.finer("executeJar method: return code from the jar command is '" + rc + "'");
        	}
	        if (rc != 0) {
	        	String msg = "executeJar method: return code from the jar commans is '" + rc + "'. It should be 0.";
				log.severe(msg);
				throw new RuntimeException(msg);
	        }
		} catch (Exception e) {
			String msg = "executeJar method: exception was thrown '" + HelperUtils.getExceptionAsString(e) + "'";
			log.severe(msg);
			throw new RuntimeException(msg, e);
		}
	}
	
	public String buildAttrEntry(ObjectName oName, Attribute attribute, String pluginId) {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering buildAttrEntry method");
  		}
		// build metric records
		StringBuilder sb =  new StringBuilder();
		String mgName = getMGName(pluginId, oName);
		String name = mgName + "." + attribute.getName();  // added to make unique attribute name across different deployments, jobs, etc. in one domain
		Object value = attribute.getValue();
		CompositeDataSupport cds;
		String unit;
		if ((value instanceof CompositeDataSupport)) {
			cds = (CompositeDataSupport) value;
			String cdsName;
			for (String key : cds.getCompositeType().keySet()) {
				if (cds.get(key) instanceof Number) {
					cdsName = name + "." + key;
					unit = getUnit(cdsName);
					sb.append(MG_LINE_3_0).append(cdsName).append(MG_LINE_3_1).append(unit)  
						.append(MG_LINE_3_2).append(NEW_LINE);
					if (log.isLoggable(Level.FINER)) {
						log.finer("buildAttrEntry method: measure name is '" + cdsName + ", measure value is value '" + format(cds.get(key)) + "'");
					}
				} else {
					log.warning(new StringBuilder("buildAttrEntry method: domain: ").append(oName.getDomain()) 
						.append(", keys: ").append(oName.getCanonicalKeyPropertyListString())
						.append(", attribute: ").append(name).append(".").append(key)
						.append(", value: ").append(cds.get(key))
						.append(" is ignored because of non-numeric value").toString());
				}
			}
		} else if (value instanceof Number) {
			unit = getUnit(name);
			sb.append(MG_LINE_3_0).append(name).append(MG_LINE_3_1).append(unit)  
			.append(MG_LINE_3_2).append(NEW_LINE);
			if (log.isLoggable(Level.FINER)) {
				log.finer(new StringBuilder("buildAttrEntry method: domain: ").append(oName.getDomain()) 
						.append(", keys: ").append(oName.getCanonicalKeyPropertyListString())
						.append(", attribute: ").append(name).append(", value: ").append(value == null ? "null" : value.toString())
						.toString());
			}
		} else {
			log.warning(new StringBuilder("buildAttrEntry method: domain: ").append(oName.getDomain()) 
					.append(", keys: ").append(oName.getCanonicalKeyPropertyListString())
					.append(", attribute: ").append(name).append(", value: ").append(value == null ? "null" : value.toString())
					.append(" is ignored because of non-numeric value").toString());
		}
		return sb.toString();
	}
	
	public String getUnit(String name) {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering getUnit method");
  		}
		Set<Entry<String, Pattern>> entries = UNIT_PATTERNS_MAP.entrySet();
		for (Entry<String, Pattern> entry : entries) {
			if (entry.getValue().matcher(name).matches()) {
				if (log.isLoggable(Level.FINER)) {
					log.finer("getUnit method: found match for attribute '" + name + "' is '" + entry.getKey() + "'");
				}
				if (entry.getKey().equals(UNIT_SECOND)) {
					// check name matches millisecond or nanosecond
					Pattern p = UNIT_PATTERNS_MAP.get(UNIT_MILLISECOND);
					if (p.matcher(name).matches()) {
						return UNIT_MILLISECOND;
					}
					p = UNIT_PATTERNS_MAP.get(UNIT_NANOSECOND);
					if (p.matcher(name).matches()) {
						return UNIT_NANOSECOND;
					}
				}
				return entry.getKey();
			}
		}
		
		return "number";
	}
		
	public String buildMGEntry(ObjectName name, String pluginId) {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering buildMGEntry method");
  		}
		String mg = getMGName(pluginId, name);
		// build metric group record
		String v = transformString(mg);
		StringBuilder sb = new StringBuilder();
		sb.append(MG_LINE_1_0).append(v).append(MG_LINE_1_1).append(mg).append(MG_LINE_1_2).append(NEW_LINE);
		sb.append(MG_LINE_2).append(NEW_LINE);
		return sb.toString();
	}
	
	public static String transformString(String source) {
		String v = source.replaceAll("[^A-Za-z0-9\\.]", "o");
		String[] as = v.split("\\.");
		StringBuilder sbId = new StringBuilder();
		for (String s : as) {
			if (Character.isDigit(s.charAt(0))) {
				s = "a" + s;
			}
			sbId.append(BaseConstants.DOT).append(s);
		}
		v = sbId.toString().substring(1).toLowerCase(); 
		return v;
	}
	
	public String getMGName(String pluginId, ObjectName name) {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering getMGName method");
  		}
		StringBuilder sb;
		if (!pluginId.isEmpty()) {
			sb = new StringBuilder(pluginId).append(BaseConstants.DOT).append(name.getDomain());
		} else {
			sb = new StringBuilder(name.getDomain());
		}
		String keyList = name.getKeyPropertyListString();
		String[] as = keyList.split(",");
		String[] av;
		for (String key : as) {
			av = key.split(BaseConstants.EQUAL);
			sb.append(BaseConstants.DOT).append(av[0]).append(BaseConstants.DOT).append(av[1]);
		}
		return sb.toString();
	}
	
	public void buildMeasure(MonitorEnvironment env, ObjectName oName, Attribute attribute, String pluginId) {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering buildMeasure method");
  		}
		String mgName = getMGName(pluginId, oName);
		String attrName = mgName + "." + attribute.getName();  // added to make unique attribute name across different deployments, jobs, etc. in one domain
		Object value = attribute.getValue();
		Double d = Double.NaN;
		CompositeDataSupport cds;
		if ((value instanceof CompositeDataSupport)) {
			Object obj;
			cds = (CompositeDataSupport) value;
			String cdsName;
			for (String key : cds.getCompositeType().keySet()) {
				if ((obj = cds.get(key)) instanceof Number) {
					cdsName = attrName + "." + key;
					d = ((Number)obj).doubleValue();
					if (log.isLoggable(Level.FINER)) {
						log.finer("buildMeasure method: measure name is '" + cdsName + ", measure value is value '" + d + "'");
					}
					// set measure
					populateMeasure(env, mgName, cdsName, d);
				} else {
					if (log.isLoggable(Level.FINER)) {
						log.finer(new StringBuilder("buildMeasure method: measure: ").append(mgName) 
							.append(", attribute: ").append(attrName).append(".").append(key)
							.append(", value: ").append(cds.get(key))
							.append(" is ignored because of non-numeric value").toString());
					}
				}
			}
		} else if (value instanceof Number) {
			d = ((Number)value).doubleValue();
			// set measure
			populateMeasure(env, mgName, attrName, d);
		}
		
	}
	
	public void populateMeasure(MonitorEnvironment env, String mgName, String metricName, Double d) {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering populateMeasure method: metric group name is '" + mgName + "', metric name is '" + metricName + "', value = " + d);
  		}
		Collection<MonitorMeasure> measures;
		if ((measures = env.getMonitorMeasures(mgName, metricName)) != null) {
            for (MonitorMeasure measure : measures) {
                log.finer("populateMeasure method: Populating measure '" + metricName + "' for metric group '" + mgName + "'");
                measure.setValue(d);
            }
        }
	}
	
	public String format(Object value) {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering format method");
  		}
		if (value == null) {
			return "null";
		} else if ((value instanceof String)) {
			return (String) value;
		} else if ((value instanceof Number)) {
			NumberFormat f = NumberFormat.getInstance();
			f.setMaximumFractionDigits(2);
			f.setGroupingUsed(false);
			return f.format(value);
		} else if ((value instanceof Object[])) {
			return Integer.toString(Arrays.asList((Object[]) value).size());
		}
		return value.toString();
	}
	
	public JMXMonitorProperties setConfiguration(PluginEnvironment env) {
		if (log.isLoggable(Level.FINER)) {
  			log.finer("Entering setConfiguration method");
  		}
		String v;
		
		JMXMonitorProperties props = new JMXMonitorProperties();
		props.setTask(env.getConfigBoolean(CONFIG_IS_TASK));
		if (props.isTask()) {
			if (!(env instanceof TaskEnvironment)) {
				String msg = "setConfiguration message: plugin should be configured as a Task because isTask indicator is set to '" + props.isTask() + "'";
				log.severe(msg);;
				throw new RuntimeException(msg);
			}
		}
		props.setCustomJmxUrlInd(env.getConfigBoolean(CONFIG_IS_CUSTOM_JMX_URL));
		if (props.isCustomJmxUrlInd()) {
			String msg = "setConfiguration method: Custom JMX URL is not implemented yet.";
			log.severe(msg);
			throw new UnsupportedOperationException(msg);
		}
		if (props.isTask()) {
			props.setJmxHost(v = (v = env.getConfigString(CONFIG_JMX_HOST)) != null ? v.trim() : BaseConstants.EMPTY_STRING);
			if (v.isEmpty()) {
				String msg = "setConfiguration method: the JMX Host when plugin is configured as a task must not be empty";
				log.severe(msg);
				throw new RuntimeException(msg);
			}
		} else {
			props.setJmxHost(env.getHost().getAddress());
		}
		props.setJmxPort(env.getConfigLong(CONFIG_JMX_PORT));
		props.setPluginId(v = ((v = env.getConfigString(CONFIG_PLUGIN_ID)) == null ? BaseConstants.EMPTY_STRING : v.trim()));
		if (!v.isEmpty()) {
			if (!StringUtils.isAlphanumeric(v)) {
				String msg = "setConfiguration method: the pluginId is '" + v + "'. It contains non-alphanumeric characters.";
				log.severe(msg);
				throw new RuntimeException(msg);
			}
		} else {
			props.setPluginId(new StringBuilder(props.getJmxHost() ).append(".").append(props.getJmxPort()).toString());
			if ((v = props.getPluginId()).trim().isEmpty()) {
				String msg = "setConfiguration method: the pluginId is '" + v + "'. It should not be empty.";
				log.severe(msg);
				throw new RuntimeException(msg);
			}
		}
		props.setBundleSymbolicName(new StringBuilder(BUNDLE_SYMBOLIC_NAME).append(transformString(props.getPluginId())).toString());
		
		props.setAuthenticationOn(env.getConfigBoolean(CONFIG_IS_AUTHENTICATION_ON));
		if (props.isAuthenticationOn()) {
			props.setUser((v = env.getConfigString(CONFIG_USER)) != null ? v.trim() : BaseConstants.EMPTY_STRING);
			props.setPassword(env.getConfigPassword(CONFIG_PASSWORD));
		}
		props.setCustomJmxUrl((v = env.getConfigString(CONFIG_CUSTOM_JMX_SERVICE_URL)) != null ? v.trim() : BaseConstants.EMPTY_STRING);
		props.setJmxServerType((v = env.getConfigString(CONFIG_JMX_SERVER_TYPE)) != null ? v.trim() : BaseConstants.EMPTY_STRING);
		JMX_SERVER_TYPE jmxST;
		try {
			jmxST = JMX_SERVER_TYPE.valueOfIgnoreCase(v);
		} catch (IllegalArgumentException e) {
			String msg = "setConfiguration method: Incorrect value of the JMX SERVER TYPE parameter '" + v + "'";
			log.severe(msg);
			throw new RuntimeException(msg);
		}
		if (jmxST != JMX_SERVER_TYPE.CF_OPS_METRICS_TOOL && jmxST != JMX_SERVER_TYPE.JVM) {
			String msg = "setConfiguration method: the JMX_SERVER_TYPE '" + jmxST.name() + "' is not implemented yet.";
			log.severe(msg);
			throw new UnsupportedOperationException(msg);
		}
		v = ((v = env.getConfigString(CONFIG_INCLUDE_MBEAN_PATTERN)) != null ? v.trim() : BaseConstants.EMPTY_STRING);
		try {
			props.setIncludeMBeansPatterns(IOUtils.readLines(IOUtils.toInputStream(v), DEFAULT_ENCODING));
		} catch (IOException e) {
			String msg = "setConfiguration method: exception was thrown when executing the props.setIncludeMBeansPatterns method '" + HelperUtils.getExceptionAsString(e) + "'";
			log.severe(msg);
			throw new RuntimeException(msg, e);
		}
		
		v = ((v = env.getConfigString(CONFIG_EXCLUDE_MBEAN_PATTERN)) != null ? v.trim() : BaseConstants.EMPTY_STRING);
		try {
			props.setExcludeMBeansPatterns(IOUtils.readLines(IOUtils.toInputStream(v), DEFAULT_ENCODING));
		} catch (IOException e) {
			String msg = "setConfiguration method: exception was thrown when executing the props.setExcludeMBeansPatterns method '" + HelperUtils.getExceptionAsString(e) + "'";
			log.severe(msg);
			throw new RuntimeException(msg, e);
		}
		if (props.isTask()) {
			props.setJarVersion(v = ((v = env.getConfigString(CONFIG_JAR_VERSION)) != null ? v.trim() : BaseConstants.EMPTY_STRING));
			if (v.isEmpty()) {
				String msg = "setConfiguration method: version of the jar file must not be empty";
				log.severe(msg);
				throw new RuntimeException(msg);
			}
			v = ((v = env.getConfigString(CONFIG_DIR_JAR)) != null ? v.trim() : BaseConstants.EMPTY_STRING);
			if (v.isEmpty()) {
				String msg = "setConfiguration method: the directory for the jar file is the Dynatrace Collector home directory";
				log.info(msg);
				v = "." + File.separator;
			}
			if (!v.endsWith(File.separator)) {
				v = new StringBuilder(v).append(File.separator).toString();
			}
			props.setWorkDir(v);
			props.setDirJar(new StringBuilder(v).append("mg").append(File.separator).toString());
			props.setArchiveDir(new StringBuilder(v).append("archive").append(File.separator).toString());
			props.setPluginXml(props.getDirJar() + PLUGIN_XML_FILE_NAME);
			props.setJarFileName(new StringBuilder(JAR_FILE_NAME).append(props.getPluginId()).append("_").append(props.getJarVersion()).append(JAR_FILE_NAME_SUFFIX).toString());
			props.setJarAbsFileName(new StringBuilder(v).append("build").append(File.separator).append(props.getJarFileName()).toString());	
			props.setManifestAbsFileName(new StringBuilder(props.getDirJar()).append("META-INF").append(File.separator).append("MANIFEST.MF").toString());
			props.setManifest(chopRecords(new StringBuilder(MANIFEST_MF_1).append(props.getJarVersion()).append(MANIFEST_MF_2).append(props.getPluginId())
					.append(MANIFEST_MF_3).append(props.getBundleSymbolicName()).append(MANIFEST_MF_4).toString()));
			// jar -cvfm build\com.dynatrace.diagnostics.plugin.jmx.metricgroup_1.0.0.jar  META-INF\MANIFEST.MF .
			props.setShellScriptContent(new StringBuilder("cd ").append(props.getDirJar()).append(NEW_LINE)
						.append("jar -cvfm build").append(File.separator).append(props.getJarFileName())
						.append(" META-INF").append(File.separator).append("MANIFEST.MF .").toString());
			String[] cmd = new String[1];
			String shellSuffix = ".sh";
			if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
				 shellSuffix = ".bat";
			}
			cmd[0] = new StringBuilder(props.getWorkDir()).append("export").append(shellSuffix).toString();
			props.setJarCommand(cmd);
		}
		// set JMX Service URL string
		props.setJmxServiceUrlString(new StringBuilder(JMX_SERVICE_URL_1).append(props.getJmxHost()).append(BaseConstants.COLON).append(props.getJmxPort())
				.append(JMX_SERVICE_URL_2).append(props.getJmxHost()).append(BaseConstants.COLON).append(props.getJmxPort())
				.append(JMX_SERVICE_URL_3).toString());
		try {
			props.setJmxServiceUrl(new JMXServiceURL(props.getJmxServiceUrlString()));
			Map<String, String[]> map = new HashMap<String, String[]>();
			if (props.isAuthenticationOn()) {
				String[] auth = {props.getUser(), props.getPassword()};
				map.put(JMX_REMOTE_CREDENTIALS, auth);
			}
			props.setCredentialsMap(map);
			props.setJmxConnector(JMXConnectorFactory.connect(props.getJmxServiceUrl(), map));
			props.setJmxConnection(props.getJmxConnector().getMBeanServerConnection());
		} catch (Exception e) {
			String msg = "setConfiguration method: '" + HelperUtils.getExceptionAsString(e) + "'";
			log.severe(msg);
			throw new RuntimeException(msg, e);
		}
		
		return props;
	}
	
	public static String chopRecords(String in) {
		String[] lines = in.split(NEW_LINE);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			while(line.length() > MANIFEST_FILE_RECORD_LENGTH) {
				sb.append(line.substring(0, MANIFEST_FILE_RECORD_LENGTH)).append(NEW_LINE);
				line = " " + line.substring(MANIFEST_FILE_RECORD_LENGTH);
				continue;
			}
			sb.append(line).append(NEW_LINE);
		}
		
		return sb.toString();
	}
	
	public static void zipMGDir(JMXMonitorProperties props) throws IOException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd_HH-mm-ss.SSS");
//        createZip("c:/temp/workDir/mg", "c:/temp/output_99.zip");
		File f = new File(props.getArchiveDir());
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(props.getDirJar());
		if (!f.exists()) {
			f.mkdirs();
			return;
		}
		String in = props.getDirJar().substring(0,  props.getDirJar().length() - 1);
		long time = new Date().getTime();
		String timestamp = simpleDateFormat.format(time);
		String zipArchiveOut = new StringBuilder(props.getArchiveDir()).append(new File(in).getName()).append("_")
				.append(props.getJarVersion()).append("_").append(timestamp).append(".zip").toString();
		createZip(in, zipArchiveOut);
    }
	
	 public static void createZip(String directoryPath, String zipPath) throws IOException {
	        FileOutputStream fOut = null;
	        BufferedOutputStream bOut = null;
	        ZipArchiveOutputStream tOut = null;
	 
	        try {
	            fOut = new FileOutputStream(new File(zipPath));
	            bOut = new BufferedOutputStream(fOut);
	            tOut = new ZipArchiveOutputStream(bOut);
	            addFileToZip(tOut, directoryPath, "");
	        } catch (Exception e) {
	        	String msg = "createZip method: exception stacktrace is '" + HelperUtils.getExceptionAsString(e) + "'";
	        	log.severe(msg);
	        	throw e;
	        } finally {
	            try {if (tOut != null)tOut.finish();} catch (Exception e) {}
	            try {if (tOut != null) tOut.close();} catch (Exception e) {}
	            try {if (bOut != null) bOut.close();} catch (Exception e) {}
	            try {if (fOut != null) fOut.close();} catch (Exception e) {}
	        }
	}
	 
	 private static void addFileToZip(ZipArchiveOutputStream zOut, String path, String base) throws IOException {
	        File f = new File(path);
	        String entryName = base + f.getName();
	        ZipArchiveEntry zipEntry = new ZipArchiveEntry(f, entryName);
	 
	        zOut.putArchiveEntry(zipEntry);
	 
	        if (f.isFile()) {
	            FileInputStream fInputStream = null;
	            try {
	                fInputStream = new FileInputStream(f);
	                IOUtils.copy(fInputStream, zOut);
	                zOut.closeArchiveEntry();
	            } finally {
	                IOUtils.closeQuietly(fInputStream);
	            }
	 
	        } else {
	            zOut.closeArchiveEntry();
	            File[] children = f.listFiles();
	 
	            if (children != null) {
	                for (File child : children) {
	                    addFileToZip(zOut, child.getAbsolutePath(), entryName + "/");
	                }
	            }
	        }
	    }
	
	public void cleanupArchiveDir(JMXMonitorProperties props) {
		Collection<File> collection = FileUtils.listFiles(new File(props.getArchiveDir()), FileFilterUtils.suffixFileFilter(".zip"), null);
		if (collection.size() <= MAX_SIZE_ARCHIVE_DATA) {
			return;
		}
		List<File> files = new ArrayList<File>(collection);
		Collections.sort(files, new Comparator<File>() {
			public int compare(File o1, File o2) {
				return -o1.getName().compareTo(o2.getName());
			}
		});
		for (int i = MAX_SIZE_ARCHIVE_DATA; i < files.size(); i++) {
			FileUtils.deleteQuietly(files.get(i));
		}
	}
}
