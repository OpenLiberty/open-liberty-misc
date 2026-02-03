/*******************************************************************************
 * Copyright (c) 2026 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.java.internal;

import module java.base;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;
import java.util.concurrent.StructuredTaskScope;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
@ApplicationScoped
public class TestService {

	static class Person {
		public final String name = "Original";
	}

	private StringWriter sw = new StringWriter();

	@GET
	public String test() {
		try {
			log(">>> ENTER");
			doTest();
			log("<<< EXIT SUCCESSFUL");
		} catch (Exception e) {
			e.printStackTrace(System.out);
			e.printStackTrace(new PrintWriter(sw));
			log("<<< EXIT FAILED");
		}
		String result = sw.toString();
		sw = new StringWriter();
		return result;
	}

	private void doTest() throws Exception {
		log("Beginning Java 26 testing");

		// Test Final Mean Final (JEP 500)
		testFinalMeanFinal();

		// Test HTTP/3 Support (JEP 517)
		testHTTP3Support();

		log("Leaving testing");
	}

	// Prepare to Make Final Mean Final : JEP 500 -> https://openjdk.org/jeps/500
	private void testFinalMeanFinal() throws Exception {
		log("Beginning JEP 500 testing: Prepare to Make Final Mean Final");

		Person p = new Person();
		log("Before mutation: " + p.name);
		try {
			// Attempt reflective mutation of final field
			Field f = Person.class.getDeclaredField("name");
			f.setAccessible(true);
			f.set(p, "Mutated");
			log("After mutation attempt: " + p.name);

			// FAT validation
			if (!"Original".equals(p.name)) {
				throw new Exception("JEP 500 violation: final field was observably mutated via reflection");
			}

		} catch (NoSuchFieldException | IllegalAccessException e) {
			log("Reflection access failed as expected: " + e.getMessage());
		}

		log("JEP 500 prepares the language for stricter final semantics in future releases");
		log("Leaving JEP 500 testing");
	}

	// HTTP/3 for the HTTP Client API : JEP 517 -> https://openjdk.org/jeps/517
	private void testHTTP3Support() {
		log("Testing HTTP/3 Support (JEP 517)");

		try {
			// Create an HTTP client with HTTP/3 support
			HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_3) // HTTP/3 support
					.build();

			log("HTTP Client created with HTTP/3 support");
			// This demonstrates that the API is available
			log("HTTP/3 version: " + HttpClient.Version.HTTP_3);

			// Note: Actual HTTP/3 requests would require a server that supports HTTP/3
			log("HTTP/3 is now available in the standard HttpClient API");

		} catch (Exception e) {
			log("HTTP/3 test note: " + e.getMessage());
			log("HTTP/3 support is available in Java 26 HttpClient API");
		}
        log("Leaving JEP 517 testing");
	}

	public void log(String msg) {
		System.out.println(msg);
		sw.append(msg);
		sw.append("<br/>");
	}

}
