package in.arun.batch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.arun.batch.model.StudentCsv;
import in.arun.batch.model.StudentResponse;

@Service
public class StudentResponseService {
	
	List<StudentResponse> list;
	
	public List<StudentResponse> restCallToGetStudents(){
		
		RestTemplate restTemplate = new RestTemplate();
		StudentResponse[] studentReponseArray = restTemplate.getForObject("http://localhost:8081/api/v1/students", StudentResponse[].class);
		
		list = new ArrayList<StudentResponse>();
		
		for (StudentResponse sr : studentReponseArray) {
			list.add(sr);
		}
		
		return list;
		
		
	}

	public StudentResponse getStudent(long id , String name) {
		
		System.out.println("id = "+id +" and name "+name);
		
		if (list == null) {
			restCallToGetStudents();
		}
		if (list != null && !list.isEmpty()) {
			return list.remove(0);
		}
		
		return null;
		
	}
	
	public StudentResponse restCallToCreateStudent(StudentCsv studentCsv) {
		RestTemplate restTemplate  =new RestTemplate();
		return restTemplate.postForObject("http://localhost:8081/api/v1/createStudent", studentCsv, StudentResponse.class);
	}
}
