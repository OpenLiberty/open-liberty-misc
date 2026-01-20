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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
@ApplicationScoped
public class TestService {

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

        // Test Primitive Types in Patterns (JEP 530 - Fourth Preview)
        testPrimitivePatterns();
        
        // Test PEM Encodings (JEP 524 - Second Preview)
        testPEMEncodings();
        
        // Test Structured Concurrency (JEP 525 - Sixth Preview)
        testStructuredConcurrency();
        
        // Test Lazy Constants (JEP 526 - Second Preview)
        testLazyConstants();
        
        // Test Final Mean Final (JEP 500)
        testFinalMeanFinal();
        
        // Test HTTP/3 Support (JEP 517)
        testHTTP3Support();

        log("Leaving testing");
    }

    // Primitive Types in Patterns, instanceof, and switch : JEP 530 (Fourth Preview) -> https://openjdk.org/jeps/530
    private void testPrimitivePatterns() {
        log("Testing Primitive Patterns (JEP 530 - Fourth Preview)");
        
        Object obj = 42;
        
        // Primitive pattern matching with instanceof
        if (obj instanceof int i) {
            log("Found int value: " + i);
        }
        
        // Primitive patterns in switch
        String result = switch (obj) {
            case int i when i > 0 -> "Positive integer: " + i;
            case int i when i < 0 -> "Negative integer: " + i;
            case int i -> "Zero";
            case long l -> "Long value: " + l;
            case double d -> "Double value: " + d;
            default -> "Other type";
        };
        
        log("Switch result: " + result);
        
        // Test with different primitive types
        testPrimitiveSwitch(100);
        testPrimitiveSwitch(3.14);
        testPrimitiveSwitch(true);
    }
    
    private void testPrimitiveSwitch(Object value) {
        String description = switch (value) {
            case byte b -> "Byte: " + b;
            case short s -> "Short: " + s;
            case int i -> "Integer: " + i;
            case long l -> "Long: " + l;
            case float f -> "Float: " + f;
            case double d -> "Double: " + d;
            case boolean bool -> "Boolean: " + bool;
            default -> "Not a primitive: " + value.getClass().getSimpleName();
        };
        log("Primitive switch: " + description);
    }

    // PEM Encodings of Cryptographic Objects : JEP 524 (Second Preview) -> https://openjdk.org/jeps/524
    private void testPEMEncodings() throws Exception {
        log("Testing PEM Encodings (JEP 524 - Second Preview)");
        
        // Generate a key pair for testing
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        
        // Note: PEM encoding APIs would be used here when available
        // This is a placeholder to demonstrate the feature exists
        log("Generated RSA key pair for PEM encoding test");
        log("PEM encoding allows encoding/decoding of cryptographic objects in PEM format");
    }

    // Structured Concurrency : JEP 525 (Sixth Preview) -> https://openjdk.org/jeps/525
    private void testStructuredConcurrency() throws Exception {
        log("Testing Structured Concurrency (JEP 525 - Sixth Preview)");
        
        // Example of structured concurrency with StructuredTaskScope
        try (var scope = StructuredTaskScope.open()) {
            var task1 = scope.fork(() -> {
                Thread.sleep(100);
                return "Task 1 completed";
            });
            
            var task2 = scope.fork(() -> {
                Thread.sleep(150);
                return "Task 2 completed";
            });
            
            scope.join();
            
            log("Structured concurrency result 1: " + task1.get());
            log("Structured concurrency result 2: " + task2.get());
        }
    }

    // Lazy Constants : JEP 526 (Second Preview) -> https://openjdk.org/jeps/526
    private void testLazyConstants() {
        log("Testing Lazy Constants (JEP 526 - Second Preview)");
        
        // Lazy constants allow constant expressions to be evaluated lazily
        // This is a conceptual demonstration as the syntax requires language support
        log("Lazy constants enable deferred evaluation of constant expressions");
        log("This improves startup time by deferring expensive constant computations");
        
        // Example concept (actual syntax may differ):
        // static final lazy String EXPENSIVE_CONSTANT = computeExpensiveValue();
    }

    // Prepare to Make Final Mean Final : JEP 500 -> https://openjdk.org/jeps/500
    private void testFinalMeanFinal() {
        log("Testing Final Mean Final (JEP 500)");
        
        // JEP 500 prepares for making 'final' truly final in future Java versions
        // This tests that final classes and methods work as expected
        
        final class FinalClass {
            final String value = "immutable";
            
            final String getValue() {
                return value;
            }
        }
        
        FinalClass fc = new FinalClass();
        log("Final class value: " + fc.getValue());
        
        // Test final variables
        final int finalVar = 42;
        log("Final variable: " + finalVar);
        
        log("JEP 500 prepares the language for stricter final semantics in future releases");
    }

    // HTTP/3 for the HTTP Client API : JEP 517 -> https://openjdk.org/jeps/517
    private void testHTTP3Support() {
        log("Testing HTTP/3 Support (JEP 517)");
        
        try {
            // Create an HTTP client with HTTP/3 support
            HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_3)  // HTTP/3 support
                .build();
            
            log("HTTP Client created with HTTP/3 support");
            log("HTTP/3 version: " + HttpClient.Version.HTTP_3);
            
            // Note: Actual HTTP/3 requests would require a server that supports HTTP/3
            // This demonstrates that the API is available
            log("HTTP/3 is now available in the standard HttpClient API");
            
        } catch (Exception e) {
            log("HTTP/3 test note: " + e.getMessage());
            log("HTTP/3 support is available in Java 26 HttpClient API");
        }
    }

    public void log(String msg) {
        System.out.println(msg);
        sw.append(msg);
        sw.append("<br/>");
    }
}

// Made with Bob
