# jzmq-api

[![Build Status](https://travis-ci.org/zeromq/jzmq-api.png)](https://travis-ci.org/zeromq/jzmq-api)

A Java ØMQ API for abstracting the various implementations of ZeroMQ Message Transport Protocol.

## Warning

The API is still in it's infancy and will be subject to much change.

## Installation

Before you can begin using this library, you need to have the zmq and jzmq
shared libraries in either: `/usr/lib`, `/usr/local/lib`.

## Getting Started

The latest [javadocs](http://zeromq.github.com/jzmq-api/javadocs/).

Be sure to read the [wiki](https://github.com/zeromq/jzmq-api/wiki).

### Ubuntu 12.04 Precise

This is currently how I go about setting up my environment.

```bash
# This installs v3.2.2 of libzmq 
sudo add-apt-repository ppa:chris-lea/zeromq
sudo apt-get update
sudo apt-get install libzmq-dev libpgm-dev

cd /tmp
wget https://raw.github.com/zeromq/jzmq-api/master/debs/jzmq_2.1.0-1_amd64.deb
sudo dpkg -i jzmq_2.1.0-1_amd64.deb
```
Add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>org.zeromq</groupId>
    <artifactId>jzmq-api</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>            
```
```xml
<repositories>
  <repository>
    <id>sonatype-nexus-snapshots</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  </repository>
</repositories>
```
## Eclipse

```bash
mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true
```
## Usage

## Contribution Process

This project uses the [C4 process](http://rfc.zeromq.org/spec:16) for all code changes.

## License

Copyright © 2013 Trevor Bernard

Copyright other contributors as noted in the AUTHORS file.

This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
