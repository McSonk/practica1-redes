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
	 * The actual content of the file in a byte format
	 */
	private byte[] bytes;
	/**
	 * Internal id randomly generated.
	 * The only purpose of this ID Is to unique
	 * indentified the file in the Java SWING window
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
	
	/**
	 * Constructor sin par&aacute;metros.
	 * Necesario pero in&uacute;til.
	 */
	public MyFile( ){}
	
	/**
	 * Creates an object and associates it to the
	 * given parameter. It fills the length and name
	 * variables, and generates a random id. 
	 * @param file The file to be described
	 */
	public MyFile( File file ){
		//Setter
		this.file = file;
		//"Luego luego" obtiene el tamanio del archivo
		length = (int)file.length();
		//y tambien el nombre
		name = file.getName( );
		//Generamos un ID Unico.
		id = new Random( ).nextInt( 500 ) + System.currentTimeMillis( );
	}
	
	/**
	 * Getter
	 * @return El archivo en un formato de bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}
	
	/**
	 * Getter
	 * @return El archivo en objeto {@link File}
	 */
	public File getFile( ){
		return file;
	}
	
	public long getId( ){
		return id;
	}
	
	/**
	 * The length of the file itself.
	 * This value doesn't necessary match with the
	 * size of the byte array.
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * El nombre del archivo
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Initializes the byte array with the necessary
	 * size based on the length of the file and the 
	 * {@link #SEGMENT_FILE_SIZE}
	 */
	public void initBytes( ){
		//Con base en el tamaño del segmento (SEGMENT_FILE_SIZE), calculamos
		//cuantos segmentos enviaremos teniendo en cuenta el tamaño del archivo
		//Nota: Realizamos el casting individual de "length" y SEGMENT_FILE_SIZE
		//Por motivos descritos en las siguientes preguntas: 
		//http://stackoverflow.com/questions/787700/how-to-make-the-division-of-2-ints-produce-a-float-instead-of-another-int
		//http://stackoverflow.com/questions/3144610/integer-division-how-do-you-produce-a-double
		double segmentosAEnviar = ( (double) length / (double)SEGMENT_FILE_SIZE );
		
		//Luego de tener el numero de segmentos a enviar, redondeamos el numero obtenido
		//(que viene en formato decimal) para siempre obtener un calculo con porciones
		//de mas que de menos (Mejor que sobren cajitas de envio a que falten)
		int proportion = (int) Math.ceil( segmentosAEnviar );
		
		//Para prevenir que nos falte informacion (Java agrega datos para saber cuando acaba
		//de enviarse el archivo) agregamos una porcion extra
		proportion += 1;
		//Inicializamos el arreglo de bytes que contendra
		//a los datos del archivito, con base en los numeors
		//calculados
		bytes = new byte[SEGMENT_FILE_SIZE * proportion ];
	}
	
	
	
	public void setLength(int length) {
		this.length = length;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * M&eacute;todo generado por Eclipse, para obtener
	 * informaci&oacute;n relevante en un System.out.println( ) 
	 */
	@Override
	public String toString() {
		return "MyFile [name=" + name + ", length=" + length + "]";
	}
}
