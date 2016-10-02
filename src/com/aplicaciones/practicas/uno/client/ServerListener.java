package com.aplicaciones.practicas.uno.client;

/**
 * Use this class to control the
 * behaviour of the application in the
 * process of sending the file.
 */
public interface ServerListener {
	/**
	 * Executed when the file has been
	 * successfully sent to the server 
	 */
	public void onFileCompleted( );
	/**
	 * Executed whenever a segment of the file
	 * has been successfully sent to the server
	 * @param portion The number of portions that have to be
	 * send to the server
	 * @param total The total number of portions to send
	 */
	public void onFileSegmentSend( double portion, double total );
	/**
	 * When the length of the file is greater than
	 * the maximum allowed.
	 * @param fileLength The actual length of the file.
	 */
	public void onFileLengthNotAlowed( long fileLength );
	/**
	 * When a problem of the server occurred,
	 * causing an error on sending this file
	 */
	public void onFileRejected( );
}
