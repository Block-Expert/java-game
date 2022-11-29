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

	private Timer timer;
	private int IMAGE_COUNT = 8;
	private Image dogs[] = new Image[IMAGE_COUNT];

	private int jumped_count = 0;
	private int count = 0;
	private int pos_x = 0;
	private int pos_y = 200;
	private int index = 0;
	private boolean is_start = false;
	private boolean is_pass_obstacle = false;

	private int obstacle_x = 0;
	private int obstacle_y = ORIGINAL_POS_Y + DOG_HEIGHT - OBSTACLE_HEIGHT;
	private Image obstacle_image[] = new Image[3];
	private int ob_index = 0;

	private Image gameOverImage;
	private Image replayImage;

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
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			pos_x = 0;
			pos_y = 100;
			index = 0;
			is_start = false;
			is_pass_obstacle = false;
			obstacle_x = 0;
			obstacle_y = ORIGINAL_POS_Y;

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
		if (obstacle_x < 2 * DOG_WIDTH) {
			obstacle_x = 2 * DOG_WIDTH;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}

	private void doDrawing(Graphics g) {
		//if (is_start) {
			if (pos_y < ORIGINAL_POS_Y) {
				jumped_count = (jumped_count + 1) % 6;
				if (jumped_count == 0) {
					pos_y = ORIGINAL_POS_Y;
				}
			}

			g.drawImage(obstacle_image[ob_index], obstacle_x, obstacle_y, this);
			g.drawImage(dogs[index], pos_x, pos_y, this);
			Toolkit.getDefaultToolkit().sync();
		if(!is_start){
			gameOver(g);
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

		if (DOG_WIDTH == obstacle_x) {
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

	private void jump() {
		pos_y = ORIGINAL_POS_Y - 100;
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
			}
		}
	}
}
