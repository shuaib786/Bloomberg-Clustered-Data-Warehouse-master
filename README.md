##Clustered Data Warehouse

Suppose you are part of a scrum team developing data warehouse for Bloomberg to analyze FX deals. One of customer stories is to import deals details from files into DB. The requested performance is to be able to import the file containing 100,000 records in less than 5 seconds.


##Request logic as following :

File format is CSV contains the following fields (Deal Unique Id, From Currency ISO Code "Ordering Currency", To Currency ISO Code, Deal timestamp, Deal Amount in ordering currency).
Validate row structure.(e.g: Missing fields, Type format..etc. We do not expect you to cover all possible cases but we'll look to how you'll implement validations)
Valid rows should be stored in table/document, with reference to source file name .
Invalid rows should be stored into another table/document, with reference to source file name.
The DB contains another table to maintain accumulative count of deals per Ordering Currency "Columns : Currency ISO Code, CountOfDeals ", so upon completion of importing process the system should increase count of deals per currency.
System should not import same file twice.
No rollback allowed, what every rows imported should be saved in DB.

##Technical Specs :

Access to DB should be through JPA.
For DB type, you can select between (MySql or MongoDB)
Provide a web interface for uploading files and inquire about results "using filename" following web applications 3 tier architecture. Spring Batch is not allowed.
Provide a web interface for inquire about results "using filename" .·
We expect you to generate sample data set to use it during development
We expect the system to display a summary report for each file after process is finished; summary may contain process duration, number of imported deals and number of invalid records.
We expect you to generate sample data set to use it during development. 

##Deliverables should be ready to work including :

Workable deployment including sample file.
Deployment steps including sample data of 100K records, contains all invalid/valid records that the system handles.
Maven or Gradle project is required for full source code.
Proper error/exception handling.
Proper Logging.
It is essential to follow TDD and include unit testing of your code and provide it as part of the assignment, noting that this will be one of the major assessment points, minimum code coverage should be 70%.
It is preferred to deliver via github or bitbucket repository.
Provide deployment as a Vagrant or Docker is a plus.

###1.Create table in MySQL

Copy the text in src/main/resources/sample/bloomberg_db.sql and run it to create a table in MySQL (in phpmyadmin or MySQLworkbench or mysql command line environment)

###2.Build package
```
$ mvn clean install -DskipTests=true
```
###3.Run SpringBootWebApplication

Run the main method as java applications in eclipse.

###Test it

Go to http://localhost:8099/ and test it.

#Steps to set up development environment in Eclipse

##After you clone this project from github, run
```
$ mvn clean install -DskipTests=true
```
Open Eclipse to import the project
Click top File -> Import -> Maven -> Existing Maven Projects (Choose the folder that you just downloded)
Right click your project, click Properties -> Java Build Path, remove all M2_REPO/***/*** jars (names started with M2_REPO), click OK.
Check if Maven dependencies are setup correctly. Right click your project, click Properties -> Java Build Path-> Libraries, and the Maven Dependencies include all your dependent jar files
Right click pom.xml, click Run as-> Maven clean, then Run as-> Maven Install
Right click your project, click Run as -> Run as SpringBoot Applciation/ Java Application (SpringBootWebApplication.java is the main class)


Development environment

Windows 10 64bit, STS 3.6.2.RELEASE