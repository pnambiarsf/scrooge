SFDC Specific README
--------------------

## Build
You will need to [install](http://www.scala-sbt.org/release/tutorial/Installing-sbt-on-Mac.html) sbt.

### How to publish to local maven repository

`git clone https://github.com/pnambiarsf/scrooge`

`cd scrooge`

`sbt ++2.11.7  publish-m2`
### How to publish to nexus

Publish to local repo as explained above first. This is to build the jars. We only want to publish scrooge-generator_2.11 artifact to nexus. You can do this as follows:

`sbt ++2.11.7  scrooge-generator/*:publish`
