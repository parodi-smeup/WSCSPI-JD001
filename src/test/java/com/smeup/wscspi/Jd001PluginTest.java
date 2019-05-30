package com.smeup.wscspi;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import com.smeup.wscspi.jd001.Jd001Plugin;

import Smeup.smeui.wscspi.datastructure.interfaces.SezConfInterface;
import Smeup.smeui.wscspi.datastructure.interfaces.SezInterface;
import Smeup.smeui.wscspi.datastructure.interfaces.SubInterface;
import Smeup.smeui.wscspi.datastructure.wscconnector.SPIWsCConnectorConf;
import Smeup.smeui.wscspi.datastructure.wscconnector.SPIWsCConnectorInput;
import Smeup.smeui.wscspi.datastructure.wscconnector.SPIWsCConnectorResponse;
import Smeup.smeui.wscspi.interfaces.SPIWsCFrameworkInterface;

public class Jd001PluginTest {
	
    private SPIWsCConnectorConf connectorConf = new SPIWsCConnectorConf();
    private SPIWsCConnectorInput connectorInput = new SPIWsCConnectorInput();
    private SPIWsCConnectorResponse connectorResponse = new SPIWsCConnectorResponse();
    private SezInterface sezInterface = getSezInterfaceInstance();
    private Jd001Plugin jd001Plugin = new Jd001Plugin();
	
	@Test
	public void test_launch() {
		/*
		 * WARNING! Due to temporarly unsupported RT (return RPG program indicator), 
		 * intepreted programs cannot run in statefull mode (so no memory manteined between two calls) 
		 * This means vars like "UrlRootPath" need to be set again on invoke call, more precisely
		 * UrlRootPath can't be obtained appending value from previous init method and next invoke method.
		 */
        connectorConf.addData("HttpDebug", "true");
        connectorConf.addData("UrlRootPath", "http://www.smeup.com/"); //WARNING! No effect cause this value will be override by invoke
        connectorConf.addData("RpgSources", "src/test/resources/rpg/");
        jd001Plugin.init(sezInterface, connectorConf);
        
        connectorInput.addData("Query", "http://www.mocky.io/v2/5185415ba171ea3a00704eed");
        connectorResponse = jd001Plugin.invoke("", connectorInput);

        final String mockyResponse = "{\"hello\": \"world\"}";
        assertTrue(mockyResponse.equals(connectorResponse.getFreeResponse().trim()));
	}
	
    private SezInterface getSezInterfaceInstance() {
    	return new SezInterface() {

            boolean iB64Response= false;
            @Override
            public void log(String arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public boolean init(SPIWsCFrameworkInterface arg0, String arg1, String arg2) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public String getUrl() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getType() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getTOgg() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public HashMap<String, SubInterface> getSubTable() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getPluginClass() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getPgm() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getOgg() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return "A01";
            }

            @Override
            public String getId() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getHttpMode() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getHttpAuthUser() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getHttpAuthPassword() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getHttpAuthMode() {
                return null;
            }

            @Override
            public SezConfInterface getConf() {
                return null;
            }

            @Override
            public int getTimeout() {
                // TODO Auto-generated method stub
                return 30;
            }
            
            @Override
            public void setB64Response(boolean aFlag)
            {
                iB64Response= aFlag;
            }
            
            @Override
            public boolean getB64Response()
            {
                return iB64Response;
            }

        };
    }

	
}
