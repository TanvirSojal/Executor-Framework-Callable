import seriescalculaton.ArithmeticProgression;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public BigInteger computeArithmeticProgressionSequential(long n, long m, long d){
        BigInteger result = BigInteger.ZERO;
        for (long i = 0; i <= n; i++){
            BigInteger term = BigInteger.valueOf(m + i * d);
            //System.out.println(m + i*d);
            result = result.add(term);
        }
        return result;
    }
    public BigInteger computeArithmeticProgressionParallel(long n, long m, long d) throws ExecutionException, InterruptedException {
        BigInteger result = BigInteger.ZERO;

        int cores = Runtime.getRuntime().availableProcessors();

        long start = 0;
        long end = n;

        long step_size = end / cores;

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(cores);

        List<Future<BigInteger>> futureBigIntList = new ArrayList<>();

        for (int i = 0; i < cores; i++){
//            if (i == cores - 1)
//                System.out.println(i  + ": " + start + "   " + (end));
//            else
//                System.out.println(i  + ": " + start + "   " + (start + step_size-1));
            Future<BigInteger> future;
            if (i == cores - 1)
                future = fixedThreadPool.submit(new ArithmeticProgression(start, end, m, d));
            else
                future = fixedThreadPool.submit(new ArithmeticProgression(start, start+step_size-1, m, d));
            futureBigIntList.add(future);
            start = start + step_size;
        }

        fixedThreadPool.shutdown();

        for (Future<BigInteger> fb : futureBigIntList)
            result = result.add(fb.get());

        return result;
    }

    public Main() throws ExecutionException, InterruptedException {
        long n = 1000000000;
        long m = 99;
        long d = -99;
        long start = System.currentTimeMillis();
        BigInteger ansSequential = computeArithmeticProgressionSequential(n, m, d);
        long end = System.currentTimeMillis();

        double sequentialTime = (end - start) / 1000.0;

        start = System.currentTimeMillis();
        BigInteger ansParallel = computeArithmeticProgressionParallel(n, m, d);
        end = System.currentTimeMillis();

        double parallellTime = (end - start) / 1000.0;

        double speedup = (sequentialTime / parallellTime);

        System.out.println(sequentialTime);
        System.out.println(parallellTime);
        System.out.println(speedup);

        try (RandomAccessFile output = new RandomAccessFile("testruns.csv", "rw")){
            long length = output.length();
            output.seek(length);
            String out = String.format("%d,%d,%d,%.2f,%.2f,%.2fx\n", n, m, d, sequentialTime, parallellTime, speedup);
            output.writeBytes(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            new Main();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
