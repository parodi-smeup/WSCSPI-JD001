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

	private SezInterface sez = null;
	private SPIWsCConnectorConf configuration = null;
	private final String RPG_FILENAME = "JD_001B.rpgle";
	private String rpgSourceName = null;
	private boolean httpDebug = false;
	private String urlRoothPath = null;
	private CommandLineProgram commandLineProgram;
	private JavaSystemInterface javaSystemInterface;
	private ByteArrayOutputStream byteArrayOutputStream;
	private PrintStream printStream;
	private CommandLineProgram program;

	public boolean init(SezInterface sezInterface, SPIWsCConnectorConf connectorConf) {

		log(0, "Called init " + getClass().getName());

		sez = sezInterface;
		configuration = connectorConf;

		// To handle system.out response
		byteArrayOutputStream = new ByteArrayOutputStream();
		printStream = new PrintStream(byteArrayOutputStream);

		// load Jd_url program (a java programm called as an RPG from an interpreted
		// RPG)
		javaSystemInterface = new JavaSystemInterface(printStream);
		javaSystemInterface.addJavaInteropPackage("com.smeup.jd");

		
		// Parameters from script SCP_SET.LOA38_JD1
		if (configuration != null) {
			String debug = configuration.getData("HttpDebug");
			httpDebug = (debug != null && !"".equals(debug)) ? Boolean.valueOf(debug) : false;
			urlRoothPath = configuration.getData("UrlRootPath");
			rpgSourceName = configuration.getData("RpgSources").trim() + RPG_FILENAME;
		}

		program = RunnerKt.getProgram(rpgSourceName, javaSystemInterface);
		
		program.setTraceMode(true);
		
		if (httpDebug) {
			switchDebug(true);
		}

		// Call rpg program with parameters
		List<String> parms = new ArrayList<String>();
		parms.add("INZ");
		parms.add("");
		parms.add(urlRoothPath);
		parms.add("");
		String response = callProgram(parms);

		log(0, response + " ...done.");

		return configuration != null;
	}

	public SPIWsCConnectorResponse invoke(String aMetodo, SPIWsCConnectorInput aDataTable) {

		log(0, "Called invoke " + getClass().getName());

		SPIWsCConnectorResponse connectorResponse = new SPIWsCConnectorResponse();

		// Call rpg program with parameters
		List<String> parms = new ArrayList<String>();
		parms.add("ESE");
		parms.add("");
		parms.add(aDataTable.getData("Query"));
		parms.add("");
		connectorResponse.setFreeResponse(callProgram(parms));

		completeResponse(connectorResponse);

		log(0, connectorResponse.getFreeResponse() + " ...done.");

		return connectorResponse;
	}

	public SezInterface getSez() {
		// TODO Auto-generated method stub
		return sez;
	}

	private String callProgram(final List<String> parms) {
		log(0, "Calling " + rpgSourceName + " with " + parms.size() + " parms: " + String.join(",", parms));

		program.singleCall(parms);
		String response = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
		byteArrayOutputStream.reset();
		
		return response;
	}

	public boolean unplug() {

		switchDebug(false);

		// Call rpg program with parameters
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

	private void completeResponse(SPIWsCConnectorResponse connectorResponse) {

		String responseString = connectorResponse.getFreeResponse();
		HashMap<String, String> dataRow = new HashMap<String, String>();
		String responseCode = "200";
		String statusCode = "*OK";
		if (responseString.contains("*ERROR")) {
			responseCode = "99";
			statusCode = "*ERROR";
		}
		dataRow.put("ResponseCode", responseCode);
		dataRow.put("ResponseRawText", responseString);
		dataRow.put("Status", statusCode);
		dataRow.put("Value", responseString);
		dataRow.put("ResponseString", responseString);
		connectorResponse.addRow(dataRow);
	}

	private void switchDebug(final boolean switchDebugON) {

		if (switchDebugON) {
			log(0, "Set ON Debug HTTP");
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
		} else {
			log(0, "Set OFF Debug HTTP");
			httpDebug = false;
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
	}

}
