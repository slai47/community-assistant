package com.utilities.storage;

import android.os.Environment;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;

public class OpenManager<T> {

	private Class<T> type;
	
	public OpenManager(Class<T> type) {
		this.type = type;
	}
	
	/**
	 * opens and reads a JSON object out from the file
	 * 
	 * @param file
	 * @return
	 */
	public T openObject(File file){
		T obj = null;
		try {
			FileReader fr = new FileReader(file);
			obj = new Gson().fromJson(fr, type);
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public T openObject(String fileName){
		File f = new File(fileName);
		T obj = openObject(f);
		return obj;
	}
	
	/**
	 * 
	 * 
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public T openObject(File dir, String fileName){
		T obj = null;
		File f = new File(dir, fileName);
		obj = openObject(f);
		return obj;
	}
	/**
	 * opens a object from external environment area 
	 * 
	 * @param tempDirectoryName
	 * @param fileName
	 * @return
	 */
	public T openObject(String tempDirectoryName, String fileName){
		T obj = null;
		File dir = new File(Environment.getExternalStorageDirectory(), tempDirectoryName);
		obj = openObject(dir, fileName);
		return obj;
	}
}
