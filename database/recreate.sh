#!/bin/bash
function main {
local databaseName="play-silhouette-rest-mongo"
local hostAddress="localhost"
local portNumber="27017"
local scriptsFolder="./scripts"

mongo "mongodb://${hostAddress}:${portNumber}/${databaseName}" --eval "db.dropDatabase()"
mongo "mongodb://${hostAddress}:${portNumber}/${databaseName}" ${scriptsFolder}/createSchema.js
mongoimport --db ${databaseName} -h ${hostAddress}:${portNumber} --collection user --file ${scriptsFolder}/user.json --jsonArray
mongoimport --db ${databaseName} -h ${hostAddress}:${portNumber} --collection password --file ${scriptsFolder}/password.json --jsonArray
}
main
