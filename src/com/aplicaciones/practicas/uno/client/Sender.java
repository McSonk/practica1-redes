package com.aplicaciones.practicas.uno.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.aplicaciones.practicas.uno.MyFile;

/**
 * Clase que sirve para ejecutar todas las llamadas
 * necesarias con el servidor.
 * Esta clase est&aacute; pensada para ejecutarse en paralelo
 * (usa hilos)
 */
public class Sender implements Runnable {
	/**
	 * Objeto que representa la conexi&oacute;n con el
	 * servidor
	 */
	private Socket socket;
	/**
	 * Encapsulador del flujo de entrada
	 */
	private DataInputStream dis;
	/**
	 * Encapsulador del flujo de salida
	 */
	private DataOutputStream dos;
	/**
	 * Objeto que representa el archivo a enviar
	 */
	private File file;
	/**
	 * Objeto que lidiar&aacute; con todos los eventos
	 * disparados por la comunicaci&oacute;n con el servidor
	 */
	private ServerListener serverListener;
	
	/**
	 * Cierra la conexi&oacute;n con el servidor
	 * sin enviar ning&uacute;n dato
	 * @see Sender#close()
	 */
	public void abruptClose( ){
		//Verificamos si el socket es nulo
		if( socket == null ){
			//Si es nulo, significa que no se ha iniciado
			//ninguna conexion con el servidor.
			//Es decir, no hay nada que hacer...
			return;
		}
		
		try {
			//Escribimos un "-1" para 
			//indicarle al servidor que queremos
			//desconectarnos
			//(Nota: Esto es solo porque asi se definiio
			//en nuestro propio "protocolo", no es un estandar)
			dos.writeInt( -1 );
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			//Cerramos la conexion
			close( );
		}
	}
	
