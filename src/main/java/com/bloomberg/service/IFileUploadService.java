package com.bloomberg.service;

import java.util.List;

import com.bloomberg.model.CSVRecord;
import com.bloomberg.model.DealModel;
import com.bloomberg.model.ValidDeal;
/**
 * @author shuaib
 *
 */
public interface IFileUploadService {

	void saveValidData(List<CSVRecord> dealDetails);
	
	void saveInValidData(List<CSVRecord> dealDetails);
	
	boolean checkFileExist(String fileName);

}
