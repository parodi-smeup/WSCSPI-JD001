package com.smeup.wscspi.jd001;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
	boolean overrideSystemOut = false;
	boolean iHttpDebug = false;
	String iUrlRootPath = null;
	int iTimeout = 60;

	public boolean init(SezInterface aSez, SPIWsCConnectorConf aConfiguration) {
		iSez = aSez;

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

		String[] args = new String[5];
		args[0] = iRpgSourceName;
		args[1] = "INZ";
		args[2] = "";
		args[3] = iUrlRootPath;
		args[4] = "";

		String response = executeRunnerKt(args);

		log(0, response + " ...done.");

		return iConfiguration != null;
	}

	public SPIWsCConnectorResponse invoke(String aMetodo, SPIWsCConnectorInput aDataTable) {

		SPIWsCConnectorResponse vRet = new SPIWsCConnectorResponse();

		log(0, "Calling 'ESE' on " + iRpgSourceName + "...");
		String query = aDataTable.getData("Query");
		String[] args = new String[5];
		args[0] = iRpgSourceName;
		args[1] = "ESE";
		args[2] = "";
		args[3] = query;
		args[4] = "";
		vRet.setFreeResponse(executeRunnerKt(args));
		log(0, vRet.getFreeResponse() + " ...done.");

		return vRet;
	}

	private String executeRunnerKt(final String[] args) {

		String response = "";

		// load Jd_url program (a java programm called as an RPG from an interpreted
		// RPG)
		JavaSystemInterface.INSTANCE.addJavaInteropPackage("com.smeup.jd");
		if (overrideSystemOut) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			PrintStream old = System.out;
			System.setOut(ps);
			RunnerKt.main(args);
			System.out.flush();
			System.setOut(old);
			response = baos.toString();
		}else {
			RunnerKt.main(args);
		}

		return response;
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
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.methods",
					"INFO");
			System.setProperty(
					"org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.methods.multipart", "INFO");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.auth", "INFO");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.protocol",
					"INFO");
		}

		log(0, "Calling 'CLO' on " + iRpgSourceName + "...");

		String[] args = new String[5];
		args[0] = iRpgSourceName;
		args[1] = "CLO";
		args[2] = "";
		args[3] = "";
		args[4] = "";

		String response = executeRunnerKt(args);

		RunnerKt.main((new String[] { iRpgSourceName, "CLO", "", "", "" }));
		log(0, response + " ...done.");

		return true;
	}

	public boolean ping() {
		// TODO Auto-generated method stub
		return true;
	}

}
