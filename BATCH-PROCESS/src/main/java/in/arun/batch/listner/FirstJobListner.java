package in.arun.batch.listner;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstJobListner implements JobExecutionListener{

	@Override
	public void beforeJob(JobExecution jobExecution) {
		
		System.out.println("Before Job  : "+jobExecution.getJobInstance().getJobName());
		System.out.println("Job  Params : "+jobExecution.getJobParameters());
		System.out.println("Job Exec Contex : "+jobExecution.getExecutionContext());

		jobExecution.getExecutionContext().put("jec" , "job value");
		
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		
		System.out.println("After Job  : "+jobExecution.getJobInstance().getJobName());
		System.out.println("Job  Params : "+jobExecution.getJobParameters());
		System.out.println("Job Exec Contex : "+jobExecution.getExecutionContext());
	}

	
}
