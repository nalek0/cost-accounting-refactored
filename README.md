# Cost accounting

Small website to take into account the costs...

## Setup

[Here](./src/main/resources/database-config.json) you should provide your connection config 
to the database in the format:

```json
{
  "user": "...",
  "password": "...",
  "hostname": "...",
  "port": "...",
  "databaseName": "..."
}
```

_I am using postgresql_

## Documentation:

* [Application.kt](./src/main/kotlin/com/nalek0/Application.kt) - 
main application script
* [clearDatabase.kt](./src/main/kotlin/com/nalek0/clearDatabase.kt) - 
debug script to clear all databases
* [initDatabase.kt](./src/main/kotlin/com/nalek0/initDatabase.kt) - 
debug script to init all required for the application tables
