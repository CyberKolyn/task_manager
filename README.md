
------------------------------------------------------------
Gradle 8.14.4
------------------------------------------------------------
Kotlin:        2.0.21
Groovy:        3.0.25
Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
Launcher JVM:  21.0.2 (Oracle Corporation 21.0.2+13-58)


------------------------------------------------------------
Database
------------------------------------------------------------
Postgres: 16.6
prod database: task_db
test database: task_db_test

## env 
DB_URL:      URL connection database
DB_USER:     Database username
DB_PASSWORD: Database username password

# Build
./gradlew build

# bootRun
./gradlew bootRun
