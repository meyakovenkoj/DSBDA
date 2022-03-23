package com.yakovenko.lab1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.fs.Path;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.apache.hadoop.filecache.DistributedCache;
import java.io.BufferedReader;
import java.io.FileReader;

public class LabReducer extends Reducer<Text, IntWritable, Text, Text> {
    private TreeMap<Integer, String> temperatureMap = new TreeMap<Integer, String>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        try{
            Path[] cacheFiles = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            if(cacheFiles != null && cacheFiles.length > 0) {
                for(Path cacheFile : cacheFiles) {
                    if (cacheFile.getName().toUpperCase().contains("TEMPERATURE")) {
                        readFile(cacheFile);
                    }
                }
            }
        } catch(IOException ex) {
            System.err.println("Exception in mapper setup: " + ex.getMessage());
        }
    }

    private String getTemperature(Integer clicks) {
        Map.Entry<Integer, String> entry = temperatureMap.floorEntry(clicks);
        if (entry != null && entry.getValue() == null) {
            entry = temperatureMap.lowerEntry(clicks);
        }
        return entry == null ? null : entry.getValue();
    }

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        while (values.iterator().hasNext()) {
            sum += values.iterator().next().get();
        }
        String temperatureName = getTemperature(sum);
        context.getCounter("Temperature", key.toString()).increment(sum);
        context.write(key, new Text(temperatureName));
    }

    private void readFile(Path filePath) {
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toString()));
            String temp = null;
            while((temp = bufferedReader.readLine()) != null) {
                String[] params = temp.split("=");
                Integer temperature = Integer.parseInt(params[0]);
                temperatureMap.put(temperature, params[1].toUpperCase());
            }
        } catch(IOException ex) {
            System.err.println("Exception while reading stop words file: " + ex.getMessage());
        }
    }
}
