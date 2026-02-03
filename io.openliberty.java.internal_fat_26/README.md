# Repo for quick testing of Liberty apps
Especially useful for new Java versions


### For versions of Java that already have gradle support
To build the WAR file locally, from the root directory of the open-liberty-misc repository run:

```
./gradlew io.openliberty.java.internal_fat_26:build
```


### For versions of Java that do not have gradle support yet
Make the following updates in the following files (example given here as if you were working with pre-gradle support for Java 26):

- In **build.gradle**, set:

```
   languageVersion = JavaLanguageVersion.of(26)
```

- And then set the environment variable JDK26 to your Java 26 JDK home, for example:

```
(Mac)      export JDK26="/path/to/your/jdk26/home"
(Unix)     JDK26="/path/to/your/jdk26/home"
(Win DOS)  set JDK26="C:\path\to\your\jdk26\home"
(Win PS)   $env:JDK26="C:\path\to\your\jdk26\home"
```

- To build the WAR file locally, from the root directory of the open-liberty-misc repository run:

```
./gradlew io.openliberty.java.internal_fat_26:build -P"org.gradle.java.installations.fromEnv=JDK26"
```
where **JDK26** is a system environment variable that reflects the location of your Java 26 JDK to use to compile the source (see last step).


### When moving to a new release of Java
- **build.gradle**

```
    appUrl = 'http://localhost:9080/io.openliberty.java.internal_fat_26/'

    <Make sure to add any new dependencies or update existing ones to the proper levels>
```

Then add new code to **TestServices.java** either directly or via another class and make sure **TestApp.java** is in the same directory.
