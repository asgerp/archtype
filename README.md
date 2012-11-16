# Archtype

Masters school project on how to annotate source code to make sure that architectural patterns/styles used to design a system is upheld in the actual implementation.

## Requirements

maven

* checker framework
* alloy

install alloy locally:

	mvn install:install-file -DlocalRepositoryPath=deps -DcreateChecksum=true -Dpackaging=jar -Dfile=alloy4.2.jar -DgroupId=org.alloy -DartifactId=alloy -Dversion=4.2