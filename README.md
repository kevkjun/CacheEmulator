# CacheEmulator
## Computer Architecture - Project 1 - README
## Kevin Jun February 3, 2020

### Files
The source code is in the /src directory. The driver class is CacheSim.java. 

### The Emulator
Written using Java 11.0.5.

**Running**
* Navigate into /src directory
* Compile: javac *java
* Run: java CacheSim **arguments**
  ** – Syntax for the arguments follows the request qrguments in the project description
* Output: Upon call to the driver, there will be console output of the inputs provided to the emulator. Results will also be
printed to console upon completion of the algorithm.

### Code Description

The emulator is implemented in CacheSim.java where a CPU object is created. Within the CPU class, a Cache object is instan- tiated and within the Cache a RAM object is instantiated.

The implementations of these classes extend the UML sketch provided in the project description.

The CacheSim class includes the implementation of the daxpy, mxm, and mxm block algorithms as well as methods to print out the input arguments and output to console.
