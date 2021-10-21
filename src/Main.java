import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;
import java.util.ArrayList;

import static java.lang.System.out;

public class Main {
    static final String RESET = "\u001B[0m";
    static final String C1 = "\u001B[38;2;50;165;255m";
    static final String C2 = "\u001B[38;2;100;200;80m";
    static final String C3 = "\u001B[38;2;255;100;100m";
    static final String C4 = "\u001B[38;2;135;80;245m";
    static final String C5 = "\u001B[38;2;90;15;15m";
    static final String C6 = "\u001B[38;2;200;200;200m";
    static final String C7 = "\u001B[38;2;150;150;150m";
    static final String C8 = "\u001B[38;2;100;100;100m";

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
    static String EMPTY = "\u001B[38;2;85;85;85m · " + RESET;
    static String NUM = C1 + " 1 " + RESET;
    static String FLAG = "\u001B[38;2;255;255;0m ⚠ " + RESET;
    static String MINE = "\u001B[38;2;100;80;80m ֍ " + RESET;
    static String LAST = "\u001B[38;2;255;70;70m ֍ " + RESET;

    public static void main(String[] args) {
        out.println(Messages.GREETINGS);
        new NewGameThread().start();
    }

    static void createField() {
        boolean valueAccepted = false;
        String line;
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        // field width setting up
        while (!valueAccepted) {
            out.print(Messages.TYPE_WIDTH);

            try {
                line = r.readLine();

                if (line == null || line.isEmpty()) {
                    width = random(100);
                    out.println(Messages.RANDOM_WIDTH);
                    valueAccepted = true;
                } else {
                    try {
                        width = Integer.parseInt(line);

                        if (width <= 0 || width > 100) {
                            out.println(Messages.WIDTH_OUT_OF_RANGE);
                        } else {
                            valueAccepted = true;
                        }
                    } catch (NumberFormatException e) {
                        out.println(Messages.ERROR);
                    }
                }
            } catch (IOException e) {
                out.println(Messages.ERROR);
            }
        }
        valueAccepted = false;

        // field height setting up
        while (!valueAccepted) {
            out.print(Messages.TYPE_HEIGHT);

            try {
                line = r.readLine();

                if (line == null || line.isEmpty()) {
                    height = random(100);
                    out.println(Messages.RANDOM_HEIGHT);
                    valueAccepted = true;
                } else {
                    try {
                        height = Integer.parseInt(line);

                        if (height <= 0 || height > 100) {
                            out.println(Messages.HEIGHT_OUT_OF_RANGE);
                        } else {
                            valueAccepted = true;
                        }
                    } catch (NumberFormatException e) {
                        out.println(Messages.ERROR);
                    }
                }
            } catch (IOException e) {
                out.println(Messages.ERROR);
            }
        }

        valueAccepted = false;

        // mines generation
        while (!valueAccepted) {
            out.print(Messages.TYPE_MINES);

            try {
                line = r.readLine();

                if ( line == null || line.isEmpty() ) {
                    minesCount = random(width * height);
                    out.println(Messages.RANDOM_MINES);
                    valueAccepted = true;
                } else {
                    try {
                        minesCount = Integer.parseInt(line);

                        if (minesCount <= 0 || minesCount > width * height) {
                            out.println(Messages.MINES_OUT_OF_RANGE);
                        } else {
                            valueAccepted = true;
                        }
                    } catch (NumberFormatException e) {
                        out.println(Messages.ERROR);
                    }
                }
            } catch (IOException e) {
                out.println(Messages.ERROR);
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
        out.println(Messages.RESULT);
        String row;
        String columns;
        StringBuilder columnsSb, rowsSb;

        if (height < 10) {
            columns = " ";
        } else if (height < 100) {
            columns = "  ";
        } else {
            columns = "   ";
        }
        columnsSb = new StringBuilder(columns);

        for (int i = 0; i < width - 1; i++) {
            if (i < 8) {
                columnsSb.append(i + 1).append("  ");
            } else if (i < 99) {
                columnsSb.append(i + 1).append(" ");
            } else {
                columnsSb.append(i + 1);
            }
        }
        columnsSb.append(width);
        out.println("x→" + columnsSb);

        for (int h = 0; h < height; h++) {
            row = "";

            if (height >= 10 && height < 100) {
                if (h < 9) {
                    row = " ";
                }
            } else if (height >= 100) {
                if (h < 99) {
                    row = " ";
                }

                if (h < 9) {
                    row += " ";
                }
            }
            rowsSb = new StringBuilder(row);
            rowsSb.append(h + 1).append(" ");

            for (int w = 0; w < width; w++) {
                rowsSb.append(resultMap[h][w]);
            }
            out.println(rowsSb + " " + (h + 1));
        }
        out.println("↑y" + columnsSb + "\n");
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
        if (!xEmptyCell.isEmpty()) {
            xEmptyCell.remove(0);
            yEmptyCell.remove(0);
        }

        int xStart = x == 0 ? 0 : x - 1;
        int yStart = y == 0 ? 0 : y - 1;
        int xEnd = x == width - 1 ? x : x + 1;
        int yEnd = y == height - 1 ? y : y + 1;

        for (int h = yStart; h <= yEnd; h++) {
            for (int w = xStart; w <= xEnd; w++) {
                if (!(h == y && w == x) && !touched[h][w]) {
                    if (resultMap[h][w].equals(FLAG)) {
                        flags--;
                    }
                    int around = getCountAround(w, h);

                    if (around > 0) {
                        String color = switch (around) {
                            case 1 -> C1;
                            case 2 -> C2;
                            case 3 -> C3;
                            case 4 -> C4;
                            case 5 -> C5;
                            case 6 -> C6;
                            case 7 -> C7;
                            default -> C8;
                        };

                        resultMap[h][w] = ' ' + color + around + RESET + ' ';
                        touched[h][w] = true;
                        touches--;
                    } else if (Math.abs(x - w) != Math.abs(y - h)) {
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
}
