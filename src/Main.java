import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Date;

public class Main {
    static boolean[][] mines;               // mines array
    static boolean[][] touched;             // touches made array
    static String[][] resultMap;            // result map array
    static ArrayList<Integer> minesList;    // generated indexes of mines
    static int touches = 0;                 // available touches
    static int flags = 0;                   // used flags
    static int width, height, minesCount;   // game parameters
    static ArrayList<Integer> xEmptyCell;   // list of x-coordinates of empty cells
    static ArrayList<Integer> yEmptyCell;   // list of y-coordinates of empty cells
    static String UNTOUCHED = "[░]";
    static String EMPTY = "[ ]";
    static String NUM = "[1]";
    static String FLAG = "[·]";
    static String MINE = "[֍]";
    static String LAST = "[x]";

    public static void main(String[] args) {
        System.out.println( "MINESWEEPER v1.0\n" +
                            "w.shuminski\n\n" +
                            "Type dimensions of the new field and mines quantity.\n" +
                            "Negative value means that the value will be generated randomly.\n" +
                            "Type coordinates of the cell to discover it.\n" +
                            "If you'd like to flag the cell type \"flag\" instead of any coordinate.\n" +
                            "In this case you'll go to the flag mode. To return to the discovery mode just press Enter.");
        startGame();
    }

    static void startGame() {
        System.out.println( "----------------------------\n" +
                            "Game started\n\n");
        createField();
        touch();
    }

