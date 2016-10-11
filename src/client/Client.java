package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

import client.view.ClientUI;
import client.view.GUI;
import server.Packet;
import server.ServerMain;

/**
 * This class represents a single thread that handles communication with a
 * connected server. It receives events from the server connection via a socket
 * as well as send actions to the server about the player's command.
 *
 * @author Rafaela 300350087
 * @author Hector (Fang Zhao 300364061)
 *
 */
public class Client extends Thread {

	/**
	 * The period between every broadcast
	 */
	public static final int DEFAULT_UPDATE_CLK_PERIOD = 20;

	/**
	 * The pointer to the controller so we can let controller update renderer
	 * and GUI.
	 */
	private ClientUI controller;

	/**
	 * The socket connection with server
	 */
	private final Socket socket;

	/**
	 * The upload link to the server
	 */
	private DataOutputStream output;

	/**
	 * The download link from the server
	 */
	private DataInputStream input;

	/**
	 * This flag is used to indicate whether the user is ready to enter the
	 * game.
	 */
	private boolean isUserReady;

	/**
	 * This flag is used to indicate whether the game is running
	 */
	private boolean isGameRunning;

	/**
	 * Constructor. It also initialise the socket input and output.
	 *
	 * @param socket
	 *            --- the socket connecting the server
	 * @param controller
	 *            --- the controller
	 */
	public Client(Socket socket, ClientUI controller) {
		this.socket = socket;
		this.controller = controller;
		isUserReady = false;
		isGameRunning = false;

		// initialise the socket input and output
		try {
			output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (IOException e) {
			GUI.showMsgPane("Error", "I/O exceptions, connection ended.");
			e.printStackTrace();
		}
	}

	/**
	 * This method will send a packet to server.
	 *
	 * @param packet
	 *            --- the packet need to send
	 */
	public void send(Packet packet) {
		try {
			output.writeByte(packet.toByte());
			output.flush();
		} catch (IOException e) {
			GUI.showMsgPane("Error", "I/O exceptions, cannot send packet to server.");
			e.printStackTrace();
		}
	}

	/**
	 * This method will send a packet to server, with an integer followed. This
	 * integer is usually used as a index for special commands.
	 *
	 * @param packet
	 *            --- the packet need to send
	 * @param i
	 *            --- the index
	 */
	public void sendWithIndex(Packet packet, int i) {
		try {
			output.writeByte(packet.toByte());
			output.writeInt(i);
			output.flush();
		} catch (IOException e) {
			GUI.showMsgPane("Error", "I/O exceptions, cannot send packet to server.");
			e.printStackTrace();
		}
	}

	/**
	 * This method will send a packet to server, with an String followed. This
	 * string is usually used as extra message for special commands.
	 *
	 * @param packet
	 *            --- the packet need to send
	 * @param str
	 *            --- extra message
	 */
	public void sendWithString(Packet packet, String str) {
		try {
			output.writeByte(packet.toByte());
			output.writeUTF(str);
			output.flush();
		} catch (IOException e) {
			GUI.showMsgPane("Error", "I/O exceptions, " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Set ready to enter the game.
	 *
	 * @param isUserReady
	 */
	public void setUserReady(boolean isUserReady) {
		this.isUserReady = isUserReady;
	}

	/**
	 * This is a closing method, all cleaning process is done here.
	 */
	public void closeSocket() {
		// close the socket.
		try {
			socket.close();
		} catch (IOException e) {
			// shouldn't happen.
		}
	}

	/**
	 * This method is called when the client thread runs. It receives the
	 * pre-configuration informations from server, initialise the client side
	 * data, and runs in loop to constantly receive the server broadcast.
	 */
	@Override
	public void run() {
		try {
			// 1. receive from server about the maps
			String incoming = input.readUTF();
			while (!incoming.equals("Fin")) {
				controller.parseMap(incoming);
				System.out.println("[Debug] received map string:\n" + incoming);
				incoming = input.readUTF();
			}

			// 2. get the uId from server.
			int uid = input.readInt();
			controller.parseUID(uid);

			// 3. tell the server the avatar index and user name
			output.writeByte(controller.getAvatar().ordinal());
			output.flush();
			output.writeUTF(controller.getUserName());
			output.flush();

			// 4. get the virus type from server.
			int virusIndex = input.readInt();
			controller.parseVirus(virusIndex);

			// 5. get everybody's avatar
			String avatars = input.readUTF();
			controller.parseAvatars(avatars);

			// don't start the game until server tell us to start.
			// (when all clients are ready).
			while (true) {
				if (isUserReady) {
					output.writeByte(Packet.Ready.toByte());
					output.flush();
					break;
				}

				try {
					Thread.sleep(ServerMain.DEFAULT_BROADCAST_CLK_PERIOD);
				} catch (InterruptedException e) {
					// Should never happen
				}
			}

			// wait until the server tells us ready to begin
			while (true) {
				if (input.available() != 0) {

					Packet packet = Packet.fromByte(input.readByte());
					if (packet == Packet.Ready) {
						isGameRunning = true;
					}
				}

				if (isGameRunning && isUserReady) {
					break;
				}

				try {
					Thread.sleep(ServerMain.DEFAULT_BROADCAST_CLK_PERIOD);
				} catch (InterruptedException e) {
					// Should never happen
				}
			}

			// now start rendering the game interface
			controller.startGame();

			// last, let the client constantly get updated from server.
			while (isGameRunning) {
				if (input.available() > 0) {
					incoming = input.readUTF();
					stringToGame(incoming);

					// ======= [TESTING] ==========
					// printTestString(incoming);
				}

				try {
					Thread.sleep(DEFAULT_UPDATE_CLK_PERIOD);
				} catch (InterruptedException e) {
					// Should never happen
				}
			}
		} catch (IOException e) {
			GUI.showMsgPane("Error", "I/O Error, cannot receive packet to server.");
			e.printStackTrace();
		}

	}

	/**
	 * This method generates a String representation of the game status. The
	 * format of it is:
	 *
	 * <p>
	 * <li>Time
	 * <li>Health
	 * <li>Visibility
	 * <li>Positions of all players
	 * <li>The inventory of the player in this client
	 * <li>The status of player holding torch or not.
	 * <li>Is there a winner?
	 * <li>Chat message if there is any.
	 *
	 * <p>
	 * Each one of them is separated by a new line character '\n'. The format of
	 * each part should refer to
	 * {@link client.ParserUtilities #parseTime(String) parseTime},
	 * {@link client.ParserUtilities #parsePosition(java.util.Map, String)
	 * parsePosition}, {@link client.ParserUtilities #parseInventory(String)
	 * parseInventory}, and
	 * {@link client.ParserUtilities #parseTorchStatus(Map, String)
	 * parseTorchStatus}..
	 *
	 * @return --- a String representation of the game status
	 */
	private void stringToGame(String gameStr) {
		Scanner s = new Scanner(gameStr);
		String line;

		// 1. time
		if (s.hasNextLine()) {
			line = s.nextLine();
			controller.parseTime(line);
		} else {
			System.out.println("Data incomplete, no time received.");
			// Data is incomplete, ignore.
			s.close();
			return;
		}

		// 2. health
		if (s.hasNextLine()) {
			line = s.nextLine();
			try {
				controller.parseHealth(Integer.valueOf(line));
			} catch (NumberFormatException e) {
				// Data is incorrect, ignore.
				System.out.println("Data incorrect, health is not integer.");
				s.close();
				return;
			}
		} else {
			System.out.println("Data incomplete, no health received.");
			// Data is incomplete, ignore.
			s.close();
			return;
		}

		// 3. visibility
		if (s.hasNextLine()) {
			line = s.nextLine();
			try {
				controller.parseVisibility(Integer.valueOf(line));
			} catch (NumberFormatException e) {
				// Data is incorrect, ignore.
				System.out.println("Data incorrect, visibility is not integer.");
				s.close();
				return;
			}
		} else {
			System.out.println("Data incomplete, no visibility received.");
			// Data is incomplete, ignore.
			s.close();
			return;
		}

		// 4. all players' positions
		if (s.hasNextLine()) {
			line = s.nextLine();
			controller.parsePosition(line);
		} else {
			System.out.println("Data incomplete, no player's positions received.");
			// Data is incomplete, ignore.
			s.close();
			return;
		}

		// 5. inventory String
		if (s.hasNextLine()) {
			line = s.nextLine();
			controller.parseInventory(line);
		} else {
			System.out.println("Data incomplete, no inventory received.");
			// Data is incomplete, ignore.
			s.close();
			return;
		}

		// 6. holding torch or not for all players.
		if (s.hasNextLine()) {
			line = s.nextLine();
			controller.parseTorchStatus(line);
		} else {
			System.out.println("Data incomplete, no torch status received.");
			// Data is incomplete, ignore.
			s.close();
			return;
		}

		// 7. alive or not for all players
		if (s.hasNextLine()) {
			line = s.nextLine();
			controller.parseAliveStatus(line);
		} else {
			System.out.println("Data incomplete, no status for player's aliveness received.");
			// Data is incomplete, ignore.
			s.close();
			return;
		}

		// 8. win/loose
		if (s.hasNextLine()) {
			line = s.nextLine();
			String[] strs = line.split(",");
			boolean hasWinner = Integer.valueOf(strs[0]) == 1 ? true : false;

			if (hasWinner) {
				String winnerName = strs[1];
				isGameRunning = false;
				controller.gameOver("We have a winner", "The winner is " + winnerName);
			}

		} else {
			System.out.println("Data incomplete, no win/loose status received.");
			// Data is incomplete, ignore.
			s.close();
			return;
		}

		// ======= optional message broadcast =======

		while (s.hasNextLine()) {
			line = s.nextLine();

			if (line.startsWith("M")) {
				// 9. chat message
				controller.parseChatMessage(line.substring(1));
			} else if (line.startsWith("N")) {
				// 10. notification
				controller.parseNotificationMsg(line.substring(1));
			}
		}

		// close the scanner
		s.close();
	}

	/**
	 * A helper method for testing
	 *
	 * @param incoming
	 *            --- the string received from server
	 */
	@SuppressWarnings("unused")
	private void printTestString(String incoming) {
		Scanner sc = new Scanner(incoming);

		System.out.println("Time: " + sc.nextLine());
		System.out.println("Health: " + sc.nextLine());
		System.out.println("Visibility: " + sc.nextLine());
		System.out.println("Players: " + sc.nextLine());
		System.out.println("Inventory: " + sc.nextLine());
		System.out.println("Torch status: " + sc.nextLine());
		System.out.println("Aliveness status: " + sc.nextLine());
		System.out.println("Win/loose status: " + sc.nextLine());
		System.out.println("============Optional==============");

		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (sc.nextLine().startsWith("M")) {
				System.out.println("Chat msg: " + sc.nextLine());
			} else if (line.startsWith("N")) {
				System.out.println("notification: " + sc.nextLine());
			}

		}

		sc.close();
	}

}
