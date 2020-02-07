final class Address {

    private int addr;
    private int tag;
    private int index;
    private int offset;

    private static int binlog(int bits) { //returns 0 for 0 bits
        int log = 0;
        if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
        if( bits >= 256 ) { bits >>>= 8; log += 8; }
        if( bits >= 16  ) { bits >>>= 4; log += 4; }
        if( bits >= 4   ) { bits >>>= 2; log += 2; }
        return log + ( bits >>> 1 );
    }

    Address(int addr, int numBlocks, int numSets, int blockSize) { //blockSize is in bytes/block
        this.addr = addr;
        int logBlockSize = binlog(blockSize);
        this.offset = addr & ((1 << logBlockSize) - 1);

        int logSet = binlog(numSets);
        this.index = (addr >> logBlockSize) & ((1 << logSet) - 1);

        this.tag = (addr >> logSet + logBlockSize) & ((1 << (64L - logSet - logBlockSize)) - 1);
    }

    final int getTag() {
        return tag;
    }

    final int getIndex() {
        return index;
    }

    final int getOffset() {
        return offset;
    }

    final int getAddr() {
        return addr;
    }

//    public static void main(String[] args) {
//        int d = 480;
//        int sz = 8;
//        int i = 479;
//        int j = 479;
//
//        int d1 = 2*d*d*sz + sz*(i*d + j);
//        long d2 = 2*d*d*sz + sz*(i*d + j);
//        System.out.println(d1);
//        System.out.println(d2);
//
//        int tag = (d1 >> 10 + 8) & ((1 << (64L - 10 - 8)) - 1);
//        long tag2 = (d1 >> 10L + 8L) & ((1L << (64L - 10L - 8L)) - 1L);
//        System.out.println(tag);
//        System.out.println(tag2);
//    }
}

/*
https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
https://cboard.cprogramming.com/c-programming/156061-cache-simulator-bit-shifting.html
 */