package com.aplicaciones.practicas.uno.client.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.aplicaciones.practicas.uno.MyFile;
import com.aplicaciones.practicas.uno.client.Sender;

public class SenderGUI extends JFrame implements MyFileListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1005583711426550968L;
	private JPanel contentPane;
	//private List<MyFile> files;
	private List<FilePane> filePanes;
	private JScrollPane jScrollPane;
	private JPanel filesPanel;
	private JLabel lblStatus;
	private Sender sender;
	private JButton btnSend;
	private JMenuItem mntmOpcionesSocket;
	private JMenuItem mntmSalir;
	/**
	 * Create the frame.
	 */
	public SenderGUI( Sender sender ) {
		this.sender = sender;
		
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setTitle( "Enviar archivos" );
		setBounds(100, 100, 450, 414);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);
		
		JMenuItem mntmCambiarServidor = new JMenuItem("Cambiar servidor...");
		mntmCambiarServidor.addActionListener( this );
		mnArchivo.add(mntmCambiarServidor);
		
		mntmOpcionesSocket = new JMenuItem("Opciones socket...");
		mntmOpcionesSocket.addActionListener( this );
		mnArchivo.add(mntmOpcionesSocket);
		
		mntmSalir = new JMenuItem("Salir");
		mntmSalir.addActionListener( new ActionListener( ) {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit( );
			}
		});
		mnArchivo.add(mntmSalir);
		
		contentPane = new JPanel( );
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane( contentPane );
		contentPane.setLayout( null );
		setResizable( false );
		
		//files = new ArrayList<MyFile>( );
		filePanes = new ArrayList<FilePane>( );
		
		JButton chooseBtn = new JButton("Agregar archivos...");
		chooseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getFiles( );
			}
		});
		
		chooseBtn.setBounds(115, 12, 188, 25);
		contentPane.add(chooseBtn);
		
		filesPanel = new JPanel();
		filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
		
		jScrollPane = new JScrollPane( filesPanel );
		jScrollPane.setBounds(35, 64, 363, 199);
		
		//contentPane.add(filesPanel);
		contentPane.add( jScrollPane );
		
		
		lblStatus = new JLabel( );
		changeLabel( "" );
		lblStatus.setBounds(12, 341, 386, 15);
		contentPane.add(lblStatus);
		
		btnSend = new JButton("Enviar");
		
		btnSend.addActionListener( new ActionListener( ) {
			public void actionPerformed(ActionEvent e) {
				sendFiles( );
			}
		} );
		
		btnSend.setBounds(155, 286, 117, 25);
		contentPane.add( btnSend );
		
		setVisible( true );
		addWindowListener( new WindowAdapter( ) {
			public void windowClosing(java.awt.event.WindowEvent e) {
				exit( );
			};
		} );
	}
	
	public void paintModel( ){
		filesPanel.removeAll( );
		/*for( MyFile myFile : files ){
			filesPanel.add( new FilePane( myFile, myFile.getId(), this ) );
		}*/
		for( FilePane filePane : filePanes ){
			filesPanel.add( filePane );
		}
		
		revalidate( );
		repaint( );
	}
	
	public void getFiles( ){
		File [] fileArray = null;
		JFileChooser chooser = new JFileChooser( );
		MyFile myFile = null;
		
		chooser.setMultiSelectionEnabled( true );
		if( chooser.showOpenDialog( null  ) == JFileChooser.APPROVE_OPTION ){
			fileArray = chooser.getSelectedFiles( );
			for( File file : fileArray ){
				if( file.length() >= Integer.MAX_VALUE ){
					System.out.println( "Skipping \"" + file.getName() + "\" because it size is too long." );
					continue;
				}
				//files.add( new MyFile( file ) );
				myFile = new MyFile( file );
				filePanes.add( new FilePane( myFile, myFile.getId( ), this  ) );
			}
			paintModel( );
		}
		
		
	}

	@Override
	public void onDelete(long index) {
		//Iterator<MyFile> iterator = files.iterator( );
		Iterator<FilePane> iterator = filePanes.iterator( );
		MyFile aux = null;
		
		while( iterator.hasNext( ) ){
			aux = iterator.next( ).getMyFile( );
			if( aux.getId( ) == index ){
				iterator.remove( );
				paintModel( );
				return;
			}
		}
		JOptionPane.showMessageDialog( null, "Hubo un error al eliminar el archivo", "Error", JOptionPane.ERROR_MESSAGE );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( ((JMenuItem)e.getSource() ).getText( ).equals( mntmOpcionesSocket.getText( ) ) ){
			new SocketFrame(sender,  this ).setVisible( true );
		}
		else{			
			new SelectHostFrame( sender, this ).setVisible( true );
		}
	}

	public void changeStatusLabel( Sender sender ){
		this.sender = sender;
		changeLabel( "" );
		repaint( );
	}
	
	private void sendFiles( ){
		Iterator<FilePane> iterator = null;
		
		if( filePanes == null || filePanes.isEmpty() ){
			JOptionPane.showMessageDialog( null, "No se ha elegido ningun archivo", "Imposible enviar", JOptionPane.WARNING_MESSAGE );
			return;
		}
		
		if( !sender.init( filePanes.size() ) ){
			JOptionPane.showMessageDialog( null, "No se pudo establecer la conexi\u00f3n", "Imposible enviar", JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		changeLabel( "Enviando archivos..." );
		
		iterator = filePanes.iterator( );
		new Thread( new SenderFile2( iterator, sender, this ) ).start( );
		filesPanel.setEnabled( false );
	}
	
	void changeLabel( String message ){
		lblStatus.setText( "Conectado a: " + sender.getHost( ) + ":" + sender.getPort() + ". " + message );
	}
	
	public void exit( ){
		sender.abruptClose( );
		System.exit( 0 );
	}
	
	public void setSender( Sender sender ){
		this.sender = sender;
	}
	
}