    static void createField() {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Type field width (1-999): ");
            width = Integer.parseInt(r.readLine());

            if (width == 0 || width > 999) {
                System.out.println("The width must be in 1-999 range. Try again.\n\n");
                createField();
            }

            if (width < 0) {
                width = random(1, 999);
                System.out.println("The width generated randomly.\n\n");
            }

            System.out.print("Type field height (1-999): ");
            height = Integer.parseInt(r.readLine());

            if (height == 0 || height > 999) {
                System.out.println("The height must be in 1-999 range. Try again.\n\n");
                createField();
            }

            if (height < 0) {
                height = random(1, 999);
                System.out.println("The height generated randomly.\n\n");
            }

            System.out.print("Type mines quantity: ");
            minesCount = Integer.parseInt(r.readLine());

            if (minesCount > width * height) {
                System.out.println("It's impossible to place mines more than cells. Try again.\n\n");
                createField();
            }

            if (minesCount < 0) {
                minesCount = random(width * height);
                System.out.println("The mines quantity generated randomly.");
            }

            mines = new boolean[height][width];
            touched = new boolean[height][width];
            resultMap = new String[height][width];
            minesList = new ArrayList<>();
            touches = width * height - minesCount;
            xEmptyCell = new ArrayList<>();
            yEmptyCell = new ArrayList<>();

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

            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    resultMap[h][w] = UNTOUCHED;
                }
            }
        }
        catch (IOException | NumberFormatException e) {
            System.out.println("Try again.\n\n");
            createField();
        }
    }

    static void drawResultMap() {
        System.out.println();
        System.out.println("Result map");
        String row;
        String columns;
        StringBuilder columnsSb, rowsSb;

        if (height < 10) {
            columns = " ";
        }
        else {
            if (height < 100) {
                columns = "  ";
            }
            else {
                columns = "   ";
            }
        }

        columnsSb = new StringBuilder(columns);

        for (int i = 0; i < width - 1; i++) {
            if (i < 8) {
                columnsSb.append(i + 1 + "  ");
            }
            else {
                if (i < 99) {
                    columnsSb.append(i + 1 + " ");
                }
                else {
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
            }
            else {
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

            rowsSb.append(h + 1 + " ");

            for (int w = 0; w < width; w++) {
                rowsSb.append(resultMap[h][w]);
            }

            System.out.println(rowsSb + " " + (h + 1));
        }

        System.out.println("↑y" + columnsSb);
    }

    static void touch() {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            String xRead, yRead;
            int x, y;
            System.out.println( "\n" +
                                "Touch\n");
            System.out.print("x: ");
            xRead = r.readLine().toLowerCase();

            if (xRead.equals("flag")) {
                flag();
            }
            else {
                x = Integer.parseInt(xRead) - 1;
                System.out.print("y: ");
                yRead = r.readLine().toLowerCase();

                if (yRead.equals("flag")) {
                    flag();
                }
                else {
                    y = Integer.parseInt(yRead) - 1;

                    if (x + 1 > width || y + 1 > height || x < 0 || y < 0) {
                        System.out.println("You got out of the map. Try again.");
                        touch();
                    }
                    else {
                        if (touched[y][x]) {
                            System.out.println("You've touched this cell. Try another.");
                            touch();
                        }
                        else {
                            if (resultMap[y][x].equals(FLAG)) {
                                flags--;
                            }

                            if (mines[y][x]) {
                                showMinesOnResultMap();
                                resultMap[y][x] = LAST;
                                gameOver(true);
                            }
                            else {
                                touched[y][x] = true;

                                if (getCountAround(x, y) == 0) {
                                    resultMap[y][x] = EMPTY;
                                    xEmptyCell.add(x);
                                    yEmptyCell.add(y);

                                    while (!xEmptyCell.isEmpty()) {
                                        openEmpty(xEmptyCell.get(0), yEmptyCell.get(0));
                                    }
                                }
                                else {
                                    resultMap[y][x] = '[' + String.valueOf(getCountAround(x, y)) + ']';
                                }

                                if (--touches == 0) {
                                    showMinesOnResultMap();
                                    gameOver(false);
                                }
                                else {
                                    drawResultMap();
                                    touch();
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (IOException | NumberFormatException e) {
            System.out.println("Try again.");
            touch();
        }
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
                    }
                    else {
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

    static boolean isNear(int x1, int y1, int x2, int y2) {
        return (Math.abs(x2 - x1) <= 1) && (Math.abs(y2 - y1) <= 1);
    }

    static void gameOver(boolean lose) throws IOException {
        System.out.println("============================");
        drawResultMap();
        System.out.println();
        System.out.print("YOU ");

        if (lose) { System.out.println("LOSE."); }
        else      { System.out.println("WON."); }

        System.out.println( "----------------------------\n" +
                            NUM + " - cell near the mine\n" +
                            EMPTY + " - empty cell");

        if (flags != 0) {
            System.out.println(FLAG + " - flag");
        }

        System.out.println(MINE + " - mine");

        if (lose) {
            System.out.println(UNTOUCHED + " - untouched cell");
            System.out.println(LAST + " - your last touch");
        }

        whatToDo();
    }

    static void whatToDo() throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        System.out.println( "\n" +
                            "Wold you like to play again?\n\n" +
                            "[ Yes ] - new game\n" +
                            "[ Anything else ] - exit\n\n");
        System.out.print("So? : ");
        String answer = r.readLine().toLowerCase();

        if (answer.equals("yes")) {
            startGame();
        }
        else {
            System.out.println("\nThanks for playing!");
        }
    }

    static void flag() {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            int x, y;
            System.out.println("\nType flag coordinates.");
            System.out.print("x: ");
            x = Integer.parseInt(r.readLine()) - 1;
            System.out.print("y: ");
            y = Integer.parseInt(r.readLine()) - 1;

            if (x + 1 > width || y + 1 > height || x < 0 || y < 0) {
                System.out.println("You got out of the map. Try again.");
            }
            else {
                if (touched[y][x]) {
                    System.out.println("You've touched this cell. Try another.");
                }
                else {
                    if (resultMap[y][x] == FLAG) {
                        resultMap[y][x] = UNTOUCHED;
                        flags--;
                    }
                    else {
                        resultMap[y][x] = FLAG;
                        flags++;
                    }

                    drawResultMap();
                }
            }

            flag();
        }
        catch (IOException | NumberFormatException e) {
            touch();
        }
    }

    static int random(int max) {
        return (int) (Math.random() * (max + 1));
    }

    static int random(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    //draws mines position on the map
    static void drawFieldMines() {
        System.out.println();
        System.out.println("Mines map");
        String row = new String();

        for (boolean[] mine : mines) {
            row = "";

            for (boolean isMine : mine) {
                if (isMine) { row += "[֍]"; }
                else        { row += "[ ]"; }
            }

            System.out.println(row);
        }
    }
}