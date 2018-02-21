package com.via.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class JZip {
    public static final int NO_ERROR = 0;
    public static final int SOURCE_FILE_ERROR = 1;
    public static final int TARGET_DIR_ERROR = 2;
    public static final int PASSWORD_NOT_FOUND = 3;
    public static final int ZIP_INITIAL_ERROR = 4;
    public static final int SOURCE_CONTENT_ERROR = 5;
    public static final int EXTRACT_FILE_ERROR = 6;
    public static final int SOURCE_SHOULD_HAS_PASSWORD = 7;
    public static final int CREATE_FILE_ERROR = 8;
    
    public static int unzip(String srcZipFile, String specifiedFile, String targetDirectory, String passwd) {
        // Create and check source zip file
        File srcFile = new File(srcZipFile);
        if (!srcFile.exists() || srcFile.isDirectory()) {
            System.out.println("[JZip] Source file does not exist or correct.");
            return SOURCE_FILE_ERROR;
        }
        
        // Check target directory
        File destDir = new File(targetDirectory);
        destDir.mkdir();
        if (!destDir.isDirectory()) {
            System.out.println("[JZip] targetDirectory should be directory.");
            return TARGET_DIR_ERROR;
        }
        
        // Init and set password
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(srcZipFile);
            
            if (zipFile.isEncrypted()) {
                if (passwd != null && !passwd.isEmpty()) {
                    zipFile.setPassword(passwd.toCharArray());
                }
                else {
                    System.out.println("[JZip] No password indicated for a password-protected file.");
                    return PASSWORD_NOT_FOUND;
                }
            }
            else {
                System.out.println("[JZip] This zip file should has password but it doesn't.");
                return SOURCE_SHOULD_HAS_PASSWORD;
            }
        }
        catch (ZipException e) {
            System.out.println("[JZip] Create zip object or set password causes cexception.");
            return ZIP_INITIAL_ERROR;
        }
        
        // Check content and extract
        String targetFile = targetDirectory + (targetDirectory.endsWith(File.separator) ? "" : File.separator) + specifiedFile;
        try {
        	List<?> fileHeaderList = zipFile.getFileHeaders();
            for (int i = 0; i < fileHeaderList.size(); i++) {
            	FileHeader fileHeader = (FileHeader)fileHeaderList.get(i);
            	if (fileHeader != null && fileHeader.getFileName().equals(specifiedFile)) {
            		zipFile.extractFile(specifiedFile, targetDirectory);
            		break;
            	}
            }
            if (!new File(targetFile).isFile()) {
            	System.out.println("[JZip] Input zip file does not have correct content.");
            	return SOURCE_CONTENT_ERROR;
            }
        }
        catch (ZipException e) {
            System.out.println("[JZip] Extract zip file causes exception.");
            // It will extract a zero-size file when password is incorrect, should be deleted
            new File(targetFile).delete();
            return EXTRACT_FILE_ERROR;
        }
        
        return NO_ERROR;
    }
    
    public static int create(List<String> sourceFiles, String targetFile, String password) {
    	ArrayList<File> filesToAdd = new ArrayList<File>();
    	for (String name : sourceFiles) {
    		File file = new File(name);
    		if (!file.exists()) {
    			System.out.println("[JZip] Input file: " + name + " does not exist. process terminated.");
    			return SOURCE_FILE_ERROR;
    		}
    		
    		filesToAdd.add(file);
    	}
    	
    	ZipParameters parameters = new ZipParameters();
    	parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);				// set compression method to store compression
    	parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);		// set the compression level
    	parameters.setEncryptFiles(true);
    	parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);			// set the encryption method to Standard Zip Encryption
    	parameters.setPassword(password);
    	
		try {
			ZipFile zipFile = new ZipFile(targetFile);
			
			// Note: If the zip file already exists and if this zip file is a split file
			// then this method throws an exception as Zip Format Specification does not
			// allow updating split zip files
			zipFile.addFiles(filesToAdd, parameters);
		} catch (ZipException e) {
			e.printStackTrace();
			System.out.println("[JZip] create failed. " + e.getMessage());
			return CREATE_FILE_ERROR;
		}
    	
    	return NO_ERROR;
    }
    
    public static List<String> list(String srcZipFile) {
    	List<String> list = new ArrayList<String>();
    	
    	try {
			ZipFile zipFile = new ZipFile(srcZipFile);
			
			List<?> fileHeaderList = zipFile.getFileHeaders();
			for (int i = 0; i < fileHeaderList.size(); i++) {
				FileHeader fileHeader = (FileHeader)fileHeaderList.get(i);
				list.add(fileHeader.getFileName());
			}
		}
    	catch (ZipException e) {
    		System.out.println("[JZip] read file list from the zip failed. " + e.getMessage());
		}
    	
    	return list;
    }
    
    public static boolean exist(String srcZipFile, String specifiedFile) {
    	
    	try {
			ZipFile zipFile = new ZipFile(srcZipFile);
			
			List<?> fileHeaderList = zipFile.getFileHeaders();
			for (int i = 0; i < fileHeaderList.size(); i++) {
				FileHeader fileHeader = (FileHeader)fileHeaderList.get(i);
				if (fileHeader.getFileName().equals(specifiedFile)) return true;
			}
		}
    	catch (ZipException e) {
    		System.out.println("[JZip] read file list from the zip failed. " + e.getMessage());
		}
    	
    	return false;
    }
}
