# ResModus: A *Live Event Feedback* System
An application allowing for real-time event feedback

## Context
* CS261 (Software Engineering) Group Project: Group 32
* See the project specification [here](https://warwick.ac.uk/fac/sci/dcs/teaching/material/cs261/)
* Authors: George Denny, Alexander Price, Moustafa Eladawy, Yang Tang, Thomas Cowley (@tmcowley)
* Project Tutor: Mahshid Mehr Nezhad
* Date: January to March, 2021

## Solution Overview
Web-based client, native Android application, native iOS application

### Front-end
Web: [React](https://reactjs.org/)<br>
Mobile: [React Native](https://reactnative.dev/)

### Back-end
[Spark Java](https://sparkjava.com/)<br>
Database: [PostgreSQL v13.2](https://www.postgresql.org/)

## Set-up and Initialization

### Database
```
pg_ctl -D /usr/local/var/postgres start && brew services start postgresql
```

```
psql postgres
```

### Start-up

Compile:
```
cd app; mvn compile 
```

Execute:
```
mvn exec:java 
```

Render Website: 
```
http://localhost:4567/
```


## Demonstration
