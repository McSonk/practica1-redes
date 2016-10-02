package com.aplicaciones.practicas.uno.client.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.aplicaciones.practicas.uno.client.Sender;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SelectHostFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6255764006514105484L;
	private JPanel contentPane;
	private JTextField tfAddress;
	private JTextField tfPort;
	private SenderGUI senderGui;
	private Sender sender;
	
	public SelectHostFrame( String host, int port ){
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setTitle("Datos del servidor");
		setBounds(100, 100, 371, 163);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblDireccinIp = new JLabel("Dirección IP");
		lblDireccinIp.setBounds(54, 31, 82, 15);
		contentPane.add(lblDireccinIp);
		
		tfAddress = new JTextField( );
		tfAddress.setBounds(165, 29, 114, 19);
		tfAddress.setText( host );
		contentPane.add(tfAddress);
		tfAddress.setColumns(10);
		
		JLabel lblPuerto = new JLabel("Puerto");
		lblPuerto.setBounds(88, 73, 48, 15);
		contentPane.add(lblPuerto);
		
		tfPort = new JTextField();
		tfPort.setBounds(165, 71, 114, 19);
		tfPort.setText( Integer.toString( port ) );
		contentPane.add(tfPort);
		tfPort.setColumns(10);
		
		JButton btnAccept = new JButton("Aceptar");
		btnAccept.addActionListener( this );
		btnAccept.setBounds(109, 101, 117, 25);
		contentPane.add(btnAccept);
	}
	
	
	public SelectHostFrame( Sender sender, SenderGUI senderGui ) {
		this( sender.getHost(), sender.getPort( ) );
		this.senderGui = senderGui;
		this.sender = sender;
		senderGui.setEnabled( false );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if( sender != null ){
			sender.abruptClose( );
		}
		
		sender = new Sender( );
		
		if( !sender.open( tfAddress.getText( ), Integer.parseInt( tfPort.getText( ) ) ) ){
			JOptionPane.showMessageDialog( null, "No se pudo establecer la conexi\u00f3n", "Error", JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		if( senderGui == null )
			new SenderGUI( sender );
		else{
			senderGui.setEnabled( true );
			senderGui.changeStatusLabel( sender );
			senderGui.repaint( );
		}
		
		super.setVisible( false );
	}
}
