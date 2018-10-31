package com.utilities.storage;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.utilities.interfaces.ISave;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class SaveManager<T> {

	/**
	 * Saves an object to a file
	 * 
	 * if file doesn't exist it will create it
	 * 
	 * if obj instanceof ISave
	 * 		this will call readyForSave()
	 * 
	 * @param obj
	 * @param fileToSaveTo
	 * @return
	 */
	public boolean saveObject(T obj, File fileToSaveTo){
		boolean saved = false;
		try{
			if(!fileToSaveTo.exists()){
				fileToSaveTo.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(fileToSaveTo);
			PrintWriter pw = new PrintWriter(fos);
			if(obj instanceof ISave)
				((ISave) obj).readyForSave();
			
			pw.print(new Gson().toJson(obj));
			pw.flush();
			fos.close();
			saved = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return saved;
	}
	
	/**
	 * Saves an object to a file in given directory
	 * 
	 * if directory(s) don't exist it will create it or them
	 * 
	 * if file doesn't exist it will create it
	 * 
	 * @param obj - T object to save
	 * @param dir - directory of file
	 * @param fileName - filename to save to
	 * @return
	 */
	public boolean saveObject(T obj, File dir, String fileName){
		boolean saved = false;
		try {
			if(!dir.exists())
				dir.mkdirs();
			File file = new File(dir, fileName);
			saved = saveObject(obj, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return saved;
	}
	
	/**
	 * Saves an Object to a file with the given name in the given directory.
	 * 
	 * if file doesn't exist it will create it
	 * 
	 * @param obj
	 * @param directoryName
	 * @param fileName
	 * @return
	 */
	public boolean saveObject(T obj, String directoryName, String fileName){
		boolean saved = false;
		try {
			File dir = new File(directoryName);
			if(!dir.exists()){
				dir.mkdir();
			}else if(dir.isDirectory()){
				saved = saveObject(obj, dir, fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return saved;
	}
	
	/**
	 * Saves the obj to a file with the name given in to the directory that Context.getFileStreamPath(filename) gives you.
	 * 
	 * if file doesn't exist it will create it
	 * 
	 * @param obj
	 * @param tempFileName
	 * @return
	 */
	public boolean saveObject(T obj, Context ctx, String tempFileName){
		boolean saved = false;
		File fileToSaveTo = ctx.getFileStreamPath(tempFileName);
		saved = saveObject(obj, fileToSaveTo);
		return saved;
	}
	
	/**
	 * Saves in External Storage to a Directory
	 * 
	 * if file doesn't exist it will create it
	 * 
	 * @param obj
	 * @param nameOfFile
	 * @return
	 */
	public File saveTempFile(T obj, String tempDirectoryName, String nameOfFile){
		File temp = null;
		try {
			File dir = new File(Environment.getExternalStorageDirectory(), tempDirectoryName);
			dir.mkdirs();
			temp = new File(dir, nameOfFile);
			temp.createNewFile();
			saveObject(obj, temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
}
