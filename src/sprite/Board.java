package sprite;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

	private static final long serialVersionUID = 4940708456978284418L;

	private final int B_WIDTH = 1024;
	private final int B_HEIGHT = 320;

	private final int DOG_WIDTH = 64;
	private final int DOG_HEIGHT = 64;
	private final int STEP = 16;
	private final int DELAY = 70;
	private final int ORIGINAL_POS_Y = 200;
	
	private final int OBSTACLE_WIDTH = 34;
	private final int OBSTACLE_HEIGHT = 34;
	private static final double SCORE_INC = 0.5;
	
	private static final int SCORE_LENGTH = 5;
	private static final int NUMBER_WIDTH = 20;
	private static final int NUMBER_HEIGHT = 21;
	// here i calculate position of score on screen
	private final int CURRENT_SCORE_X = B_WIDTH - (SCORE_LENGTH * NUMBER_WIDTH + 50);
	private final int SCORE_Y = B_HEIGHT / 25;
	
	private Timer timer;
	private int IMAGE_COUNT = 8;
	private Image dogs[] = new Image[IMAGE_COUNT];

	private int jumped_count = 0;
	private int count = 0;
	private int pos_x = 100;
	private int pos_y = 200;
	private int index = 0;
	private boolean is_start = false;
	private boolean is_pass_obstacle = false;
	
	private int land_x = 0;
	private int land_y = ORIGINAL_POS_Y + DOG_HEIGHT;

	private int obstacle_x = 0;
	private int obstacle_y = ORIGINAL_POS_Y + DOG_HEIGHT - OBSTACLE_HEIGHT;
	private Image obstacle_image[] = new Image[3];
	private int ob_index = 0;

	private Image gameOverImage;
	private Image replayImage;
	private Image landImage;
	private Image scoreImage;
	private double score;
	
	private JLabel txt_description = new JLabel("Press SPACE keyboard.\nYou can start the game");

	public Board() {
		initBoard();
	}

	private void initBoard() {
		addKeyListener(new TAdapter());
		setFocusable(true);
		loadImages();
		locateObstacle();
		startGame();
		add(txt_description);
		this.getComponent(0).setBounds(200, 400, 500, 100);
	}

	private void loadImages() {
		try {
			gameOverImage = ImageIO.read(new File("src/resources/game-over.png"));
			replayImage = ImageIO.read(new File("src/resources/replay.png"));
			scoreImage = ImageIO.read(new File("src/resources/numbers.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		landImage = new ImageIcon("src/resources/land.png").getImage();
		for (int i = 0; i < 3; i++) {
			String path = "src/resources/ob" + i + ".png";
			ImageIcon imgIcon = new ImageIcon(path);
			obstacle_image[i] = imgIcon.getImage();
		}
		for (int i = 0; i < IMAGE_COUNT; i++) {
			String path = "src/resources/" + i + ".png";
			ImageIcon imgIcon = new ImageIcon(path);
			dogs[i] = imgIcon.getImage().getScaledInstance(DOG_WIDTH, DOG_HEIGHT, Image.SCALE_DEFAULT);
		}
	}

	private void startGame() {
		if (is_start == false) {
			timer = new Timer(DELAY, this);
			timer.start();
			is_start = true;
		}
	}

	private void reStartGame() {
		if (is_start == false) {
			jumped_count = 0;
			count = 0;
			pos_x = 100;
			pos_y = 100;
			index = 0;
			is_pass_obstacle = false;
			obstacle_x = 0;
			score = 0;
			locateObstacle();

			timer = new Timer(DELAY, this);
			timer.start();
			is_start = true;
		}
	}

	private void stopGame() {
		if (is_start == true) {
			timer.stop();
			is_start = false;
		}
	}

	private void locateObstacle() {
		ob_index = (int) (Math.random() * 3);
		obstacle_x = (((int) (Math.random() * B_WIDTH)) / STEP) * STEP;
		if (obstacle_x < 5 * DOG_WIDTH) {
			obstacle_x = 5 * DOG_WIDTH;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}

	private void doDrawing(Graphics g) {
		if (pos_y < ORIGINAL_POS_Y) {
			jumped_count = (jumped_count + 1) % 5;
			if (jumped_count == 0) {
				pos_y = ORIGINAL_POS_Y;
			}
		}

		g.drawImage(obstacle_image[ob_index], obstacle_x, obstacle_y, this);
		g.drawImage(dogs[index], pos_x, pos_y, this);
		g.drawImage(landImage, land_x, land_y, this);
		displayScore(g);
		Toolkit.getDefaultToolkit().sync();
		if(!is_start){
			gameOver(g);
			is_start = false;
		}
	}

	public void actionPerformed(ActionEvent e) {
		move();
		repaint();
	}

	private void gameOver(Graphics g) {

		g.drawImage(gameOverImage, B_WIDTH / 2 - ((BufferedImage) gameOverImage).getWidth() / 2,
				B_HEIGHT / 2 - ((BufferedImage) gameOverImage).getHeight() * 2, null);
		g.drawImage(replayImage, B_WIDTH / 2 - ((BufferedImage) replayImage).getWidth() / 2, B_HEIGHT / 2, null);
	}

	private void move() {
		// pos_x = (pos_x + STEP) % B_WIDTH;
		index = (index + 1) % IMAGE_COUNT;
		obstacle_x = obstacle_x - STEP;

		score += SCORE_INC;
		if (obstacle_x < DOG_WIDTH + pos_x && obstacle_x > pos_x) {
			if (pos_y == ORIGINAL_POS_Y) {
				// is_start = false;
				stopGame();
			}
		}

		if (is_pass_obstacle == true) {
			locateObstacle();
			is_pass_obstacle = false;
		}

		if (obstacle_x == 0) {
			is_pass_obstacle = true;
		}

	}

	private void displayScore(Graphics g) {
		int scoreArray[] = scoreToArray(score);
		for(int i = 0; i < SCORE_LENGTH; i++) {
			// this if needed to make blinking animation when score increased by 100
			if((!((int)score >= 12 && (int)score % 100 <= 12) || (int)score % 3 == 0))
				g.drawImage(cropImage((BufferedImage)scoreImage, scoreArray[SCORE_LENGTH - i - 1]), CURRENT_SCORE_X + i * NUMBER_WIDTH, SCORE_Y, null);
		}
	}
	
	private int[] scoreToArray(double scoreType) {
		int scoreArray[] = new int[SCORE_LENGTH];
		int tempScore = (int)scoreType;
		for(int i = 0; i < SCORE_LENGTH; i++) {
			int number = tempScore % 10;
			tempScore = (tempScore - number) / 10;
			scoreArray[i] = number;
		}
		return scoreArray;
	}

	private BufferedImage cropImage(BufferedImage image, int number) {
		return image.getSubimage(number * NUMBER_WIDTH, 0, NUMBER_WIDTH, NUMBER_HEIGHT);
	}

	private void jump() {
		pos_y = ORIGINAL_POS_Y - 70;
	}

	private class TAdapter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {

			int key = e.getKeyCode();

			if (key == KeyEvent.VK_SPACE) {
				if (is_start == false) {
					reStartGame();
				} else {
					// stopGame();
					jump();
				}
			} else if(key == KeyEvent.VK_UP ) {
				if (is_start == true) {
					jump();
				}
			}
		}
	}
}
