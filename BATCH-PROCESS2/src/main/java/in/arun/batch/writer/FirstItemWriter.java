package in.arun.batch.writer;

import java.util.List;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import in.arun.batch.model.StudentCsv;
import in.arun.batch.model.StudentJdbc;

@Component
public class FirstItemWriter implements ItemWriter<StudentCsv> {

	@Override
	public void write(List<? extends StudentCsv> items) throws Exception {

		System.out.println("Inside Item Writer ");

		items.stream().forEach(System.out::println);

	}

}
