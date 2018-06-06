# Reverser of String Encoders
Instructions with Eclipse
----------------
*Requirements*: Java SE >= 1.8

Z3 : You need to build z3 (https://github.com/Z3Prover/z3.git) for JAVA api and copy your com.microsoft.z3.jar, libz3.so and libz3java.so into lib folder

CVC4 : you need to build CVC4  (https://github.com/CVC4/CVC4.git) and copy the file CVC4 into lib folder.

## After finishing the preparation intorduced above, the easiest way to use the libraries and build them is to open them in Eclipse. You need to use a recent version of Eclipse (> Mars) otherwise you might see some problems.

##

## TestGENIC/src/test/z3factory/Z3FactoryUnitTest.java -> Run As  (on right click menu)-> Run Configuration > Environment tab -> new variable LD_LIBRARY_PATH with value ../GENIClib/lib -> Run
