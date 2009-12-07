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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

/**
 * An integration test which requires a cometd server to be running, by default on
 * http://localhost:8080/cometd-demo/cometd
 * <p/>
 * You can overload the <b>comet.uri</b> system property to define the comet connection URI
 * to something like <b>cometd://localhost/other</b>
 * @version $Revision: 812040 $
 */
public class CometProducerTest extends CamelTestSupport {
	
	//@Ignore
	@Test
	// ignored test. Make sure you have setup the comet server properly and remove the @Ignore's
	public void testSendMessage() throws InterruptedException {
		getMockEndpoint("mock:result").expectedBodiesReceived("Hello", "World");
		getMockEndpoint("mock:result").assertNoDuplicates(body());
		template.sendBody("direct:start", "Hello");
		template.sendBody("direct:start", "World");
		assertMockEndpointsSatisfied();
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				String cometUri = System.getProperty("comet.uri", "comet://localhost:8080/cometd-demo/cometd");
				from("direct:start").to(cometUri + "#/messages").to("mock:result");
			}
		};
	}
}
