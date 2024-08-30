package com.moriset.bcephal.billing.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.BillTemplate;
import com.moriset.bcephal.billing.domain.BillTemplateBrowserData;
import com.moriset.bcephal.billing.domain.BillingTemplateEditorData;
import com.moriset.bcephal.billing.domain.BillingTemplateLabel;
import com.moriset.bcephal.billing.domain.BillingTemplateLabelValue;
import com.moriset.bcephal.billing.domain.InvoiceVariables;
import com.moriset.bcephal.billing.repository.BillTemplateRepository;
import com.moriset.bcephal.billing.repository.BillingTemplateLabelRepository;
import com.moriset.bcephal.billing.repository.BillingTemplateLabelValueRepository;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FileUtil;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.ZipFileUnZipUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillTemplateService  extends MainObjectService<BillTemplate, BrowserData> {
	
	
	@Value("${bcephal.project.data-dir}")
	String projectDataDir;
	
	
	@Value("${bcephal.languages:en}")
	List<String> locales;
	
	@Value("${bcephal.billing.bill.template.bundle.basename}")
	String bundleBasename;
	
	
	@Autowired
	BillTemplateRepository billTemplateRepository;
	
	@Autowired
	BillingTemplateLabelRepository billingTemplateLabelRepository;
	
	@Autowired
	BillingTemplateLabelValueRepository billingTemplateLabelValueRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.BILLING_TEMPLATE;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}
	
	@Override
	public BillTemplateRepository getRepository() {
		return billTemplateRepository;
	}
	
	@Override
	public BillingTemplateEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<BillTemplate> base = super.getEditorData(filter, session, locale);
		BillingTemplateEditorData data = new BillingTemplateEditorData(base);
		data.variables = new InvoiceVariables().getAll();
		data.locales = locales;
		
		if (data.getItem() != null && !data.getItem().isPersistent() && data.getItem().getCode() != null) {
			getTemplateLabels(data.getItem())
				.forEach(label -> {						
					data.getItem().getLabelListChangeHandler().addNew(label);
				});	
		}
			
		return data;
	}
	
	
	public void createDefaultTemplates(String projectName) {
		createDefaultTemplate(true, projectName);
		createDefaultTemplate(false, projectName);
	}
		
	private BillTemplate createDefaultTemplate(boolean invoice, String projectName) {
		BillTemplate template = null;
		String paramCode = invoice ? BillingParameterCodes.BILLING_INVOICE_DEFAULT_TEMPLATE : BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_DEFAULT_TEMPLATE;
		String templateName = invoice ? "Default invoice template" : "Default credit note template";
		Parameter parameter = parameterRepository.findByCodeAndParameterType(paramCode, ParameterType.BILL_TEMPLATE);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<BillTemplate> result = billTemplateRepository.findById(parameter.getLongValue());
			if (result.isPresent()) {
				template = result.get();
			}
		}
		String repository = null;
		if (template == null) {
			String code = invoice ? "default_invoice_template" : "default_credit_note_template";
			repository = Paths.get(projectDataDir, projectName, "billingtemplates", code).toString();
			template = new BillTemplate();
			template.setName(templateName);
			template.setCode(code);
			template.setGroup(getDefaultGroup());
			template.setSystemTemplate(true);
			template.setRepository(repository);
			template.setMainFile(invoice ? "invoice_main_report.jrxml" : "credit_note_main_report.jrxml");
		}
		else {
			repository = Paths.get(projectDataDir, projectName, "billingtemplates", template.getCode()).toString();
		}
		File file = new File(repository); 
		if (!file.exists()) {
			String dir = System.getProperty("user.dir");
			Path source = Path.of(dir, "templates", invoice ? "invoice" : "credit");
			try {
				log.trace("Copy : {} TO {}", source.toString(), repository);
				copyDirectory(source.toString(), repository);
				log.debug("Default template files copied : {}", repository);
			} catch (Exception e) {
				log.error("Unable to copy default template : ", e);
				throw new BcephalException("Unable to copy default template : " + e.getMessage(), e);
			}	
		}
		
		if(template != null) {
			if(template.getId() == null) {
				for (BillingTemplateLabel label : getTemplateLabels(template)) {
					template.getLabelListChangeHandler().addNew(label);
				}
				ListChangeHandler<BillingTemplateLabel> labels = template.getLabelListChangeHandler();			
				template = billTemplateRepository.save(template);
				saveBillingTemplateLabel(labels, template);
			}
			if (parameter == null) {
				parameter = new Parameter(paramCode, ParameterType.BILL_TEMPLATE);
			}
			parameter.setLongValue(template.getId());
			parameterRepository.save(parameter);
		}		
		return template;
	}
	
	private void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws Exception {
		File file = new File(destinationDirectoryLocation); 
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
	    Files.walk(Paths.get(sourceDirectoryLocation))
	      .forEach(source -> {
	          Path destination = Paths.get(destinationDirectoryLocation, source.toString()
	            .substring(sourceDirectoryLocation.length()));
	          try {
	              Files.copy(source, destination);
	          } catch (IOException e) {
	              e.printStackTrace();
	          }
	      });
	}
	
	private boolean deleteDirectory(File directoryToBeDeleted) throws Exception {
		File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	@Override
	protected BillTemplate getCopy(BillTemplate item) {
		BillTemplate copy = item.copy();
		String dir = item.getRepository();		
		Path path = Paths.get(dir).getParent();		
		copy.setCode("" + System.currentTimeMillis());
		String repository = Paths.get(path.toString(), copy.getCode()).toString();
		copy.setRepository(repository);
		
		try {
			copyDirectory(dir, repository);
		} catch (Exception e) {
			log.error("Unable to copy Bill template : {}", item.getName(), e);
		}
		
		return copy;
	}
	
	public boolean changeLogo(Long id, String path, String projectName) {
		BillTemplate template = getById(id);
		if(template == null) {
			throw new BcephalException("Bill template not found :" + id);
		}
		
		if(!StringUtils.hasText(path)) {
			throw new BcephalException("Logo path is not setted!");
		}		
		
		String repository = Paths.get(projectDataDir, projectName, "billingtemplates", template.getCode()).toString();
		//template.setRepository(repository);
				
		if (StringUtils.hasText(path) && Paths.get(path).toFile().exists()) {
			try {
				if (Path.of(repository).toFile().exists()) {
					repository = Paths.get(repository, "logo.png").toString();
					FileUtil.copy(Path.of(path).toFile(), Path.of(repository).toFile());
					FileUtil.delete(Path.of(path).toFile());
				}				
				
			} catch (IOException e) {
				log.error("Unable to unzipfile {}", e);
			}
		}
		return true;
	}
	
	public BillTemplate save(BillTemplate template, Locale locale, String projectName) {
		String zipName = "template.zip";
		String tempDir = template.getRepository();
		if(!StringUtils.hasText(tempDir)) {
			throw new BcephalException("Repository is not setted!");
		}
		
		if(!StringUtils.hasText(template.getCode())) {
			template.setCode("" + System.currentTimeMillis());
		}
		String repository = Paths.get(projectDataDir, projectName, "billingtemplates", template.getCode()).toString();
		template.setRepository(repository);
		
		ListChangeHandler<BillingTemplateLabel> labels = template.getLabelListChangeHandler();
		int count = labels.getItems().size();	
		template = super.save(template, locale);
		BillTemplate id = template;
		if (StringUtils.hasText(tempDir) && Paths.get(tempDir, zipName).toFile().exists()) {
			try {
				if (Path.of(repository).toFile().exists()) {
					FileUtil.delete(Path.of(repository).toFile());
				}
				ZipFileUnZipUtil.unzipFile(Paths.get(tempDir, zipName), Path.of(repository));
				FileUtil.delete(Paths.get(tempDir, zipName).toFile());
				FileUtil.delete(Path.of(tempDir).toFile());
			} catch (IOException e) {
				log.error("Unable to unzipfile {}", e);
			}
		}
		if (count <= 0) {
			for (BillingTemplateLabel label : getTemplateLabels(template)) {
				labels.addNew(label);
			}
		}
		saveBillingTemplateLabel(labels, id);
		return template;
	}
	
	public BillTemplate save(BillTemplate template, Locale locale) {
		String tempDir = template.getRepository();
		if(!StringUtils.hasText(tempDir)) {
			throw new BcephalException("Repository is not setted!");
		}
		
		if(!StringUtils.hasText(template.getCode())) {
			template.setCode("" + System.currentTimeMillis());
		}
		
		ListChangeHandler<BillingTemplateLabel> labels = template.getLabelListChangeHandler();
		int count = labels.getItems().size();	
		template = super.save(template, locale);
		BillTemplate id = template;		
		if (count <= 0) {
			for (BillingTemplateLabel label : getTemplateLabels(template)) {
				labels.addNew(label);
			}
		}
		saveBillingTemplateLabel(labels, id);
		return template;
	}
	
	private void saveBillingTemplateLabel(ListChangeHandler<BillingTemplateLabel> labels, BillTemplate id) {
		labels.getNewItems().forEach(item -> {
			saveBillingTemplateLabel(item, id);
		});
		labels.getUpdatedItems().forEach(item -> {
			saveBillingTemplateLabel(item, id);
		});
		labels.getDeletedItems().forEach(item -> {
			deleteBillingTemplateLabel(item);
		});		
	}
	
	private void saveBillingTemplateLabel(BillingTemplateLabel label, BillTemplate id) {
		ListChangeHandler<BillingTemplateLabelValue> values = label.getValueListChangeHandler();		
		log.trace("Try to save billing template label value : {}", label);
		label.setBilling(id);
		label = billingTemplateLabelRepository.save(label);
		BillingTemplateLabel labelId = label;
		log.trace("Billing template label value saved : {}", label.getId());
		values.getNewItems().forEach(item -> {
			log.trace("Try to save billing template label value : {}", item);
			item.setLabel(labelId);
			billingTemplateLabelValueRepository.save(item);
			log.trace("Billing template label value saved : {}", item.getId());
		});
		values.getUpdatedItems().forEach(item -> {
			log.trace("Try to save billing template label value : {}", item);
			item.setLabel(labelId);
			billingTemplateLabelValueRepository.save(item);
			log.trace("Billing template label value saved : {}", item.getId());
		});
		values.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete billing template label value : {}", item);
				billingTemplateLabelValueRepository.deleteById(item.getId());
				log.trace("Billing template label value deleted : {}", item.getId());
			}
		});		
	}
	
	private void deleteBillingTemplateLabel(BillingTemplateLabel label) {
		ListChangeHandler<BillingTemplateLabelValue> values = label.getValueListChangeHandler();
		values.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete billing template label value : {}", item);
				billingTemplateLabelValueRepository.deleteById(item.getId());
				log.trace("Billing template label value deleted : {}", item.getId());
			}
		});
		values.getDeletedItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete billing template label value : {}", item);
				billingTemplateLabelValueRepository.deleteById(item.getId());
				log.trace("Billing template label value deleted : {}", item.getId());
			}
		});	
		if (label.getId() != null) {
			log.trace("Try to delete billing template label : {}", label);
			billingTemplateLabelRepository.deleteById(label.getId());
			log.trace("Billing template label deleted : {}", label.getId());
		}
	}
	
	@Override
	protected BillTemplate getNewItem() {
		BillTemplate billTemplate = new BillTemplate();
		String baseName = "BillTemplate ";
		int i = 1;
		billTemplate.setName(baseName + i);
		while(getByName(billTemplate.getName()) != null) {
			i++;
			billTemplate.setName(baseName + i);
		}
		return billTemplate;
	}
	
	
	@Override
	public void delete(BillTemplate billTemplate) {
		log.debug("Try to delete BillTemplate : {}", billTemplate);	
		if(billTemplate == null || billTemplate.getId() == null) {
			return;
		}
		
		ListChangeHandler<BillingTemplateLabel> labels = billTemplate.getLabelListChangeHandler();
		
		labels.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Billinglabel : {}", item);
				deleteBillingTemplateLabel(item);
				log.trace("Billinglabel deleted : {}", item.getId());
			}
		});
			
		getRepository().deleteById(billTemplate.getId());
		String repository = billTemplate.getRepository();		
		try {
			if (Files.exists(Path.of(repository))) {
				deleteDirectory(Path.of(repository).toFile());
			}			
		} catch (Exception e) {
			log.error("Unable to detete dir : {}", repository, e);
		}
		
		log.debug("Grid successfully to delete : {} ", billTemplate);
	    return;	
	}

	@Override
	protected BillTemplateBrowserData getNewBrowserData(BillTemplate item) {
		return new BillTemplateBrowserData(item);
	}

	@Override
	protected Specification<BillTemplate> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<BillTemplate> qBuilder = new RequestQueryBuilder<BillTemplate>(root, query, cb);
		    qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), 
		    		root.get("group"), root.get("visibleInShortcut"), 
		    		root.get("creationDate"), root.get("modificationDate"));	
		    if (filter != null && StringUtils.hasText(filter.getCriteria())) {
		    	qBuilder.addLikeCriteria("name", filter.getCriteria());
		    }
		    qBuilder.addNoTInObjectId(hidedObjectIds);
		    if(filter.getColumnFilters() != null) {
				qBuilder.addFilter(filter.getColumnFilters());
			}
	        return qBuilder.build();
		};
	}

	
	public List<BillingTemplateLabel> getTemplateLabels(BillTemplate template) {
		log.debug("Get the labels for the selected template.");
		
		List<BillingTemplateLabel> labels = new ArrayList<>();
//		BillTemplate template = billTemplateRepository.findByCode(templateCode);
//		if (template == null) {
//			return labels;
//		}
		
		Map<String, Properties> mapProperties = new HashMap<>();
		String baseName = bundleBasename + "{0}.properties";
		
		String dir = template.getRepository();
		String file = FilenameUtils.concat(dir, MessageFormat.format(baseName, ""));
		try {
			Properties defaultProp = readPropertiesFile(file);	
			for(String lg : locales) {
				String key = "_" + lg;
				file = FilenameUtils.concat(dir, MessageFormat.format(baseName, key));
				try {
					Properties prop = readPropertiesFile(file);
					mapProperties.put(lg, prop);
				}
				catch (Exception e) {
					mapProperties.put(lg, defaultProp);
				}
			}
			
			defaultProp.entrySet().parallelStream().forEach(e -> {							
				BillingTemplateLabel label = new BillingTemplateLabel();
				label.setCode((String)e.getKey());					
				locales.forEach(l -> {						
					BillingTemplateLabelValue val = new BillingTemplateLabelValue();
					val.setLocale(l);
					Properties prop = mapProperties.get(l);
					if(prop.containsKey(e.getKey())) {
						val.setValue(prop.getProperty((String)e.getKey()));	
					}
					else {
						val.setValue(defaultProp.getProperty((String)e.getKey()));
					}
					label.getValueListChangeHandler().addNew(val);
				});	
				labels.add(label);
			});
		} catch (Exception e1) {
			log.error(e1.getMessage());
		}
		
		return labels;
	}
	
	public Properties readPropertiesFile(String fileName) throws Exception {
      FileInputStream fis = null;
      Properties prop = null;
      try {
         fis = new FileInputStream(fileName);
         prop = new Properties();
         prop.load(new InputStreamReader(fis, StandardCharsets.UTF_8));
      } catch(Exception e) {
         log.error("Unable to read file : {}", fileName, e);
         throw e;
      } finally {
    	  if(fis != null) {
    		  fis.close();
    	  }
      }
      return prop;
   }

}
