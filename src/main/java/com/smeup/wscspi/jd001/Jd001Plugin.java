package com.smeup.wscspi.jd001;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;

import com.smeup.rpgparser.RunnerKt;
import com.smeup.rpgparser.jvminterop.JavaSystemInterface;

import Smeup.smeui.wscspi.datastructure.interfaces.SezInterface;
import Smeup.smeui.wscspi.datastructure.wscconnector.SPIWsCConnectorConf;
import Smeup.smeui.wscspi.datastructure.wscconnector.SPIWsCConnectorInput;
import Smeup.smeui.wscspi.datastructure.wscconnector.SPIWsCConnectorResponse;
import Smeup.smeui.wscspi.interaction.SPIWsCConnectorAdapter;


public class Jd001Plugin extends SPIWsCConnectorAdapter {

    SezInterface iSez = null;
    SPIWsCConnectorConf iConfiguration = null;
    private static final String RPG_SOURCE_NAME = "D:/IOT/rpg/JD_001B.rpgle";
    boolean iHttpDebug = false;
    String iUrlRootPath = null;
    int iTimeout = 60;

    public boolean init(SezInterface aSez, SPIWsCConnectorConf aConfiguration) {
        iSez = aSez;
      
        iConfiguration = aConfiguration;
        String vHttpDebugMode = "false";
        if (iConfiguration != null) {
            Enumeration<String> vKeyEnum = iConfiguration.getPropertyTable().keys();
            while (vKeyEnum.hasMoreElements()) {
                String vKey = (String) vKeyEnum.nextElement();
                if ("HttpDebug".equalsIgnoreCase(vKey)) {
                    vHttpDebugMode = iConfiguration.getData("HttpDebug");
                }
                if ("UrlRootPath".equalsIgnoreCase(vKey)) {
                	iUrlRootPath = iConfiguration.getData("UrlRootPath");
                }
            }
        }

        iHttpDebug = (vHttpDebugMode != null) ? Boolean.valueOf(vHttpDebugMode) : false;
        if (iHttpDebug) {
            log(0, "Abilito Debug HTTP");
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
            System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.conn", "DEBUG");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.client", "DEBUG");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.client", "DEBUG");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "DEBUG");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.auth", "DEBUG");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.methods", "DEBUG");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.methods.multipart", "DEBUG");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.protocol", "DEBUG");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.wire", "DEBUG");
        }

        log(0, "Inizializzato " + getClass().getName()); 
        log(0, "Calling 'INZ'...");
        String response = executeOverridingSystemOut(new String[] {RPG_SOURCE_NAME, "INZ", iUrlRootPath, "", ""});
        log(0, response + " ...done.");
        
        return iConfiguration != null;
    }
/*
    private void log(String aText) {
        SezInterface vSez = getSez();
        if (vSez != null) {
            vSez.log(aText);
        } else {
            System.out.println(aText);
        }
    }
*/
    public SPIWsCConnectorResponse invoke(String aMetodo,
            SPIWsCConnectorInput aDataTable) {

        SPIWsCConnectorResponse vRet = new SPIWsCConnectorResponse();

        log(0, "Calling 'EXE'...");
        String query = aDataTable.getData("Query");
        vRet.setFreeResponse(executeOverridingSystemOut(new String[] {RPG_SOURCE_NAME, "EXE", query, "", ""}));
//        RunnerKt.main(new String[] {RPG_SOURCE_NAME, "EXE", query, "", ""});
        log(0, vRet.getFreeResponse() + " ...done.");

        return vRet;
    }
    
	private String executeOverridingSystemOut(final String[] args) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);
	    JavaSystemInterface.INSTANCE.addJavaInteropPackage("com.smeup.jd");
		RunnerKt.main(args);
		System.out.flush();
		System.setOut(old);
		return baos.toString();
	}

    public SezInterface getSez() {
        // TODO Auto-generated method stub
        return iSez;
    }

    public boolean unplug() {
        if (iHttpDebug) {
            iHttpDebug = false;
            System.setProperty("org.apache.commons.logging.Log", "");
            System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");

            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.wire", "INFO");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "INFO");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.methods", "INFO");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.methods.multipart", "INFO");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.auth", "INFO");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.protocol", "INFO");
        }
        log(0, "Calling 'CLO'...");
        RunnerKt.main((new String[] {RPG_SOURCE_NAME, "CLO", "", "", ""}));
        log(0, " ...done.");
        return true;
    }

    public boolean ping() {
        // TODO Auto-generated method stub
        return true;
    }

}
