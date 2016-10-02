package com.aplicaciones.practicas.uno.client.view;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import com.aplicaciones.practicas.uno.client.ServerListener;

public class ProgressBarFile implements ServerListener  {

	public static final String PROGRESS_BAR_ID = "fileProgress";
	private FilePane filePane;
	private JProgressBar progress;
	private JFrame frame;
	private JButton backup;
	
	public ProgressBarFile( FilePane filePane, JFrame frame ){
		this.filePane = filePane;
		this.frame = frame;
		Component component;
		
		component = getComponent( FilePane.DELETE_BTN_ID, filePane.getComponents() );
		if( component == null ){
			System.err.println( "Delete Component not found" );
			return;
		}
		
		progress = new JProgressBar( );
		progress.setName( PROGRESS_BAR_ID );
		backup = (JButton) component;
		filePane.remove( backup );
		progress.setMinimum( 1 );
		progress.setStringPainted( true );
		filePane.add( progress );
		
	}
	
	private Component getComponent( String name, Component[] components ){
		for( int i = 0; i < components.length; i++ ){
			if( name.equals( components[i].getName( ) ) ){
				return components[i];
			}
		}
		return null;
	}
	
	
	@Override
	public void onFileCompleted() {
		progress.setValue( 100 );
		filePane.setSent( true );
		repaint( );
	}

	@Override
	public void onFileSegmentSend(double portion, double total) {
		progress.setMaximum( (int)total );
		progress.setValue( (int) portion );
		repaint( );
	}
	
	public void repaint( ){
		frame.revalidate( );
		frame.repaint( );
	}

	@Override
	public void onFileLengthNotAlowed(long fileLength) {
		filePane.setSent( false );
	}

	@Override
	public void onFileRejected() {
		filePane.remove( getComponent(PROGRESS_BAR_ID, filePane.getComponents( ) ) );
		filePane.add( backup );
		filePane.setSent( false );
	}

}
