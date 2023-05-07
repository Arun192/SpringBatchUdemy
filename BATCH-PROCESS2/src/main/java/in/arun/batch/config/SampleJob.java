package in.arun.batch.config;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.JsonObjectMarshaller;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import in.arun.batch.model.StudentCsv;
import in.arun.batch.model.StudentJdbc;
import in.arun.batch.model.StudentJson;
import in.arun.batch.model.StudentResponse;
import in.arun.batch.model.StudentXml;
import in.arun.batch.processor.FirstItemProcessor;
import in.arun.batch.reader.FirstItemReader;
import in.arun.batch.service.StudentResponseService;
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
	private FirstItemReader firstItemReader;
	
	@Autowired
	private FirstItemProcessor firstItemProcessor;
	
	@Autowired
	private FirstItemWriter firstItemWriter;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private StudentResponseService responseService;
	
	
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.quartzdemodatasource")
	public DataSource quartzdemoDataSource() {
		
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	public Job chunkJob() {
		return jobBuilderFactory.get("Chunk Job")
				.incrementer(new RunIdIncrementer())
				.start(firstChunkStep())
				.build();
	}
	
	private Step firstChunkStep() {
		return stepBuilderFactory.get("First Chunk Step")
				.<StudentCsv, StudentCsv>chunk(3)
				.reader(flatFileItemReader(null))
				//.reader(jsonItemReader(null))
				//.reader(staxEventItemReader(null))
				//.reader(jdbcCursorItemReader())
				//.reader(itemReaderAdapter())
				//.processor(firstItemProcessor)
				//.writer(firstItemWriter)
				//.writer(flatFileItemWriter(null))
				//.writer(jsonFileItemWriter(null))
				//.writer(staxEventItemWriter(null))
				//.writer(jdbcBatchItemWriter())
				//.writer(jdbcBatchItemWriter1())
				.writer(itemReaderAdapter())
				.build();
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<StudentCsv> flatFileItemReader( @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		
		FlatFileItemReader<StudentCsv> flatFileItemReader = 
				new FlatFileItemReader<StudentCsv>();
		
//		flatFileItemReader.setResource(new FileSystemResource(
//				new File("C:\\Users\\User\\Documents\\UDEMY-BATCH\\BATCH-PROCESS2\\InputFiles\\students.csv")));
		
		flatFileItemReader.setResource(fileSystemResource);
		
		flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames("ID", "First Name", "Last Name", "Email");
						setDelimiter("|");
					}
				});
				
				setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
					{
						setTargetType(StudentCsv.class);
					}
				});
				
			}
		});
		
		DefaultLineMapper<StudentCsv> defaultLineMapper = 
				new DefaultLineMapper<>();
		
	/*	DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames("ID","First Name ","Last Name" ,"Email");
		
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		
		BeanWrapperFieldSetMapper<StudentCsv> fieldSetMapper =
				new BeanWrapperFieldSetMapper<StudentCsv>();
		
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		
		fieldSetMapper.setTargetType(StudentCsv.class);
		
		
		*/
		
		flatFileItemReader.setLinesToSkip(1);
		
		return flatFileItemReader;
	}
	
	@Bean
	@StepScope
	public JsonItemReader<StudentJson> jsonItemReader( @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource){
		
		JsonItemReader<StudentJson> jsonItemReader = new  JsonItemReader<StudentJson>();
		
		jsonItemReader.setResource(fileSystemResource);
		jsonItemReader.setJsonObjectReader(
				
				new JacksonJsonObjectReader<>(StudentJson.class) );
		
		jsonItemReader.setMaxItemCount(8);
		jsonItemReader.setCurrentItemCount(2);
		
		return jsonItemReader;
		
	}

	@Bean
	@StepScope
	public StaxEventItemReader<StudentXml> staxEventItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource){
		
		
		StaxEventItemReader<StudentXml> staxEventItemReader = new StaxEventItemReader<StudentXml>();
		staxEventItemReader.setResource(fileSystemResource);
		staxEventItemReader.setFragmentRootElementName("student");
		staxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
			
			{
				setClassesToBeBound(StudentXml.class);
			}
		});
		
		
		
		return staxEventItemReader;
		
	}
	
	public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader(){
		
		JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader = new JdbcCursorItemReader<StudentJdbc>();
		
		jdbcCursorItemReader.setDataSource(quartzdemoDataSource());
		jdbcCursorItemReader.setSql(
				"select  id , first_name as firstName,last_name as lastName , email from student");
		jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>(){
		{
			setMappedClass(StudentJdbc.class);
		}	
		});
