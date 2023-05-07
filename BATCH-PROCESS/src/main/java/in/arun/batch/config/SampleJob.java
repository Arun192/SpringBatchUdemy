package in.arun.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import in.arun.batch.listner.FirstJobListner;
import in.arun.batch.listner.FirstStepListner;
import in.arun.batch.processor.FirstItemProcessor;
import in.arun.batch.reader.FirstItemReader;
import in.arun.batch.service.SecondTasklet;
import in.arun.batch.writer.FirstItemWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SampleJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    private SecondTasklet secondTasklet;
    
    @Autowired
    private FirstJobListner firstJobListner;
    
    @Autowired
    private FirstStepListner firstStepListner;
    
    @Autowired
    private FirstItemReader firstItemReader;
    
    @Autowired
    private FirstItemProcessor firstItemProcessor;
    
    @Autowired
    private FirstItemWriter firstItemWriter;

    @Bean
    public Job firstJob() {
        return jobBuilderFactory
                .get("First Job")
                .incrementer(new RunIdIncrementer())
                .start(firstStep())
                .next(secondStep())
                .listener(firstJobListner)
                .build();
    }

    private Step firstStep() {
        return stepBuilderFactory
        		.get("First Step")
        		.tasklet(firstTask())
        		.listener(firstStepListner)
        		.build();
    }

    private Tasklet firstTask() {
        return new Tasklet() {

            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

            	log.info("This is first tasklet step");
                System.out.println("This is first tasklet step");
                System.out.println("SEC = "+chunkContext.getStepContext());
                
                
                return RepeatStatus.FINISHED;
            }
        };
    }

    
    private Step secondStep() {
        return stepBuilderFactory
        		.get("Second Step")
        		.tasklet(secondTasklet)
        		.build();
    }

//    private Tasklet secondTask() {
//        return new Tasklet() {
//
//            @Override
//            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//
//            	log.info("Second");
//                System.out.println("This is second tasklet step");
//
//                return RepeatStatus.FINISHED;
//            }
//        };
//    }
    
    @Bean
    public Job secondJob() {
    	
    	return jobBuilderFactory.get("Second Job")
    			.incrementer(new RunIdIncrementer())
    			.start(firstChunkStep())
    			.next(secondStep())
    			.build();
    }
    
    private Step firstChunkStep() {
    	
    	return stepBuilderFactory.get("First Chunk Step")
    			.<Integer,Long>chunk(4)
    			.reader(firstItemReader)
    			.processor(firstItemProcessor)
    			.writer(firstItemWriter)
    			.build();
    }
}


