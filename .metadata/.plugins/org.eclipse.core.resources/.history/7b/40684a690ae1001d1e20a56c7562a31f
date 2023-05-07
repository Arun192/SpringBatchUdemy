package in.arun.batch.controller;

import java.util.List;

import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.arun.batch.request.JobParamRequest;
import in.arun.batch.service.JobService;

@RestController
@RequestMapping("/api/job")
public class JobController {
	
	@Autowired
	private JobService  jobService;
	
	@Autowired
	JobOperator jobOperator;
	

	@GetMapping("/start/{jobName}")
	public String startJob(@PathVariable String jobName ,
			@RequestBody List<JobParamRequest> jobParamRequestList)  {
		
		
		jobService.startJob(jobName ,jobParamRequestList);
		
		return "Job Started......!";
	}
	
	@GetMapping("/stop/{jobExecutionId}")
	public String stopJob(@PathVariable long jobExecutionId) {
		
		try {
			jobOperator.stop(jobExecutionId);
		} catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "Job stopped...!";
	}
}
