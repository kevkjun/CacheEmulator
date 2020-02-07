public class CacheSim {

    private static final int sz = 8;       //8 bytes per Double

    //Default instructions for instantiating a CPU
    private static int c = 65_536;         //size of cache in bytes
    private static int b = 64;             //size of data block in bytes
    private static int n = 2;              //n-way associativity of cache
    private static String r = "LRU";       //replacement policy
    private static String a = "mxm_block"; //algorithm to simulate
    private static int d = 480;            //dimension of matrix or vector operation
    private static boolean p = false;      //enable printing of resulting "solution" matrix product or daxpy vector
    private static int f = 32;             //blocking factor for blocked matrix multiplication algo
    //End instructions

    //registers for computation
    private static double register0;
    private static double register1;
    private static double register2;
    private static double register3;
    private static double register4;

    //Cpu
    private static Cpu myCpu;

    ///Begin Methods///
    static void daxpy() {
        Address[] aArr = new Address[d];
        Address[] bArr = new Address[d];
        Address[] cArr = new Address[d];

        for (int i = 0; i < d; i++) {
            aArr[i] = new Address(i*sz, c/b, c/b/n, b);
            myCpu.storeDouble(aArr[i], i);
            //myCpu.getCache().getRam().getData()[aArr[i].getIndex()].getData()[aArr[i].getOffset()] = i;

            bArr[i] = new Address(d*sz + i*sz, c/b, c/b/n, b);
            myCpu.storeDouble(bArr[i], 2*i);
            //myCpu.getCache().getRam().getData()[bArr[i].getIndex()].getData()[bArr[i].getOffset()] = 2*i;

            cArr[i] = new Address(2*d*sz + i*sz, c/b, c/b/n, b);
            myCpu.storeDouble(cArr[i], 0);
            //myCpu.getCache().getRam().getData()[cArr[i].getIndex()].getData()[cArr[i].getOffset()] = 0;
        }
        //Store D
        register0 = 3;

        //Run the daxpy with D
        for (int i = 0; i < d; i++) {
            register1 = myCpu.loadDouble(aArr[i]);
            register2 = myCpu.multDouble(register0, register1);
            register3 = myCpu.loadDouble(bArr[i]);
            register4 = myCpu.addDouble(register2, register3);
            myCpu.storeDouble(cArr[i], register4);
        }
        if (p) {
            System.out.print("Result vector: {");
            for (int i = 0; i < d - 1; i++) {
                System.out.print(myCpu.loadDouble(cArr[i]) + ", ");
            }
            System.out.println(myCpu.loadDouble(cArr[d-1]) + "}");
            System.out.println();
        }
    }

    static void mxm() {
        Address[][] aMat = new Address[d][d];
        Address[][] bMat = new Address[d][d];
        Address[][] cMat = new Address[d][d];

        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                aMat[i][j] = new Address(sz*(i*d + j), c/b, c/b/n, b);
                myCpu.storeDouble(aMat[i][j], i*d + j);
                //myCpu.getCache().getRam().getData()[aMat[i][j].getIndex()].getData()[aMat[i][j].getOffset()] = i*d + j;

                bMat[i][j] = new Address(d*d*sz + sz*(i*d + j), c/b, c/b/n, b);
                myCpu.storeDouble(bMat[i][j], 2*(i*d + j));
                //myCpu.getCache().getRam().getData()[bMat[i][j].getIndex()].getData()[bMat[i][j].getOffset()] = 2*(i*d + j);

                cMat[i][j] = new Address(2*d*d*sz + sz*(i*d + j), c/b, c/b/n, b);
                myCpu.storeDouble(cMat[i][j], 0);
                //myCpu.getCache().getRam().getData()[cMat[i][j].getIndex()].getData()[cMat[i][j].getOffset()] = 0;
            }
        }
        //Init at 0
        register3 = 0;

        //Matrix multiplication
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                for (int k = 0; k < d; k++) {
                    register0 = myCpu.loadDouble(aMat[i][k]);
                    register1 = myCpu.loadDouble(bMat[k][j]);
                    register2 = myCpu.multDouble(register0, register1);
                    register3 = myCpu.addDouble(register2, register3);
                }
                myCpu.storeDouble(cMat[i][j], register3);
                register3 = 0;
            }
        }

        //Print the matrices
        if (p) {
            printMatrix(aMat, bMat, cMat);
        }
    }

    static void mxm_block(int f) {
        Address[][] aMat = new Address[d][d];
        Address[][] bMat = new Address[d][d];
        Address[][] cMat = new Address[d][d];

        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                aMat[i][j] = new Address(sz*(i*d + j), c/b, c/b/n, b);
                myCpu.storeDouble(aMat[i][j], i*d + j);
                //myCpu.getCache().getRam().getData()[aMat[i][j].getIndex()].getData()[aMat[i][j].getOffset()] = i*d + j;

                bMat[i][j] = new Address(d*d*sz + sz*(i*d + j), c/b, c/b/n, b);
                myCpu.storeDouble(bMat[i][j], 2*(i*d + j));
                //myCpu.getCache().getRam().getData()[bMat[i][j].getIndex()].getData()[bMat[i][j].getOffset()] = 2*(i*d + j);

                cMat[i][j] = new Address(2*d*d*sz + sz*(i*d + j), c/b, c/b/n, b);
                myCpu.storeDouble(cMat[i][j], 0);
                //myCpu.getCache().getRam().getData()[cMat[i][j].getIndex()].getData()[cMat[i][j].getOffset()] = 0;
            }
        }
        //Init at 0
        //register3 = 0;
