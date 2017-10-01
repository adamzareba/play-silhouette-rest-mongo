set databaseName=play-silhouette-rest-mongo
set hostAddress="localhost"
set portNumber="27017"
set scriptsFolder=%CD%/scripts

mongo "mongodb://%hostAddress%:%portNumber%/%databaseName%" --eval "db.dropDatabase()"
mongo "mongodb://%hostAddress%:%portNumber%/%databaseName%" %scriptsFolder%/createSchema.js
mongoimport --db %databaseName% -h %hostAddress%:%portNumber% --collection user --file %scriptsFolder%/user.json --jsonArray
mongoimport --db %databaseName% -h %hostAddress%:%portNumber% --collection password --file %scriptsFolder%/password.json --jsonArray
@timeout 3