	/**
	 * Closes the streams and the socket itself.
	 * Please note that if this method is called without
	 * sending any file, will cause an {@link EOFException} 
	 * on the server. If you want to close the streams without sending
	 * files, use {@link #abruptClose()}
	 */
	public void close( ){
		if( socket != null ){
			try {
				dos.close( );
				dis.close( );
				socket.close( );
				
				socket = null;
				dis = null;
				dos = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public DataInputStream getDis() {
		return dis;
	}
	
	public DataOutputStream getDos() {
		return dos;
	}
	
	public File getFile() {
		return file;
	}
	/**
	 * get the host associated with the socket
	 * @return the host of the socket, or <code>null</code> 
	 * if the socket is <code>null</code>
	 */
	public String getHost( ){
		if( socket == null || socket.getInetAddress() == null ){
			return null;
		}
		return socket.getInetAddress( ).getHostAddress( );
	}
	
	/**
	 * M&eacute;todo in&uacute;til.
	 * @return
	 * @throws SocketException
	 */
	public boolean getKeepAlive() throws SocketException {
		return socket.getKeepAlive();
	}
	
	/**
	 * M&eacute;todo in&uacute;til.
	 * @return
	 * @throws SocketException
	 */
	public boolean getOOBInline() throws SocketException {
		return socket.getOOBInline();
	}
	
	/**
	 * Gets the port number associated with the socket.
	 * @return the port number, or -1 if the socket is 
	 * <code>null</code>
	 */
	public int getPort( ){
		if( socket == null ){
			return -1;
		}
		return socket.getPort( );
	}

	/**
	 * M&eacute;todo in&uacute;til.
	 * @return
	 * @throws SocketException
	 */
	public int getReceiveBufferSize() throws SocketException {
		return socket.getReceiveBufferSize();
	}


	/**
	 * M&eacute;todo in&uacute;til.
	 * @return
	 * @throws SocketException
	 */
	public int getSendBufferSize() throws SocketException {
		return socket.getSendBufferSize();
	}
	
	public ServerListener getServerListener() {
		return serverListener;
	}
	
	public Socket getSocket() {
		return socket;
	}

	/**
	 * M&eacute;todo in&uacute;til.
	 * @return
	 * @throws SocketException
	 */
	public int getSoLinger() throws SocketException {
		return socket.getSoLinger();
	}

	/**
	 * M&eacute;todo in&uacute;til.
	 * @return
	 * @throws SocketException
	 */
	public int getSoTimeout() throws SocketException {
		return socket.getSoTimeout();
	}

	/**
	 * M&eacute;todo in&uacute;til.
	 * @return
	 * @throws SocketException
	 */
	public boolean getTcpNoDelay() throws SocketException {
		return socket.getTcpNoDelay();
	}

	/**
	 * Initializes the connection between
	 * the client and the socket
	 * @param numOfFiles The total files to be sent
	 * @return <code>true</code> if the connection went ok
	 */
	public boolean init( int numOfFiles ){
		if( socket == null ){
			return false;
		}
		
		try {
			//En la primera conexion, enviamos el numero
			//de archivos a enviar
			dos.writeInt( numOfFiles );
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	/**
	 * Open the port to communicate
	 * @param host The host of the server
	 * @param port The port of the server
	 * @return <code>true</code> if the connection is successful
	 */
	public boolean open( String host, int port ){
		try {
			//abrimos la conexion...
			socket = new Socket( host, port );
			//E inicializamos los flujos...
			dis = new DataInputStream( socket.getInputStream() );
			dos = new DataOutputStream( socket.getOutputStream() );
		} catch (UnknownHostException e) {
			//Esta excepcion se ejecuta cuando el servidor no esta arriba o no existe
			System.err.println( "The host " + host + " are unknown"  );
			e.printStackTrace( );
			socket = null;
			return false;
		}catch( ConnectException e ){
			//Ocurre cuando hay algun problema en el servidor (por lo general, el puerto no esta abierto)
			System.err.println( "The host( " + host + " ) or the port( " + port + "  ) are unavailable" );
			e.printStackTrace( );
			return false;
		} catch (IOException e) {
			//Error general de Input/Output
			e.printStackTrace( );
			socket = null;
			return false;
		}
		
		return true;
	}

	/**
	 * Open and initializes the connection with the server
	 * @param host The host of the server
	 * @param port The port of the server
	 * @param numOfFiles The total amount of files to be sent
	 * @return <code>true</code> if the connection could be established 
	 * and initialized
	 */
	public boolean open( String host, int port, int numOfFiles ){
		return open( host, port ) && init( numOfFiles );
	}

	/**
	 * Metodo para ejecutarse en hilos
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		sendFile( file, serverListener );
	}

	/**
	 * Send a single file to the server
	 * @param file The file to be sent
	 * @param serverListener The listener to attach to the sender.
	 * @return <code>true</code> if the file could be successfully sent
	 */
	public boolean sendFile( File file, ServerListener serverListener ){
		//Flujo de lectura para leer el archivo
		FileInputStream fis = null;
		//arreglo temporal de bytes para almacenar
		//las porciones del archivo
		//La longitud de dichas porciones esta determinada por
		//SEGMENT_FILE_SIZE
		byte [] buffer;
		//"pivote" que indica en que posicion de byte
		//estamos dentro del archivo especificado
		int pointer = 0;
		//Numero total de partes que se enviaran del archivo
		//( largo del archivo / longitud del segmento )
		int parts;
		//Porcentaje del archivo que ya ha sido enviado al servidor.
		//El valor de esta variable se le envia al progressBar
		double step;
		//Porcion del porcentaje total del archivo que cada paquete (SEGMENT_FILE_SIZE)
		//tiene
		double stepSize;
		
		//Validacion: Si el socket es nulo (es decir, no se ha establecido conexion con el servidor)
		if( socket == null ){
			//Dispara el evento "Archivo rechazado"
			serverListener.onFileRejected( );
			return false;
		}
		
		//Si el tamaño del archivo excede el tamaño maximo permitido
		if( file.length() > Integer.MAX_VALUE ){
			//Dispara el evento "Largo no permitido"
			serverListener.onFileLengthNotAlowed( file.length() );
			return false;
		}
		
		try {
			//Inicializa el flujo de salida al servidor
			dos = new DataOutputStream( socket.getOutputStream( ) );
			//Inicializa el flujo de entrada al servidor
			dis = new DataInputStream( socket.getInputStream( ) );
			//Inicializa el flujo de entrada al archivo
			fis = new FileInputStream( file );
			
			//Cacular el numero de partes a enviar
			parts = (int)Math.ceil( (double)file.length( ) / (double)MyFile.SEGMENT_FILE_SIZE );
			//E iniciar en la primera
			parts++;
			//Primero enviaremos el largo del archivo
			dos.writeInt( (int)file.length( ) );
			//vaciamos buffer
			dos.flush( );
			//En segundo lugar, enviamos el nombre del archivo
			dos.writeUTF( file.getName( ) );
			//vaciamos buffer
			dos.flush( );
			
			//Iniciamos en el progreso "0 de 100 porciento completado"
			step = 0;
			//Analizamos el porcentaje del tamaño de cada paquete
			stepSize = (double)100 / (double)parts;
			//Finalmente, comenzamos con el envio del archivo
			//el valor de "pointer" sera "-1" si el archivo se ha enviado completamente
			//EN caso contrario, devolvera la posicion del ultimo byte enviado (> 0)
			while( pointer != -1 ){
				//Iniicamos el arreglo temporal de lectura
				buffer = new byte[MyFile.SEGMENT_FILE_SIZE];
				//leemos un dato del buffer de entrada desde el servidor
				//pero lo "tiramos a la basura"
				//esto es realmente solo para comprobar que la conexion con el servidor esta en orden
				dis.read( );
				//Lee "n" bytes del archivo
				//donde: n = SEGMENT_FILE_SIZE
				//Y guarda los bytes leidos en la variable "buffer"
				//Por otro lado, guarda la posicion del ultimo byte leido en "pointer"
				pointer = fis.read( buffer, 0, MyFile.SEGMENT_FILE_SIZE );
				//Escribe los bytes leidos en el servidor
				dos.write( buffer );
				dos.flush( );
				//incrementa el porcentaje de archivo enviado
				step += stepSize;
				//dispara el evento "Segmento enviado"
				serverListener.onFileSegmentSend( step,  100);
			}
			//cierra el flujo del archivo
			fis.close( );
			//Solo por si aun no se ha disparado en este punto
			//(digamos pues, que quedo en un 99.99999999999... )
			if( step != 100 ){
				//disparr el ultimo evento "Segmento enviado", con un 100
				serverListener.onFileSegmentSend( 100,  100);
			}
			//disparar el evento "Archivo enviado"
			serverListener.onFileCompleted( );
		} catch (IOException e) {
			e.printStackTrace();
			serverListener.onFileRejected( );
			return false;
		}
		
		return true;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	public void setFile(File file) {
		this.file = file;
	}
	public void setKeepAlive(boolean on) throws SocketException {
		socket.setKeepAlive(on);
	}
	public void setOOBInline(boolean on) throws SocketException {
		socket.setOOBInline(on);
	}
	public void setReceiveBufferSize(int size) throws SocketException {
		socket.setReceiveBufferSize(size);
	}
	public void setSendBufferSize(int size) throws SocketException {
		socket.setSendBufferSize(size);
	}
	public void setServerListener(ServerListener serverListener) {
		this.serverListener = serverListener;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public void setSoLinger(boolean on, int linger) throws SocketException {
		socket.setSoLinger(on, linger);
	}
	public void setSoTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}
	public void setTcpNoDelay(boolean on) throws SocketException {
		socket.setTcpNoDelay(on);
	}
}
