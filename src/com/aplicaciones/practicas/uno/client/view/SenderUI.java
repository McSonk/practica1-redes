package com.aplicaciones.practicas.uno.client.view;

import java.io.File;

import javax.swing.JFileChooser;

public class SenderUI {
	private JFileChooser chooser;
	
	public File [] getFiles( ){
		chooser = new JFileChooser( );
		chooser.setMultiSelectionEnabled( true );
		if( chooser.showOpenDialog( null  ) == JFileChooser.APPROVE_OPTION ){
			return chooser.getSelectedFiles( );			
		}
		
		return null;
	}
	
}
