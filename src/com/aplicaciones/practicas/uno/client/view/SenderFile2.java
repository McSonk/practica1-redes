package com.aplicaciones.practicas.uno.client.view;

import java.util.Iterator;

import javax.swing.JOptionPane;

import com.aplicaciones.practicas.uno.client.Sender;

public class SenderFile2 implements Runnable {

	private Iterator<FilePane> iterator;
	private Sender sender;
	private SenderGUI senderGui;
	
	
	
	public SenderFile2(Iterator<FilePane> iterator, Sender sender, SenderGUI senderGui) {
		super();
		this.iterator = iterator;
		this.sender = sender;
		this.senderGui = senderGui;
	}

	public void sendFiles( ){
		int errors = 0;
		FilePane aux = null;
		String host;
		int port;
		
		while( iterator.hasNext() ){
			aux = iterator.next( );
			
			if( sender.sendFile( aux.getMyFile( ).getFile( ), new ProgressBarFile( aux, senderGui ) ) ){
				iterator.remove( );
			}
			else{
				errors++;
			}
		}	
		
		if( errors == 0 ){
			JOptionPane.showMessageDialog( null, "Se han enviado todos los archivos" , "Envio finalizado", JOptionPane.INFORMATION_MESSAGE );
		}
		else{
			JOptionPane.showMessageDialog( null, "Envio finalizado. " + errors + " archivo(s) no pudieron enviarse." , "Envio finalizado", JOptionPane.WARNING_MESSAGE );
		}
		
		senderGui.changeLabel( "Archivos enviados" );
		senderGui.paintModel( );
		host = sender.getHost();
		port = sender.getPort();
		System.out.println( "Closing sender...");
		sender.close( );
		System.out.println( "Openning sender..");
		sender.open( host, port );
		System.out.println( "done" );
	}
	
	@Override
	public void run() {
		sendFiles( );
	}

}
