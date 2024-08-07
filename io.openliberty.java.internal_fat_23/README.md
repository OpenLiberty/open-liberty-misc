# Repo for quick testing of Liberty apps
Especially useful for new Java versions


### For versions of Java that already have gradle support
To build the WAR file locally, from the root directory of the open-liberty-misc repository run:

```
./gradlew io.openliberty.java.internal_fat_23:build
```


### For versions of Java that do not have gradle support yet
Make the following updates in the following files (example given here as if you were working with pre-gradle support for Java 23):

- In **build.gradle**, set:

```
   languageVersion = JavaLanguageVersion.of(23)
```

- And then set the environment variable JDK23 to your Java 23 JDK home, for example:

```
(Mac)      export JDK23="/path/to/your/jdk23/home"
(Unix)     JDK23="/path/to/your/jdk23/home"
(Win DOS)  set JDK23="C:\path\to\your\jdk23\home"
(Win PS)   $env:JDK23="C:\path\to\your\jdk23\home"
```

- To build the WAR file locally, from the root directory of the open-liberty-misc repository run:

```
./gradlew io.openliberty.java.internal_fat_23:build -P"org.gradle.java.installations.fromEnv=JDK23"
```
where **JDK23** is a system environment variable that reflects the location of your Java 23 JDK to use to compile the source (see last step).


### When moving to a new release of Java
- **build.gradle**

```
    appUrl = 'http://localhost:9080/io.openliberty.java.internal_fat_23/'

    <Make sure to add any new dependencies or update existing ones to the proper levels>
```

Then add new code to **TestServices.java** either directly or via another class and make sure **TestApp.java** is in the same directory.
