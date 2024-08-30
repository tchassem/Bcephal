/**
 * 
 */
package com.moriset.bcephal.billing.service.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import com.google.common.io.Files;
import com.moriset.bcephal.billing.domain.BillTemplate;
import com.moriset.bcephal.billing.domain.BillingTemplateLabel;
import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.UTF8Control;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author Moriset
 *
 */
@Slf4j
public class InvoicePrinter {
	
	
	private BillTemplate billTemplate;
	
	private String reportTemplateDir;
	
	private String reportTemplateBundleDir;
	
	private String billsPath;
	
	private String reportFormat = "pdf";
	
	private JasperReport jasperReport;

	private String defaultLang;
	
	private List<BillingTemplateLabel> labels = new ArrayList<>();
	
	
	private String bundleName = "bcephalmes";
	

	public InvoicePrinter(BillTemplate template, String billsPath) {
		this.billTemplate = template ;
		this.billsPath = billsPath;
		if(!StringUtils.hasText(this.billsPath)) {
			this.billsPath = Paths.get(System.getProperty("user.home"), ".bcephal","v8","invoice").toString();
		}
	}	
	
	public String print(Invoice invoice) throws Exception  {
		compileJasper();
		String name = invoice.getName();
				
		name = name.replaceAll("/","_");
		name = name.replaceAll("\\\\","_");
		
//		int version = invoice.getVersion();
//		String billName = name.concat("_V" + version).concat(".").concat(reportFormat);
		String billName = name.concat(".").concat(reportFormat);
		
        File billFile = Paths.get(this.billsPath, billName).toFile();
        if(billFile != null && !billFile.exists()) {
        	 if(billFile.getParentFile() != null && !billFile.getParentFile().exists()) {
        		 billFile.getParentFile().mkdirs();
             }
        }
        
        if(billFile == null) {
        	return null;
        }
        try {
        	String language = null;
        	invoice.buildVatItems();
        	language = invoice.getClientLanguage();	        
	        List<Invoice> invoices = new ArrayList<>(0);         
	        invoices.add(invoice);     
	        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(invoices);	        
	        Map<String, Object> parameters = new HashMap<>();
	        parameters.put("createdBy", "Bcephal");
	        parameters.put("SUBREPORT_DIR", this.reportTemplateDir);
	        URL resUrl = null;
	        try {
		        resUrl = Paths.get(this.reportTemplateBundleDir).toUri().toURL();	        
		        Locale locale = Locale.forLanguageTag(StringUtils.hasText(language) ? language : defaultLang);
		        parameters.put(JRParameter.REPORT_LOCALE, locale);
		        log.info("List files : {} {}",language,new File(resUrl.toExternalForm()).list() );
		        parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, ResourceBundle.getBundle(bundleName, locale, new URLClassLoader(new URL[]{resUrl}), new UTF8Control()));
		        parameters.put("net.sf.jasperreports.default.pdf.encoding", "UTF-8");
	        }
	        catch (Exception e) {
	        	log.trace("Unable to find locale resource bundle {}.", e);
			}
	        JasperPrint jasperPrint = JasperFillManager.fillReport(this.jasperReport, parameters, dataSource);
	        JasperExportManager.exportReportToPdfFile(jasperPrint, billFile.getPath());
	        log.trace("Bill printed : {}", billFile.getPath());
	        
	        if(resUrl != null && !new File(resUrl.toExternalForm()).getName().equals("bundle")) {
	        	try {
	        	FileUtils.forceDelete(new File(resUrl.toExternalForm()));
	        	}catch (Exception e) {
	        		log.trace("Unable to delete tmp dir for bundle : {}",resUrl.toExternalForm());
				}
	        }
	        invoice.setVersion(invoice.getVersion() + 1);
	        return billFile.getPath();
	        	        
        } 
        catch (Exception e) {
        	log.error("", e);
        	throw new BcephalException("Unable to print bill.", e);
		}
        finally {
        	invoice.getItems().clear();
		}
	}
	
	private void compileJasper() throws BcephalException {	
		if(jasperReport == null) {
			if(this.billTemplate == null) {
	        	throw new BcephalException("Unable to print bill. Template is NULL.");
	        }
			if(!StringUtils.hasText(this.billTemplate.getMainFile())) {
				throw new BcephalException("Unable to print pdf : the template main file is not setted.");
			}
			File file = Paths.get(this.billTemplate.getRepository(), this.billTemplate.getMainFile()).toFile();
			
			if(!file.exists()) {
	        	throw new BcephalException("Unable to print bill. Template main file not found : " + file.getPath()) ;
	        }
			log.trace("Try to compile jasper report : {}", file.getPath());	
			
        	this.reportTemplateDir = file.getParent();
        	
        	this.reportTemplateBundleDir = Paths.get(this.reportTemplateDir,"bundle").toString();
        	if(labels != null && labels.size() > 0) {
        		try {
					this.reportTemplateBundleDir = Files.createTempDir().getCanonicalPath();
					Map<String, Properties> maps = getBundleByLocale(labels);
					for(String key : maps.keySet()) {
						load(maps.get(key), key);
					}					
				} catch (IOException e) {
					log.trace("Unable to created temp dir for bundle : {}");
					//this.reportTemplateBundleDir = Paths.get(this.reportTemplateDir,"bundle").toString();
				}
        	}
        	
	        try {
	        	this.jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
	        }
	        catch (Exception e) {
	        	log.error("", e);
	        	throw new BcephalException("Unable to print pdf due to a template compilation error.", e);
			}
	        
		}
	}
	
	Map<String, Properties> getBundleByLocale(List<BillingTemplateLabel> labels) {
		
		Map<String, Properties> map = new LinkedMap<String,Properties>();
		
		labels.parallelStream().forEach(label -> {
			if (label != null) {
				label.getValueListChangeHandler().getItems().parallelStream().forEach(labelValue -> {
					Properties	mapValue = map.get(labelValue.getLocale());
					if(mapValue == null) {
						mapValue = new Properties();
						map.put(labelValue.getLocale(), mapValue);
					}
					mapValue.put(label.getCode(), labelValue.getValue());
				});
			}
		});
		
		return map;
	}
	
	private void load(Properties prop, String locale) throws IOException {
		if(prop != null) {		
		String fileName = bundleName;
		String extension = ".properties";
		if(Paths.get(this.reportTemplateBundleDir, fileName.concat(extension)).toFile().exists()) {
			fileName = fileName.concat("_").concat(locale);
		}
		fileName = fileName.concat(extension);
		String dataPath = Paths.get(this.reportTemplateBundleDir,fileName).toString();
		FileOutputStream out_ = new FileOutputStream(dataPath);
		OutputStreamWriter out = new OutputStreamWriter(out_, StandardCharsets.UTF_8);
		prop.store(out, "");
		out.close();
		}
	}
	
	public void setBillingModelLabels(List<BillingTemplateLabel> labels) {
		this.labels.addAll(labels);
	}
	
	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}
	
