package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import server.game.Game;
import server.game.player.Avatar;
import server.game.player.Player;
import server.game.world.Area;

/**
 * This class represents a single thread that handles communication with a
 * connected client. It receives events from a client connection via a socket as
 * well as send information to the client about the current board state.
 *
 * @author Rafaela
 * @author Hector (Fang Zhao 300364061)
 *
 */
public class Receptionist extends Thread {

	/**
	 * The server
	 */
	private ServerMain server;

	/**
	 * The game instance running on server side.
	 */
	private Game game;

	/**
	 * User (client) id of this connection.
	 */
	private int uid;

	/**
	 * User (client) specified name of this connection.
	 */
	private String userName;

	/**
	 * The communicating socket.
	 */
	private final Socket socket;

	/**
	 * The download link from the client.
	 */
	private DataInputStream input;

	/**
	 * The upload link to the client
	 */
	private DataOutputStream output;

	/**
	 * A flag indicating whether this client is ready to enter game. If it's
	 * false, the server will keep this client in lobby waiting for all clients
	 * ready.
	 */
	private boolean isClientReady = false;

	/**
	 * A flag indicating whether the game is running or not.
	 */
	private boolean isGameRunning = false;

	/**
	 * Constructor. It also initialises the socket input and output.
	 *
	 * @param server
	 *            --- the server
	 * @param socket
	 *            --- the socket used to connect to client
	 * @param uid
	 *            --- the unique id of this client
	 * @param game
	 *            --- the game instance
	 */
	public Receptionist(ServerMain server, Socket socket, int uid, Game game) {
		this.server = server;
		this.game = game;
		this.socket = socket;
		this.uid = uid;

		// create the output and input stream.
		try {
			output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (IOException e) {
			System.err.println("Lost connection with client." + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Send the game world maps as strings to client, and tell the client his
	 * id.
	 */
	public void sendMapID() {

		try {
			// tell the client all maps
			Map<Integer, Area> areas = game.getAreas();
			for (Area map : areas.values()) {
				output.writeUTF(map.toString());
			}
			output.writeUTF("Fin");
			output.flush();

			// tell the client his uid
			output.writeInt(uid);
			output.flush();

		} catch (IOException e) {
			System.err.println("Lost connection with client." + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Receive from client about the player's name and avatar.
	 */
	public void receiveNameAvatar() {
		try {
			byte avatarIndex = input.readByte();
			String name = input.readUTF();
			this.userName = name;
			game.joinPlayer(new Player(uid, Avatar.get(avatarIndex), name));

			System.out.println("player uid: " + uid + ". avatar " + avatarIndex);
			System.out.println("initialisation done. joined player: " + uid + " with avatar " + avatarIndex);

		} catch (IOException e) {
			System.err.println("IO exception: " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Send the player's virus type to the client
	 */
	public void sendVirusType() {
		// tell the client his virus type
		int virusIndex = game.getPlayerVirus(uid).ordinal();
		try {
			output.writeInt(virusIndex);
			output.flush();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Send each player's avatar to the client.
	 */
	public void sendAvatars() {
		try {
			output.writeUTF(game.getAvatarsString());
			output.flush();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Is this client ready to enter game?
	 *
	 * @return --- true/false for yes/no
	 */
	public boolean isReady() {
		return isClientReady;
	}

	/**
	 * This method usually is called by server to tell the client ready to enter
	 * game.
	 */
	public void setGameRunning() {
		this.isGameRunning = true;
	}

	/**
	 * This method is called when the server Receptionist thread runs. It
	 * broadcast the pre-configuration informations like area maps, in-game
	 * player avatars, etc, and runs in loop to constantly broadcast the new
	 * game state.
	 */
	@Override
	public void run() {
		try {

			// wait until all users are ready
			while (true) {
				if (input.available() > 0) {
					Packet packet = Packet.fromByte(input.readByte());
					if (packet == Packet.Ready) {
						isClientReady = true;
						break;
					}
				}

				try {
					Thread.sleep(ServerMain.DEFAULT_BROADCAST_CLK_PERIOD);
				} catch (InterruptedException e) {
					// Should never happen
				}
			}

			// don't start the game until server tell us to start.
			// (when all clients are ready).
			while (true) {
				if (isGameRunning) {
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

			// last, let the receptionist constantly communicate with clients.
			while (isGameRunning) {
				// broadcast the game status.
				String str = gameToString();
				output.writeUTF(str);
				output.flush();

				// check if clients has requested anything
				if (input.available() > 0) {
					// read input from client
					byte b = input.readByte();
					Packet packet = Packet.fromByte(b);

					// what did the client want?
					switch (packet) {
					case Forward:
						game.playerMoveForward(uid);
						break;
					case Backward:
						game.playerMoveBackward(uid);
						break;
					case Left:
						game.playerMoveLeft(uid);
						break;
					case Right:
						game.playerMoveRight(uid);
						break;
					case TurnLeft:
						game.playerTurnLeft(uid);
						break;
					case TurnRight:
						game.playerTurnRight(uid);
						break;
					case Transit:
						if (!game.playerTransit(uid)) {
							server.addNotification(uid, "Can't enter/exit the room");
						}
						break;
					case UseItem:
						int index_1 = input.readInt();
						game.playerUseItem(uid, index_1);
						break;
					case DestroyItem:
						int index_2 = input.readInt();
						game.playerDestroyItem(uid, index_2);
						break;
					case TakeOutItem:
						if (!game.playerTakeItemsFromContainer(uid)) {
							server.addNotification(uid, "T");
						}
						break;
					case PutItemIntoContainer:
						int index_3 = input.readInt();
						if (!game.playerPutItemIntoContainer(uid, index_3)) {
							server.addNotification(uid, "P");
						}
						break;
					case Unlock:
						if (!game.playerUnlockLockable(uid)) {
							server.addNotification(uid, "You don't have correct key.");
						}
						break;
					case Save:
						server.save(uid);
						break;
					case Load:
						server.load(uid);
						break;
					case Chat:
						String message = "[" + userName + "] " + input.readUTF();
						server.addMessage(message);
						break;
					case Disconnect:
						input.close();
						output.close();
						break;
					default:
						break;
					}
				}

				// a little nap
				Thread.sleep(ServerMain.DEFAULT_BROADCAST_CLK_PERIOD);
			}
		} catch (IOException e) {
			System.err.println("Player " + uid + " disconnected.");
		} catch (InterruptedException e) {
			System.err.println("Thread sleep interrupted. No big deal.");
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			game.disconnectPlayer(uid);
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("I/O error. But who cares, Clients disconnected anyway. ");
			}
		}
	}

	/**
	 * This method generates a String representation of the game status. The
	 * format of it is:
	 *
	 * <p>
	 * Mandatory informations:
	 * <li>1. Time
	 * <li>2. Health
	 * <li>3. Visibility
	 * <li>4. Positions of all players
	 * <li>5. The inventory of the player in this client
	 * <li>6. The status of player holding torch or not.
	 * <li>7. alive or not for all players.
	 * <li>8. win/loose
	 * 
	 * <p>
	 * optional informations:
	 * <li>9. Chat message if there is any.
	 * <li>10. notification message if there is any.
	 *
	 * <p>
	 * Each one of them is separated by a new line character '\n'. The format of
	 * each part should refer to
	 * {@link client.ParserUtilities #parseTime(String) parseTime},
	 * {@link client.ParserUtilities #parsePosition(java.util.Map, String)
	 * parsePosition}, {@link client.ParserUtilities #parseInventory(String)
	 * parseInventory},
	 * {@link client.ParserUtilities #parseTorchStatus(Map, String)
	 * parseTorchStatus}, and
	 * {@link client.ParserUtilities #parseAliveness(Map, String)
	 * parseAliveness}.
	 *
	 * 
	 * @return --- a String representation of the game status
	 */
	private String gameToString() {
		StringBuilder gameString = new StringBuilder();

		// 1. time
		String time = game.getClockString();
		gameString.append(time);
		gameString.append('\n');

		// 2. health
		int health = game.getPlayerHealth(uid);
		gameString.append(health);
		gameString.append('\n');

		// 3. visibility
		int visibility = game.getPlayerVisibility(uid);
		gameString.append(visibility);
		gameString.append('\n');

		// 4. positions of all players
		for (Player p : game.getPlayers().values()) {
			String s = p.getGeographicString();
			gameString.append(s);
			gameString.append('|');
		}
		gameString.append('\n');

		// 5. inventory
		String inventory = game.getPlayerInventoryString(uid);
		gameString.append(inventory);
		gameString.append('\n');

		// 6. holding torch or not for all players.
		String torchStatus = game.getTorchStatusString();
		gameString.append(torchStatus);
		gameString.append('\n');

		// 7. alive or not for all players
		String aliveStatus = game.getAliveString();
		gameString.append(aliveStatus);
		gameString.append('\n');

		// 8. win/loose
		String winStatus = game.getWinString();
		gameString.append(winStatus);
		gameString.append('\n');

		// ======= optional message broadcast =======

		// 9. chat message
		String message = server.retrieveMessage();
		if (message != null) {
			// prefix a 'M' for message
			gameString.append('M');
			gameString.append(message);
			gameString.append('\n');
		}

		// 10. notification
		String nMsg = server.retrieveNotification(uid);
		if (nMsg != null) {
			// prefix a 'N' for message
			gameString.append('N');
			gameString.append(nMsg);
			gameString.append('\n');
		}

		return gameString.toString();
	}

	/**
	 * This method is used when the server loads game back, the game instance
	 * gets re-referenced.
	 *
	 * @param game
	 *            --- the new game instance
	 */
	public void setGame(Game game) {
		this.game = game;
	}

}