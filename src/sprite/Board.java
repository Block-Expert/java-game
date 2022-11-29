package sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

	private static final long serialVersionUID = 4940708456978284418L;

	private final int B_WIDTH = 1024;
	private final int B_HEIGHT = 768;

	private final int DOG_WIDTH = 256;
	private final int DOG_HEIGHT = 128;
	private final int STEP = 32;
	private final int DELAY = 140;
	private final int ORIGINAL_POS_Y = 100;

	private Timer timer;
	private int IMAGE_COUNT = 8;
	private Image dogs[] = new Image[IMAGE_COUNT];

	private int jumped_count = 0;
	private int count = 0;
	private int pos_x = 0;
	private int pos_y = 100;
	private int index = 0;
	private boolean is_start = false;
	private boolean is_pass_obstacle = false;

	private int obstacle_x = 0;
	private int obstacle_y = ORIGINAL_POS_Y + DOG_HEIGHT + 50;
	private Image obstacle_image;

	public Board() {
		initBoard();
	}

	private void initBoard() {
		addKeyListener(new TAdapter());
		setFocusable(true);
		loadImages();
		locateObstacle();
		startGame();
	}

	private void loadImages() {
		obstacle_image = new ImageIcon("src/resources/obstacle.png").getImage();
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
			obstacle_y = ORIGINAL_POS_Y + DOG_HEIGHT + 50;

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
		obstacle_x = (((int) (Math.random() * B_WIDTH)) / STEP) * STEP;
		if(obstacle_x < DOG_WIDTH) {
			obstacle_x = DOG_WIDTH;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}

	private void doDrawing(Graphics g) {
		if (is_start) {
			if (pos_y < ORIGINAL_POS_Y) {
				jumped_count = (jumped_count + 1) % 5;
				if (jumped_count == 0) {
					pos_y = ORIGINAL_POS_Y;
				}
			}

			g.drawImage(obstacle_image, obstacle_x, obstacle_y, this);
			g.drawImage(dogs[index], pos_x, pos_y, this);
			Toolkit.getDefaultToolkit().sync();
		} else {
			gameOver(g);
		}
	}

	public void actionPerformed(ActionEvent e) {
		move();
		repaint();
	}

	private void gameOver(Graphics g) {

		String msg = "Game Over";
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);

		g.setColor(Color.black);
		g.setFont(small);
		g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
	}

	private void move() {
		pos_x = (pos_x + STEP) % B_WIDTH;
		index = (index + 1) % IMAGE_COUNT;
		
		if ((pos_x + DOG_WIDTH) % B_WIDTH == obstacle_x) {
			if (pos_y == ORIGINAL_POS_Y) {
				//is_start = false;
				stopGame();
			}
			is_pass_obstacle = true;
		}

		if(is_pass_obstacle == true) {
			locateObstacle();
			is_pass_obstacle = false;
		}

	}

	private void jump() {
		pos_y = ORIGINAL_POS_Y - 50;
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
