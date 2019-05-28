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
		//Init (::SEZ Cod="A01")
        connectorConf.addData("HttpDebug", "true");
        connectorConf.addData("UrlRootPath", "http://www.smeup.com/");
        jd001Plugin.init(sezInterface, connectorConf);
        
        //Exec (::SUB Cod="001")
        connectorInput.addData("Query", "aziende-del-gruppo-2");
        connectorResponse = jd001Plugin.invoke("001", connectorInput);
        
        System.out.println(connectorResponse.getFreeResponse());
        assertTrue(true);
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
