#### Sprint Cycle 1: Term 2, Week 5 (Feb 1st to Feb 7th)
# Project Initialization

## Maven and Project Directory Structure
### Generating the project structure
```
mvn archetype:generate -DgroupId=app -DartifactId=app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
```

### Maven commands:<br>
```mvn compile``` - Compile the project<br>
```mvn test``` - Compile the project, run tests from ```/app/src/test```<br>
```mvn exec:java``` - Execute the project<br>

<br>

### Project directory overview and description:
```
.
├── app
│   ├── resources
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── app
│       │   │       ├── controllers
│       │   │       ├── objects
│       │   │       └── util
│       │   └── resources
│       │       ├── static
│       │       │   ├── css
│       │       │   └── images
│       │       └── velocity
│       └── test
│           └── java
│               └── app
├── deliverables
│   ├── 1_requirements-analysis
│   └── 2_design-and-planning
│       └── assets
└── documentation
```

>```./app``` - contains development code<br>
>
>> ```/resources``` - back-end resources, such as the word-list<br>
>>
>> ```/src/main``` - main source files<br>
>>
>>> ```/java/app``` - main source files<br>
>>>
>>>> ```/controllers``` - APIs and Auth<br>
>>>> ```/objects``` - Models for events, feedback, etc.<br>
>>>> ```/util``` - utility methods for front-end<br>
>>>>
>>> ```/resources``` - front-end resources<br>
>>>
>> ```/src/test/java/app``` - test source files<br>
>>
>```./deliverables``` - contains group project deliverables<br>
>
>```./documentation``` - documentation on dev and project sprint cycles<br>
>

<br>

### Adding the Dependencies to Maven (via pom.xml)

#### The Spark Java: core and velocity-template dependencies
```
<dependencies>
    <dependency>
        <groupId>com.sparkjava</groupId>
        <artifactId>spark-core</artifactId>
        <version>2.9.3</version>
    </dependency>

    <dependency>
        <groupId>com.sparkjava</groupId>
        <artifactId>spark-template-velocity</artifactId>
        <version>2.7.1</version>
    </dependency>
...
</dependencies>
```

#### The Sentiment Analyser (Vader) dependency
```
<dependencies>
    <dependency>
      <groupId>com.github.apanimesh061</groupId>
      <artifactId>vader-sentiment-analyzer</artifactId>
      <version>1.0</version>
    </dependency>
...
</dependencies>
```
<br>

## Database 

The chosen relational database language is PostgreSQL, a client-server database model. Unlike embedded counterparts, a PSQL server instance has to be launched before the back-end is built. 

### MacOS Guide:
Start PostgreSQL server:
```
pg_ctl -D /usr/local/var/postgres start && brew services start postgresql
```

Interact with database:
```
psql postgres 
\c database
```

Stop PostgreSQL server:
```
pg_ctl -D /usr/local/var/postgres stop && brew services stop postgresql
```

### Linux Guide:
```
```

### Windows Guide:
```
```