//		jdbcCursorItemReader.setCurrentItemCount(2);
//		jdbcCursorItemReader.setMaxItemCount(8);
		
		return jdbcCursorItemReader;
	}

	/*
	public ItemReaderAdapter<StudentReponse> itemReaderAdapter(){
		
		ItemReaderAdapter<StudentReponse> itemReaderAdapter = new ItemReaderAdapter<StudentReponse>();
		
		itemReaderAdapter.setTargetObject(responseService);
		itemReaderAdapter.setTargetMethod("getStudent");
		
		itemReaderAdapter.setArguments(new Object[] {1L,"Test"});
		return itemReaderAdapter;
		
	}
	*/

	@Bean
	@StepScope
	public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource){
		
		FlatFileItemWriter<StudentJdbc> flatFileItemWriter = new FlatFileItemWriter<StudentJdbc>();
		
		flatFileItemWriter.setResource(fileSystemResource);
		flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
			
			@Override
			public void writeHeader(Writer writer) throws IOException {
				
				writer.write("Id|First Name|Last Name|Email");
				
			}
			
		});
		flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<StudentJdbc>() {
			
			{
				setDelimiter("|");
				setFieldExtractor(new BeanWrapperFieldExtractor<StudentJdbc>() {
					
					{
						setNames(new String[] {"id","firstName","lastName","email"});
					}
				});
			}
		});
		
		flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
			
			@Override
			public void writeFooter(Writer writer) throws IOException {
				// TODO Auto-generated method stub
					writer.write("Created @ "+ new Date());
			}
		});
		return flatFileItemWriter;
	}
	
	@Bean
	@StepScope
	public JsonFileItemWriter<StudentJson> jsonFileItemWriter(@Value("#{jobParameters['outputFile']}")

	FileSystemResource fileSystemResource)
	{

		JsonFileItemWriter<StudentJson> jsonFileItemWriter = new JsonFileItemWriter<StudentJson>(fileSystemResource,
				new JacksonJsonObjectMarshaller<StudentJson>());

		return jsonFileItemWriter;

	}
	
	@Bean
	@StepScope
	public StaxEventItemWriter<StudentJdbc> staxEventItemWriter(@Value("#{jobParameters['outputFile']}")

	FileSystemResource fileSystemResource){
		
		StaxEventItemWriter<StudentJdbc> staxEventItemWriter = new StaxEventItemWriter<StudentJdbc>();
		
		staxEventItemWriter.setResource(fileSystemResource);
		staxEventItemWriter.setRootTagName("students");
		staxEventItemWriter.setMarshaller(new Jaxb2Marshaller() {
			
			{
				setClassesToBeBound(StudentJdbc.class);
			}
		});
		
		return staxEventItemWriter;
	}
	
	@Bean
	//@StepScope
	public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter(){
		
		JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter = new JdbcBatchItemWriter<StudentCsv>();
		jdbcBatchItemWriter.setDataSource(quartzdemoDataSource());
		jdbcBatchItemWriter.setSql(
				"INSERT INTO STUDENT(ID, first_name,last_name,email) "
		+ "values (:id, :firstName, :lastName, :email)"
				);
		
		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<StudentCsv>());
		return jdbcBatchItemWriter;
	}
	
	@Bean
	//@StepScope
	public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter1(){
		
		JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter = new JdbcBatchItemWriter<StudentCsv>();
		jdbcBatchItemWriter.setDataSource(quartzdemoDataSource());
		jdbcBatchItemWriter.setSql(
				"INSERT INTO STUDENT(ID, first_name,last_name,email) "
		+ "values (?,?,?,?)"
				);
		
		jdbcBatchItemWriter.setItemPreparedStatementSetter(
				new ItemPreparedStatementSetter<StudentCsv>() {
			
			@Override
			public void setValues(StudentCsv item, PreparedStatement ps) throws SQLException {
				
				ps.setLong(1, item.getId());
				ps.setString(2, item.getFirstName());
				ps.setString(3, item.getLastName());
				ps.setString(4, item.getEmail());
				
			}
		});;
		return jdbcBatchItemWriter;
	}
	
	
	public ItemWriterAdapter<StudentCsv> itemReaderAdapter() {

		ItemWriterAdapter<StudentCsv> itemWriterAdapter = new ItemWriterAdapter<StudentCsv>();

		itemWriterAdapter.setTargetObject(responseService);
		itemWriterAdapter.setTargetMethod("restCallToCreateStudent");

		return itemWriterAdapter;
	}



}

















