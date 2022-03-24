import com.yakovenko.lab1.CounterType;
import com.yakovenko.lab1.LabMapper;
import com.yakovenko.lab1.LabReducer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for counters
 */
public class CountersTest {

    private MapDriver<LongWritable, Text, Text, IntWritable> mapDriver;
    private ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;

    private final String testMalformedLog = "mama mila ramu";
    private final String testLog = "50,80,16880011,1000";

    private final String testMalformedName = "mama mila ramu";
    private final String sectorName = "SECTOR_0";
    private final String sectorName2 = "SECTOR_10";

    private List<IntWritable> values;

    /**
     * Init test case
     */
    @Before
    public void setUp() {
        values = new ArrayList<IntWritable>();
        values.add(new IntWritable(7));
        values.add(new IntWritable(5));
        URI[] cacheFiles = new URI[2];
        cacheFiles[0] = new Path("SECTORS").toUri();
        cacheFiles[1] = new Path("TEMPERATURE").toUri();
        LabMapper mapper = new LabMapper();
        LabReducer reducer = new LabReducer();
        mapDriver = MapDriver.newMapDriver(mapper);
        reduceDriver = ReduceDriver.newReduceDriver(reducer);
        mapDriver.setCacheFiles(cacheFiles);
        reduceDriver.setCacheFiles(cacheFiles);
    }

    /**
     * Pass bad log - get 1 malformed
     * 
     * @throws IOException
     */
    @Test
    public void testMapperCounterOne() throws IOException {
        mapDriver
                .withInput(new LongWritable(), new Text(testMalformedLog))
                .runTest();
        assertEquals("Expected 1 counter increment", 1, mapDriver.getCounters()
                .findCounter(CounterType.MALFORMED).getValue());
    }

    /**
     * Pass good log and get 0 malformed
     * 
     * @throws IOException
     */
    @Test
    public void testMapperCounterZero() throws IOException {
        mapDriver
                .withInput(new LongWritable(), new Text(testLog))
                .withOutput(new Text(sectorName), new IntWritable(1))
                .runTest();

        assertEquals("Expected 1 counter increment", 0, mapDriver.getCounters()
                .findCounter(CounterType.MALFORMED).getValue());
    }

    /**
     * Test 1 good and 2 malformed logs
     * 
     * @throws IOException
     */
    @Test
    public void testMapperCounters() throws IOException {
        mapDriver
                .withInput(new LongWritable(), new Text(testLog))
                .withInput(new LongWritable(), new Text(testMalformedLog))
                .withInput(new LongWritable(), new Text(testMalformedLog))
                .withOutput(new Text(sectorName), new IntWritable(1))
                .runTest();

        assertEquals("Expected 2 counter increment", 2, mapDriver.getCounters()
                .findCounter(CounterType.MALFORMED).getValue());
    }

    /**
     * Reduce bad results
     * 
     * @throws IOException
     */
    @Test
    public void testReducerCounterZero() throws IOException {
        List<IntWritable> zero = new ArrayList<>();
        zero.add(new IntWritable(0));
        reduceDriver
                .withInput(new Text(testMalformedName), zero)
                .runTest();

        assertEquals("Expected 0 counter increment", 0, reduceDriver.getCounters()
                .findCounter(CounterType.ACTIVE_SECTORS).getValue());
    }

    /**
     * Reduce 1 active sector
     * 
     * @throws IOException
     */
    @Test
    public void testReducerCounterOne() throws IOException {
        reduceDriver
                .withInput(new Text(sectorName), values)
                .withOutput(new Text(sectorName), new IntWritable(2))
                .runTest();

        assertEquals("Expected 1 counter increment", 1, reduceDriver.getCounters()
                .findCounter(CounterType.ACTIVE_SECTORS).getValue());
    }

    /**
     * Reduce 2 active sectors
     * 
     * @throws IOException
     */
    @Test
    public void testReducerCounters() throws IOException {
        reduceDriver
                .withInput(new Text(sectorName), values)
                .withInput(new Text(sectorName2), values)
                .withOutput(new Text(sectorName), new IntWritable(2))
                .withOutput(new Text(sectorName2), new IntWritable(2))
                .runTest();

        assertEquals("Expected 2 counter increment", 2, reduceDriver.getCounters()
                .findCounter(CounterType.ACTIVE_SECTORS).getValue());
    }
}
