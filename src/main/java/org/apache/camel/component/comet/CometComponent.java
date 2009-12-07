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
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Component for Comet client.
 * 
 * URI style:
 * 
 * comet://example.com/cometd/#/channel
 * comet://user:pass@example.com:8080/cometd/#/topic/other
 * comets://user:pass@example.com:443/cometd/#/topic/other
 * 
 * @version $Revision:520964 $
 */
public class CometComponent extends DefaultComponent {
	private static final transient Log LOG = LogFactory
			.getLog(CometComponent.class);

	/**
	 * keep a cache of endpoints so they can be properly cleaned up
	 */
	private Map<String, CometEndpoint> endpointCache = new HashMap<String, CometEndpoint>();

	@Override
	protected Endpoint createEndpoint(String uri, String remaining,
			Map<String, Object> parameters) throws Exception {

		if (endpointCache.containsKey(uri)) {
			LOG.debug("Using cached endpoint for URI " + uri);
			return endpointCache.get(uri);
		}

		LOG.debug("Creating new endpoint for URI " + uri);
		CometEndpoint endpoint = new CometEndpoint(uri, this);
		URI u = new URI(uri);
		endpoint.setHost(u.getHost());
		endpoint.setScheme(u.getScheme());
		if (u.getPort() > 0)
			endpoint.setPort(u.getPort());
		else {
			endpoint.setPort(endpoint.isSecure() ? 443 : 80);
		}
		if (u.getUserInfo() != null) {
			endpoint.setUser(u.getUserInfo());
		}
		String remainingPath = u.getPath();
		endpoint.setPath(remainingPath != null ? remainingPath : "/");
		endpoint.setChannel(u.getFragment());
		endpoint.initialize();

		endpointCache.put(uri, endpoint);
		
		return endpoint;
	}

	@Override
	protected synchronized void doStop() throws Exception {
		for (Map.Entry<String, CometEndpoint> entry : endpointCache.entrySet()) {
			entry.getValue().destroy();
		}
		endpointCache.clear();
	}

}
