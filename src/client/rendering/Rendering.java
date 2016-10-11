package client.rendering;

import java.awt.Point;
import java.util.Map;
import client.view.GUI;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import server.game.player.Avatar;
import server.game.player.Direction;
import server.game.player.Position;
import javafx.scene.paint.ImagePattern;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;

/**
 * This class represents the main rendering class, this class will control the
 * rendering of the game board, character, and objects.
 *
 * @author Angelo Main Author (300323076)
 *
 */
public class Rendering {

	private int gamePaneHeight = GUI.HEIGHT_VALUE - 35;
	private int gamePanelWidth = GUI.GAMEPANE_WIDTH_VALUE - 1;
	private int tileWidth = 130;
	private int tileHeight = 50;
	private double imageOffset = 15;
	private double scale = 0.8;
	public double centerWidth = gamePanelWidth / 2;
	private int squaresInFront = 0;
	private int squaresToLeft = 0;
	private int squaresToRight = 0;
	private Pane renderGroup;
	private Label mapDescription;
	private ColorAdjust colorAdjust = new ColorAdjust();

	public Rendering() {
	}

	/**
	 * Renders the entire game, method gets called from the CLient UI class
	 * 
	 * @param playerLoc
	 * @param worldMap
	 * @param visibility
	 * @param uid
	 * @param avatars
	 * @param positions
	 * @param torchStatus
	 * @param b
	 */
	public void render(Position playerLoc, char[][] worldMap, int visibility, int uid, Map<Integer, Avatar> avatars,
			Map<Integer, Position> positions, Map<Integer, Boolean> torchStatus, int hourOfTime,
			Map<Integer, Boolean> isPlayerDead) {
		renderGroup.getChildren().clear();
		Direction direction = playerLoc.getDirection();
		Image background;
		Image grass;
		// For Night and Day Backgrounds
		if (hourOfTime >= 6 && hourOfTime < 18) {
			background = Images.DAYTIME_IMAGE;
			grass = Images.GRASS_IMAGE;
			if (worldMap.length < 30)
				grass = Images.ROOMTILE_IMAGE;
		} else {
			background = Images.NIGHTIME_IMAGE;
			grass = Images.GRASSNIGHT_IMAGE;
			if (worldMap.length < 30)
				grass = Images.ROOMTILE_IMAGE;
		}
		/////////////////////////////////
		addImage(renderGroup, background, gamePanelWidth, gamePaneHeight, 0, 0, hourOfTime);
		setNumSquares(worldMap.length, worldMap[0].length, direction, playerLoc, worldMap);
		double yTop = getTopOffset();
		double previousTileWidth = tileWidth * Math.pow(scale, squaresInFront);
		double xRightTop = centerWidth + previousTileWidth / 2;
		double xLeftTop = centerWidth - previousTileWidth / 2;
		// ===================================================================================================
		// Below this is point is the code for all the rendering for first
		// person
		// ====================================================================================================
		for (int row = 0; row < squaresInFront; row++) {
			Polygon squareFront = new Polygon();
			squareFront.setFill(new ImagePattern(grass));
			squareFront.setLayoutY(10);
			double currentTileWidth = tileWidth * Math.pow(scale, squaresInFront - row - 1);
			double currentTileHeight = tileHeight * Math.pow(scale, squaresInFront - row - 1);
			double xLeftBottom = centerWidth - currentTileWidth / 2;
			double yBottom = yTop + currentTileHeight;
			if (squaresInFront - row <= visibility + 1) {
				addTile(squareFront, xLeftTop, xRightTop, xLeftBottom + currentTileWidth, xLeftBottom, yBottom, yTop,
						renderGroup);
				if (direction.equals(Direction.North) || direction.equals(Direction.South)) {
					addObject(xLeftTop, yBottom, xRightTop, row, playerLoc.x, "middle", worldMap, renderGroup,
							direction, yTop, hourOfTime);
					addAvatar(xLeftTop, yBottom, xRightTop, row, playerLoc.x, "middle", worldMap, renderGroup,
							direction, yTop, avatars, positions, uid, torchStatus, hourOfTime, isPlayerDead);
				} else {
					addObject(xLeftTop, yBottom, xRightTop, row, playerLoc.y, "middle", worldMap, renderGroup,
							direction, yTop, hourOfTime);
					addAvatar(xLeftTop, yBottom, xRightTop, row, playerLoc.y, "middle", worldMap, renderGroup,
							direction, yTop, avatars, positions, uid, torchStatus, hourOfTime, isPlayerDead);
				}
				for (int col = squaresToLeft - 1; col >= 0; col--) {
					Polygon squareLeft = new Polygon();
					squareLeft.setLayoutY(10);
					squareLeft.setFill(new ImagePattern(grass));
					double tileXLeftTop = xLeftTop - previousTileWidth - (col * previousTileWidth);
					double tileXRightTop = xLeftTop - (col * previousTileWidth);
					double tileXRightBottom = xLeftBottom - col * currentTileWidth;
					double tileXLeftBottom = xLeftBottom - currentTileWidth - (col * currentTileWidth);
					if (tileXRightTop >= 0) {
						addTile(squareLeft, tileXLeftTop, tileXRightTop, tileXRightBottom, tileXLeftBottom, yBottom,
								yTop, renderGroup);
						addObject(tileXLeftTop, yBottom, tileXRightBottom, row, col, "left", worldMap, renderGroup,
								direction, yTop, hourOfTime);
						addAvatar(tileXLeftTop, yBottom, tileXRightBottom, row, col, "left", worldMap, renderGroup,
								direction, yTop, avatars, positions, uid, torchStatus, hourOfTime, isPlayerDead);
					}
				}
				for (int col = squaresToRight - 1; col >= 0; col--) {
					Polygon squareRight = new Polygon();
					squareRight.setFill(new ImagePattern(grass));
					squareRight.setLayoutY(10);
					double tileXLeftTop = xLeftTop + previousTileWidth + (col * previousTileWidth);
					double tileXRightTop = xLeftTop + (previousTileWidth * 2) + (col * previousTileWidth);
					double tileXRightBottom = xLeftBottom + (currentTileWidth * 2) + (col * currentTileWidth);
					double tileXLeftBottom = xLeftBottom + currentTileWidth + (col * currentTileWidth);
					if (tileXLeftTop >= 0) {
						addTile(squareRight, tileXLeftTop, tileXRightTop, tileXRightBottom, tileXLeftBottom, yBottom,
								yTop, renderGroup);
						addObject(tileXLeftBottom, yBottom, tileXRightTop, row, col, "right", worldMap, renderGroup,
								direction, yTop, hourOfTime);
						addAvatar(tileXLeftBottom, yBottom, tileXRightTop, row, col, "right", worldMap, renderGroup,
								direction, yTop, avatars, positions, uid, torchStatus, hourOfTime, isPlayerDead);
					}
				}
			}
			xLeftTop = xLeftBottom;
			xRightTop = xLeftBottom + currentTileWidth;
			yTop = yBottom;
			previousTileWidth = currentTileWidth;
		}
	}

