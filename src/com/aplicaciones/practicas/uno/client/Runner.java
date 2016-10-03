package com.aplicaciones.practicas.uno.client;

import com.aplicaciones.practicas.uno.client.view.SelectHostFrame;

/**
 * Clase con el &uacute;nico prop&oacute;sito de ejecutar
 * la aplicaci&oacute;n.
 * <b>No es un hilo</b>
 * @author sonk
 *
 */
public class Runner {
	/**
	 * Direccion IP a la que se conectara
	 */
	
	private static final String HOST = "127.0.0.1";
	/**
	 * Puerto por el cual iniciar la conexion
	 */
	private static final int PORT = 8580;

	public static void main(String[] args) {
		//Al llamar a la clase con un "new", se ejecuta el
		//constructor. Como no queremos hacer nada con la ventanita despues,
		//no es necesario asignarla a una variable.
		//"setVisible" es un metodo de JFrame para hacer visible la ventanita
		//(por default, este atributo esta en "false", con lo que no se ve nada).
		new SelectHostFrame( HOST, PORT ).setVisible( true );
	}
}
