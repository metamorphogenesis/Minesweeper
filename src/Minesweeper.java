import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;
import java.util.ArrayList;

public class Minesweeper {
    static volatile boolean flag;           // determines which of touch or flag threads are allowed
    static boolean[][] mines;               // mines array
    static boolean[][] touched;             // touches made array
    static boolean lose;                    // means that you're lose.
    static boolean fieldCreated;            // shows if field was created
    static String[][] resultMap;            // result map array
    static int touches = 0;                 // available touches
    static int flags = 0;                   // used flags
    static int width, height, minesCount;   // game parameters
    static ArrayList<Integer> xEmptyCell;   // list of x-coordinates of empty cells
    static ArrayList<Integer> yEmptyCell;   // list of y-coordinates of empty cells
    static ArrayList<Integer> minesList;    // generated indexes of mines
    static String UNTOUCHED = "[░]";
    static String EMPTY = "[ ]";
    static String NUM = "[1]";
    static String FLAG = "[·]";
    static String MINE = "[֍]";
    static String LAST = "[x]";

    public static class NewGameThread extends Thread{
        @Override
        public void run() {
            System.out.println(Messages.NEW);
            fieldCreated = false;
            createField();
            drawResultMap();
            new CellTouchingThread().start();
        }
    }

    static class CellTouchingThread extends Thread{
        private static int x, y;

