final class DataBlock {

    private int size;
    private int tag;
    private double[] data;

    DataBlock(int size, int tag, double[] data) {
        this.size = size;
        this.tag = tag;
        this.data = data;
    }

    final int getTag() {
        return tag;
    }

    final double[] getData() {
        return data;
    }

    final void setTag(int tag) {this.tag = tag;}

}
