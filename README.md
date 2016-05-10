jVoid
=======

Tests are boring. Running tests is even more boring. No one has time and/or patience to run the tests.

Fear no more brave developer! JVoid is here to rescue you and your team from the dreaded world of waiting for tests to complete.

Using extremely intelligent hacks^C^C^C^C^C algorithms, JVoid keeps track of modifications to your codebase and makes sure that **only** the tests relevant for those modifications are executed. Basically it skips all the tests whose outcome would not be altered by your changes to the code.


# How To Use

To execute your tests with jVoid you only need to attach the jVoid agent to your JVM and, at a minimum, have a configuration file with the basePackage

Example `jvoid.config` file:
```
app.package=com.example.app
```

## Gradle

Example `build.gradle` file:

```
apply plugin: 'java'

repositories {
    mavenCentral()
}

configurations {
  testAgent
}

test.doFirst {
  jvmArgs "-javaagent:${configurations.testAgent.singleFile}"
}

dependencies {
    testCompile <JUnit/Spock/TestNG dependencies>  

    testAgent "com.jvoid:jvoid:1.0.0-SNAPSHOT"
}
```

## Maven

Example `pom.xml`:

```xml
...
	<plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>${maven-surefire-plugin.version}</version>
            <configuration>
                <argLine>-javaagent:${maven.dependency.jvoid.jvoid.jar.path}</argLine>
            </configuration>
        </plugin>
	</plugins>
...
```

## Other

java -javaagent:&lt;absolute path to jvoid-0.9.0.jar&gt;[:&lt;abolsute path to jvoid configuration file&gt;] &lt;other parameters&gt;

# Configuration

All of jVoid configurations are stored in a dedicated file. This file is loaded from any (or all!) the following locations. The configurations in one file will overwrite the previously configurations:
1. User home directory
2. Working directory
3. java agent parameter

## Configuration Reference

| Property | Optional | Default value | Description |
| ----- | ----- | ----- | ----- |
| basePackage | :x:  |             | The base package where all your application code resides |
| db.url| :white_check_mark: | jdbc:h2:./jvoiddb;<br>AUTO_RECONNECT=TRUE;<br>AUTO_SERVER=TRUE;<br>PAGE_SIZE=512;<br>CACHE_SIZE=131072;<br>CACHE_TYPE=SOFT_LRU;<br>QUERY_CACHE_SIZE=64;<br>mode=MySQL | The url to connect to the datbase |
| db.username | :white_check_mark: | jvoid | The database username |
| db.password | :white_check_mark: | jvoid | The database password|
| app.heuristic.excludeCglib | :white_check_mark: | true | Exclude CGLIB methods |
| app.heuristic.excludeJacoco | :white_check_mark: | true | Exclude Jacoco methods |
| app.heuristic.excludeGroovyCallSite | :white_check_mark: | true | Exclude Groovy CallSite implementations |
| app.includes | :white_check_mark: | | Regex to fine tune which methods will be used. Empty means everything is included. |
| app.excludes | :white_check_mark: | | Regex to fine tune which methods will be excluded from JVoid . Empty means nothing is exclude. |

# Supported Test Frameworks

The test frameworks currently supported by jVoid are:

* JUnit4
* Spock

TestNG support is planned for future releases.

# FAQs

## Is this safe? How am I sure that jVoid is not making me look bad when I break the build?

By design JVoid is conservative and, in doubt, will never skip a test. In our view it this cases it is better to wait a bit for the tests to complete than let a bug creep in.

## Can I connect jvoid to an external database server?

Yes! MySQL is also supported. Just add a MySQL connection url/username/password to your `jvoid.config` *and* a MySQL connector driver to the test classpath.

build.gradle

```
dependencies {
    testCompile "org.codehaus.groovy:groovy-all:2.4.1"
    testCompile "org.spockframework:spock-core:1.0-groovy-2.4"
    
    testCompile 'mysql:mysql-connector-java:5.1.38'
    testAgent "com.jvoid:jvoid:1.0.0-SNAPSHOT"
}
```

jvoid.config:

```
app.package=com.example.app
db.url=jdbc:mysql://localhost/jvoid
db.username=user
db.password=$ecr3t
```

## How do I restart the jVoid database to it's initial state?

At this moment you will need to delete the `jeniusdb.*` files in your working directory or delete the data from the external MySQL database server.

## Is jVoid compatible with JaCoCo?

Unfortunately, at the present time jVoid and JaCoCo are mutually exclusive. Theoretically, that also makes sense because jVoid allows you to skip useless tests executions that are though necessary to gather the data used by JaCoCo for the full coverage reports. A solution for now, would be to execute JaCoCo (and suppress jVoid) based on an environment variable that will be set only on your CI runtime. In that way, you'll always have a full-coverage report from your CI and super-fast development time as an awesome software ninja!

But no worries: full support of JaCoCo (and its integration) are planned for future releases.

## Does jVoid send any information to 3rd party entities?

No! jVoid is totally local (except if you connect to an external database server). Not even a single bit is sent anywhere :)

## Guys, I really need [*fill with any feature name and/or description*], otherwise JVoid is useless for me/my organization!

We are glad you spent a bit of your busy time looking at jVoid. Please tell us that using an issue in GitHub and/or suggest a change via a Pull Request that contains that impressive feature.

## I LOVE JVoid! How can I help?

Awww... Thanks! If you can code, create a Pull Request with that new amazing feature or squash a nasty bug. If you can't, we are sure that you have an awesome skill that is super useful! :)
