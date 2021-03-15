# ResModus: A *Live Event-Feedback* System
An application allowing for real-time event feedback

## Context
* CS261 (Software Engineering) Group Project: Group 32
* See the project specification [here](https://warwick.ac.uk/fac/sci/dcs/teaching/material/cs261/)
* Authors: George Denny, Alexander Price, Moustafa Eladawy, Yang Tang, Thomas Cowley (@tmcowley)
* Project Tutor: Mahshid Mehr Nezhad
* Date: January to March, 2021

## Solution Overview
Web-based client, accessible by mobile devices

### Technologies
<!-- Web Front-end (old): [React](https://reactjs.org/)<br> -->
<!-- Mobile Front-end (old): [React Native](https://reactnative.dev/)<br> -->
Web Front-end: [HTML5 with Apache Velocity]()<br>
Back-end: [Spark Java](https://sparkjava.com/)<br>
Database: [PostgreSQL v13.2](https://www.postgresql.org/)

## Set-up and Initialization

<br>

### Database

#### Start Postgres server with Docker (localhost port 5432):
```
cd app;
docker-compose -f docker-compose.yml up --remove-orphans
```
[Note: can be done with terminal, and powershell]<br>
[to close: `crtl+c` the initial window] <br>
[to update following schema changes, run `docker rm app_db_1`] <br>


#### Database interaction:
```
psql -h localhost -p 5432 --username postgres
[password is fas200]

\c cs261
```

<!--
Start PostgreSQL server (Mac):
```
pg_ctl -D /usr/local/var/postgres start && brew services start postgresql
```

Interact with database (Mac):
```
psql postgres 
\c database
```

Stop PostgreSQL server (Mac):
```
pg_ctl -D /usr/local/var/postgres stop && brew services stop postgresql
```
-->

<br>

### Start-up

Launch the web-app (after DB launch):
```
cd app; 
mvn clean;
mvn compile;
mvn exec:java; 
```

Run tests: `mvn test`

Render website: visit [`http://localhost:4567/`](http://localhost:4567/)

## Demonstration
