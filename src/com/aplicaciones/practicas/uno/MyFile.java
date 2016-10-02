package com.aplicaciones.practicas.uno;

import java.io.File;
import java.util.Random;

/**
 * Class to save the details of the
 * file before and after sending it to the server.
 *
 */
public class MyFile {
	/**
	 * The file will be send to the server in little segments.
	 * This is the size of each segment.
	 */
	public static final int SEGMENT_FILE_SIZE = 1024;
	/**
	 * The actual length of the file.
	 * @see File#length()
	 */
	private int length;
	/**
	 * The actual content of the file
	 */
	private byte[] bytes;
	/**
	 * Internal id randomly generated.
	 */
	private long id;
	/**
	 * The name of the file
	 * @see File#getName()
	 */
	private String name;
	/**
	 * Auxiliary variable to save the {@link File} object. 
	 */
	private File file;
	
	public MyFile( ){}
	
	/**
	 * Creates an object and associates it to the
	 * given parameter. It fills the length and name
	 * variables, and generates a random id. 
	 * @param file
	 */
	public MyFile( File file ){
		this.file = file;
		length = (int)file.length();
		name = file.getName( );
		id = new Random( ).nextInt( 500 ) + System.currentTimeMillis( );
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	
	public File getFile( ){
		return file;
	}
	
	public long getId( ){
		return id;
	}
	
	/**
	 * The length of the file itself.
	 * This value don't necessary match with the
	 * size of the byte array.
	 */
	public int getLength() {
		return length;
	}
	public String getName() {
		return name;
	}
	
	/**
	 * Initializes the byte array with the necessary
	 * size based on the length of the file and the 
	 * {@link #SEGMENT_FILE_SIZE}
	 */
	public void initBytes( ){
		//rounds to the upper integer.
		int proportion = (int)Math.ceil( (double)length / (double)SEGMENT_FILE_SIZE );
		//The stream is written one more time (to write the EOF)
		proportion += 1;
		bytes = new byte[SEGMENT_FILE_SIZE * proportion ];
	}
	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "MyFile [name=" + name + ", length=" + length + "]";
	}
}
