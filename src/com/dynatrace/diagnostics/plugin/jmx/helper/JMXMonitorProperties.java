package com.dynatrace.diagnostics.plugin.jmx.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

//import org.apache.http.impl.client.CloseableHttpClient;

import com.dynatrace.diagnostics.sdk.resources.BaseConstants;

public class JMXMonitorProperties {
	private boolean isTask;
	private String pluginId;
	private String bundleSymbolicName;
	private boolean customJmxUrlInd;
	private String jmxHost;
	private long jmxPort;
	private boolean isAuthenticationOn;
	private String user;
	private String password;
	private String urlPath;
	private String customJmxUrl;
	private String jmxServerType;
	private List<String> includeMBeansPatterns;
	private List<String> excludeMBeansPatterns;
	private String workDir;
	private String dirJar;
	private String archiveDir;
	private String jarFileName;
	private String jarAbsFileName;
	private String manifestAbsFileName;
	private String manifest;
	private String pluginXml;
	private String jarVersion;
	private String[] jarCommand;
	// commented because of the bug described in the SUPDT-12082
//	private String dtServer;
//	private long dtPort;
//	private String dtProtocol;
//	private boolean isDtAuth;
//	private String dtUser;
//	private String dtPassword;
	private String shellScriptContent;
	private String jmxServiceUrlString;
	private JMXServiceURL jmxServiceUrl;
	private Map<String, String[]> credentialsMap;
	private JMXConnector jmxConnector;
	private MBeanServerConnection jmxConnection;
	// commented because of the bug described in the SUPDT-12082
//	private CloseableHttpClient httpClient;
	private String dtRestUrl;
	private int httpStatusCode;
	
	public boolean isTask() {
		return isTask;
	}
	public void setTask(boolean isTask) {
		this.isTask = isTask;
	}
	public String getPluginId() {
		return pluginId;
	}
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}
	public void setBundleSymbolicName(String bundleSymbolicName) {
		this.bundleSymbolicName = bundleSymbolicName;
	}
	public boolean isCustomJmxUrlInd() {
		return customJmxUrlInd;
	}
	public void setCustomJmxUrlInd(boolean customJmxUrlInd) {
		this.customJmxUrlInd = customJmxUrlInd;
	}
	public String getJmxHost() {
		return jmxHost;
	}
	public void setJmxHost(String jmxHost) {
		this.jmxHost = jmxHost;
	}
	public long getJmxPort() {
		return jmxPort;
	}
	public void setJmxPort(long rmiPort) {
		this.jmxPort = rmiPort;
	}
	public boolean isAuthenticationOn() {
		return isAuthenticationOn;
	}
	public void setAuthenticationOn(boolean isAuthenticationOn) {
		this.isAuthenticationOn = isAuthenticationOn;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUrlPath() {
		return urlPath;
	}
	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}
	public String getCustomJmxUrl() {
		return customJmxUrl;
	}
	public void setCustomJmxUrl(String customJmxUrl) {
		this.customJmxUrl = customJmxUrl;
	}
	public String getJmxServerType() {
		return jmxServerType;
	}
	public void setJmxServerType(String jmxServerType) {
		this.jmxServerType = jmxServerType;
	}
	public List<String> getIncludeMBeansPatterns() {
		return includeMBeansPatterns;
	}
	public void setIncludeMBeansPatterns(List<String> includeMBeansPatterns) {
		this.includeMBeansPatterns = includeMBeansPatterns;
	}
	public List<String> getExcludeMBeansPatterns() {
		return excludeMBeansPatterns;
	}
	public void setExcludeMBeansPatterns(List<String> excludeMBeansPatterns) {
		this.excludeMBeansPatterns = excludeMBeansPatterns;
	}
	public String getWorkDir() {
		return workDir;
	}
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}
	public String getArchiveDir() {
		return archiveDir;
	}
	public void setArchiveDir(String archiveDir) {
		this.archiveDir = archiveDir;
	}
	public String getDirJar() {
		return dirJar;
	}
	public void setDirJar(String jarFile) {
		this.dirJar = jarFile;
	}
	public String getJarFileName() {
		return jarFileName;
	}
	public void setJarFileName(String jarFileName) {
		this.jarFileName = jarFileName;
	}
	public String getJarAbsFileName() {
		return jarAbsFileName;
	}
	public void setJarAbsFileName(String jarAbsFileName) {
		this.jarAbsFileName = jarAbsFileName;
	}
	public String getJarVersion() {
		return jarVersion;
	}
	public void setJarVersion(String jarVersion) {
		this.jarVersion = jarVersion;
	}
	public String[] getJarCommand() {
		return jarCommand;
	}
	public void setJarCommand(String[] jarCommand) {
		this.jarCommand = jarCommand;
	}
	// // commented because of the bug described in the SUPDT-12082
