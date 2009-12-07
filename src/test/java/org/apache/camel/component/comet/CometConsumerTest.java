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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * An integration test which requires a cometd server to be running, by default on
 * http://localhost:8080/cometd-demo/cometd
 * <p/>
 * You can overload the <b>comet.uri</b> system property to define the comet connection URI
 * to something like <b>cometd://localhost/other</b>
 * @version $Revision: 812040 $
 */
public class CometConsumerTest extends CamelTestSupport {
	
	//@Ignore
	@Test
	// ignored test. Make sure you have setup the comet server properly and remove the @Ignore's
	public void testSendAndReceiveMessage() throws InterruptedException {
//		getMockEndpoint("mock:sent").expectedBodiesReceived("Hello", "World");
//		getMockEndpoint("mock:sent").assertNoDuplicates(body());
		getMockEndpoint("mock:received").expectedBodiesReceived("Hello", "World");
		getMockEndpoint("mock:received").assertNoDuplicates(body());
		getMockEndpoint("mock:received").expectedMessageCount(2);
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.submit(new Runnable() {
			public void run() {
				template.sendBody("direct:start", "Hello");
			}
		});
		executor.submit(new Runnable() {
			public void run() {
				template.sendBody("direct:start", "World");
			}
		});
		//template.sendBody("mock:received", "maknyus");
		assertMockEndpointsSatisfied();
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				String cometUri = System.getProperty("comet.uri", "comet://localhost:8080/cometd-demo/cometd");
				from("direct:start").to(cometUri + "#/messages");
//				from("direct:start").to("mock:sent");
				from(cometUri + "#/messages").to("mock:received");
			}
		};
	}

}
