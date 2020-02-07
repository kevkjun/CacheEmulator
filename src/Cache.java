import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Random;

final class Cache {

    private int numSets;
    private int nWay;         //associativity - default: 2
    private String repPolicy; //replacement policy of cache - default: LRU

    private ArrayDeque<DataBlock>[] blocks;

    private Ram ram;

    //Logging Information//
    private double readHitCount = 0;
    private double readMissCount = 0;
    private double writeHitCount = 0;
    private double writeMissCount = 0;

    Cache(int cacheSize, int blockSize, int nWay, String repPolicy, int ramSize) {
        this.nWay = nWay;
        this.repPolicy = repPolicy;
        int numBlocks = cacheSize / blockSize;
        this.numSets = numBlocks / nWay;

        this.ram = new Ram(ramSize, blockSize);

        blocks = new ArrayDeque[numSets];

        for (int i = 0; i < numSets; i++) {
            blocks[i] = new ArrayDeque<>(nWay);
        }
    }

    public double getDouble(Address addr) {
        int setID = addr.getIndex() % numSets;
        ArrayDeque<DataBlock> deq = blocks[setID];

        //There is no data in the Set yet
        if (deq.size() == 0) {
            readMissCount++;
            DataBlock ret;
            ret = getBlock(addr);
            ret.setTag(addr.getTag());
            deq.offerLast(ret);
            return ret.getData()[addr.getOffset()];

        //There is data
        } else {
            //Read Hit Scenario
            for (DataBlock db : deq) {
                if (db.getTag() == addr.getTag()) {
                    readHitCount++;
                    if (repPolicy.equals("lru") && deq.peekFirst() != db) {
                        deq.removeIf(e -> e == db);
                        deq.offerFirst(db);
                    }
                    return db.getData()[addr.getOffset()];
                }
            }

            //Read Miss Scenario
            readMissCount++;
            DataBlock ret;
            ret = getBlock(addr);
            ret.setTag(addr.getTag());

            //if deq is not full
            if (deq.size() != nWay) {
                if (repPolicy.equals("lru")) deq.offerFirst(ret);
                else deq.offerLast(ret);

            //if deq is full
            } else {
                if (repPolicy.equals("lru")) {
                    deq.pollLast();
                    deq.offerFirst(ret);
                } else if (repPolicy.equals("random")) {
                    int randInt = new Random().nextInt(nWay-1);
                    Iterator<DataBlock> it = deq.iterator();
                    DataBlock rem;
                    for (int i = 0; i < randInt; i++) {
                        it.next();
                    }
                    rem = it.next();
                    deq.removeIf(e -> e == rem);
                    deq.offerLast(ret);
                } else { //FIFO
                    deq.pollFirst();
                    deq.offerLast(ret);
                }
            }
            return ret.getData()[addr.getOffset()];
        }
    }

    public void setDouble(Address addr, double value) {
        int setID = addr.getIndex() % numSets;
        ArrayDeque<DataBlock> deq = blocks[setID];

        //There is no data in the Set yet
        if (deq == null) {
            writeMissCount++;
            //readMissCount++;

            DataBlock ret;
            ret = getBlock(addr);
            ret.setTag(addr.getTag());

            //Write Allocate - write to memory then load the DataBlock
            ret.getData()[addr.getOffset()] = value;
            deq.offerLast(ret);
        }

        //If there is data in the Set
        else {
            //Write Hit Scenario
            for (DataBlock db : deq) {
                if (db.getTag() == addr.getTag()) {
                    writeHitCount++;
                    if (repPolicy.equals("lru") && deq.peekFirst() != db) {
                        //Move the accessed DataBlock to the front
                        deq.removeIf(e -> e == db);
                        deq.offerFirst(db);
                    }
                    //Write Through
                    db.getData()[addr.getOffset()] = value;
                    setBlock(addr, db);
                    return;
                }
            }

            //Write Miss Scenario
            writeMissCount++;
            //readMissCount++;
            DataBlock ret;
            ret = getBlock(addr);
            ret.setTag(addr.getTag());

            //Write to memory
            ret.getData()[addr.getOffset()] = value;

            //if deq is not full
            if (deq.size() != nWay && deq.size() > 0) {
                if (repPolicy.equals("lru")) deq.offerFirst(ret);
                else deq.offerLast(ret);

            //if deq is full
            } else {
                if (repPolicy.equals("lru")) {
                    deq.pollLast();
                    deq.offerFirst(ret);
                } else if (repPolicy.equals("random")) {
                    int randInt = new Random().nextInt(nWay);

                    Iterator it = deq.iterator();
                    for (int i = 0; i < randInt - 1; i++) {
                        it.next();
                    }
                    deq.removeIf(e -> e == it.next());
                    deq.offerLast(ret);
                } else { //FIFO
                    deq.pollFirst();
                    deq.offerLast(ret);
                }
            }
        }
    }

    final DataBlock getBlock(Address addr) {
        return ram.getBlock(addr);
    }

    final void setBlock(Address addr, DataBlock block) {
        ram.setBlock(addr, block);
    }

    final Ram getRam() {
        return ram;
    }

    final double getReadHitCount() {
        return readHitCount;
    }

    final double getReadMissCount() {
        return readMissCount;
    }

    final double getWriteHitCount() {
        return writeHitCount;
    }

    final double getWriteMissCount() {
        return writeMissCount;
    }
}
