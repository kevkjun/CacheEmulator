final class Cpu {

    private Cache cache;

    //Logging Information//
    private double instrCount;

    Cpu(int c, int b, int n, String r, int ramSize) {
        this.cache = new Cache(c, b, n, r, ramSize);
    }

    public double loadDouble(Address addr) {
        instrCount++;
        return cache.getDouble(addr);
    }

    public void storeDouble(Address addr, double value) {
        instrCount++;
        cache.setDouble(addr, value);
    }

    public double addDouble(double value1, double value2) {
        instrCount++;
        return value1 + value2;
    }

    public double multDouble(double value1, double value2) {
        instrCount++;
        return value1 * value2;
    }

    final Cache getCache() {
        return cache;
    }

    final double getInstrCount() {
        return instrCount;
    }
}
