final class Ram {

    private int ramSize;
    private int blockSize;
    private DataBlock[] data;

    Ram(int ramSize, int blockSize) {
        this.ramSize = ramSize;
        this.blockSize = blockSize;
        int numBlocks = (int) Math.ceil(ramSize / (double) blockSize);
        this.data = new DataBlock[numBlocks];

        for (int i = 0; i < numBlocks; i++) {
            data[i] = new DataBlock(blockSize, -1, new double[blockSize]);
        }
    }

    final DataBlock getBlock(Address addr) {
        return data[addr.getAddr() / blockSize];
    }

    final void setBlock(Address addr, DataBlock value) {
        data[addr.getAddr() / blockSize] = value;
    }

    final int getRamSize() {
        return ramSize;
    }

    final DataBlock[] getData() {
        return data;
    }
}
