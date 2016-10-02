package com.aplicaciones.practicas.uno.client.view;

import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.aplicaciones.practicas.uno.client.Sender;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SocketFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4932696153797140858L;
	private JPanel contentPane;
	private JTextField tfLinger;
	private JTextField tfReceiveBuffer;
	private JTextField txSendBuffer;
	private JTextField txTiimeOut;
	private JCheckBox chckbxNagle;
	private JCheckBox chckbxUrgent;
	private JCheckBox chckbxSoKeepAlive;
	private JCheckBox chckbxSoLin;
	private Sender sender;
	private SenderGUI senderGui;
	
	public SocketFrame( Sender sender, SenderGUI senderGui ) {
		this.senderGui = senderGui;
		senderGui.setEnabled( false );
		setTitle("Opciones de Socket");
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		setBounds(100, 100, 308, 282);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout( null );
		setContentPane(contentPane);
		try{
			chckbxNagle = new JCheckBox("NAGLE");
			chckbxNagle.setBounds(25, 8, 129, 23);
			chckbxNagle.setSelected( sender.getTcpNoDelay( ) );
			contentPane.add(chckbxNagle);
			
			this.sender = sender;
			
			chckbxUrgent = new JCheckBox("Paquetes urgentes");
			chckbxUrgent.setBounds(25, 35, 176, 23);
			chckbxUrgent.setSelected( sender.getOOBInline( ) );
			contentPane.add(chckbxUrgent);
			
			chckbxSoKeepAlive = new JCheckBox("SO_KEEPALIVE");
			chckbxSoKeepAlive.setBounds(25, 62, 176, 23);
			chckbxSoKeepAlive.setSelected( sender.getKeepAlive( ) );
			contentPane.add(chckbxSoKeepAlive);
			
			chckbxSoLin = new JCheckBox("SO_LINGER");
			chckbxSoLin.setSelected( sender.getSoLinger( ) != -1 );
			chckbxSoLin.setBounds(25, 89, 100, 23);
			contentPane.add(chckbxSoLin);
			
			tfLinger = new JTextField();
			tfLinger.setText( sender.getSoLinger() == -1 ? "" : Integer.toString( sender.getSoLinger( ) ) );
			tfLinger.setBounds(135, 91, 114, 19);
			contentPane.add(tfLinger);
			tfLinger.setColumns(10);
			
			JLabel lblSorcvbuf = new JLabel("SO_RCVBUF");
			lblSorcvbuf.setBounds(25, 120, 114, 15);
			contentPane.add(lblSorcvbuf);
			
			tfReceiveBuffer = new JTextField();
			tfReceiveBuffer.setBounds(135, 118, 114, 19);
			tfReceiveBuffer.setText( Integer.toString( sender.getReceiveBufferSize( ) ) );
			contentPane.add(tfReceiveBuffer);
			tfReceiveBuffer.setColumns(10);
			
			JLabel lblNewLabel = new JLabel("SO_SNDBUF");
			lblNewLabel.setBounds(25, 147, 114, 15);
			contentPane.add(lblNewLabel);
			
			txSendBuffer = new JTextField();
			txSendBuffer.setBounds(135, 147, 114, 19);
			txSendBuffer.setText( Integer.toString( sender.getSendBufferSize( )  ) );
			contentPane.add(txSendBuffer);
			txSendBuffer.setColumns(10);
			
			JLabel lblSotimeout = new JLabel("SO_TIMEOUT");
			lblSotimeout.setBounds(25, 174, 114, 15);
			contentPane.add(lblSotimeout);
			
			txTiimeOut = new JTextField();
			txTiimeOut.setBounds(135, 178, 114, 19);
			txTiimeOut.setText( Integer.toString( sender.getSoTimeout( ) ) ); 
			contentPane.add(txTiimeOut);
			txTiimeOut.setColumns(10);
			
			
			JButton btnNewButton = new JButton("Aceptar");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setStuff( );
					leave( );
				}
			});
			btnNewButton.setBounds(22, 220, 117, 25);
			contentPane.add(btnNewButton);
			
			JButton btnCancelar = new JButton("Cancelar");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					leave( );
				}
			});
			btnCancelar.setBounds(157, 220, 117, 25);
			contentPane.add(btnCancelar);
		}catch( SocketException ex ){
			JOptionPane.showMessageDialog( null , "Ha habido un error con el socket", "Error", JOptionPane.ERROR_MESSAGE );
			ex.printStackTrace();
		}
	}
	
	public void leave( ){
		senderGui.setSender( sender );
		senderGui.setEnabled( true );
		senderGui.setFocusable( true );
		super.setVisible( false );
	}
	
	public void setStuff( ){
		if( sender == null ){
			return;
		}
		try {
			
			sender.setReceiveBufferSize( Integer.parseInt( tfReceiveBuffer.getText( ) ) );
			sender.setSendBufferSize( Integer.parseInt( txSendBuffer.getText() ) );
			
			if( !tfLinger.getText().isEmpty() )
				sender.setSoLinger( chckbxSoLin.isSelected() , Integer.parseInt( tfLinger.getText() ) );
			
			if( !txTiimeOut.getText().isEmpty() )
				sender.setSoTimeout( Integer.parseInt( txTiimeOut.getText( ) ) );
			
			sender.setTcpNoDelay( chckbxNagle.isSelected( ) );
			sender.setKeepAlive( chckbxSoKeepAlive.isSelected( ) );
			sender.setOOBInline( chckbxUrgent.isSelected() );
			
		} catch (NumberFormatException | SocketException e) {
			e.printStackTrace();
		}
	}
}
