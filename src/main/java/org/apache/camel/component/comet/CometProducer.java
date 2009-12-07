/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.comet;


import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ObjectHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Producer to send messages using Bayeux protocol to a Cometd server.
 * 
 * @version $Revision: 748103 $
 */
public class CometProducer extends DefaultProducer {
	private static final transient Log LOG = LogFactory.getLog(CometProducer.class);
	
	private CometEndpoint endpoint;
	
    public CometProducer(CometEndpoint endpoint) {
		super(endpoint);
		this.endpoint = endpoint;
	}

	public void process(Exchange exchange) throws Exception {
        ObjectHelper.notNull(endpoint.getBayeuxClient(), "bayeuxClient");

        if (LOG.isTraceEnabled()) {
            LOG.trace("Delivering to cometd url: " + endpoint.getCometUrl() + " channel:" + endpoint.getChannel() + " exchange: " + exchange);
        }
        endpoint.getBayeuxClient().publish(endpoint.getChannel(), exchange.getIn().getBody(), null);
        // Simply pass through
        exchange.setOut(exchange.getIn().copy());
	}
    
}
