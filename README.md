# CacheEmulator
## Computer Architecture - Project 1 - README
## Kevin Jun 
## February 3, 2020

### Files
The source code is in the /src directory. The driver class is CacheSim.java. 

### The Emulator
Written using Java 11.0.5.

**Running**
* Navigate into /src directory
* Compile: ```javac *java```
* Run: ```java CacheSim **arguments**```
    * Syntax for the arguments follows the request arguments in the project description
        * ```-c``` : size of the cache in bytes (default: 65,536)
        * ```-b``` : size of a data block in bytes (default: 64)
        * ```-n``` : n-way associativity (default: 2)
        * ```-r``` : replacement policty from LRU, FIFO, random (default: LRU)
        * ```-a``` : selected algorithm from daxpy, mxm, mxm_block (default: mxm_block)
        * ```-d``` : dimensions of array or matries (default: 480)
        * ```-p``` : flag to print to console
        * ```-f``` : blocking factor for mxm_block (default: 32)
* Considerations for arguments:
      * _c_, _b_, _n_, and _f_ should be powers of 2 - similar to the architecture of modern computers
      * _f_ must divide into _d_ perfectly (i.e. _d mod f = 0_)
      * _c_ should be much larger than _b_ (i.e. there should be a sizable number of blocks in a cache)
* Output: Upon call to the driver, there will be console output of the inputs provided to the emulator. Results will also be
printed to console upon completion of the algorithm.
 * The RAM size that gets printed out is the bare minimum RAM needed to store the outputs calculated rounded up to the nearest multiple of the data block size in bytes (default: 64)

### Code Description

The emulator is implemented in CacheSim.java where a CPU object is created. Within the CPU class, a Cache object is instan- tiated and within the Cache a RAM object is instantiated.

The implementations of these classes extend the UML sketch provided in the project description.

The CacheSim class includes the implementation of the daxpy, mxm, and mxm block algorithms as well as methods to print out the input arguments and output to console.