	/**
	 * Calculates the offset
	 * 
	 * @return
	 */
	private double getTopOffset() {
		double count = 0;
		for (int i = 0; i < squaresInFront; i++)
			count += tileHeight * Math.pow(scale, squaresInFront - i - 1);
		return gamePaneHeight - count;
	}

	/**
	 * Creates the tiles / grass on the board via the provided parameters.
	 * 
	 * @param p
	 * @param xLeftTop
	 * @param xRightTop
	 * @param xRightBottom
	 * @param xLeftBottom
	 * @param yBottom
	 * @param yTop
	 * @param renderGroup
	 */
	private void addTile(Polygon p, double xLeftTop, double xRightTop, double xRightBottom, double xLeftBottom,
			double yBottom, double yTop, Pane renderGroup) {
		p.getPoints().add(xLeftTop);
		p.getPoints().add(yTop);
		p.getPoints().add(xRightTop);
		p.getPoints().add(yTop);
		p.getPoints().add(xRightBottom);
		p.getPoints().add(yBottom);
		p.getPoints().add(xLeftBottom);
		p.getPoints().add(yBottom);
		renderGroup.getChildren().add(p);
	}

	/**
	 * Calculates the number of squares to the front, left and right of the
	 * character
	 * 
	 * @param height
	 * @param width
	 * @param direction
	 * @param playerLoc
	 * @param map
	 */
	private void setNumSquares(int height, int width, Direction direction, Position playerLoc, char[][] map) {
		switch (direction) {
		case North:
			squaresInFront = playerLoc.y + 1;
			squaresToLeft = playerLoc.x;
			squaresToRight = width - playerLoc.x - 1;
			break;
		case East:
			squaresInFront = width - playerLoc.x;
			squaresToLeft = playerLoc.y;
			squaresToRight = height - playerLoc.y - 1;
			break;
		case South:
			squaresInFront = height - playerLoc.y;
			squaresToLeft = width - playerLoc.x - 1;
			squaresToRight = playerLoc.x;
			break;
		case West:
			squaresInFront = playerLoc.x + 1;
			squaresToLeft = height - playerLoc.y - 1;
			squaresToRight = (height - squaresToLeft) - 1;
			break;
		}
	}