//        //Matrix multiplication - Stanford paper - gives lower instruction count but greater miss rates
//        for (int kk = 0; kk < d; kk+=f) {
//            for (int jj = 0; jj < d; jj+=f) {
//                for (int i = 0; i < d; i++) {
//                    int min1 = Math.min(kk + f, d);
//                    for (int k = kk; k < min1; k++) {
//                        register0 = myCpu.loadDouble(aMat[i][k]);
//                        int min2 = Math.min(jj + f, d);
//                        for (int j = jj; j < min2; j++) {
//                            register1 = myCpu.loadDouble(bMat[k][j]);
//                            register2 = myCpu.multDouble(register0, register1);
//                            register3 = myCpu.addDouble(register2, register3);
//                        }
//                        myCpu.storeDouble(cMat[i][min2-1], register3);
//                        register3 = 0;
//                    }
//                }
//            }
//        }

        //Block matrix multiplication - pg 414 in P&H
        for (int sj = 0; sj < d; sj+=f) {
            for (int si = 0; si < d; si+=f) {
                for (int sk = 0; sk < d; sk += f) {
                    for (int i = si; i < si + f; ++i) {
                        for (int j = sj; j < sj + f; ++j) {
                            //init the sum register
                            register0 = myCpu.loadDouble(cMat[i][j]);
                            for (int k = sk; k < sk + f; k++) {
                                register1 = myCpu.loadDouble(aMat[i][k]);
                                register2 = myCpu.loadDouble(bMat[k][j]);
                                register3 = myCpu.multDouble(register1, register2);
                                register0 = myCpu.addDouble(register0, register3);
                            }
                            myCpu.storeDouble(cMat[i][j], register0);
                        }
                    }
                }
            }
        }

        //Print matrices
        if (p) {
            printMatrix(aMat, bMat, cMat);
        }
    }

    static void printMatrix(Address[][] aMat, Address[][] bMat, Address[][] cMat) {
        /*
        //Matrix A
        System.out.println("Input Matrix 1");
        for (int i = 0; i < d; i++) {
            System.out.print("|");
            for (int j = 0; j < d-1; j++) {
                System.out.print(String.format("%.0f", myCpu.loadDouble(aMat[i][j])) + ", ");
            }
            System.out.print(String.format("%.0f", myCpu.loadDouble(aMat[i][d-1])) + "|");
            System.out.println();
        }
        System.out.println();

        //Matrix B
        System.out.println("Input Matrix 2");
        for (int i = 0; i < d; i++) {
            System.out.print("|");
            for (int j = 0; j < d-1; j++) {
                System.out.print(String.format("%.0f", myCpu.loadDouble(bMat[i][j])) + ", ");
            }
            System.out.print(String.format("%.0f", myCpu.loadDouble(bMat[i][d-1])) + "|");
            System.out.println();
        }
        System.out.println();
        */
        //Matrix C
        System.out.println("Result Matrix");
        for (int i = 0; i < d; i++) {
            System.out.print("|");
            for (int j = 0; j < d-1; j++) {
                System.out.print(String.format("%.0f", myCpu.loadDouble(cMat[i][j])) + ", ");
            }
            System.out.print(String.format("%.0f", myCpu.loadDouble(cMat[i][d-1])) + "|");
            System.out.println();
        }
        System.out.println();
    }

    static void printInputs() {
        //Calculate RAM size
        //ramSize
        int ramSize = (int) (b * (Math.ceil(myCpu.getCache().getRam().getRamSize() / (double) b)));

        //Print outputs
        System.out.println("INPUTS================================================");
        System.out.println("Ram Size =                      " + ramSize + " bytes");
        System.out.println("Cache Size =                    " + c + " bytes");
        System.out.println("Block Size =                    " + b + " bytes");
        System.out.println("Total Blocks in Cache =         " + c/b);
        System.out.println("Associativity =                 " + n);
        System.out.println("Number of Sets =                " + c/b/n);
        System.out.println("Replacement Policy =            " + r.toUpperCase());
        System.out.println("Algorithm =                     " + a);
        if (a.equals("mxm_block")) System.out.println("MXM Blocking Factor =           " + f);
        System.out.println("Matrix or Vector dimension =    " + d);
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-c":
                    c = Integer.parseInt(args[++i]);
                    break;
                case "-b":
                    b = Integer.parseInt(args[++i]);
                    break;
                case "-n":
                    n = Integer.parseInt(args[++i]);
                    break;
                case "-r":
                    r = args[++i].toLowerCase();
                    if (!(r.equals("lru") || r.equals("random") || r.equals("fifo"))) System.err.println("Double check your replacement policy input.");
                    break;
                case "-a":
                    a = args[++i];
                    break;
                case "-d":
                    d = Integer.parseInt(args[++i]);
                    break;
                case "-p":
                    p = true;
                    break;
                case "-f":
                    f = Integer.parseInt(args[++i]);
                    break;
                default:
                    System.err.println("Double check your console inputs.");
                    return;
            }
        }

        //Mark start of the algorithm
        long start = System.currentTimeMillis();
        System.out.format("It is now: %tc%n", start);

        //run appropriate algorithm
        if (a.equals("daxpy")) {
            myCpu = new Cpu(c, b, n, r.toLowerCase(), d*24);
            printInputs();
            daxpy();
        }
        else if (a.equals("mxm")) {
            myCpu = new Cpu(c, b, n, r.toLowerCase(), d*d*24);
            printInputs();
            mxm();
        }
        else {
            myCpu = new Cpu(c, b, n, r.toLowerCase(), d*d*24);
            printInputs();
            mxm_block(f);
        }

        //Print results
        System.out.println("RESULTS==============================================");
        System.out.println("Instruction count:  " + String.format("%.0f", myCpu.getInstrCount()));
        System.out.println("Read hits:          " + String.format("%.0f", myCpu.getCache().getReadHitCount()));
        System.out.println("Read misses:        " + String.format("%.0f", myCpu.getCache().getReadMissCount()));
        System.out.println("Read miss rate:     " + String.format("%.5f", myCpu.getCache().getReadMissCount()/(myCpu.getCache().getReadHitCount() + myCpu.getCache().getReadMissCount()) * 100) + "%");
        System.out.println("Write hits:         " + String.format("%.0f", myCpu.getCache().getWriteHitCount()));
        System.out.println("Write misses:       " + String.format("%.0f", myCpu.getCache().getWriteMissCount()));
        System.out.println("Write miss rate:    " + String.format("%.5f", myCpu.getCache().getWriteMissCount()/(myCpu.getCache().getWriteHitCount() + myCpu.getCache().getWriteMissCount()) * 100) + "%");
        System.out.format("\nStart Date time: %tc%n", start);
        System.out.format("End Date time: %tc%n", System.currentTimeMillis());
    }
}




/*
https://suif.stanford.edu/papers/lam-asplos91.pdf - mxm_block algorithm explanation
 */

