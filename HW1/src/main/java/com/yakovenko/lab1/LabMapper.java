package com.yakovenko.lab1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import org.json.JSONArray;
import org.json.JSONObject;


public class LabMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);
    private HashMap<String, JSONArray> coordinateMap = new HashMap<String, JSONArray>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        try{
            URI[] cacheFiles = context.getCacheFiles();
            if(cacheFiles != null && cacheFiles.length > 0) {
                for (URI file : cacheFiles) {
                    Path cacheFile = new Path(file);
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
        for (String key:coordinateMap.keySet()) {
            JSONArray tmp = (JSONArray)coordinateMap.get(key);
            JSONArray left = (JSONArray) tmp.get(0);
            JSONArray right = (JSONArray) tmp.get(1);
            if (x_c >= left.getInt(0) && x_c <= right.getInt(0) &&
                    y_c >= left.getInt(1) && y_c <= right.getInt(1)) {
                return key;
            }
        }
        return null;
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] params = line.split(",");
        String sectorName = getSectorName(params[0], params[1]);

        if (sectorName == null) {
            System.out.println(Arrays.toString(params));
            context.getCounter(CounterType.MALFORMED).increment(1);
        } else {
            context.write(new Text(sectorName), one);
        }
    }

    private void readFile(Path filePath) {
        try{
            // BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toString()));
            // String sectorCoordinates = null;
            InputStream is = new FileInputStream(filePath.toString());
            String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonTxt);
            Iterator<String> nameItr = json.keys();
            while(nameItr.hasNext()) {
                String name = nameItr.next();
                coordinateMap.put(name, (JSONArray) json.get(name));
            }
        } catch(IOException ex) {
            System.err.println("Exception while reading stop words file: " + ex.getMessage());
        }
    }
}
