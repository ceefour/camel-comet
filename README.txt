To run the tests, you need to run a local cometd server.

Get the `cometd-demo` project here:

  http://github.com/ceefour/cometd-demo

Compile the project (or use the resulting WAR) and install it on your container
under `/cometd-demo` context so it's accesible by:

  http://localhost:8080/cometd-demo

From `camel-comet` project, run `mvn test` to run the tests.

Set the `comet.uri` Java system property to change to something else,
for example if you have a server running on:

  http://example.com:9000/cometd

Then set `comet.uri` as follows:

  comet://example.com:9000/cometd

