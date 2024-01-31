# Repo for quick testing of Liberty apps
Especially useful for new Java versions


### For versions of Java that already have gradle support
To build the WAR file locally, from the root directory of the open-liberty-misc repository run:

```
./gradlew io.openliberty.java.internal_fat_22:build
```


### For versions of Java that do not have gradle support yet
Make the following updates in the following files (example given here as if you were working with pre-gradle support for Java 22):

- In **build.gradle**, set:

```
   languageVersion = JavaLanguageVersion.of(22)
```

- And then set the environment variable JDK22 to your Java 22 JDK home, for example:

```
(Mac)      export JDK22="/path/to/your/jdk22/home"
(Unix)     JDK22="/path/to/your/jdk22/home"
(Win DOS)  set JDK22="C:\path\to\your\jdk22\home"
(Win PS)   $env:JDK22="C:\path\to\your\jdk22\home"
```

- To build the WAR file locally, from the root directory of the open-liberty-misc repository run:

```
./gradlew io.openliberty.java.internal_fat_22:build -P"org.gradle.java.installations.fromEnv=JDK22"
```
where **JDK22** is a system environment variable that reflects the location of your Java 22 JDK to use to compile the source (see last step).


### When moving to a new release of Java
- **build.gradle**

```
    appUrl = 'http://localhost:9080/io.openliberty.java.internal_fat_22/'

    <Make sure to add any new dependencies or update existing ones to the proper levels>
```

Then add new code to **TestServices.java** either directly or via another class and make sure **TestApp.java** is in the same directory.