	/**
	 * Renders the current character, and all other characters onto the board
	 * 
	 * @param xLeftTop
	 * @param yBottom
	 * @param xRightTop
	 * @param row
	 * @param x
	 * @param strings
	 * @param worldMap
	 * @param renderGroup2
	 * @param direction2
	 * @param yTop
	 * @param avatars
	 * @param positions
	 * @param uid
	 * @param torchStatus
	 * @param hourOfTime
	 * @param isPlayerDead
	 */
	public void addAvatar(double tileXLeftBottom, double yBottom, double tileXRightBottom, int row, int col,
			String side, char[][] worldMap, Pane renderGroup, Direction direction, double yTop,
			Map<Integer, Avatar> avatars, Map<Integer, Position> positions, int uid, Map<Integer, Boolean> torchStatus,
			int hourOfTime, Map<Integer, Boolean> isPlayerDead) {
		Image playerImg;
		Image otherAvatar;
		// Current player / contains torches
		Boolean currentPlayerNotDead = isPlayerDead.get(uid);
		if (currentPlayerNotDead)
			playerImg = Images.getAvatarImageBySide(avatars.get(uid), Side.Back, torchStatus.get(uid));
		else
			playerImg = Images.getDeadImageBySideMyself(avatars.get(uid), Side.Back);
		Point imageCoordinate = getImagePoint(direction, row, col, side, worldMap.length, worldMap[0].length);
		if (playerImg != null && positions.get(uid).x == imageCoordinate.x
				&& positions.get(uid).y == imageCoordinate.y) {
			double height = playerImg.getHeight() * Math.pow(scale, squaresInFront - row - 1);
			double width = playerImg.getWidth() * Math.pow(scale, squaresInFront - row - 1);
			double xPoint = getImageX(width, tileXLeftBottom, tileXRightBottom);
			double yPoint = getImageY(height, yBottom, yTop);
			addImage(renderGroup, playerImg, width, height, xPoint, yPoint + imageOffset, hourOfTime);
		}
		// Other players / contains torches
		for (Integer userID : positions.keySet()) {
			Position userPosition = positions.get(userID);
			Avatar avatarIDs = avatars.get(userID);
			int otherPlayerX = userPosition.x;
			int otherPlayerY = userPosition.y;
			if (imageCoordinate.x == otherPlayerX && imageCoordinate.y == otherPlayerY) {
				Boolean otherPlayersNotDead = isPlayerDead.get(userID);
				if (otherPlayersNotDead)
					otherAvatar = Images.getAvatarImageByDirection(avatarIDs, direction, userPosition.getDirection(),
							torchStatus.get(userID));
				else
					otherAvatar = Images.getDeadImageByDirectionOther(avatarIDs, direction,
							userPosition.getDirection());
				if (otherAvatar != null) {
					double height = otherAvatar.getHeight() * Math.pow(scale, squaresInFront - row - 1);
					double width = otherAvatar.getWidth() * Math.pow(scale, squaresInFront - row - 1);
					double xPoint = getImageX(width, tileXLeftBottom, tileXRightBottom);
					double yPoint = getImageY(height, yBottom, yTop);
					addImage(renderGroup, otherAvatar, width, height, xPoint, yPoint + imageOffset, hourOfTime);
				}
			}
		}
	}

