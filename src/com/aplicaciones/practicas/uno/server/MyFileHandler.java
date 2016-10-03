package com.aplicaciones.practicas.uno.server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.aplicaciones.practicas.uno.MyFile;

/**
 * Clase para implementar los metodos relacionados con los archivitos
 * De momento solo tiene un metodo
 * @author sonk
 *
 */
public class MyFileHandler {
	/**
	 * M&eacute;todo para guardar un archivo en una ruta especificada.
	 * @param myFile Objeto con la informacion del archivo a guardar
	 * @param path Ubicacion donde se guardara el archivo
	 * @return <code>true</code> si el archivo se pudo guardar
	 */
	public boolean saveFile( MyFile myFile, String path ){
		FileOutputStream fos = null;
		//si el archivo es "Null"
		if( myFile == null ){
			//decirle NEL
			System.out.println( "Can't save null file" );
			return false;
		}
		
		try {
			//Inicializar el flujo de escritura
			fos = new FileOutputStream( path + myFile.getName( ) );
			//escribir
			fos.write( myFile.getBytes( ) );
			//y cerrar
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
