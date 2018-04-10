package protocol;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerUnderlyingCommunicationControl {
	private DataOutputStream outgoingStream;
	private BufferedReader ingoingStream;
	public ServerUnderlyingCommunicationControl(DataOutputStream output, BufferedReader input) {
		outgoingStream=output;
		ingoingStream=input;
	}
	
	public void sendPacket(String packet) throws IOException {
		outgoingStream.writeBytes(packet);
	}
	
	public String receivePacket() throws IOException {
		return ingoingStream.readLine();
	}
}
