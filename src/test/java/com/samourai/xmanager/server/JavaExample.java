package com.samourai.xmanager.server;

import com.samourai.xmanager.protocol.XManagerEndpoint;
import com.samourai.xmanager.protocol.XManagerEnv;
import com.samourai.xmanager.protocol.XManagerProtocol;
import com.samourai.xmanager.protocol.rest.AddressRequest;
import com.samourai.xmanager.protocol.rest.AddressResponse;

public class JavaExample {
    public void example() {

        XManagerEnv env = XManagerEnv.TESTNET;
        XManagerProtocol xmProtocol = XManagerProtocol.getInstance();

        {
            // URL
            String serviceUrl = xmProtocol.getUrlAddress(env);

            // request format
            AddressRequest requestBody = new AddressRequest("WHIRLPOOL");

            // response format
            AddressResponse responseBean;

            // now send POST(requestBody) to "serviceUrl", read response into "responseBean"
        }
    }
}
