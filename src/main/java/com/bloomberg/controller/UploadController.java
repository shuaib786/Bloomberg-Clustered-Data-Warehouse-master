package com.bloomberg.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bloomberg.model.CSVRecord;
import com.bloomberg.service.FileUploadService;
import com.bloomberg.util.Logger;
import com.opencsv.CSVReader;

/**
 * This class is the main controller for the application 
 *  
 *  @author Shuaib
 */

@Controller
public class UploadController {
	
    private final Logger logger = Logger.getLogger(UploadController.class);

	
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

	@Autowired
	FileUploadService service ;
	
	@Autowired
    private MessageSource messageSource;
	
    @GetMapping("/*")
	public String index() {

		logger.logInfo("Stepped into the index() method");

//		List<CSVRecord> list = new ArrayList<>();
//		for (int i = 0; i < 10; i++) {
//			CSVRecord dealDetails = new CSVRecord();
//			dealDetails.setAmount(new BigInteger("5"));
//			dealDetails.setDealDate(new Date());
//			dealDetails.setFromCurrency("PKR");
//			dealDetails.setToCurrency("USD");
//			list.add(dealDetails);
//		}
//		
//		service.saveValidData(list);
		
		
		return "fileUpload";
	}

    @PostMapping(value = "/upload") //new annotation since 4.3
    public String uploadFile(
            ModelMap model,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
    	
    	String METHOD_NAME = "uploadFile";
    	Instant start = Instant.now();
    	Map<String, String> messages = new HashMap<String, String>();
    	logger.logInfo(METHOD_NAME + "started");
        if (file.isEmpty()) {
        	messages.put("alert-danger", messageSource.getMessage("missing.file",null, Locale.getDefault()));
            model.put("messages", messages);
            logger.logInfo("Validation failed file is empty");
            return "fileUpload";
        }
        else if(service.checkFileExist(file.getOriginalFilename())){
        	messages.put("alert-danger", "File already exist");
            model.put("messages", messages);
        	logger.logInfo("File already exist");
        	return "fileUpload";
        }
        String fileName = file.getOriginalFilename();
        String rootPath = request.getSession().getServletContext().getRealPath("/");
        File dir = new File(rootPath + File.separator + "uploadedfile");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
     
        File serverFile = new File(dir.getAbsolutePath() + File.separator + (fileName));
        
        
        try {
            try (InputStream is = file.getInputStream();
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
                int i;
                //write file to server
                while ((i = is.read()) != -1) {
                    stream.write(i);
                }
                stream.flush();
            }
        } catch (IOException ex) {
            messages.put("alert-danger", messageSource.getMessage("failed.msg",new Object [] {ex}, Locale.getDefault()) );
            model.put("messages", messages);
            logger.LogException(METHOD_NAME + " crashed ", ex);
            return "fileUpload";
            
        }
        List<CSVRecord> validDeals = new ArrayList<>();
        List<CSVRecord> inValidDeals = new ArrayList<>();
        try {
        	logger.logInfo(" reading CSV file");
            for(String[] line:readCSVFile(serverFile, fileName)) {
                	CSVRecord csvRecord = extractData(line);
                	csvRecord.setFileName(fileName);
                	
                	if(StringUtils.isEmpty(csvRecord.getFromCurrency()) ||
                	StringUtils.isEmpty(csvRecord.getToCurrency()) ||
                	StringUtils.isEmpty(csvRecord.getDealDate()) ||
                	StringUtils.isEmpty(csvRecord.getAmount()) 
                	){
                		inValidDeals.add(csvRecord);
                	}
                	else{
                		validDeals.add(csvRecord);
                		
                	}
                
            }
            if(validDeals.size() > 0)
            	service.saveValidData(validDeals);
            if(inValidDeals.size() > 0)
            	service.saveInValidData(inValidDeals);
            
        } catch (Exception e) {
        	logger.LogException(METHOD_NAME + " crashed ", e);
        } 
        Instant end = Instant.now();
        messages.put("success", messageSource.getMessage("success.msg",new Object [] {file.getOriginalFilename() 
        				+"<br> Valid Deals Record is " + Integer.toString(validDeals.size()) 
        				+ " <br> Invalid Deal Records is " + Integer.toString(inValidDeals.size())
        				+" <br> Elapsed time is " + Duration.between(start, end)}, Locale.getDefault()) );
        model.put("messages", messages);
    	logger.logInfo(METHOD_NAME + " completed successfully!!!");
        return "fileUpload";
    }
    

    /**
     * This method extract the data and return the CVSRecord class 
     * @param line The line of CSV file.
     */
    CSVRecord extractData(String[] line){
    	
    	CSVRecord dealModel = new CSVRecord();
    	try {
    		dealModel.setToCurrency(line[0]);
        	dealModel.setFromCurrency(line[1]);
    		dealModel.setDealDate(formatter.parse(line[2]));
    		dealModel.setAmount(new BigInteger(line[3]));
		} catch (ParseException e) {
			logger.LogException("Exception while parsing CSV columns: ", e);
		}
    	return dealModel;
    }
    
    List<String[]> readCSVFile(File serverFile, String fileName){
    	String METHOD_NAME = "readCSVFile";
    	List<String[]> lines = null;
    	try {
            //read file
            //CSVReader(fileReader, ',', '\'', 1) means
            //using separator , and using single quote ' . Skip first line when read
        	logger.logInfo(" reading CSV file");
            	FileReader fileReader = new FileReader(serverFile);
                CSVReader reader = new CSVReader(fileReader, ',');
                lines = reader.readAll();

            	
            
        } catch (IOException e) {
        	logger.LogException(METHOD_NAME + " crashed ", e);
        } 
    	
    	return lines;
    }
    

}