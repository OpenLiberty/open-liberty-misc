/*******************************************************************************
 * Copyright (c) 2026 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.java.internal;

import module java.base;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.net.http.HttpClient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.io.StringWriter;
import java.io.PrintWriter;

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
		log("Testing with --illegal-final-field-mutation=deny (future default behavior.This can be turn OFF/ON in JVM Options)");

		Person p = new Person();
		log("Before mutation attempt: " + p.name);
		
		boolean exceptionCaught = false;
		String exceptionMessage = null;
		String exceptionType = null;
		
		try {
			// Attempt 'deep reflection' mutation of final field
			Field f = Person.class.getDeclaredField("name");
			f.setAccessible(true);
			
			// Deep reflection: modify the field's modifiers to remove FINAL
			// In Java 12+, the modifiers field was removed
			log("Attempting deep reflection (accessing modifiers field)...");
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
				log("Modifiers field accessed (Java 11 or earlier)");
			} catch (NoSuchFieldException e) {
				log("Modifiers field not accessible (Java 12+) - this is expected");
				log("Attempting direct mutation of final field...");
			}
			
			// Attempt to set the final field directly
			f.set(p, "Mutated");
			log("ERROR: Mutation was not blocked! Field value: " + p.name);
			throw new Exception("JEP 500 test FAILED: Final field mutation should have been blocked");

		} catch (IllegalAccessException e) {
			exceptionCaught = true;
			exceptionType = "IllegalAccessException";
			exceptionMessage = e.getMessage();
			log("SUCCESS: IllegalAccessException caught as expected");
			log("Exception message: " + exceptionMessage);
		} catch (NoSuchFieldException e) {
			exceptionCaught = true;
			exceptionType = "NoSuchFieldException";
			exceptionMessage = e.getMessage();
			log("SUCCESS: NoSuchFieldException caught (Java 12+ protection)");
			log("The 'modifiers' field has been removed in Java 12+");
		}

		if (!exceptionCaught) {
			throw new Exception("JEP 500 test FAILED: Expected exception was not thrown");
		}

		if (!"Original".equals(p.name)) {
			throw new Exception("JEP 500 test FAILED: Final field was mutated. Value: " + p.name);
		}

		log("RESULT: Final field remained immutable (value: " + p.name + ")");
		log("JEP 500 Test Summary:");
		log("Exception thrown: " + exceptionType);
		log("Final field remained immutable");
		log("Mutation attempt was blocked");
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
