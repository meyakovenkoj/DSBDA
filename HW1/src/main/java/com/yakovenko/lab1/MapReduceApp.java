package com.yakovenko.lab1;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.util.Objects;


public class MapReduceApp extends Configured implements Tool
{
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MapReduceApp.class);
    private boolean debug = false;
    public static void main( String[] args ) throws Exception
    {
        int exitCode = ToolRunner.run(new MapReduceApp(), args);
        System.exit(exitCode);
    }

    public int run(String[] args) throws Exception {
        if (args.length == 5 && Objects.equals(args[4], "--debug")) {
            debug = true;
        } else if (args.length != 4) {
            throw new RuntimeException("You should specify input, output, sectors dict and temp dict");
        }

        Job job = Job.getInstance();
        job.setJarByClass(MapReduceApp.class);
        job.setJobName("Click Temperature Map");
        job.setMapperClass(LabMapper.class);
        job.setReducerClass(LabReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        if (debug) {
            job.setOutputFormatClass(TextOutputFormat.class);
        } else {
            job.setOutputFormatClass(SequenceFileOutputFormat.class);
            FileOutputFormat.setCompressOutput(job, true);
            FileOutputFormat.setOutputCompressorClass(job, SnappyCodec.class);
            SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.BLOCK);
        }

        // add coordinates - sectors dictionary
        job.addCacheFile(new Path(args[2]).toUri());
        
        // add clicks - temperature dictionary
        job.addCacheFile(new Path(args[3]).toUri());

        Path outputDirectory = new Path(args[1]);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, outputDirectory);
        log.info("=====================JOB STARTED=====================");
        job.waitForCompletion(true);
        log.info("=====================JOB ENDED=====================");
        // проверяем статистику по счётчикам
        Counter counterBad = job.getCounters().findCounter(CounterType.MALFORMED);
        log.info("=====================COUNTERS " + counterBad.getName() + ": " + counterBad.getValue() + "=====================");
        Counter counterGood = job.getCounters().findCounter(CounterType.ACTIVE_SECTORS);
        log.info("=====================COUNTERS " + counterGood.getName() + ": " + counterGood.getValue() + "=====================");
        return 0;
    }
}