//	private BillTemplate getBillTemplate() {
//		log.debug("Try to read bill template with code : {}", this.billTemplateCode);
//		BillTemplate template = null;
//		try {
//			BillTemplateService service = new BillTemplateService(userSession);
//			template = service.getByCode(this.billTemplateCode);
//			if(template == null) {
//				log.debug("Ther is no bill template with code : {}", this.billTemplateCode);
//			}	
//			else {
//				if(StringUtils.isBlank(template.getMainFile())) {
//					log.error("Unable to load bill template with code : {}. Main file is NULL : {}", this.billTemplateCode);
//				}
//				else {
//					File file = Paths.get(FilenameUtils.separatorsToSystem(userSession.filePath), "billtemplates", this.billTemplateCode, template.getMainFile()).toFile();
//					if(file.exists()) {
//						template.setRepository(file.getParent());
//					}
//					else {
//						log.error("Unable to read bill template with code : {}. File not found : {}", this.billTemplateCode, file.getPath());
//						template = null;
//					}
//				}
//			}
//		}
//		catch (Exception e) {
//			log.error("Unable to read bill template with code : {}", this.billTemplateCode, e);
//		}
//		
//		if(template == null) {
//			File file = Paths.get(System.getProperty("user.dir"), "resources", "bill", "invoice_detail.jrxml").toFile();
//			log.trace("Template : {}", file);        
//	        if(file.exists()) {
//	        	template = new BillTemplate();
//	        	template.setMainFile("invoice_detail.jrxml");
//	        	template.setRepository(file.getParent());
//	        }
//		}	
//		return template;
//	}

}
