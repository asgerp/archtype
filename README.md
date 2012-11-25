# Archtype

Masters project on how to annotate source code to make sure that architectural patterns/style,s used to design a system, is upheld in the actual implementation.

## Run it

compile the project and then call: 
	
	javac -processor com.apkc.archtype.processors.ComponentProcessor -cp ~/path/to/archtype-1.0.jar path/to/TestController.java path/to/TestView.java path/to/TestModel.java


## Requirements

all extern jars should be placed in lib/ folder.

* alloy
