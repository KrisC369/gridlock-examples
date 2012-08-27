# GridLock examples

Some examples on how to use the GridLock simulation framework.  This example shows how to create a custom network loader capable of reading in a network defined in CSV files.

It also contains code to generate a list of vehicles based on flows and assign the route they have to follow based on route fractions.

If you want to execute the examples on the commandline, copy/paste the appropriate lines. You may need to use mvn instead of mvn3 for Maven-execution.

##GeneratorExample
mvn3 -Dexec.classpathScope=runtime "-Dexec.args=-classpath %classpath be.kuleuven.cs.gridlock.examples.csvgenerator.GeneratorExample" -Dexec.executable=/usr/lib/jvm/default-java/bin/java process-classes org.codehaus.mojo:exec-maven-plugin:1.2:exec


##MatlabExample
mvn3 -Dexec.classpathScope=runtime "-Dexec.args=-classpath %classpath be.kuleuven.cs.gridlock.examples.MatLabExample" -Dexec.executable=/usr/lib/jvm/default-java/bin/java process-classes org.codehaus.mojo:exec-maven-plugin:1.2:exec
