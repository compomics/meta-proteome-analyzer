package de.mpa.util;

public class MemoryTest {

    /**
     * @param args
     */
    public static void getMemoryUsage() {
        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): "
                + Runtime.getRuntime().availableProcessors());
        /*
         * Total amount of free memory available to the JVM
         */
        long freeMemory = Runtime.getRuntime().freeMemory();
        System.out.println("Free memory (bytes): "
                + freeMemory);
        /*
         * This will return Long.MAX_VALUE if there is no preset limit
         */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /*
         * Maximum amount of memory the JVM will attempt to use
         */
        System.out.println("Maximum memory (bytes): "
                + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));
        /*
         * Total memory currently in use by the JVM
         */
        System.out.println("Total memory (bytes): "
                + Runtime.getRuntime().totalMemory());
        

        
        System.out.println("Testing for Collection inside Loop...");
        
        System.out.println("Occupied At the End: "+ (freeMemory - Runtime.getRuntime().freeMemory()));
        System.out.println("End of Test");
    }

}