	/**
	 * Given the provided arguments, Find a point in which to access the array,
	 * if it contains a image char, then pass that char into getImageFromChar
	 * which will then return an image object which can then be drawn
	 * 
	 * @param tileXLeftBottom
	 * @param yBottom
	 * @param tileXRightBottom
	 * @param row
	 * @param col
	 * @param side
	 * @param worldMap
	 * @param renderGroup
	 * @param direction
	 * @param yTop
	 * @param hourOfTime
	 * 
	 */
	private void addObject(double tileXLeftBottom, double yBottom, double tileXRightBottom, int row, int col,
			String side, char[][] worldMap, Pane renderGroup, Direction direction, double yTop, int hourOfTime) {
		Point imageCoordinate = getImagePoint(direction, row, col, side, worldMap.length, worldMap[0].length);
		char object = worldMap[imageCoordinate.y][imageCoordinate.x];
		Image image = getImageFromChar(object);
		if (image != null) {
			double height = image.getHeight() * Math.pow(scale, squaresInFront - row - 1);
			double width = image.getWidth() * Math.pow(scale, squaresInFront - row - 1);
			double xPoint = getImageX(width, tileXLeftBottom, tileXRightBottom);
			double yPoint = getImageY(height, yBottom, yTop);
			addImage(renderGroup, image, width, height, xPoint, yPoint + imageOffset, hourOfTime);
		}
	}

	/**
	 * Calculates the front, left, and right coordinates for finding char
	 * objects, creates a point and then return the newly created point
	 * 
	 * @author Dipen (Math Correction Bug Fix)
	 * 
	 * @param direction
	 * @param row
	 * @param col
	 * @param side
	 * @param boardHeight
	 * @param boardWidth
	 * @return
	 * 
	 */
	private Point getImagePoint(Direction direction, int row, int col, String side, int boardHeight, int boardWidth) {
		switch (direction) {
		case North:
			if (side.equals("left"))
				return new Point(squaresToLeft - col - 1, row);
			else if (side.equals("right"))
				return new Point(squaresToLeft + col + 1, row);
			else
				return new Point(col, row);
		case South:
			if (side.equals("left"))
				return new Point(boardWidth - (squaresToLeft - col), boardHeight - row - 1);
			else if (side.equals("right"))
				return new Point((squaresToRight - col) - 1, boardHeight - row - 1);
			else
				return new Point(col, boardHeight - row - 1);
		case East:
			if (side.equals("left"))
				return new Point(boardWidth - 1 - row, squaresToLeft - col - 1);
			else if (side.equals("right"))
				return new Point(boardWidth - 1 - row, squaresToLeft + col + 1);
			else
				return new Point(boardWidth - 1 - row, col);
		case West:
			if (side.equals("left"))
				return new Point(row, boardHeight - (squaresToLeft - col));
			else if (side.equals("right"))
				return new Point(row, squaresToRight - col - 1);
			else
				return new Point(row, col);
		}
		return null;
	}

	/**
	 * Provided the given character, match an image to it, and return the image
	 * object
	 * 
	 * @param input
	 * @return
	 */
	private Image getImageFromChar(char input) {
		return Images.MAP_OBJECT_IMAGES.get(input);
	}

