SFDC Specific README
--------------------

## Build
You will need to [install](http://www.scala-sbt.org/release/tutorial/Installing-sbt-on-Mac.html) sbt.

### How to publish to local maven repository

`git clone https://github.com/pnambiarsf/scrooge`
`cd scrooge`
`git fetch`
`git checkout master`
`sbt ++2.11.7  publish-m2`

### How to clean and build

`sbt ++2.11.7  clean clean-files compile publish-m2`

### How to publish to nexus

Publish to local repo as explained above first. This is to build the jars. We only want to publish scrooge-generator_2.11 artifact to nexus. You can do this as follows:

* Create a nexus.properties file under the home directory (~/nexus.properties) with the following content

```properties
realm=Sonatype Nexus Repository Manager
host=nexus.soma.salesforce.com
user=<replace_with_nexus_username>
password=<replace_with_nexus_password>
```

#### Publish as follows:
* Edit vi project/Build.scala with new sfdc classifier (For example, if sfdc-1.2, then change to sfdc-1.3).
  Line 198 (`previous.copy('classifier' = Some("sfdc-1.1"))`

`sbt ++2.11.7 scrooge-generator/*:publish`

* If above command fails, you will have to upload jar manually:

`sbt ++2.11.7  clean clean-files package`

`mvn deploy:deploy-file -DgroupId=com.twitter -DartifactId=scrooge-generator_2.11 -Dversion=4.1.0 -Dclassifier=${CLASSIFIER} -Dpackaging=jar -DrepositoryId=nexus -Durl=https://nexus.soma.salesforce.com/nexus/content/repositories/thirdparty -Dfile=/Users/pnambiar/code/scrooge/scrooge-generator/target/scala-2.11/scrooge-generator_2.11-4.1.0-${CLASSIFIER}.jar -DgeneratePom=false` where ${CLASSIFIER} is replaced by sfdc-1.X that was changed in project/Build.scala.

* The above command fail as well and still publish the jar correctly. Make sure to always check nexus that the jar was published:

https://nexus.soma.salesforce.com/nexus/content/groups/public/com/twitter/scrooge-generator_2.11/4.1.0/
