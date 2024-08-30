package com.moriset.bcephal.etl.controller;

//@RestController
//@RequestMapping("/etl")
public class EtlController {
	
//@Autowired MT940Service mt940Service;
//	
//	@Autowired
//    JobLauncher jobLauncher;
//	@Autowired
//	 JobExplorer jobExplorer;
//	@Autowired
//	 JobOperator jobOperator;
//	
//	
//    @Autowired Job mt940Job;
//   // @Autowired Job mt941Job;
//    @Autowired Job mt942Job;
//    
//	
//	@GetMapping("/get")
//	public Collection<StepExecution> getEtl() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException  {
//		Date runDate = new Date();
//		JobParameters parameters = new JobParametersBuilder()
//				.addString(ParameterCode.RUN_ID, new SimpleDateFormat("yyyyMMddHHmmss").format(runDate))
//				.addString(ParameterCode.RUN_DATE, new SimpleDateFormat("yyyyMMdd").format(runDate))
//				.addString(ParameterCode.BASE_DIR, Path.of(System.getProperty("user.dir"), "mt940").toString())
//				.addString(ParameterCode.OUTPUT_DIR, Path.of(System.getProperty("user.dir"), "out", "mt940").toString())
//				.addString(ParameterCode.FILE_NAMES, "mt940_01.txt;mt940_02.txt")
//				.addString(ParameterCode.CONTINUE_WHEN_ERROR, "true")
//				.addString(ParameterCode.CSV_OUT_SEPARATOR, ParameterCode.CSV_SEPARATOR_VALUE)
//				.addString(ParameterCode.ADD_HEADER, "true")
//				
//				.addString(ParameterCode.DATE_IN_FORMAT, ParameterCode.DATE_IN_FORMAT_VALUE)
//				.addString(ParameterCode.DATE_OUT_FORMAT, ParameterCode.DATE_OUT_FORMAT_VALUE)
//				.addString(ParameterCode.TIME_FORMAT, ParameterCode.TIME_FORMAT_VALUE)
//				.addString(ParameterCode.DECIMAL_IN_SEPARATOR, ParameterCode.DECIMAL_IN_SEPARATOR_VALUE)
//				.addLong(ParameterCode.DECIMAL_IN_COUNT, ParameterCode.DECIMAL_IN_COUNT_VALUE)
//				.addString(ParameterCode.DECIMAL_OUT_SEPARATOR, ParameterCode.DECIMAL_OUT_SEPARATOR_VALUE)
//				.addLong(ParameterCode.DECIMAL_OUT_COUNT, ParameterCode.DECIMAL_OUT_COUNT_VALUE)
//				.toJobParameters();
//		
//		JobExecution jobexecution = jobLauncher.run(mt940Job, parameters);
//		return jobexecution.getStepExecutions();
//	}
//	
//	
//	public void startJob() throws Exception{
//		JobExecution jobexecution = jobExplorer.getJobExecution(216213l);
//		
//		jobOperator.restart(jobexecution.getJobId());
//	}
//
//	@GetMapping("/start")
//	public void stopRunningProcess() {
//		Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions("mt940Job");
//		jobExecutions.forEach(jobExecution -> {
//			try {
//				jobOperator.stop(jobExecution.getId());
//			} catch (NoSuchJobExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (JobExecutionNotRunningException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		});
//		}
//	

}