        @Override
        public void run() {
            boolean valueAccepted = false;
            String line;
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(Messages.TOUCH);

            // x-coordinate of touching cell
            while (!valueAccepted) {
                System.out.print("x: ");

                try {
                    line = r.readLine();

                    if (line == null || line.length() == 0) {
                        System.out.println();
                        valueAccepted = true;
                        flag = true;
                    } else {
                        try {
                            x = Integer.parseInt(line) - 1;

                            if (x < 0 || x >= width) {
                                System.out.println(Messages.GOT_OUT_OF_WIDTH);
                            } else {
                                valueAccepted = true;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(Messages.ERROR);
                        }
                    }
                } catch (IOException e) {
                    System.out.println(Messages.ERROR);
                }
            }

            // y-coordinate of touching cell
            if (!flag) {
                valueAccepted = false;

                while (!valueAccepted) {
                    System.out.print("y: ");

                    try {
                        line = r.readLine();

                        if (line == null || line.length() == 0) {
                            System.out.println();
                            valueAccepted = true;
                            flag = true;
                        } else {
                            try {
                                y = Integer.parseInt(line) - 1;

                                if (y < 0 || y >= height) {
                                    System.out.println(Messages.GOT_OUT_OF_HEIGHT);
                                } else {
                                    valueAccepted = true;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println(Messages.ERROR);
                            }
                        }
                    } catch (IOException e) {
                        System.out.println(Messages.ERROR);
                    }
                }
            }

            if (!flag) {
                if (!fieldCreated) {
                    generateMines(x, y);
                    fieldCreated = true;
                }

                // touch logic
                if (touched[y][x]) {
                    System.out.println(Messages.TOUCHED);
                    new CellTouchingThread().start();
                } else {
                    if (resultMap[y][x].equals(FLAG)) {
                        flags--;
                    }

                    if (mines[y][x]) {
                        showMinesOnResultMap();
                        resultMap[y][x] = LAST;
                        lose = true;
                        new GameOverThread().start();
                    } else {
                        touched[y][x] = true;

                        if (getCountAround(x, y) == 0) {
                            resultMap[y][x] = EMPTY;
                            xEmptyCell.add(x);
                            yEmptyCell.add(y);

                            while (!xEmptyCell.isEmpty()) {
                                openEmpty(xEmptyCell.get(0), yEmptyCell.get(0));
                            }
                        } else {
                            resultMap[y][x] = '[' + String.valueOf(getCountAround(x, y)) + ']';
                        }

                        if (--touches == 0) {
                            showMinesOnResultMap();
                            lose = false;
                            new GameOverThread().start();
                        } else {
                            drawResultMap();
                            new CellTouchingThread().start();
                        }
                    }
                }
            }

            if (flag) {
                System.out.println("Switched to Flag mode.\n");
                new FlagThread().start();
            }
        }
    }

    static class FlagThread extends Thread {
        private static int x, y;

        @Override
        public void run() {
            boolean valueAccepted = false;
            String line;
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(Messages.FLAG);

            // x-coordinate of flag
            while (!valueAccepted) {
                System.out.print("x: ");

                try {
                    line = r.readLine();

                    if (line == null || line.length() == 0) {
                        System.out.println();
                        valueAccepted = true;
                        flag = false;
                    } else {
                        try {
                            x = Integer.parseInt(line) - 1;

                            if (x < 0 || x >= width) {
                                System.out.println(Messages.GOT_OUT_OF_WIDTH);
                            } else {
                                valueAccepted = true;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(Messages.ERROR);
                        }
                    }
                } catch (IOException e) {
                    System.out.println(Messages.ERROR);
                }
            }

            // y-coordinate of flag
            if (flag) {
                valueAccepted = false;

                while (!valueAccepted) {
                    System.out.print("y: ");

                    try {
                        line = r.readLine();

                        if (line == null || line.length() == 0) {
                            System.out.println();
                            new CellTouchingThread().start();
                            System.out.println("flag thread interrupt");
                            currentThread().interrupt();
                        } else {
                            try {
                                y = Integer.parseInt(line) - 1;

                                if (y < 0 || y >= height) {
                                    System.out.println(Messages.GOT_OUT_OF_HEIGHT);
                                } else {
                                    valueAccepted = true;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println(Messages.ERROR);
                            }
                        }
                    } catch (IOException e) {
                        System.out.println(Messages.ERROR);
                    }
                }
            }

            if (flag) {
                if (!fieldCreated) {
                    generateMines();
                    fieldCreated = true;
                }

                // flag placement logic
                if (touched[y][x]) {
                    System.out.println(Messages.TOUCHED);
                    new FlagThread().start();
                } else {
                    if (resultMap[y][x].equals(FLAG)) {
                        resultMap[y][x] = UNTOUCHED;
                        flags--;
                    } else {
                        resultMap[y][x] = FLAG;
                        flags++;
                    }

                    drawResultMap();
                    new FlagThread().start();
                }
            }

            if (!flag) {
                System.out.println("Switched to Touch mode.\n");
                new CellTouchingThread().start();
            }
        }
    }

    static class GameOverThread extends Thread{
        @Override
        public void run() {
            System.out.println(Messages.DOUBLE_LINE);
            drawResultMap();

            if (lose) {
                System.out.println(Messages.LOSE);
            } else {
                System.out.println(Messages.WON);
            }

            System.out.println( Messages.LINE + "\n" +
                    NUM + Messages.NUM_IS +
                    EMPTY + Messages.EMPTY_IS);

            if (flags != 0) {
                System.out.println(FLAG + Messages.FLAG_IS);
            }

            System.out.println(MINE + Messages.MINE_IS);

            if (lose) {
                System.out.println(UNTOUCHED + Messages.UNTOUCHED_IS);
                System.out.println(LAST + Messages.LAST_IS);
            }

            new UserWaitingThread().start();
        }
    }

    static class UserWaitingThread extends Thread{
        @Override
        public void run() {
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            System.out.println( Messages.QUESTION.toString() +
                                Messages.ANSWERS);
            System.out.print(Messages.SO);
            String answer;

            try {
                answer = r.readLine().toLowerCase();

                if (answer.equalsIgnoreCase(Messages.YES.toString())) {
                    new NewGameThread().start();
                }
                else {
                    System.out.println(Messages.THANKS);
                }
            } catch (IOException e) {
                System.out.println(Messages.THANKS);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(Messages.GREETINGS);
        new NewGameThread().start();
    }

    static void createField() {
        boolean valueAccepted = false;
        String line;
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        // field width setting up
        while (!valueAccepted) {
            System.out.print(Messages.TYPE_WIDTH);

            try {
                line = r.readLine();

                if (line == null || line.length() == 0) {
                    width = random(100);
                    System.out.println(Messages.RANDOM_WIDTH);
                    valueAccepted = true;
                } else {
                    try {
                        width = Integer.parseInt(line);

                        if (width <= 0 || width > 100) {
                            System.out.println(Messages.WIDTH_OUT_OF_RANGE);
                        } else {
                            valueAccepted = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(Messages.ERROR);

                        System.out.println(e);

                    }
                }
            } catch (IOException e) {
                System.out.println(Messages.ERROR);
                System.out.println(e);
            }
        }

        valueAccepted = false;

        // field height setting up
        while (!valueAccepted) {
            System.out.print(Messages.TYPE_HEIGHT);

            try {
                line = r.readLine();

                if (line == null || line.length() == 0) {
                    height = random(100);
                    System.out.println(Messages.RANDOM_HEIGHT);
                    valueAccepted = true;
                } else {
                    try {
                        height = Integer.parseInt(line);

                        if (height <= 0 || height > 100) {
                            System.out.println(Messages.HEIGHT_OUT_OF_RANGE);
                        } else {
                            valueAccepted = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(Messages.ERROR);
                    }
                }
            } catch (IOException e) {
                System.out.println(Messages.ERROR);
            }
        }

        valueAccepted = false;

        // mines generation
        while (!valueAccepted) {
            System.out.print(Messages.TYPE_MINES);

            try {
                line = r.readLine();

                if (line == null || line.length() == 0) {
                    minesCount = random(width * height);
                    System.out.println(Messages.RANDOM_MINES);
                    valueAccepted = true;
                } else {
                    try {
                        minesCount = Integer.parseInt(line);

                        if (minesCount <= 0 || minesCount > width * height) {
                            System.out.println(Messages.MINES_OUT_OF_RANGE);
                        } else {
                            valueAccepted = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(Messages.ERROR);
                    }
                }
            } catch (IOException e) {
                System.out.println(Messages.ERROR);
            }
        }

        mines = new boolean[height][width];
        touched = new boolean[height][width];
        resultMap = new String[height][width];
        minesList = new ArrayList<>();
        touches = width * height - minesCount;
        xEmptyCell = new ArrayList<>();
        yEmptyCell = new ArrayList<>();

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                resultMap[h][w] = UNTOUCHED;
            }
        }
    }

        // excludes touched cell from available for generating
    static void generateMines(int x, int y) {
        boolean full = false;
        if (minesCount == width * height) {
            full = true;
        } else {
            for (int i = 0; i < minesCount; i++) {
                int index = random(width * height - 1);

                while (minesList.contains(index) || index == getCellIndex(x, y, width)) {
                    index = random(width * height - 1);
                }

                minesList.add(index);
            }
        }

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (full || minesList.contains(getCellIndex(w, h, width))) {
                    mines[h][w] = true;
                }
            }
        }
    }

    static void generateMines() {
        for (int i = 0; i < minesCount; i++) {
            int index = random(width * height - 1);

            while (minesList.contains(index)) {
                index = random(width * height - 1);
            }

            minesList.add(index);
        }

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (minesList.contains(w + h * width)) {
                    mines[h][w] = true;
                }
            }
        }
    }

    static void drawResultMap() {
        System.out.println(Messages.RESULT);
        String row;
        String columns;
        StringBuilder columnsSb, rowsSb;

        if (height < 10) {
            columns = " ";
        } else {
            if (height < 100) {
                columns = "  ";
            } else {
                columns = "   ";
            }
        }

        columnsSb = new StringBuilder(columns);

        for (int i = 0; i < width - 1; i++) {
            if (i < 8) {
                columnsSb.append(i + 1).append("  ");
            } else {
                if (i < 99) {
                    columnsSb.append(i + 1).append(" ");
                } else {
                    columnsSb.append(i + 1);
                }
            }
        }

        columnsSb.append(width);
        System.out.println("x→" + columnsSb);

        for (int h = 0; h < height; h++) {
            row = "";

            if (height >= 10 && height < 100) {
                if (h < 9) {
                    row = " ";
                }
            } else {
                if (height >= 100) {
                    if (h < 99) {
                        row = " ";
                    }

                    if (h < 9) {
                        row += " ";
                    }
                }
            }

            rowsSb = new StringBuilder(row);
            rowsSb.append(h + 1).append(" ");

            for (int w = 0; w < width; w++) {
                rowsSb.append(resultMap[h][w]);
            }

            System.out.println(rowsSb + " " + (h + 1));
        }

        System.out.println("↑y" + columnsSb + "\n");
    }

    static void showMinesOnResultMap() {
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (mines[h][w]) {
                    resultMap[h][w] = MINE;
                }
            }
        }
    }