//	public String getDtServer() {
//		return dtServer;
//	}
//	public void setDtServer(String dtServer) {
//		this.dtServer = dtServer;
//	}
//	public long getDtPort() {
//		return dtPort;
//	}
//	public void setDtPort(long dtPort) {
//		this.dtPort = dtPort;
//	}
//	public String getDtProtocol() {
//		return dtProtocol;
//	}
//	public void setDtProtocol(String dtProtocol) {
//		this.dtProtocol = dtProtocol;
//	}
//	public boolean isDtAuth() {
//		return isDtAuth;
//	}
//	public void setDtAuth(boolean isDtAuth) {
//		this.isDtAuth = isDtAuth;
//	}
//	public String getDtUser() {
//		return dtUser;
//	}
//	public void setDtUser(String dtUser) {
//		this.dtUser = dtUser;
//	}
//	public String getDtPassword() {
//		return dtPassword;
//	}
//	public void setDtPassword(String dtPassword) {
//		this.dtPassword = dtPassword;
//	}
	public String getShellScriptContent() {
		return shellScriptContent;
	}
	public void setShellScriptContent(String shellScriptContent) {
		this.shellScriptContent = shellScriptContent;
	}
	public String getManifestAbsFileName() {
		return manifestAbsFileName;
	}
	public void setManifestAbsFileName(String manifestAbsFileName) {
		this.manifestAbsFileName = manifestAbsFileName;
	}
	public String getManifest() {
		return manifest;
	}
	public void setManifest(String manifest) {
		this.manifest = manifest;
	}
	public String getPluginXml() {
		return pluginXml;
	}
	public void setPluginXml(String pluginXml) {
		this.pluginXml = pluginXml;
	}
	public String getJmxServiceUrlString() {
		return jmxServiceUrlString;
	}
	public void setJmxServiceUrlString(String jmxServiceUrlString) {
		this.jmxServiceUrlString = jmxServiceUrlString;
	}
	public JMXServiceURL getJmxServiceUrl() {
		return jmxServiceUrl;
	}
	public void setJmxServiceUrl(JMXServiceURL jmxServiceUrl) {
		this.jmxServiceUrl = jmxServiceUrl;
	}
	public Map<String, String[]> getCredentialsMap() {
		return credentialsMap;
	}
	public void setCredentialsMap(Map<String, String[]> credentialsMap) {
		this.credentialsMap = credentialsMap;
	}
	public JMXConnector getJmxConnector() {
		return jmxConnector;
	}
	public void setJmxConnector(JMXConnector jmxConnector) {
		this.jmxConnector = jmxConnector;
	}
	public MBeanServerConnection getJmxConnection() {
		return jmxConnection;
	}
	public void setJmxConnection(MBeanServerConnection jmxConnection) {
		this.jmxConnection = jmxConnection;
	}
