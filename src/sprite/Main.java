package sprite;

import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Main extends JFrame {

	private static final long serialVersionUID = -2965252528445049072L;

	public Main() {
		 initUI();
	}

	private void initUI() {
		add(new Board());
		setResizable(false);
		pack();
		setTitle("SpriteAnimation Example");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			JFrame ex = new Main();
			ex.setBounds(100, 100, 1024, 320);
			ex.setVisible(true);
			ex.getContentPane().setBackground(Color.white);
			((JComponent) ex.getContentPane()).setOpaque(false);
		});

	}

}