    static void openEmpty(int x, int y) {
        if ( ! xEmptyCell.isEmpty()) {
            xEmptyCell.remove(0);
            yEmptyCell.remove(0);
        }

        int xStart = x == 0 ? 0 : x - 1;
        int yStart = y == 0 ? 0 : y - 1;
        int xEnd = x == width - 1 ? x : x + 1;
        int yEnd = y == height - 1 ? y : y + 1;

        for (int h = yStart; h <= yEnd; h++) {
            for (int w = xStart; w <= xEnd; w++) {
                if ( ! (h == y && w == x) && ! touched[h][w]) {
                    if (resultMap[h][w].equals(FLAG)) {
                        flags--;
                    }

                    if (getCountAround(w, h) > 0) {
                        resultMap[h][w] = '[' + String.valueOf(getCountAround(w, h)) + ']';
                        touched[h][w] = true;
                        touches--;
                    } else {
                        if (Math.abs(x - w) != Math.abs(y - h)) {
                            resultMap[h][w] = EMPTY;
                            xEmptyCell.add(w);
                            yEmptyCell.add(h);
                            touched[h][w] = true;
                            touches--;
                        }
                    }
                }
            }
        }
    }

    static int getCountAround(int x, int y) {
        int count = 0;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (mines[h][w] && isNear(y, x, h, w)) {
                    count++;
                }
            }
        }

        return count;
    }

    static boolean isNear(int x1, int y1, int x2, int y2) {
        return (Math.abs(x2 - x1) <= 1) && (Math.abs(y2 - y1) <= 1);
    }

    static int getCellIndex(int x, int y, int width) {
        return (width * y + x);
    }

    static int random(int max) {
        return (int) (Math.random() * (max + 1));
    }

    /*
    static int random() {
        //static int random(int min, int max) {
        //return (int) (Math.random() * (max - min + 1) + min);
        return (int) (Math.random() * 100 + 1);
    }
    */

    /*
      Draws mines position on the map.

      Use this method for testing to see all mines without taking
      any effect on gameplay
    */

    /*
    static void drawFieldMines() {
        System.out.println(Messages.MINES);
        StringBuilder rowSb;

        for (boolean[] mine : mines) {
            rowSb = new StringBuilder();

            for (boolean isMine : mine) {
                if (isMine) {rowSb.append("[֍]"); }
                else        {rowSb.append("[ ]"); }
            }

            System.out.println(rowSb);
        }
    }
    */
}

