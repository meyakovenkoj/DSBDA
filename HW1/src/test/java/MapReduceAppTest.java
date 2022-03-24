import com.yakovenko.lab1.LabMapper;
import com.yakovenko.lab1.LabReducer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for Map Reduce App
 */
public class MapReduceAppTest {

    private MapDriver<LongWritable, Text, Text, IntWritable> mapDriver;
    private ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;
    private MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, IntWritable> mapReduceDriver;

    private final String testLog = "50,80,16880011,1000\n";
    private final String sectorName = "SECTOR_0";

    /**
     * init test case
     */
    @Before
    public void setUp() {
        URI[] cacheFiles = new URI[2];
        cacheFiles[0] = new Path("SECTORS").toUri();
        cacheFiles[1] = new Path("TEMPERATURE").toUri();
        LabMapper mapper = new LabMapper();
        LabReducer reducer = new LabReducer();
        mapDriver = MapDriver.newMapDriver(mapper);
        reduceDriver = ReduceDriver.newReduceDriver(reducer);
        mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
        mapDriver.setCacheFiles(cacheFiles);
        reduceDriver.setCacheFiles(cacheFiles);
        mapReduceDriver.setCacheFiles(cacheFiles);
    }

    /**
     * test mapper on example, expects sector 0
     * 
     * @throws IOException
     */
    @Test
    public void testMapper() throws IOException {
        mapDriver
                .withInput(new LongWritable(), new Text(testLog))
                .withOutput(new Text(sectorName), new IntWritable(1))
                .runTest();
    }

    /**
     * test reducer - use 2 values
     * 
     * @throws IOException
     */
    @Test
    public void testReducer() throws IOException {
        List<IntWritable> values = new ArrayList<IntWritable>();
        values.add(new IntWritable(7));
        values.add(new IntWritable(5));
        reduceDriver
                .withInput(new Text(sectorName), values)
                .withOutput(new Text(sectorName), new IntWritable(2))
                .runTest();
    }

    /**
     * test map reduce process - expects sector 0 - 1 click
     * 
     * @throws IOException
     */
    @Test
    public void testMapReduce() throws IOException {
        mapReduceDriver
                .withInput(new LongWritable(), new Text(testLog))
                .withInput(new LongWritable(), new Text(testLog))
                .withOutput(new Text(sectorName), new IntWritable(1))
                .runTest();
    }
}
