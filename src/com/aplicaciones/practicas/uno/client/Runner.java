package com.aplicaciones.practicas.uno.client;

import com.aplicaciones.practicas.uno.client.view.SelectHostFrame;

public class Runner {
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8580;

	public static void main(String[] args) {
		new SelectHostFrame( HOST, PORT ).setVisible( true );
	}
}
