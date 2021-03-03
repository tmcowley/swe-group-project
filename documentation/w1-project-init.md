### Sprint Cycle 1: Week 5 (Feb 1st to Feb 7th)
# Project Initialization

### Maven and Project Directory Structure
Generating the project structure
```
mvn archetype:generate -DgroupId=app -DartifactId=app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
```

Maven commands:<br>
```mvn compile``` - Compile the project<br>
```mvn test``` - Compile the project, run tests from ```/app/src/test```<br>
```mvn exec:java``` - Execute the project<br>

Directory overview:
...
