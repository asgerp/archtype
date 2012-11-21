# Archtype

Masters school project on how to annotate source code to make sure that architectural patterns/styles used to design a system is upheld in the actual implementation.

## Run it

compile the project and then call: 
	
	javac -processor com.apkc.archtype.processors.ComponentProcessor -cp ~/path/to/archtype-1.0.jar path/to/TestController.java path/to/TestView.java path/to/TestModel.java


## Requirements

maven

* checker framework
* alloy

install alloy locally:

	mvn install:install-file -DlocalRepositoryPath=deps -DcreateChecksum=true -Dpackaging=jar -Dfile=alloy4.2.jar -DgroupId=org.alloy -DartifactId=alloy -Dversion=4.2