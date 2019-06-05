package com.smeup.wscspi.jd001;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.smeup.rpgparser.CommandLineProgram;
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
	private final String RPG_FILENAME = "JD_001B.rpgle";
	private String iRpgSourceName = null;
	boolean overrideSystemOut = true;
	boolean iHttpDebug = false;
	private String iUrlRootPath = null;
	private CommandLineProgram commandLineProgram;
	
	private JavaSystemInterface javaSystemInterface;
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private PrintStream  ps = new PrintStream(baos);

	public boolean init(SezInterface aSez, SPIWsCConnectorConf aConfiguration) {
		iSez = aSez;

		baos = new ByteArrayOutputStream();
		ps = new PrintStream(baos);
		// load Jd_url program (a java programm called as an RPG from an interpreted RPG)
		javaSystemInterface = new JavaSystemInterface(ps);
		javaSystemInterface.addJavaInteropPackage("com.smeup.jd");
		
		iConfiguration = aConfiguration;
		String vHttpDebugMode = "false";
		if (iConfiguration != null) {
			vHttpDebugMode = iConfiguration.getData("HttpDebug");
			iUrlRootPath = iConfiguration.getData("UrlRootPath");
			iRpgSourceName = iConfiguration.getData("RpgSources").trim() + RPG_FILENAME;
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
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.methods",
					"DEBUG");
			System.setProperty(
					"org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.methods.multipart",
					"DEBUG");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.protocol",
					"DEBUG");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.wire", "DEBUG");
		}
		

		log(0, "Inizializzato " + getClass().getName());
		log(0, "Calling 'INZ' on " + iRpgSourceName + "...");
		
		List<String> parms = new ArrayList<String>();
		parms.add("INZ");
		parms.add("");
		parms.add(iUrlRootPath);
		parms.add("");

		String response = callProgram(parms);

		log(0, response + " ...done.");

		return iConfiguration != null;
	}

	public SPIWsCConnectorResponse invoke(String aMetodo, SPIWsCConnectorInput aDataTable) {

		SPIWsCConnectorResponse vRet = new SPIWsCConnectorResponse();

		log(0, "Calling 'ESE' on " + iRpgSourceName + "...");
		String query = aDataTable.getData("Query");

		List<String> parms = new ArrayList<String>();
		parms.add("ESE");
		parms.add("");
		parms.add(query);
		parms.add("");
		
		vRet.setFreeResponse(callProgram(parms));
		completeResponse(vRet);
		
		log(0, vRet.getFreeResponse() + " ...done.");

		return vRet;
	}

	public SezInterface getSez() {
		// TODO Auto-generated method stub
		return iSez;
	}
	
	private String callProgram(final List<String> parms) {
		
		// call JD_001 program
		commandLineProgram = RunnerKt.getProgram(iRpgSourceName, javaSystemInterface);
		commandLineProgram.setTraceMode(false);
		commandLineProgram.singleCall(parms);
		
		String response = new String(baos.toByteArray(), StandardCharsets.UTF_8);
	    
	    return response;
	}
	

	public boolean unplug() {
		if (iHttpDebug) {
			iHttpDebug = false;
			System.setProperty("org.apache.commons.logging.Log", "");
			System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");

			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.wire", "INFO");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "INFO");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.methods",
					"INFO");
			System.setProperty(
					"org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.methods.multipart", "INFO");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.auth", "INFO");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.protocol",
					"INFO");
		}

		log(0, "Calling 'CLO' on " + iRpgSourceName + "...");

		List<String> parms = new ArrayList<String>();
		parms.add("CLO");
		parms.add("");
		parms.add("");
		parms.add("");
		
		String response = callProgram(parms);

		log(0, response + " ...done.");

		return true;
	}

	public boolean ping() {
		// TODO Auto-generated method stub
		return true;
	}
	
	private void completeResponse(SPIWsCConnectorResponse vRet) {
		
		String responseString = vRet.getFreeResponse();
		
        HashMap<String, String> vDataRow = new HashMap<String, String>();
        String vResponseCode = "200";
        String vStatusCode = "*OK";
        if(responseString.contains("*ERROR")) {
            vResponseCode = "99";
            vStatusCode = "*ERROR";
        }
        vDataRow.put("ResponseCode", vResponseCode);
        vDataRow.put("ResponseRawText", responseString);
        vDataRow.put("Status", vStatusCode);
        vDataRow.put("Value", responseString);
        vDataRow.put("ResponseString", responseString);
        vRet.addRow(vDataRow);
	}

}
