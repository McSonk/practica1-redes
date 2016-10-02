package com.aplicaciones.practicas.uno.server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.aplicaciones.practicas.uno.MyFile;

public class MyFileHandler {
	public boolean saveFile( MyFile myFile, String path ){
		FileOutputStream fos = null;
		
		if( myFile == null ){
			System.out.println( "Can't save null file" );
			return false;
		}
		
		try {
			fos = new FileOutputStream( path + myFile.getName( ) );
			fos.write( myFile.getBytes( ) );
			fos.close( );
		} catch (FileNotFoundException e) {
			System.err.println( "Couldn't write in the directory" );
			e.printStackTrace( );
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
