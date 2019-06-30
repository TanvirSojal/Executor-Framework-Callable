package seriescalculaton;

import java.math.BigInteger;
import java.util.concurrent.Callable;

public class ArithmeticProgression implements Callable<BigInteger> {

    private long start;
    private long end;
    private long m;
    private long d;

    public ArithmeticProgression(long start, long end, long m, long d) {
        this.start = start;
        this.end = end;
        this.m = m;
        this.d = d;
    }

    @Override
    public BigInteger call() throws Exception {
        BigInteger sum = BigInteger.ZERO;
        for (long i = start; i <= end; i++){
            BigInteger term = BigInteger.valueOf(m + i * d);
            //System.out.println(m + i*d);
            sum = sum.add(term);
        }
        return sum;
    }
}
