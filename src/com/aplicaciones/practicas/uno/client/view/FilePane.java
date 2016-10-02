package com.aplicaciones.practicas.uno.client.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.DimensionUIResource;

import com.aplicaciones.practicas.uno.MyFile;

public class FilePane extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6444563907367342064L;
	public static final String DELETE_BTN_ID = "deleteBtn";
	
	private MyFile myFile;
	private boolean sent;
	

	public FilePane( MyFile myFile, long index, MyFileListener listener ){
		this.myFile = myFile;
		JButton deleteBtn = null;
		JSeparator separators[] = null;
		Dimension size = null;
		JLabel lblName = null;
		JLabel lblSize = null;
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
		
		separators = new JSeparator[2];
		size = new DimensionUIResource( new JSeparator( ).getPreferredSize().width, 30 );
		
		for( int i = 0; i < separators.length; i++ ){
			separators[i] = new JSeparator( JSeparator.VERTICAL );
			separators[i].setMaximumSize( size );
		}
		
		deleteBtn = new JButton( "Eliminar" );
		deleteBtn.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.onDelete( index );
			}
		});
		
		lblName = new JLabel( myFile.getName(), JLabel.LEFT );
		if( lblName.getText().length() > 25 ){
			lblName.setText( lblName.getText( ).substring( 0, 20 ) );
			lblName.setText( lblName.getText( ) + "..." );
			lblName.setText( lblName.getText( ) + myFile.getName( ).substring( myFile.getName( ).length() - 3, myFile.getName( ).length())  );
		}
		add( lblName );
		
		separators[0].setBounds( 155, 1, 5, 20 );
		add( separators[0] );
		
		lblSize = new JLabel( Integer.toString( myFile.getLength( ) ) );
		//lblSize.setBounds( 165, 1, 100, 20 );
		add( lblSize );
		
		add( separators[1] );
		add( new JLabel( "   " ) );
		deleteBtn.setName( DELETE_BTN_ID );
		add( deleteBtn );
	}
	
	public MyFile getMyFile( ){
		return myFile;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}
	
	

}
