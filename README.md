# MSL Catalog Edge

This repository is a sub-repository of the [Kenzan Million Song Library](https://github.com/kenzanmedia/million-song-library) (MSL) project, a microservices-based Web application built using [AngularJS](https://angularjs.org/), a [Cassandra](http://cassandra.apache.org/) NoSQL database, and [Netflix OSS](http://netflix.github.io/) tools.

> **NOTE:** This service was chosen to test the pros and cons of an asynchronous netty approach, and it, therefore, differs from the other MSL edge services.

> **NOTE:** For an overview of the Million Song Library microservices architecture, as well as step-by-step instructions for running the MSL demonstration, see the [Million Song Library Project Documentation](https://github.com/kenzanmedia/million-song-library/tree/develop/docs).

## Overview

You can run build all of the MSL microservices by running `mvn clean compile` from the `server` directory of the main [million-song-library](https://github.com/kenzanmedia/million-song-library/tree/develop/server) repository. Or use the commands below to build, run, and test a single microservice.

> **NOTE:** If you receive an error when running any of the commands below, try using `sudo` (Mac and Linux) or run PowerShell as an administrator (Windows).

## Server Generation

Use the following command to generate server source code from the Swagger definition, run tests, and install dependencies:

```
mvn -P build clean generate-sources install
```

Use the following command to run the server on port `9003`:

```
mvn -P dev clean jetty:run
```

To verify that the server is running, first [start Cassandra](https://github.com/kenzanmedia/million-song-library/tree/develop/tools/cassandra), and then access the following URL:

http://localhost:9003/catalog-edge/browse/album

## Code Formatting

If you make changes to the application code, use the following command to format the code according to [project styles and standards](https://github.com/kenzanmedia/styleguide):

```
mvn clean formatter:format
```

## Dependencies

Use the following command to install dependencies without running tests:

```
mvn -P no-tests clean install
```

## JAR Packaging

Use the following command to package the application code:

```
mvn -P no-tests package
```

There are some archaius properties that need to be provided when running the jar files, see example: 

```
java -jar msl-catalog-edge.jar
-Darchaius.deployment.environment=dev \
-Darchaius.configurationSource.additionalUrls=file://${PWD}/../msl-catalog-edge-config/edge-config.properties,file://${PWD}/../msl-catalog-data-client-config/data-client-config.properties
```

## Testing and Reports

You can use either Cobertura or EclEmma to run tests, whichever you prefer.

### Cobertura

Use the following command to run all unit tests and generate a report on test coverage (report is located in `/target/site/cobertura/index.html`):

```
mvn cobertura:cobertura
```

### EclEmma 

Use the following command to run all unit tests and generate a report on test coverage (report is located in `/target/site/jacoco`):

```
mvn package
```

 ## LICENSE
Copyright 2017 Kenzan, LLC <http://kenzan.com>
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.