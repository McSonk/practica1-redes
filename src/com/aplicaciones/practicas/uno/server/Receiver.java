package com.aplicaciones.practicas.uno.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.aplicaciones.practicas.uno.MyFile;

public class Receiver {
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
		Socket socket = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		MyFile [] myFiles = null;
		int numOfFiles;
		
		if( serverSocket == null ){
			System.out.println( "The server hasn't been initialized" );
			return null;
		}
		
		try {
			System.out.println( "waiting for connection..." );
			socket = serverSocket.accept( );
			System.out.println( "Connection accepted from " + socket.getInetAddress( ).getHostAddress( ) );
			dis = new DataInputStream( socket.getInputStream( ) );
			dos = new DataOutputStream( socket.getOutputStream( ) );
			
			//Request the number of files being transfered...
			numOfFiles = dis.readInt( );
			if( numOfFiles == -1 ){
				System.out.println( "The connection has finished");
				return null;
			}
			System.out.println( "Receiving " + numOfFiles + " file(s) from client" );
			myFiles = new MyFile[numOfFiles];
			
			for( int i = 0; i < numOfFiles; i++ ){
				myFiles[i] = getFile( dis, dos );
			}
			dis.close( );
			socket.close( );
			
		} catch (IOException e) {
			e.printStackTrace( );
			return null;
		}
		return myFiles;
	}
	
	private MyFile getFile( DataInputStream dis, DataOutputStream dos ) throws IOException{
		MyFile myFile;
		int pointer = 0;
		int n = 0;
		
		myFile = new MyFile( );
		myFile.setLength( dis.readInt( ) );
		myFile.setName( dis.readUTF( ) );
		myFile.initBytes( );
		
		while( n != -1 ){
			if( (myFile.getBytes().length - pointer ) < MyFile.SEGMENT_FILE_SIZE ){
				break;
			}
			dos.write( 1 );
			dos.flush( );
			n = dis.read( myFile.getBytes( ), pointer, MyFile.SEGMENT_FILE_SIZE );
			pointer += n;
		}
		
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
