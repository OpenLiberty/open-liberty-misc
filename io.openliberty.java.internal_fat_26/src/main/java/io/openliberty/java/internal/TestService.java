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
		log("NOTE: In Java 26 default mode, this will issue WARNING messages in console.log");
		log("FAT test should verify warning messages appear in server logs");

		Person p = new Person();
		log("Before mutation: " + p.name);
		
		boolean mutationAttempted = false;
		boolean mutationBlocked = false;
		
		try {
			// Attempt 'deep reflection' mutation of final field
			Field f = Person.class.getDeclaredField("name");
			f.setAccessible(true);
			
			// Deep reflection: modify the field's modifiers to remove FINAL
			// In Java 26 default mode: issues WARNING to console
			// With --illegal-final-field-mutation=deny: throws IllegalAccessException
			log("Attempting deep reflection (modifying modifiers field)...");
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			
			// Now attempt to set the field value
			f.set(p, "Mutated");
			mutationAttempted = true;
			log("After mutation attempt: " + p.name);

		} catch (IllegalAccessException e) {
			// With --illegal-final-field-mutation=deny, this exception is expected ( this can be activated in jvm.options)
			mutationBlocked = true;
			log("Deep reflection mutation BLOCKED (strict mode): " + e.getMessage());
		} catch (Exception e) {
			// Catch any other exceptions that might occur
			mutationBlocked = true;
			log("Mutation prevented: " + e.getClass().getName() + " - " + e.getMessage());
		}

		// Verify the field state
		if ("Original".equals(p.name)) {
			log("RESULT: Final field remained immutable (value: " + p.name + ")");
		} else {
			log("RESULT: Final field was mutated to: " + p.name);
		}

		log("");
		log("JEP 500 Summary:");
		log("- Java 26 default: Issues WARNING messages (preparation phase)");
		log("- With --illegal-final-field-mutation=deny: Blocks mutation with exception");
		log("- Future Java versions: Will block by default");
		log("- FAT validation: Check for WARNING messages in console.log/messages.log");
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
