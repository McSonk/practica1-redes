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
import java.net.SocketOptions;
import java.net.UnknownHostException;

import com.aplicaciones.practicas.uno.MyFile;

/**
 * Class to deal with the sending process
 * to the server
 */
public class Sender implements Runnable {
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private File file;
	private ServerListener serverListener;
	
	/**
	 * Closes the socket without
	 * sending any file to the server
	 * @see Sender#close()
	 */
	public void abruptClose( ){
		if( socket == null ){
			return;
		}
		
		try {
			
			dos.writeInt( -1 );
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
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
	
	public boolean getKeepAlive() throws SocketException {
		return socket.getKeepAlive();
	}
	
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

	public int getReceiveBufferSize() throws SocketException {
		return socket.getReceiveBufferSize();
	}



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
     * Returns setting for {@link SocketOptions#SO_LINGER SO_LINGER}.
     * -1 returns implies that the
     * option is disabled.
     *
     * The setting only affects socket close.
     *
     * @return the setting for {@link SocketOptions#SO_LINGER SO_LINGER}.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     * @since   JDK1.1
     * @see #setSoLinger(boolean, int)
     */
	public int getSoLinger() throws SocketException {
		return socket.getSoLinger();
	}

	public int getSoTimeout() throws SocketException {
		return socket.getSoTimeout();
	}

	public boolean getTcpNoDelay() throws SocketException {
		return socket.getTcpNoDelay();
	}

	/**
	 * Initializes the connection between
	 * the client and the socket
	 * @param numOfFiles The total files to be sent
	 * @return
	 */
	public boolean init( int numOfFiles ){
		if( socket == null ){
			return false;
		}
		
		try {
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
			socket = new Socket( host, port );
			dis = new DataInputStream( socket.getInputStream() );
			dos = new DataOutputStream( socket.getOutputStream() );
		} catch (UnknownHostException e) {
			System.err.println( "The host " + host + " are unknown"  );
			e.printStackTrace( );
			socket = null;
			return false;
		}catch( ConnectException e ){
			System.err.println( "The host( " + host + " ) or the port( " + port + "  ) are unavailable" );
			e.printStackTrace( );
			return false;
		} catch (IOException e) {
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
		FileInputStream fis = null;
		byte [] buffer;
		int pointer = 0;
		int parts;
		double step;
		double stepSize;
		
		if( socket == null ){
			serverListener.onFileRejected( );
			return false;
		}
		
		if( file.length() > Integer.MAX_VALUE ){
			serverListener.onFileLengthNotAlowed( file.length() );
			return false;
		}
		
		try {
			dos = new DataOutputStream( socket.getOutputStream( ) );
			dis = new DataInputStream( socket.getInputStream( ) );
			fis = new FileInputStream( file );
			
			//calculate the numbers of parts of the file
			parts = (int)Math.ceil( (double)file.length( ) / (double)MyFile.SEGMENT_FILE_SIZE );
			parts++;
			//first send the length of the file
			dos.writeInt( (int)file.length( ) );
			dos.flush( );
			//then, send the name of the file
			dos.writeUTF( file.getName( ) );
			dos.flush( );
			
			step = 0;
			stepSize = (double)100 / (double)parts;
			//finally, send the file itself.
			while( pointer != -1 ){
				buffer = new byte[MyFile.SEGMENT_FILE_SIZE];
				
				dis.read( );
				
				pointer = fis.read( buffer, 0, MyFile.SEGMENT_FILE_SIZE );
				dos.write( buffer );
				dos.flush( );
				step += stepSize;
				serverListener.onFileSegmentSend( step,  100);
			}
			fis.close( );
			
			if( step != 100 ){
				serverListener.onFileSegmentSend( 100,  100);
			}
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
