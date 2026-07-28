# Repo for quick testing of Liberty apps
Especially useful for new Java versions


### Build locally

Requires a JDK 27 installation. From the root of the repository:

```
./gradlew io.openliberty.java.internal_fat_27:build
```

Gradle resolves the JDK 27 toolchain automatically via its toolchain support. If Gradle cannot find a local JDK 27, point it at one explicitly:

```
./gradlew io.openliberty.java.internal_fat_27:build -P"org.gradle.java.installations.fromEnv=JDK27"
```

where `JDK27` is an environment variable set to your JDK 27 home:

```
(Mac/Unix)  export JDK27="/path/to/jdk-27"
(Win DOS)   set JDK27="C:\path\to\jdk-27"
(Win PS)    $env:JDK27="C:\path\to\jdk-27"
```

### When moving to a new release of Java

Update the `languageVersion` in **build.gradle**, the `appUrl` in the `ext` block, and add new test coverage to **TestService.java**. Make sure **TestApp.java** is in the same directory.

---

## Java 27 JEP Coverage

This FAT covers only **finalized (non-preview, non-incubator)** JEPs that ship as standard Java API in Java 27.

---

### JEP 527 — Post-Quantum Hybrid Key Exchange for TLS 1.3
**Link:** https://openjdk.org/jeps/527

Java 27 adds three ML-KEM/ECDHE hybrid named groups to the TLS 1.3 stack: `X25519MLKEM768`, `SecP256r1MLKEM768`, and `SecP384r1MLKEM1024`. `X25519MLKEM768` is placed first in the JDK default preference list so existing code benefits automatically without any change.

**Test:** `testPostQuantumTLS()`
- Obtains `SSLParameters` from `SSLContext.getDefault().getDefaultSSLParameters()` (not `new SSLParameters()`, which returns `null` for `getNamedGroups()`).
- Asserts `X25519MLKEM768` is the first (most preferred) named group.
- Asserts all three hybrid groups are present in the supported set.

---

### JEP 534 — Compact Object Headers by Default
**Link:** https://openjdk.org/jeps/534

Java 27 makes compact object headers the default in HotSpot, reducing object header size from 96 bits (12 bytes) to 64 bits (8 bytes) on 64-bit architectures. Controlled by `-XX:+/-UseCompactObjectHeaders`.

**Test:** `testCompactObjectHeaders()`
- Reads the `UseCompactObjectHeaders` JVM flag via `HotSpotDiagnosticMXBean`.
- Logs `SUCCESS` if the flag is `true` (the Java 27 default).
- Logs a `NOTICE` (non-fatal) if the flag was explicitly disabled — e.g. Liberty sets `-XX:-UseCompactObjectHeaders` for compatibility.
- Skips gracefully on non-HotSpot JVMs where the MBean is absent.

---

### JEP 536 — JFR In-Process Data Redaction
**Link:** https://openjdk.org/jeps/536

JFR now redacts sensitive values from built-in startup events (`jdk.InitialSystemProperty`, `jdk.InitialEnvironmentVariable`, `jdk.JVMInformation`) before writing them to a recording. Redaction is controlled by glob filters via `-XX:FlightRecorderOptions:redact-key/redact-argument`; default filters cover common patterns including `*password*`, `*token*`, `*secret*`. This is a JVM-engine feature — there is no Java annotation API.

**Test:** `testJFRDataRedaction()`
- Starts a JFR recording with `jdk.InitialSystemProperty` enabled, then reads it back via `RecordingFile`.
- Looks for a property key (`jep536.test.password`) that matches the default `*password*` filter.
- Asserts the recorded value is `[REDACTED]`, not the original plaintext — fails hard if plaintext is found.
- Logs a skip notice if the property was not present at JVM startup (the snapshot is taken once at JVM init; runtime `System.setProperty()` is too late).

> **To exercise the redaction path**, add the following to `run/jvm.options`:
> ```
> -Djep536.test.password=sup3rS3cr3t!
> ```

---

## JEPs Excluded (Preview / Incubator)

| JEP | Title | Status |
|-----|-------|--------|
| 531 | Lazy Constants | Third Preview — excluded |
| 532 | Primitive Types in Patterns, instanceof, and switch | Fifth Preview — excluded |
| 533 | Structured Concurrency | Seventh Preview — excluded |
| 538 | PEM Encodings of Cryptographic Objects | Third Preview — excluded |
| 537 | Vector API | Twelfth Incubator — excluded |
| 523 | Make G1 the Default GC in All Environments | Final but JVM-internal; no testable API surface |

---

## JVM Options (`run/jvm.options`)

No special flags are required for JEP 527 or JEP 534. To fully exercise JEP 536 redaction, add:

```
-Djep536.test.password=sup3rS3cr3t!
```
