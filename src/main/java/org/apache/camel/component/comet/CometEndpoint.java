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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cometd.Client;
import org.cometd.Message;
import org.cometd.MessageListener;
import org.cometd.client.BayeuxClient;
import org.eclipse.jetty.client.HttpClient;

/**
 * Endpoint for Camel Comet client.
 *
 * @version $Revision:520964 $
 */
public class CometEndpoint extends DefaultEndpoint {
    private static final transient Log LOG = LogFactory.getLog(CometEndpoint.class);

	private String uri;
	private CometComponent component;
	private String host;
	private int port;
	private String user;
	private String path;
	private String channel;
	private String scheme;
	private HttpClient httpClient;
	private BayeuxClient bayeuxClient;
	
	private static class BayeuxListener implements MessageListener {

		public void deliver(Client from, Client to, Message msg) {
			if (from == null) {
				LOG.debug("Got meta message " + msg);
			}
			if (LOG.isTraceEnabled()) {
				LOG.trace("Got [" + msg + "] from [" + from + "] to [" + to + "]");
			}
			
		}
		
	}
	
	/**
	 * @param uri
	 * @param component
	 */
	public CometEndpoint(String uri, CometComponent component) {
		super();
		this.uri = uri;
		this.component = component;
	}

	public Consumer createConsumer(Processor processor) throws Exception {
		return new CometConsumer(this, processor);
	}

	public Producer createProducer() throws Exception {
		return new CometProducer(this);
	}
	
	@Override
	protected String createEndpointUri() {
		try {
			return new URI(scheme, user, host, port, path, null, channel).toString();
		} catch (URISyntaxException e) {
			LOG.error(e);
			throw new RuntimeCamelException("Error creating endpoint URI.", e);
		}
	}

	public boolean isSingleton() {
		return true;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the scheme
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * @param scheme the scheme to set
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	
	public boolean isSecure() {
		return scheme.equalsIgnoreCase("comets");
	}
	
	public void initialize() throws Exception {
		httpClient = new HttpClient();
		httpClient.start();
		bayeuxClient = new BayeuxClient(httpClient, getCometUrl().toString());
		bayeuxClient.addListener(new BayeuxListener());
		bayeuxClient.start();
	}
	
	public URI getCometUrl() {
		try {
			return new URI(scheme, user, host, port, path, null, null);
		} catch (URISyntaxException e) {
			LOG.error("Unable to generate Comet URL.", e);
			return null;
		}
	}
	
	public synchronized void destroy() throws Exception {
		if (bayeuxClient != null) {
			bayeuxClient.stop();
			bayeuxClient.disconnect();
			bayeuxClient = null;
		}
		if (httpClient != null) {
			httpClient.stop();
			httpClient = null;
		}
		}

	/**
	 * @return the bayeuxClient
	 */
	public BayeuxClient getBayeuxClient() {
		return bayeuxClient;
	}
	
	public Exchange createExchange(Message message) {
		//Exchange exchange = new DefaultExchange(this, getExchangePattern());
		Exchange exchange = createExchange();
		org.apache.camel.Message camelMessage = new DefaultMessage();
		camelMessage.setBody(message.getData());
		exchange.setIn(camelMessage);
		return exchange;
	}
   
}
