# Reverser of String Encoders
Instructions with Eclipse
----------------
*Requirements*: Java SE >= 1.8

Z3 : You need to build z3 (https://github.com/Z3Prover/z3.git) for JAVA api and copy your ``com.microsoft.z3.jar``, ``libz3.so`` and ``libz3java.so`` into lib folder

CVC4 : you need to build CVC4  (https://github.com/CVC4/CVC4.git) and copy the file ``CVC4`` into lib folder.

1. After finishing the preparation intorduced above, the easiest way to use the libraries and build them is to open them in Eclipse. You need to use a recent version of Eclipse (> Mars) otherwise you might see some problems.

2. In Eclipse, configure build path as follow: 
    add jar ``/GENIC/GENIClib/lib/com.microsoft.z3.jar``
    add class folder ``/GENIC/GENIClib/lib``
    add library JUnit 4 (if you want to run the unittest)

3. Run configuration: in the environment tab, set variable ``LD_LIBRARY_PATH`` to value ``../GENIClib/lib``
