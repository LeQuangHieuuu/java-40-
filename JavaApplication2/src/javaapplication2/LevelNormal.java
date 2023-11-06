/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication2;

/**
 *
 * @author Van Tan
 */ 
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;


public class LevelNormal extends JFrame implements KeyListener {
    private JPanel gamePanel;
    private JLabel scoreLabel;
    private Timer timer;
    private int[][] board;
    private int currentShape;
    private int currentX;
    private int currentY;
    private int currentRotation;
    private int score;
    

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int SQUARE_SIZE = 30;
    private static final Color BORDER_COLOR = Color.BLACK;

    public LevelNormal() {
        setTitle("Trò chơi xếp hình");
        setSize(BOARD_WIDTH * SQUARE_SIZE + 20, BOARD_HEIGHT * SQUARE_SIZE + 50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);

        // Panel for the game board
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawShape(g);
            }
        };
        gamePanel.setPreferredSize(new Dimension(BOARD_WIDTH * SQUARE_SIZE, BOARD_HEIGHT * SQUARE_SIZE));
        add(gamePanel, BorderLayout.CENTER);

        // Label for the score
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        add(scoreLabel, BorderLayout.SOUTH);

        board = new int[BOARD_WIDTH][BOARD_HEIGHT];
        currentShape = new Random().nextInt(7); // 7 loại khối hình
        currentX = BOARD_WIDTH / 2;
        currentY = 0;
        currentRotation = 0;
        score = 0;

        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveShape(0, 1);
            }
        });

        timer.start();

    }

    private void drawBoard(Graphics g) {
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                g.setColor(BORDER_COLOR);
                g.drawRect(x * SQUARE_SIZE, y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

                if (board[x][y] != 0) {
                    g.setColor(getColor(board[x][y]));
                    g.fillRect(x * SQUARE_SIZE + 1, y * SQUARE_SIZE + 1, SQUARE_SIZE - 1, SQUARE_SIZE - 1);
                }
            }
        }
    }

    private void drawShape(Graphics g) {
        int[][] shape = Tetromino.SHAPES[currentShape][currentRotation];

        for (int x = 0; x < shape.length; x++) {
            for (int y = 0; y < shape[x].length; y++) {
                if (shape[x][y] != 0) {
                    g.setColor(getColor(shape[x][y]));
                    g.fillRect((currentX + x) * SQUARE_SIZE + 1, (currentY + y) * SQUARE_SIZE + 1, SQUARE_SIZE - 1, SQUARE_SIZE - 1);
                }
            }
        }
    }

    private void moveShape(int deltaX, int deltaY) {
        if (canMove(currentShape, currentX + deltaX, currentY + deltaY, currentRotation)) {
            currentX += deltaX;
            currentY += deltaY;
            gamePanel.repaint();
        } else if (deltaY != 0) {
            placeShape();
        }
    }

    private void placeShape() {
        int[][] shape = Tetromino.SHAPES[currentShape][currentRotation];
        int color = currentShape + 1;

        for (int x = 0; x < shape.length; x++) {
            for (int y = 0; y < shape[x].length; y++) {
                if (shape[x][y] != 0) {
                    int boardX = currentX + x;
                    int boardY = currentY + y;
                    board[boardX][boardY] = color;
                }
            }
        }

        // Check for completed lines and remove them
        for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
            boolean isFullLine = true;
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[x][y] == 0) {
                    isFullLine = false;
                    break;
                }
            }

            if (isFullLine) {
                // Remove the full line
                for (int row = y; row > 0; row--) {
                    for (int x = 0; x < BOARD_WIDTH; x++) {
                        board[x][row] = board[x][row - 1];
                    }
                }
                y++;
                score += 100; // Add score for each completed line
            }
        }

        currentX = BOARD_WIDTH / 2;
        currentY = 0;
        currentShape = new Random().nextInt(7);
        currentRotation = 0;

        if (!canMove(currentShape, currentX, currentY, currentRotation)) {
            // Game over
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over. Your Score: " + score);
            
            this.setVisible(false);
           
        }

        gamePanel.repaint();
        scoreLabel.setText("Score: " + score);
    }

    private boolean canMove(int shapeIndex, int x, int y, int rotation) {
        int[][] shape = Tetromino.SHAPES[shapeIndex][rotation];

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardX = x + i;
                    int boardY = y + j;

                    if (boardX < 0 || boardX >= BOARD_WIDTH || boardY >= BOARD_HEIGHT || board[boardX][boardY] != 0) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A) {
            moveShape(-1, 0);
        } else if (key == KeyEvent.VK_D) {
            moveShape(1, 0);
        } else if (key == KeyEvent.VK_S) {
            moveShape(0, 1);
        } else if (key == KeyEvent.VK_W) {
            rotateShape();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void rotateShape() {
        int newRotation = (currentRotation + 1) % Tetromino.SHAPES[currentShape].length;
        if (canMove(currentShape, currentX, currentY, newRotation)) {
            currentRotation = newRotation;
            gamePanel.repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LevelNormal game = new LevelNormal();
                game.setVisible(true);
            }
        });
    }

    private Color getColor(int colorIndex) {
        switch (colorIndex) {
            case 1:
                return Color.CYAN;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.ORANGE;
            case 4:
                return Color.YELLOW;
            case 5:
                return Color.GREEN;
            case 6:
                return Color.MAGENTA;
            case 7:
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }
}

class Tetromino {

    public static final int[][][][] SHAPES = {
        // I-Piece
        {
            {
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
            },
            {
                {0, 0, 1, 0},
                {0, 0, 1, 0},
                {0, 0, 1, 0},
                {0, 0, 1, 0}
            }
        },
        // J-Piece
        {
            {
                {2, 0, 0},
                {2, 2, 2},
                {0, 0, 0}
            },
            {
                {0, 2, 2},
                {0, 2, 0},
                {0, 2, 0}
            },
            {
                {0, 0, 0},
                {2, 2, 2},
                {0, 0, 2}
            },
            {
                {0, 2, 0},
                {0, 2, 0},
                {2, 2, 0}
            }
        },
        // L-Piece
        {
            {
                {0, 0, 3},
                {3, 3, 3},
                {0, 0, 0}
            },
            {
                {0, 3, 0},
                {0, 3, 0},
                {0, 3, 3}
            },
            {
                {0, 0, 0},
                {3, 3, 3},
                {3, 0, 0}
            },
            {
                {3, 3, 0},
                {0, 3, 0},
                {0, 3, 0}
            }
        },
        // O-Piece
        {
            {
                {4, 4},
                {4, 4}
            }
        },
        // S-Piece
        {
            {
                {0, 5, 5},
                {5, 5, 0}
            },
            {
                {5, 0, 0},
                {5, 5, 0},
                {0, 5, 0}
            }
        },
        // T-Piece
        {
            {
                {0, 6, 0},
                {6, 6, 6},
                {0, 0, 0}
            },
            {
                {0, 6, 0},
                {0, 6, 6},
                {0, 6, 0}
            },
            {
                {0, 0, 0},
                {6, 6, 6},
                {0, 6, 0}
            },
            {
                {0, 6, 0},
                {6, 6, 0},
                {0, 6, 0}
            }
        },
        // Z-Piece
        {
            {
                {7, 7, 0},
                {0, 7, 7},
                {0, 0, 0}
            },
            {
                {0, 0, 7},
                {0, 7, 7},
                {0, 7, 0}
            }
        }
    };
}
