package com.dynatrace.diagnostics.plugin.jmx.helper;

public enum JMX_SERVER_TYPE {
	CF_OPS_METRICS_TOOL("Cloud Foundry Ops Metrics Tool"), 
	JVM("JVM"), 
	WEBSPHERE("WebSphere"), 
	WEBLOGIC("Weblogic"), 
	JBOSS("JBoss");

	private String jmxServerType;

	JMX_SERVER_TYPE(String jmxServerType) {
		this.jmxServerType = jmxServerType;
	}

	public String jmxServerType() {
		return jmxServerType;
	}
	
	public static JMX_SERVER_TYPE valueOfIgnoreCase(String name) {
        for(JMX_SERVER_TYPE jmxST : JMX_SERVER_TYPE.values()) {
            if (jmxST.jmxServerType.equalsIgnoreCase(name)) {
                return jmxST;
            }
        }
        throw new IllegalArgumentException("There is no value with name '" + name + "' in the enum " + JMX_SERVER_TYPE.class.getName());        
    }

	// Optionally and/or additionally, toString.
	@Override
	public String toString() {
		return jmxServerType;
	}

}