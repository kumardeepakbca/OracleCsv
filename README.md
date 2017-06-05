Required S/w:
jdk1.5.0_20
Apache Maven 2.2.1


IDE: Eclipse Indigo

Description:
Just go inside project folder up to pom.xml. after that run follwing command

mvn clean install

After that jar file will generate in target folder queryData.jar.
Run jar file using following command:

java -jar queryData.jar  C:\\properties\\database.properties
Query need to keep inside sql.properties file.
File type need to keep inside config.properties file.

Note:ojdbc5 jar file required for oracle db connectivity