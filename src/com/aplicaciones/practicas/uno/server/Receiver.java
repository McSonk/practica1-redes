package com.aplicaciones.practicas.uno.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.aplicaciones.practicas.uno.MyFile;

/**
 * Clase para hacer todas las operaciones en las que esten implicadas
 * las conexiones con el cliente.
 * @author sonk
 *
 */
public class Receiver {
	/**
	 * Socket del servidor
	 */
	private ServerSocket serverSocket;
	
	/**
	 * Closes the connection of the {@link ServerSocket}. 
	 * Note that this method must be called when all the work
	 * relating with the server is done. If it's desired to 
	 * perform more operations, a call to {@link Receiver#init(int)}
	 * must be done.
	 */
	public void close( ){
		if( serverSocket == null ){
			System.out.println( "The server hasn't been initialized" );
		}
		try {
			serverSocket.close( );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Waits a connection and then retrieves
	 * the files sent by the client.
	 * @return The files, or <code>null</code> if there
	 * was an error or {@link Receiver#init(int)} hasn't been called
	 */
	public MyFile [] getFiles( ){
		//Representacion del cliente
		Socket socket = null;
		//Flujo de entrada desde cliente
		DataInputStream dis = null;
		//flujo de salida hacia el cliente
		DataOutputStream dos = null;
		//Archivos a recibir
		MyFile [] myFiles = null;
		//Numero de archivos a recibir
		int numOfFiles;
		
		//Si el servidor no se ha inicializado...
		if( serverSocket == null ){
			System.out.println( "The server hasn't been initialized" );
			return null;
		}
		
		try {
			System.out.println( "waiting for connection..." );
			//Comienza a estar a la escucha...
			//NOTA: este metodo bloqueara la ejecucion del programa hasta que
			//un cliente se conecte...
			socket = serverSocket.accept( );
			
			
			//informar del cliente que se acaba de conectar
			System.out.println( "Connection accepted from " + socket.getInetAddress( ).getHostAddress( ) );
			//Inicializar flujos...
			dis = new DataInputStream( socket.getInputStream( ) );
			dos = new DataOutputStream( socket.getOutputStream( ) );
			
			//El primer mensaje que envia el cliente es el numero de archivos a recibir.
			//Guardarlo....
			numOfFiles = dis.readInt( );
			//Si lo que se leyo es un "-1", quiere decir que el cliente
			//quiere finalizar la conexion
			if( numOfFiles == -1 ){
				//informar al usuario
				System.out.println( "The connection has finished");
				//y regresar "null"
				return null;
			}
			
			//si si se recibio un dato, mostrarlo al usuario...
			System.out.println( "Receiving " + numOfFiles + " file(s) from client" );
			//inicializar el arreglo de archivos con base en el numero de archivos a recibir
			myFiles = new MyFile[numOfFiles];
			//Ejecutar este comando "n" veces,
			//donde n = numero de archivos a recibir
			for( int i = 0; i < numOfFiles; i++ ){
				//ejecutar el metodo para recibir archivo,
				//Y guardarlo en el arreglo en la posicion "i"
				myFiles[i] = getFile( dis, dos );
			}
			//cerrar flujos...
			dis.close( );
			socket.close( );
			
		} catch (IOException e) {
			e.printStackTrace( );
			return null;
		}
		//regresar los archivos obtenidos
		return myFiles;
	}
	
	/**
	 * Obtiene un unico archivo de los enviados por el cliente
	 * @param dis Flujo de entrada desde el cliente
	 * @param dos Flujo de salida hacia el cliente
	 * @return El &uacute;ltimo archivo que fue enviado por el cliente
	 */
	private MyFile getFile( DataInputStream dis, DataOutputStream dos ) throws IOException{
		//Objeto para guardar los datos del archivo
		MyFile myFile;
		//para saber en que posicion del archivo a leer estamos
		int pointer = 0;
		//Numero de bytes leidos en cada "pasada"
		int n = 0;
		//Inicializamos el objeto que recibira el archivo
		myFile = new MyFile( );
		//El siguiente dato enviado por el cliente es el largo del archivo
		//Lo guardamos en nuestro objeto
		myFile.setLength( dis.readInt( ) );
		//Luego, el cliente envia el nombre del archivo
		//Lo guardamos en nuestro objeto
		myFile.setName( dis.readUTF( ) );
		//Con base en esos datos, podemos inicializar el arreglo
		//de bytes donde estara nuestro archivo
		myFile.initBytes( );
		
		//En "n" guardaremos el numero de bytes leidos en cada "pasada"
		//cuando "n" sea "-1", quiere decir que no hay nada mas que leer
		while( n != -1 ){
			//myFile.getBytes.length es la cantidad de bytes totales que ocupa el archivo a leer
			//pointer es el numero de bytes leidos del archivo
			//La operacion "myFile.getBytes( ).length - pointer" nos dara
			//la cantidad de bytes que nos falta por leer...
			//Si la cantidad de bytes por leer es menor a lo que ocupe un segmento
			//rompemos el ciclo
			//Esto es porque en este punto ya se ha leido todo el archivo
			//y si intentamos leer de nuevo, causara un error
			if( (myFile.getBytes().length - pointer ) < MyFile.SEGMENT_FILE_SIZE ){
				break;
			}
			//Escribimos un "1", solo para decirle al cliente que seguimos escuchando
			dos.write( 1 );
			dos.flush( );
			//Leemos la siguiente cantidad de bytes, a partir del numero especificado
			//por "pointer", y lo guardamos en "myFile.getBytes( )".
			//Guardamos la cantidad de bytes leidos en "n"
			n = dis.read( myFile.getBytes( ), pointer, MyFile.SEGMENT_FILE_SIZE );
			//agregamos a "pointer" la cantidad de bytes leidos
			pointer += n;
		}
		//En este punto, el archivo ya ha sido leido por completo
		//se lo informamos al usuario...
		System.out.println( "Received \"" + myFile.getName( ) + "\"" );
		return myFile;
	}
	
	/**
	 * Initializes the {@link ServerSocket}. This method
	 * must be called before another operation with the server.
	 * @param port The port in which the server will run
	 * @return <code>true</code> if there was no problem while
	 * initializing the server
	 */
	public boolean init( int port ){
		try {
			serverSocket = new ServerSocket( port );
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
