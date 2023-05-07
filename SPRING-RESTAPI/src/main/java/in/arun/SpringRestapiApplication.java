package in.arun;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.arun.model.StudentReponse;
import in.arun.model.StudentRequest;

@SpringBootApplication
@RestController
@RequestMapping("/api/v1")
public class SpringRestapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRestapiApplication.class, args);
	}
	
	@GetMapping("/students")
	public List<StudentReponse> students(){
		
		return Arrays.asList(
				
				new StudentReponse(1L, "John","Smith","john@gmail.com"),
				new StudentReponse(2L, "Arun","Smith","john@gmail.com"),
				new StudentReponse(3L, "Abhi","Smith","john@gmail.com"),
				new StudentReponse(4L, "Anji","Smith","john@gmail.com"),
				new StudentReponse(5L, "Ram","Smith","john@gmail.com"),
				new StudentReponse(6L, "Shyam","Smith","john@gmail.com"),
				new StudentReponse(7L, "Rohan","Smith","john@gmail.com"),
				new StudentReponse(8L, "Mohan","Smith","john@gmail.com"),
				new StudentReponse(9L, "Kalyan","Smith","john@gmail.com"),
				new StudentReponse(10L, "Kameshwari","Oleti","kameshwari@gmail.com")
				);
				
	}
	
	@PostMapping("/createStudent")
	public StudentReponse createStudent(@RequestBody StudentRequest studentRequest) {
		System.out.println("Student Created "+studentRequest.getId());
		
		return new StudentReponse(studentRequest.getId(),
				studentRequest.getFirstName(),
				studentRequest.getLastName(),
				studentRequest.getEmail());
		
	}

}
