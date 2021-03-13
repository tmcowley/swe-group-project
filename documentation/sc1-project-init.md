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
Our proposed sentiment analysis tool was the Java variant of [Vader](https://github.com/cjhutto/vaderSentiment). See the research paper from which Vader was created [Hutto, C.J. & Gilbert, E.E. (2014). VADER: A Parsimonious Rule-based Model for Sentiment Analysis of Social Media Text. Eighth International Conference on Weblogs and Social Media (ICWSM-14). Ann Arbor, MI, June 2014.](https://www.aaai.org/ocs/index.php/ICWSM/ICWSM14/paper/view/8109/8122).
It was discovered during sentiment analysis development that the Maven packaged version `1.0` of the analysis tool was out of date and contained bugs, making it unusable. To mitigate this we generated a `Jar` file from the most up to date source code (with `mvn package`), placed this in `/lib/`, and then imported the local file to Maven's `pom.xml`. This variant was chosen over an `mvn install:install-file` command due to its simplicity: no additional commands other than `mvn clean compile` and `mvn exec:java` are needed to launch the back-end.
```
<dependencies>
    <dependency>
        <groupId>com.vader.sentiment</groupId>
        <artifactId>vader-sentiment-analyzer</artifactId>
        <version>1.0</version>
        <scope>system</scope>
        <systemPath>${basedir}/lib/vader-sentiment-analyzer-1.0.jar</systemPath>
    </dependency>
...
</dependencies>
```
<br>

## The PostgreSQL Database 

The chosen relational database language is PostgreSQL, a client-server database model. Unlike embedded counterparts, a Postgres server instance has to be launched before the back-end is built and executed. 

For a common, cross-platform, method to launch a Postgres server instance, we made use of `Docker`, and its `docker-compose` command.

#### Start Postgres server with Docker (localhost port 5432):
```
cd app;
docker-compose -f docker-compose.yml up --remove-orphans
```
[Note: can be done with terminal, and powershell]<br>
[to close: `crtl+c` the initial window] <br>


#### Database interaction:
```
psql -h localhost -p 5432 --username postgres
[password is fas200]

\c cs261
```

We created the file `docker-compose.yml` to launch the server and specify environment settings:
```
version: '3.1'

services:

  db:
    image: postgres:13.2
    restart: always
    environment:
      POSTGRES_DB: cs261
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: fas200
    volumes:
        - ./database/:/docker-entrypoint-initdb.d/
    ports:
      - "5432:5432"
```

This creates a Postgres `v13.2` instance containing a database of name `cs261`, and a user username, password pair of `postgres` and `fas200`, respectively. The container's internal port `5432` is mapped to the external `localhost` port `5432`. The compose file also maps the `./database/` folder (and therefore its contents) to the internal docker entrypoint `/docker-entrypoint-initdb.d/`. This means that on restart, the database inserts each of the following, in order: `10-init.sql` (moves to the cs261 database), `20-schema.sql` (creates DB schema (tables, functions, etc.)), `30-test-data.sql` (runs testing data against the DB).

<!-- ### MacOS Guide:
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
``` -->