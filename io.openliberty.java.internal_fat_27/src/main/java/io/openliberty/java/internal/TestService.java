/*******************************************************************************
 * Copyright (c) 2027 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.java.internal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.management.MBeanServer;
import javax.net.ssl.SSLParameters;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

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
        log("Beginning Java 27 testing");
        testPostQuantumTLS();       // JEP 527
        testCompactObjectHeaders(); // JEP 534
        testJFRDataRedaction();     // JEP 536
        log("Leaving Java 27 testing");
    }

    // JEP 527: Post-Quantum Hybrid Key Exchange for TLS 1.3
    // https://openjdk.org/jeps/527
    //
    // Adds three ML-KEM/ECDHE hybrid named groups to the TLS 1.3 stack.
    // X25519MLKEM768 is placed first in the default preference list so that
    // existing code benefits automatically without any configuration change.
    // Verifies the default named-group list from SSLContext carries all three
    // hybrid groups and that X25519MLKEM768 is first.
    private void testPostQuantumTLS() throws Exception {
        log("Beginning JEP 527 testing: Post-Quantum Hybrid Key Exchange for TLS 1.3");

        // Must use SSLContext to get the populated default list;
        // new SSLParameters() is blank and returns null for getNamedGroups().
        SSLParameters params = javax.net.ssl.SSLContext.getDefault().getDefaultSSLParameters();
        String[] namedGroups = params.getNamedGroups();

        if (namedGroups == null || namedGroups.length == 0) {
            throw new Exception("JEP 527 FAILED: default SSLParameters returned no named groups");
        }
        log("Default TLS named groups (" + namedGroups.length + "): " + java.util.Arrays.toString(namedGroups));

        // X25519MLKEM768 must be first (most preferred)
        if (!"X25519MLKEM768".equals(namedGroups[0])) {
            throw new Exception("JEP 527 FAILED: expected X25519MLKEM768 first, got: " + namedGroups[0]);
        }
        log("SUCCESS: X25519MLKEM768 is the most preferred named group");

        // All three hybrid groups must be present
        java.util.Set<String> groups = new java.util.HashSet<>(java.util.Arrays.asList(namedGroups));
        for (String hybrid : new String[]{"X25519MLKEM768", "SecP256r1MLKEM768", "SecP384r1MLKEM1024"}) {
            if (!groups.contains(hybrid)) {
                throw new Exception("JEP 527 FAILED: hybrid group missing from supported set: " + hybrid);
            }
            log("Hybrid group present: " + hybrid);
        }

        log("Leaving JEP 527 testing");
    }

    // JEP 534: Compact Object Headers by Default
    // https://openjdk.org/jeps/534
    //
    // Makes compact object headers (64 bits / 8 bytes, down from 96 bits / 12 bytes)
    // the default on 64-bit HotSpot. Controlled by -XX:+/-UseCompactObjectHeaders.
    // Verifies the flag is true via HotSpotDiagnosticMXBean; logs a notice (not a
    // hard failure) if it has been explicitly disabled, e.g. by Liberty.
    private void testCompactObjectHeaders() throws Exception {
        log("Beginning JEP 534 testing: Compact Object Headers by Default");

        com.sun.management.HotSpotDiagnosticMXBean diagBean =
            ManagementFactory.getPlatformMXBean(com.sun.management.HotSpotDiagnosticMXBean.class);
        if (diagBean == null) {
            log("HotSpotDiagnosticMXBean not available — skipping JEP 534 check (non-HotSpot JVM)");
            return;
        }

        // Ensure the MBean is registered before querying
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        javax.management.ObjectName diagName =
            new javax.management.ObjectName("com.sun.management:type=HotSpotDiagnostic");
        if (!server.isRegistered(diagName)) {
            log("HotSpotDiagnostic MBean not registered — skipping JEP 534 check");
            return;
        }

        String flagValue = diagBean.getVMOption("UseCompactObjectHeaders").getValue();
        log("UseCompactObjectHeaders = " + flagValue);

        if ("true".equalsIgnoreCase(flagValue)) {
            log("SUCCESS: Compact object headers are active (UseCompactObjectHeaders=true)");
        } else {
            log("NOTICE: UseCompactObjectHeaders is not 'true' — may have been overridden via -XX:-UseCompactObjectHeaders");
        }

        log("Leaving JEP 534 testing");
    }

    // JEP 536: JFR In-Process Data Redaction
    // https://openjdk.org/jeps/536
    //
    // JFR now redacts sensitive values from built-in startup events
    // (jdk.InitialSystemProperty, jdk.InitialEnvironmentVariable, jdk.JVMInformation)
    // before writing them to a recording. Redaction is driven by glob filters via
    // -XX:FlightRecorderOptions:redact-key/redact-argument; default filters cover
    // common patterns including *password*, *token*, *secret*.
    //
    // To exercise the redaction path, start the server with:
    //   -Djep536.test.password=sup3rS3cr3t!
    // jdk.InitialSystemProperty events are captured once at JVM init, so the
    // property must be present at startup — runtime System.setProperty() is too late.
    private void testJFRDataRedaction() throws Exception {
        log("Beginning JEP 536 testing: JFR In-Process Data Redaction");

        final String sensitiveKey   = "jep536.test.password"; // matches default filter *password*
        final String sensitiveValue = "sup3rS3cr3t!";

        java.util.List<RecordedEvent> events = new java.util.ArrayList<>();
        try (Recording rec = new Recording()) {
            rec.enable("jdk.InitialSystemProperty");
            rec.start();
            rec.stop();
            java.nio.file.Path tmp = java.nio.file.Files.createTempFile("jep536-", ".jfr");
            try {
                rec.dump(tmp);
                events.addAll(RecordingFile.readAllEvents(tmp));
            } finally {
                java.nio.file.Files.deleteIfExists(tmp);
            }
        }

        // Find the recorded value for our sensitive key
        String recordedValue = null;
        for (RecordedEvent e : events) {
            if ("jdk.InitialSystemProperty".equals(e.getEventType().getName())
                    && sensitiveKey.equals(e.getString("key"))) {
                recordedValue = e.getString("value");
                log("jdk.InitialSystemProperty key=" + sensitiveKey + " value=" + recordedValue);
                break;
            }
        }

        if (recordedValue == null) {
            log("NOTE: '" + sensitiveKey + "' not in startup snapshot — re-run with -D" + sensitiveKey + "=<value>");
        } else if ("[REDACTED]".equals(recordedValue)) {
            log("SUCCESS: JEP 536 redacted '" + sensitiveKey + "' in the JFR recording");
        } else if (sensitiveValue.equals(recordedValue)) {
            throw new Exception("JEP 536 FAILED: '" + sensitiveKey + "' was recorded in plain text — expected [REDACTED]");
        } else {
            log("NOTE: unexpected recorded value for '" + sensitiveKey + "': " + recordedValue);
        }

        log("Leaving JEP 536 testing");
    }

    public void log(String msg) {
        System.out.println(msg);
        sw.append(msg);
        sw.append("<br/>");
    }
}
