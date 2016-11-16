Place all java files in a directory and compile using command

javac -cp mongodb-driver-3.3.0.jar *.java

(mongodb-driver-3.3.0.jar can be downloaded from https://oss.sonatype.org/content/repositories/releases/org/mongodb/mongodb-driver/3.3.0/)

To run the http server, use command

java -cp mongodb-driver-3.3.0.jar SaltsideHttpServer.java

To run test cases, use command

java -cp mongodb-driver-3.3.0.jar SaltsideTester.java