	/**
	 * Get the X point of each image on the board, based on the rows and cols of
	 * the image location, and being relative to the players position
	 * 
	 * @param imageWidth
	 * @param tileXLeft
	 * @param tileXRight
	 * @return
	 */
	private double getImageX(double imageWidth, double tileXLeft, double tileXRight) {
		double tileWidth = tileXRight - tileXLeft;
		double widthOffset = Math.abs(tileWidth - imageWidth) / 2;
		if (tileWidth > imageWidth)
			return tileXLeft + widthOffset;
		else
			return tileXLeft - widthOffset;
	}

	/*
	 * Get the Y point of each image on the board, based on the rows and cols of
	 * the image location, and being relative to the players position
	 * 
	 * @param imageHeight
	 * 
	 * @param tileBottom
	 * 
	 * @param tileTop
	 * 
	 * @return
	 */
	private double getImageY(double imageHeight, double tileBottom, double tileTop) {
		double tileHeight = tileBottom - tileTop;
		double heightOffset = tileHeight / 2;
		return tileBottom - heightOffset - imageHeight;
	}

	/**
	 * Given the provided arguments, add an image into the renderGroup for it to
	 * be redrawn
	 * 
	 * @param renderGroup
	 * @param image
	 * @param width
	 * @param height
	 * @param setX
	 * @param setY
	 * @param hourOfTime
	 */
	private void addImage(Pane renderGroup, Image image, double width, double height, double setX, double setY,
			int hourOfTime) {
		ImageView imageView = new ImageView();
		double brightness = getImageBrightness(hourOfTime);
		changeImageBrightness(brightness);
		imageView.setImage(image);
		imageView.setFitHeight(height);
		imageView.setFitWidth(width);
		imageView.setX(setX);
		imageView.setY(setY);
		imageView.setEffect(colorAdjust);
		renderGroup.getChildren().add(imageView);
	}

	/**
	 * Sets the brightness of the game, this is based off the clock. Simulates
	 * both day and night!
	 * 
	 * @param clock
	 * @return
	 */
	private double getImageBrightness(int clock) {
		if (clock >= 11 && clock < 14)
			return 0;
		else if (clock >= 14 && clock < 17)
			return -0.1;
		else if (clock >= 18 && clock < 20)
			return -0.25;
		else if (clock >= 20 && clock < 23)
			return -0.4;
		else if (clock >= 23 && clock <= 24)
			return -0.5;
		else if (clock >= 0 && clock < 2)
			return -0.4;
		else if (clock >= 2 && clock < 5)
			return -0.3;
		else if (clock >= 5 && clock < 8)
			return -0.2;
		else if (clock >= 8 && clock < 11)
			return -0.1;
		return 0;
	}

	/**
	 * this method is used to create label which is used to display the area
	 * name.
	 * 
	 * @author Dipen Patel
	 */
	public void setAreaDescription() {
		mapDescription = new Label();
		mapDescription.setWrapText(true);
		mapDescription.setPrefWidth(100);
		mapDescription.setPrefHeight(100);
		mapDescription.setLayoutX(gamePanelWidth - 140);
		mapDescription.setLayoutY(30);
		mapDescription.getStyleClass().add("area-description");
	}

	/**
	 * this method is set area name
	 * 
	 * @param areaDescription
	 * @author Dipen Patel
	 */
	public void updateAreaDescription(String areaDescription) {
		mapDescription.setText(areaDescription);
		renderGroup.getChildren().add(mapDescription);
	}

	/**
	 * this method is used set brightness of image。
	 * 
	 * @param brightness
	 * @author Dipen Patel
	 */
	private void changeImageBrightness(double brightness) {
		colorAdjust.setBrightness(brightness);
	}

	/**
	 * Sets the pane to be renderGroup
	 * 
	 * @param renderGroup
	 * @author Dipen Patel
	 */
	public void setGroup(Pane renderGroup) {
		this.renderGroup = renderGroup;
	}

	/**
	 * Generic toString method.
	 */
	@Override
	public String toString() {
		return "renderclass";
	}

}
