package com.yakovenko.lab1;

import lombok.extern.log4j.Log4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
// import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.io.SequenceFile.CompressionType;

@Log4j
public class MapReduceApp extends Configured implements Tool
{
    public static void main( String[] args ) throws Exception
    {
        int exitCode = ToolRunner.run(new MapReduceApp(), args);
        System.exit(exitCode);
    }

    public int run(String[] args) throws Exception {
        if (args.length != 4) {
            throw new RuntimeException("You should specify input, output, sectors dict and temp dict");
        }

        Job job = new Job();
        job.setJarByClass(MapReduceApp.class);
        job.setJobName("Click Temperature Map");
        job.setMapperClass(LabMapper.class);
        job.setReducerClass(LabReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        // job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        FileOutputFormat.setCompressOutput(job, true);
        FileOutputFormat.setOutputCompressorClass(job, SnappyCodec.class);
        SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.BLOCK);

        // add coordinates - sectors dictionary
        DistributedCache.addCacheFile(new Path(args[2]).toUri(), job.getConfiguration());
        
        // add clicks - temperature dictionary
        DistributedCache.addCacheFile(new Path(args[3]).toUri(), job.getConfiguration());

        Path outputDirectory = new Path(args[1]);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, outputDirectory);
        log.info("=====================JOB STARTED=====================");
        job.waitForCompletion(true);
        log.info("=====================JOB ENDED=====================");
        // проверяем статистику по счётчикам
        Counter counter = job.getCounters().findCounter(CounterType.MALFORMED);
        log.info("=====================COUNTERS " + counter.getName() + ": " + counter.getValue() + "=====================");
        return 0;
    }
}
