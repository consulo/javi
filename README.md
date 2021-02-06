# Javi - javac fork

This compiler allow compile java sources with almost all language features what last java provide, like:
 * records
 * if pattern matching
 * text blocks


# How to use

## Maven

pom.xml

```xml
    <repositories>
        <repository>
            <id>consulo</id>
            <url>https://maven.consulo.io/repository/snapshots/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>16</source> <!-- just max version - for correct ide set-->
                    <target>11</target> <!--min supported version is 11-->
                    <compilerId>javi</compilerId> <!--set correct compilerId -->

                    <!--others javac arguments-->
                    <showDeprecation>true</showDeprecation>
                    <showWarnings>true</showWarnings>
                    <encoding>UTF-8</encoding>
                </configuration>
                <!--required dependency-->
                <dependencies>
                    <dependency>
                        <groupId>consulo.internal</groupId>
                        <artifactId>plexus-compiler-javi</artifactId>
                        <version>16.0.3-SNAPSHOT</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```