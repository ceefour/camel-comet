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

import java.net.URISyntaxException;

import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cometd.Client;
import org.cometd.Message;
import org.cometd.MessageListener;

/**
 * A {@link org.apache.camel.Consumer Consumer} which listens to XMPP packets
 * 
 * @version $Revision: 809453 $
 */
public class CometConsumer extends DefaultConsumer implements MessageListener {
	private static final transient Log LOG = LogFactory
			.getLog(CometConsumer.class);
	private final CometEndpoint endpoint;

	public CometConsumer(CometEndpoint endpoint, Processor processor) {
		super(endpoint, processor);
		this.endpoint = endpoint;
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		LOG.info("Subscribing to [" + endpoint.getChannel() + "] on ["
				+ endpoint.getCometUrl() + "]");
		endpoint.getBayeuxClient().addListener(this);
		endpoint.getBayeuxClient().subscribe(endpoint.getChannel());
	}

	@Override
	protected void doStop() throws Exception {
		LOG.info("Unsubscribing from [" + endpoint.getChannel() + "] on ["
				+ endpoint.getCometUrl() + "]");
		endpoint.getBayeuxClient().unsubscribe(endpoint.getChannel());
		endpoint.getBayeuxClient().removeListener(this);
		super.doStop();
	}

	public void deliver(Client from, Client to, Message message) {
		if (message.getChannel().equals(endpoint.getChannel()) && message.getData() != null) {
//			if (LOG.isDebugEnabled()) {
				LOG.info("Received message [" + message + "] from [" + from
						+ "] to [" + to + "] on " + endpoint.getCometUrl());
//			}
			Exchange exchange = endpoint.createExchange(message);
			exchange.setFromEndpoint(endpoint);
			exchange.setPattern(ExchangePattern.InOnly);
			try {
				getProcessor().process(exchange);
			} catch (Exception e) {
				LOG.debug("Exception while processing message [" + message
						+ "] from [" + from + "] to [" + to + "]", e);
				exchange.setException(e);
			}
		}
	}
}
