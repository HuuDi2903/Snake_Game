import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Random;
import java.util.random.*;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 300;
    static final int SCREEN_HEIGHT = 300;
    static final int UNIT_SIZE = 20;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 120;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 3;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // Draw grid lines
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            // Draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }

                // Draw main body part
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);

                // Draw wrapped parts when crossing borders
                if (x[i] < UNIT_SIZE) {
                    // If near left border, draw on right side
                    g.fillRect(x[i] + SCREEN_WIDTH, y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else if (x[i] > SCREEN_WIDTH - UNIT_SIZE) {
                    // If near right border, draw on left side
                    g.fillRect(x[i] - SCREEN_WIDTH, y[i], UNIT_SIZE, UNIT_SIZE);
                }

                if (y[i] < UNIT_SIZE) {
                    // If near top border, draw on bottom
                    g.fillRect(x[i], y[i] + SCREEN_HEIGHT, UNIT_SIZE, UNIT_SIZE);
                }
                else if (y[i] > SCREEN_HEIGHT - UNIT_SIZE) {
                    // If near bottom border, draw on top
                    g.fillRect(x[i], y[i] - SCREEN_HEIGHT, UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Draw score
            g.setColor(Color.red);
            g.setFont(new Font("Monsterrat", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, 
                SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten) - 10, 
                g.getFont().getSize() + 8);
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
      // Check self-collision
      for (int i = bodyParts; i > 0; i--) {
          if ((x[0] == x[i]) && (y[0] == y[i])) {
              running = false;
          }
      }
  
      // Wrap around screen borders
      if (x[0] < 0) {
          x[0] = SCREEN_WIDTH - UNIT_SIZE;
      }
      if (x[0] >= SCREEN_WIDTH) {
          x[0] = 0;
      }
      if (y[0] < 0) {
          y[0] = SCREEN_HEIGHT - UNIT_SIZE;
      }
      if (y[0] >= SCREEN_HEIGHT) {
          y[0] = 0;
      }
  
      if (!running) {
          timer.stop();
      }
    }
  
    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Mosterrat", Font.BOLD, 50));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("You sucks", (SCREEN_WIDTH - metrics1.stringWidth("You sucks")) / 2, SCREEN_HEIGHT / 2);
        g.setColor(Color.red);
        g.setFont(new Font("Mosnterrat", Font.BOLD, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Final Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Final Score: " + applesEaten)) / 2, (SCREEN_HEIGHT / 2) - 50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}