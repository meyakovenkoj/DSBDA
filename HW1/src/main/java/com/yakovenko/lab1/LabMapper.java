package com.yakovenko.lab1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class LabMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);
    private HashMap<String, String> coodinateMap = new HashMap<String, String>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        try{
            URI[] cacheFiles = context.getCacheFiles();
            if(cacheFiles != null && cacheFiles.length > 0) {
                for(int i = 0; i < cacheFiles.length; i++) {
                    Path cacheFile = new Path(cacheFiles[i]);
                    if (cacheFile.getName().toUpperCase().contains("SECTORS")) {
                        readFile(cacheFile);
                    }
                }
            }
        } catch(IOException ex) {
            System.err.println("Exception in mapper setup: " + ex.getMessage());
        }
    }

    private String getSectorName(String x, String y) {
        int x_c = Integer.parseInt(x);
        int y_c = Integer.parseInt(y);
        x_c = x_c - (x_c % 100);
        y_c = y_c - (y_c % 100);
        String sectorName = coodinateMap.get(x_c + "," + y_c);
        return sectorName;
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] params = line.split(",");
        String sectorName = getSectorName(params[0], params[1]);

        if (sectorName == null) {
            context.getCounter(CounterType.MALFORMED).increment(1);
        } else {
            context.write(new Text(sectorName), one);
        }
    }

    private void readFile(Path filePath) {
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toString()));
            String sectorCoordinates = null;
            while((sectorCoordinates = bufferedReader.readLine()) != null) {
                String[] params = sectorCoordinates.split("=");
                coodinateMap.put(params[0], params[1].toUpperCase());
            }
        } catch(IOException ex) {
            System.err.println("Exception while reading stop words file: " + ex.getMessage());
        }
    }
}
