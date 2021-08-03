package controller;

import frame.Puzzle_Frame;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Controller {

    // khởi tạo khung Puzzle_Frame mới
    Puzzle_Frame frame = new Puzzle_Frame();

    //khởi tạo JPanel từ frame
    JPanel p_Puzzle = frame.getPn_Screen();

    //khởi tạo JLabel từ khung
    JLabel moveCount = frame.getLbl_MoveCount();
    JLabel elapsed = frame.getLbl_Elapsed();

    //khởi tạo JComboBox từ khung
    JComboBox size = frame.getCbo_Size();

    //khởi tạo JButton từ khung
    JButton newGame = frame.getBtn_NewGame();

    Timer timer;
    int sizeOfPuzzle;
    JButton[][] puzzle;
    boolean isGameStarted = false;
    int numberOfMoves = 0;

    public Controller() {
        //thêm hoat động cho nút newGame khi được nhấn
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //starts a new game
                newGame();
                
            }
        });

        //set the frame to show in the middle of the screen
        frame.setLocationRelativeTo(null);
        //set the frame to not resizable
        frame.setResizable(false);
        //set the frame to be visible
        frame.setVisible(true);
    }

    // khởi tạo một trò chơi mới
    void newGame() {
        //if the game has already started
        if (isGameStarted) {
            //stops the timer
            timer.stop();
            //hỏi người dùng xem họ có muốn bắt đầu một trò chơi mới không
            int confirm = JOptionPane.showConfirmDialog(frame, "Do you want to play a new game?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            //if user chooses yes then initializes a new game
            if (confirm == JOptionPane.YES_OPTION) {
                this.run();
            } //otherwise resume the timer
            else {
                timer.start();
            }
        } //otherwise starts a new game
        else {
            this.run();
        }
    }

    //reset all settings for the game
    void run() {
        //reset the timer
        this.resetTimer();
        //tạo các nút cho trò chơi
        this.generateButton();
        numberOfMoves = 0;
        moveCount.setText(String.valueOf(numberOfMoves));
        //cho biết chương trình rằng trò chơi đã bắt đầu
        isGameStarted = true;
    }

    //đặt lại thời gian chơi đã trôi qua
    void resetTimer() {
        elapsed.setText("0");
        //khởi tạo đối tượng Bộ hẹn giờ mới với độ trễ lên đến 1000ms với trình nghe hành động mới
        timer = new Timer(1000, new ActionListener() {
            int second = 0;

            //thực hiện hành động đối với nhãn thời gian khi bộ đếm thời gian chạy
            @Override
            public void actionPerformed(ActionEvent e) {
                second++;
                elapsed.setText(String.valueOf(second));
            }
        });
        //bắt đầu hẹn giờ
        timer.start();
    }    

    //tạo các nút cho trò chơi
    void generateButton() {
        //chỉ định kích thước của câu đố từ chỉ mục đã chọn của hộp combox và thêm với 3
        sizeOfPuzzle = frame.getCbo_Size().getSelectedIndex() + 3;
        //xóa tất cả các thành phần khỏi JPanel
        p_Puzzle.removeAll();
        /*đặt GridLayout mới cho JPanel với số lượng hàng và cột là kích thước của câu đố,
         và khoảng cách ngang và khoảng cách dọc có kích thước là 10*/
        p_Puzzle.setLayout(new GridLayout(sizeOfPuzzle, sizeOfPuzzle, 10, 10));
        /*đặt kích thước ưa thích của JPanel làm Thứ nguyên mới với chiều rộng và chiều cao là
         (sizeOfPuzzle * kích thước nút) + (kích thước khoảng cách nút * (sizeOfPuzzle - 1))*/
        p_Puzzle.setPreferredSize(new Dimension((sizeOfPuzzle * 70) + (10 * (sizeOfPuzzle - 1)),
                (sizeOfPuzzle * 70) + (10 * (sizeOfPuzzle - 1))));

        //khởi tạo một ma trận câu đố mới với hàng và chiều cao được tính là sizeOfPuzzle
        puzzle = new JButton[sizeOfPuzzle][sizeOfPuzzle];

        for (int i = 0; i < sizeOfPuzzle; i++) {
            for (int j = 0; j < sizeOfPuzzle; j++) {
                /*initializes a new JButton with label as a number from 1 to sizeOfPuzzle - 1 respectively*/
                JButton btn = new JButton(i * sizeOfPuzzle + j + 1 + "");
                //assigns the button to the matrix at row i and column j
                puzzle[i][j] = btn;

                //thêm hành động cho nút khi được nhấn
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //nếu nút có thể được di chuyển theo chiều ngang hoặc chiều dọc
                        if (checkMove(btn)) {
                            //moves the button
                            moveButton(btn);
                            numberOfMoves++;
                            moveCount.setText(numberOfMoves + "");
                            //nếu người dùng đã thắng trò chơi
                            if (checkWin()) {
                                //cho biết với chương trình rằng trò chơi đã kết thúc
                                isGameStarted = false;
                                //dừng bộ đếm thời gian
                                timer.stop();
                                JOptionPane.showMessageDialog(frame, "You won!");
                            }
                        }
                    }
                });
                //thêm nút vào JPanel
                p_Puzzle.add(btn);
            }
        }
        //đặt văn bản của nút cuối cùng của câu đố thành trống
        puzzle[sizeOfPuzzle - 1][sizeOfPuzzle - 1].setText("");
        //randomizes the puzzle
        randomizePuzzle();
        //pack all components in the frame
        frame.pack();
    }

    //sắp xếp lại các nút trong câu đố
    void randomizePuzzle() {
        //khởi tạo một đối tượng Ngẫu nhiên mới
        Random r = new Random();
        /*1000 là một số ngẫu nhiên mà bạn có thể đặt cho số lần ngẫu nhiên
         câu đố này*/
        for (int i = 0; i < 1000; i++) {
            //nhận vị trí của nút trống trong câu đố
            Point p = getBlankPosition();
            //nhận được sự phối hợp x và y của nút trống
            int x = p.x;
            int y = p.y;

            //ngẫu nhiên số nguyên n từ 0 đến 3
            int n = r.nextInt(4);

            //chạy từng trường hợp bằng n
            switch (n) {
                case 0: {
                    //di chuyển nút trống lên trong câu đố
                    if (y > 0) {
                        puzzle[x][y].setText(puzzle[x][y - 1].getText());
                        puzzle[x][y - 1].setText("");
                    }
                    break;
                }
                case 1: {
                    //di chuyển nút trống xuống trong câu đố
                    if (y < sizeOfPuzzle - 1) {
                        puzzle[x][y].setText(puzzle[x][y + 1].getText());
                        puzzle[x][y + 1].setText("");
                    }
                    break;
                }
                case 2: {
                    //di chuyển nút trống sang trái trong câu đố
                    if (x > 0) {
                        puzzle[x][y].setText(puzzle[x - 1][y].getText());
                        puzzle[x - 1][y].setText("");
                    }
                    break;
                }
                case 3: {
                    //di chuyển nút trống sang bên phải trong câu đố
                    if (x < sizeOfPuzzle - 1) {
                        puzzle[x][y].setText(puzzle[x + 1][y].getText());
                        puzzle[x + 1][y].setText("");
                    }
                    break;
                }
            }
        }

    }

    //kiểm tra xem người dùng đã hoàn thành trò chơi chưa
    boolean checkWin() {
        //nếu nút cuối cùng trong câu đố chưa phải là nút trống thì trả về false
        if (!puzzle[sizeOfPuzzle - 1][sizeOfPuzzle - 1].getText().equals("")) {
            return false;
        }
        int expectedNumber = 0;
        for (int row = 0; row < sizeOfPuzzle; row++) {
            for (int col = 0; col < sizeOfPuzzle; col++) {
                //tăng biến này theo từng nút được quét
                expectedNumber++;
                /*nếu quá trình quét có thể đạt đến nút được quét cuối cùng thì
                 có nghĩa là người dùng đã thắng trò chơi*/
                if ((row == sizeOfPuzzle - 1) && (col == sizeOfPuzzle - 1)) {
                    return true;
                }
                /*nếu có văn bản của nút không khớp với số lượng mong đợi
                 sau đó trả về false*/
                if (!puzzle[row][col].getText().equals(expectedNumber + "")) {
                    return false;
                }
            }
        }
        //trả về true nếu tất cả các điều kiện trên được thông qua
        return true;
    }

    //nhận vị trí của nút trống
    Point getBlankPosition() {
        for (int row = 0; row < sizeOfPuzzle; row++) {
            for (int col = 0; col < sizeOfPuzzle; col++) {
                if (puzzle[row][col].getText().equals("")) {
                    return new Point(row, col);
                }
            }
        }
        return null;
    }

    //kiểm tra xem nút đã nhấn có thể được di chuyển trong puzzle
    boolean checkMove(JButton btn) {
        //nếu đó là một nút trống thì nó không thể di chuyển được
        if (btn.getText().equals("")) {
            return false;
        }
        //get blank button's position
        Point blank = getBlankPosition();
        Point pressed = null;
        //get the pressed button's position
        for (int row = 0; row < sizeOfPuzzle; row++) {
            for (int col = 0; col < sizeOfPuzzle; col++) {
                if (puzzle[row][col].getText().equals(btn.getText())) {
                    pressed = new Point(row, col);
                }
            }
        }
        /*nếu nút trống và nút được nhấn nằm cạnh nhau theo chiều dọc thì
         nó có thể di chuyển được */
        if ((blank.x == pressed.x) && (Math.abs(blank.y - pressed.y) == 1)) {
            return true;
        }
        /*nếu nút trống và được nhấn nằm cạnh nhau theo chiều ngang thì
         nó có thể di chuyển được */
        if ((blank.y == pressed.y) && (Math.abs(blank.x - pressed.x) == 1)) {
            return true;
        }
        //nếu không thì nó không thể di chuyển được
        return false;
    }

    //di chuyển nút đã nhấn bằng nút trống
    void moveButton(JButton btn) {
        Point p = getBlankPosition();
        puzzle[p.x][p.y].setText(btn.getText());
        btn.setText("");
    }
}