//	public CloseableHttpClient getHttpClient() {
//		return httpClient;
//	}
//	public void setHttpClient(CloseableHttpClient httpClient) {
//		this.httpClient = httpClient;
//	}
	public String getDtRestUrl() {
		return dtRestUrl;
	}
	public void setDtRestUrl(String dtRestUrl) {
		this.dtRestUrl = dtRestUrl;
	}
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JMXMonitorProperties [isTask=");
		builder.append(isTask);
		builder.append(", pluginId=");
		builder.append(pluginId == null ? BaseConstants.DASH : pluginId);
		builder.append(", bundleSymbolicName=");
		builder.append(bundleSymbolicName == null ? BaseConstants.EMPTY_STRING : bundleSymbolicName);
		builder.append(", customJmxUrlInd=");
		builder.append(customJmxUrlInd);
		builder.append(", jmxHost=");
		builder.append(jmxHost);
		builder.append(", jmxPort=");
		builder.append(jmxPort);
		builder.append(", isAuthenticationOn=");
		builder.append(isAuthenticationOn);
		builder.append(", user=");
		builder.append((user == null || user.isEmpty()) ? BaseConstants.DASH : user);
		builder.append(", password=");
		builder.append((password == null || password.isEmpty()) ? BaseConstants.DASH : password);
		builder.append(", urlPath=");
		builder.append((urlPath == null || urlPath.isEmpty()) ? BaseConstants.DASH : urlPath);
		builder.append(", customJmxUrl=");
		builder.append((customJmxUrl == null || customJmxUrl.isEmpty()) ? BaseConstants.DASH : customJmxUrl);
		builder.append(", jmxServerType=");
		builder.append((jmxServerType == null || jmxServerType.isEmpty()) ? BaseConstants.DASH : jmxServerType);
		builder.append(", includeMBeansPatterns=");
		builder.append(Arrays.toString(includeMBeansPatterns.toArray()));
		builder.append(", excludeMBeansPatterns=");
		builder.append(Arrays.toString(excludeMBeansPatterns.toArray()));
		builder.append(", workDir=");
		builder.append(workDir);
		builder.append(", dirJar=");
		builder.append(dirJar);
		builder.append(", archiveDir=");
		builder.append(archiveDir);
		builder.append(", jarFileName=");
		builder.append(jarFileName);
		builder.append(", jarAbsFileName=");
		builder.append(jarAbsFileName);
		builder.append(", jarVersion=");
		builder.append(jarVersion);
		builder.append(", jarCommand=");
		builder.append(Arrays.toString(jarCommand));
		// commented because of the bug described in the SUPDT-12082
//		builder.append(", dtServer=");
//		builder.append(dtServer);
//		builder.append(", dtPort=");
//		builder.append(dtPort);
//		builder.append(", dtProtocol=");
//		builder.append(dtProtocol);
//		builder.append(", isDtAuth=");
//		builder.append(isDtAuth);
//		builder.append(", dtUser=");
//		builder.append(dtUser == null ? BaseConstants.DASH : dtUser);
//		builder.append(", dtPassword=");
//		builder.append(dtPassword == null ? BaseConstants.DASH : dtPassword);
		builder.append(", shellScriptContent=");
		builder.append(shellScriptContent);
		builder.append(", manifestAbsFileName=");
		builder.append(manifestAbsFileName);
		builder.append(", manifest=");
		builder.append(manifest);
		builder.append(", manifest=");
		builder.append(manifest);
		builder.append(", pluginXml=");
		builder.append(pluginXml);
		builder.append(", jmxServiceUrlString=");
		builder.append((jmxServiceUrlString == null || jmxServiceUrlString.isEmpty()) ? BaseConstants.DASH : jmxServiceUrlString);
		builder.append(", jmxServiceUrl=");
		builder.append(jmxServiceUrl);
		builder.append(", credentialsMap=");
		builder.append(credentialsMap);
		builder.append(", jmxConnector=");
		builder.append(jmxConnector);
		builder.append(", jmxConnection=");
		builder.append(jmxConnection);
		// commented because of the bug described in the SUPDT-12082
//		builder.append(", httpClient=");
//		builder.append(httpClient == null ? BaseConstants.DASH : httpClient.toString());
		builder.append(", dtRestUrl=");
		builder.append(dtRestUrl == null ? BaseConstants.DASH : dtRestUrl);
		builder.append("]");
		return builder.toString();
	}
}
