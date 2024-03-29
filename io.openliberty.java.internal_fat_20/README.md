# Repo for quick testing of Liberty apps
Especially useful for new Java versions

### For versions of Java that already have gradle support

To build the WAR file locally, from the root directory of the open-liberty-misc repository run:

```
./gradlew io.openliberty.java.internal_fat_20:build
```

To build+run locally, this currently requires some manual effort:
* Note, the **build.gradle** file is configured to look for a Liberty install under `/wlp`. You might need to change it to point it to your local test Liberty install instead.
* Copy the `jvm.options` file and the contents of the `server.env` file from the project's `run` directory and add them to your `/wlp/usr/servers/io.openliberty.java.internal_fat_20Server/server.env` file (or equivalent location).
* Change to the root directory of this local `io.openliberty.java.internal_fat_20` repository and run:

```
./gradlew io.openliberty.java.internal_fat_20:start
```

---

### For versions of Java that do not have gradle support yet
Make the following updates in the following files (example given here as if you were working with pre-gradle support for Java 20):

- In **build.gradle**, set:

```
   languageVersion = JavaLanguageVersion.of(20)
```

- And then set the environment variable JDK20 to your Java 20 JDK home, for example:

```
(Mac)      export JDK20="/path/to/your/jdk20/home"
(Unix)     JDK20="/path/to/your/jdk20/home"
(Win DOS)  set JDK20="C:\path\to\your\jdk20\home"
(Win PS)   $env:JDK20="C:\path\to\your\jdk20\home"
```

- To build the WAR file locally, from the root directory of the open-liberty-misc repository run:

```
./gradlew io.openliberty.java.internal_fat_20:build -P"org.gradle.java.installations.fromEnv=JDK20"
```
where **JDK20** is a system environment variable that reflects the location of your Java XX JDK you wish the source to be complied at (see last step).


To build+run locally, this currently requires some manual effort:
* Note, the **build.gradle** file is configured to look for a Liberty install under `/wlp`. You might need to change it to point it to your local test Liberty install instead.
* Copy the `jvm.options` file and the contents of the `server.env` file from the project's `run` directory and add them to your `/wlp/usr/servers/io.openliberty.java.internal_fat_20Server/server.env` file (or equivalent location).
* Change to the root directory of this local `io.openliberty.java.internal_fat_20` repository and run:

```
./gradlew io.openliberty.java.internal_fat_20:start -P"org.gradle.java.installations.fromEnv=JDK20"
```
where **JDK20** is a system environment variable that reflects the location of your Java XX JDK you wish the source to be complied at (see last step).


If testing with the `io.openliberty.java.internal_fat` FAT from OpenLiberty, copy the file gradle builds (in build/libs) `io.openliberty.java.internal_fat_20.war` to the `io.openliberty.java.internal_fat/publish/servers/java20-server/apps` directory.

---

### When moving to a new release of Java

Here are the locations where you might want to update up the Java release number to the current Java version

- **org.eclipse.wst.common.component** (in the `.settings` directory)

```
<wb-module deploy-name="io.openliberty.java.internal_fat_20">
		<property name="context-root" value="io.openliberty.java.internal_fat_20"/>
```

- **build.gradle**

```
    appUrl = 'http://localhost:9080/io.openliberty.java.internal_fat_20/'

    <Make sure to add any new dependencies or update existing ones to the proper levels>
```

- **gradle-wrapper.properties** (might as well bump up to the latest supported version of gradle)

```
	distributionUrl=https\://services.gradle.org/distributions/gradle-X.Y-bin.zip
```

Then add new code to **TestServices.java** either directly or via another class and make sure **TestApp.java** is in the same directory as it.
