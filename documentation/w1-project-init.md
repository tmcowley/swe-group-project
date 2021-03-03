#### Sprint Cycle 1: Week 5 (Feb 1st to Feb 7th)
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

### Project directory overview:
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

