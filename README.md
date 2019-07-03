# documentRedactor
Simple app that takes image/pdf as input and redact some specific information as expressed by Regex

# Pre-requisites
- Java 12
- maven
- A good internet connection :)

- #####GCP Authentication
You need to have a json file containing GCP credentials in your system and have an environment variable 
named "GOOGLE_APPLICATION_CREDENTIALS" pointing to that file for google authentication to work.

For making this application work on heroku, I had to work around google security... so you need 2 more env 
variables setup "GCP_KEY_FILE" and "GCP_CRED"... Figure out the need of these variables yourself 

# useful commands
For application build and test execution

`mvn clean install`

For running the application

- Maven Spring boot starter plugin (Recommended... it supports remote debugging on port 5005)

`mvn spring-boot:run`
 
- CML

`sh start.sh`

OR

`mvn clean install && java -jar target/documentRedactor-*.jar`


PS : Always good to build right before spinning up the application to have the latest changes