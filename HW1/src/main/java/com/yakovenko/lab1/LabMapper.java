package com.yakovenko.lab1;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class for mapper
 */
public class LabMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);
    private HashMap<String, JSONArray> coordinateMap = new HashMap<String, JSONArray>();

    /**
     * Initial setup of mapper - load cache files
     * 
     * @param context job context
     */
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        try {
            URI[] cacheFiles = context.getCacheFiles();
            if (cacheFiles != null && cacheFiles.length > 0) {
                for (URI file : cacheFiles) {
                    Path cacheFile = new Path(file);
                    if (cacheFile.getName().toUpperCase().contains("SECTORS")) {
                        readFile(cacheFile);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("Exception in mapper setup: " + ex.getMessage());
        }
    }

    /**
     * Getter for sector name by coordinates
     * 
     * @param x first coordinate of point
     * @param y second coordinate
     * @return sector name
     */
    private String getSectorName(String x, String y) {
        int x_c = Integer.parseInt(x);
        int y_c = Integer.parseInt(y);
        for (String key : coordinateMap.keySet()) {
            JSONArray tmp = coordinateMap.get(key);
            JSONArray left = (JSONArray) tmp.get(0);
            JSONArray right = (JSONArray) tmp.get(1);
            if (x_c >= left.getInt(0) && x_c <= right.getInt(0) &&
                    y_c >= left.getInt(1) && y_c <= right.getInt(1)) {
                return key;
            }
        }
        return null;
    }

    /**
     * Main mapper function. If there is no sector name, func increments MALFORMED
     * counter and exit
     * 
     * @param key     not used
     * @param value   string with log
     * @param context job context
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] params = line.split(",");
        if (params.length < 2) {
            System.out.println(Arrays.toString(params));
            context.getCounter(CounterType.MALFORMED).increment(1);
            return;
        }
        String sectorName = getSectorName(params[0], params[1]);

        if (sectorName == null) {
            System.out.println(Arrays.toString(params));
            context.getCounter(CounterType.MALFORMED).increment(1);
        } else {
            context.write(new Text(sectorName), one);
        }
    }

    /**
     * Reading file by path to {@link coordinateMap}
     * 
     * @param filePath input file path
     */
    private void readFile(Path filePath) {
        try {
            InputStream is = new FileInputStream(filePath.toString());
            String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonTxt);
            Iterator<String> nameItr = json.keys();
            while (nameItr.hasNext()) {
                String name = nameItr.next();
                coordinateMap.put(name, (JSONArray) json.get(name));
            }
        } catch (IOException ex) {
            System.err.println("Exception while reading stop words file: " + ex.getMessage());
        }
    }
}
