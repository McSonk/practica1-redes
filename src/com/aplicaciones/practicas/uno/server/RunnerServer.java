package com.aplicaciones.practicas.uno.server;

import com.aplicaciones.practicas.uno.MyFile;

/**
 * Clase para iniciar el servidor.
 * <b>No es un hilo</b>
 * @author sonk
 *
 */
public class RunnerServer {
	/**
	 * Puerto "por default" para escuchar las conexiones
	 */
	private static final int DEFAULT_PORT = 8580;
	/**
	 * Direccion "por default" donde se guardaran los archivos
	 * (Cambiar este parametro si el proyecto se instala en otra computadora)
	 */
	private static final String DEFAULT_SAVE_LOCATION = "/home/sonk/Desktop/server_files/";
	/**
	 * Objeto para lidiar con las cosas enviadas por el cliente.
	 * Es est&aacute;tico s&oacute;lo para poder ejecutarlo desde el <code>main</code>
	 */
	private static Receiver receiver;
	
	/**
	 * Agrega un "shutdownHook". Esto es, agrega un "escucha"
	 * al flujo principal del programa. Leer la descripcion abajo, en el 
	 * metodo "run"
	 */
	private static void addShutdownHook( ){
		Runtime.getRuntime().addShutdownHook( new Thread( ){
			/**
			 * Este metodo es para ejecutarse justo antes de que el programa muera.
			 * Por ejemplo, si se inserta la combinacion de teclas <ctrl + C> (terminar programa), 
			 * o si se envia una señal de "MUERTE" por medio de un comando como "kill -9".
			 * Lo unico que hace es asegurarse de que la conexion con los clientes se cierre
			 * correctamente antes de terminar la ejecucion del programa.
			 */
			public void run( ){
				receiver.close( );
			}
		});
	}
	
	public static void main( String args[] ){
		//Arreglo para guardar todos los archivos que se van a recibir
		MyFile [] files = null;
		//Puerto en el cual ejecutar el servidor
		int port;
		//inicializamos el servidor
		receiver = new Receiver( );
		//Inicializamos el manenejador de archivos
		MyFileHandler myFileHandler = new MyFileHandler( );
		//inicializamos el puerto
		port = DEFAULT_PORT;
		//informamos del puerto que se esta usando
		System.out.println( "using port " + port);
		//Inicializamos el servidor
		if( !receiver.init( port ) ){
			//si el servidor tuvo un fallo, lo informamos
			System.out.println( "Due to previous errors, the server couldn't start" );
			//y detenemos la ejecucion
			return;
		}
		
		//Agrega el escucha al flujo del programa
		addShutdownHook( );
		
		//While infinito (El programa se ejecutara por siempre, hasta que se 
		//interrumpa por medio de un <ctrl + C> o algo asi
		while( true ){
			//Recibir los archivos que vengan del cliente
			files = receiver.getFiles( );
			if( files == null ){
				//si no se recibio ningun archivo,
				//volver al inicio del "loop" (volver a la linea 74)
				continue;
			}
			//"for" para recorrer uno por uno todos los archivos enviados
			for( int i = 0; i < files.length; i++ ){
				//Intenta guardar el archivo en la posicion "i", en la ubicacion especificada
				if( myFileHandler.saveFile( files[i], DEFAULT_SAVE_LOCATION ) ){
					//Si si se pudo, informarlo al usuario
					System.out.println( "\"" + files[i].getName() + "\" successfully saved in " + DEFAULT_SAVE_LOCATION );
				}
				else{
					//sino, tambien informarlo
					System.out.println( "The file \"" + files[i].getName( ) + "\" couldn't be saved. Is the path (" + 
							DEFAULT_SAVE_LOCATION + ") accessible?" );
				}
			}
			
		}
	}
	
}
