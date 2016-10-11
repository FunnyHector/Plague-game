PLAGUE
THE POINT OF THE GAME
The winner of the game is the last player left alive.
In Plague each player starts out in randomised position in the main game area.
Each player is dying from a different virus, which causes their health to decline with time.
They must search for antidotes to temporarily raise their health again, to keep them alive for longer.
===========================================================================================================
STARTING THE GAME
1. Starting the server:
On the computer which will host the server, run the file Plague.jar file with the following command line command:
java -jar Plague.jar -server
A dialog will open requesting the number of players for the upcoming game.
Enter a number between 2 and 4 (inclusive).
2. Starting a client
Open Plague.jar using the command line command:
  java -jar Plague.jar -client
Press "Play".
3. Connecting the client to the server:
In the window which appears fill out the "Enter UserName" field with your name.
In the "Enter IP Address" field, enter the IP address of the computer hosting the server.
  This number can be found in the window that opens after the server starts, uner "Server Address".
  The number is 9 numbers long, with periods between them, in the order XXX.XXX.X.XXX
In the "Port Number " field, enter the port number from the server window also.
  This number is 4 digits long.
Press "Login"
4. A small window will open, giving the choice of "Ready" or "Leave".
  When you are ready to play, press ready.
  The game will start once the number of players specified in the server start-up are ready.
===========================================================================================================
CONTROLS
Action:		Key:
Move up		w
Move down	a
Move left	s
Move right	d
Turn left	q
Turn right	e
Get loot*	g
Unlock		f
Use door	r
Use item in slot 1	1
Use item in slot 2	2
Use item in slot 3	3
Use item in slot 4	4
Use item in slot 5	5
Use item in slot 6	6
Use item in slot 7	7
Use item in slot 8	8
*Gets all loot from a container. If the player does not have room in their inventory to carry all of the loot, then the
remaining loot is left in the container. Containers can be chests, scrap piles, or cupboards.
============================================================================================================
HOW TO PLAY
Each player must run around looking for antidotes to keep them alive.
Loot (items which you can find) can include Antidotes, Keys, Bags, and Torches. They are always found inside Chests, Scrap Piles,
and Cupboards.
Antidote:
  Antidotes each help heal a particular virus. If a player uses an antidote which matches their virus, the antidote will restore
  a portion of their health. Players can also use antidotes which cure other viruses. This has a high chance to hurt the player,
  but a small chance to give even more health than their correct antidote.
  You can use an antidote by rightclicking the antidote inventory icon, and selecting use.
  You can also use the item slot key (1-8) that corresponds with the inventory slot that which the item is in.
Key:
  A key will open a certain chest, cupboard or room door.
  To use a key, face a door, chest, or cupboard and press the unlock key (r). If the key was correct for the lock, then it wil be
  removed from the inventory.
Bag:
  A bag holds an antidote. Use the item slot key or right cick the item icon to fetch the antidote from the bag.
  The bag is destroyed when this happens.
Torch:
  At night (in the game) the player loses some visibility of the game world, and also in the minimap.
  By using a torch, the player can see in the dark.
  Torches can only be used for a limited time before running out.
  To use a torch right click the item in the inventory.
Finding Items:
  Any usable item is kept in a container.
  A player can get all loot from Scrap Piles, Chests, and Cupboards by using the getLoot key.
Putting things into containers:
  If a Scrap Pile, Chest, or Cupboard is empty, then right clicking an item icon and choosing insert, or pressing the corresponding
  item slot key will insert the item into the empty chest.
  This does not work for antidotes.
Other features:
  Above the inventory is a chat window, to talk to other players.
  To increase visibility of a section, any other section on the right side of the window can be collapsed.