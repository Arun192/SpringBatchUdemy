package in.arun.batch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentResponse {

	private long id;
	private String firstName;
	private String lastName;
	private String email;
}
