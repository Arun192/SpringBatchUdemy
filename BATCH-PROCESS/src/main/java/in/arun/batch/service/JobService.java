package in.arun.batch.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import in.arun.batch.request.JobParamRequest;

@Service
public class JobService {

	@Autowired
	private JobLauncher jobLauncher;

	@Qualifier("firstJob")
	@Autowired
	private Job firstJob;
	
	@Qualifier("secondJob")
	@Autowired
	private Job secondJob;
	
	@Async
	public void startJob(String jobName ,List<JobParamRequest> jobParamRequestList) {

		Map<String, JobParameter> params = new HashMap<>();

		params.put("currentTime", new JobParameter(System.currentTimeMillis()));
		
		jobParamRequestList.stream().forEach(jobParamReq ->{
			params.put(jobParamReq.getParamKey(),new JobParameter(jobParamReq.getParamValue()));
		});

		JobParameters jobParameters = new JobParameters(params);

		try {
			
			JobExecution jobExecution=null;
			
			if (jobName.equals("FirstJob")) {
				 jobExecution=jobLauncher.run(firstJob, jobParameters);
			} else if (jobName.equals("SecondJob")) {
				jobExecution = jobLauncher.run(secondJob, jobParameters);
			}
			
			System.out.println("Job ExecutionId "+ jobExecution.getId());
			
		} catch (Exception e) {
			
			System.out.println("Exception while starting job..");
		}
	}
}
