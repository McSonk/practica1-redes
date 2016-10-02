package com.aplicaciones.practicas.uno.server;

import com.aplicaciones.practicas.uno.MyFile;

public class RunnerServer {
	private static final int DEFAULT_PORT = 8580;
	private static final String DEFAULT_SAVE_LOCATION = "/home/megumi/Escritorio/Holi_1/";
	private static Receiver receiver;
	
	private static void addShutdownHook( ){
		Runtime.getRuntime().addShutdownHook( new Thread( ){
			public void run( ){
				receiver.close( );
			}
		});
	}
	
	public static void main( String args[] ){
		MyFile [] files = null;
		int port;
		receiver = new Receiver( );
		MyFileHandler myFileHandler = new MyFileHandler( );
		
		if( args.length == 0 ){
			System.out.println( "No port received. Using default." );
			port = DEFAULT_PORT;
		}
		else{
			port = Integer.parseInt( args[0] );
		}
		
		System.out.println( "using port " + port);
		
		if( !receiver.init( port ) ){
			System.out.println( "Due to previous errors, the server couldn't start" );
			return;
		}
		
		addShutdownHook( );
		
		while( true ){
			files = receiver.getFiles( );
			if( files == null ){
				continue;
			}
			//until this, the process stops
			for( int i = 0; i < files.length; i++ ){				
				if( myFileHandler.saveFile( files[i], DEFAULT_SAVE_LOCATION ) ){
					System.out.println( "\"" + files[i].getName() + "\" successfully saved in " + DEFAULT_SAVE_LOCATION );
				}
				else{
					System.out.println( "The file \"" + files[i].getName( ) + "\" couldn't be saved. Is the path (" + 
							DEFAULT_SAVE_LOCATION + ") accessible?" );
				}
			}
			
		}
	}
	
}
