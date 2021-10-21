import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.lang.System.out;

class TouchingThread extends Thread{
    private static int x, y;

    @Override
    public void run() {
        boolean valueAccepted = false;
        String line;
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        out.println(Messages.TOUCH);

        // x-coordinate of touching cell
        while (!valueAccepted) {
            out.print("x: ");

            try {
                line = r.readLine();

                if (line == null || line.length() == 0) {
                    out.println();
                    valueAccepted = true;
                    Main.flag = true;
                } else {
                    try {
                        x = parseInt(line) - 1;

                        if (x < 0 || x >= Main.width) {
                            out.println(Messages.GOT_OUT_OF_WIDTH);
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

        // y-coordinate of touching cell
        if (!Main.flag) {
            valueAccepted = false;

            while (!valueAccepted) {
                out.print("y: ");

                try {
                    line = r.readLine();

                    if (line == null || line.length() == 0) {
                        out.println();
                        valueAccepted = true;
                        Main.flag = true;
                    } else {
                        try {
                            y = parseInt(line) - 1;

                            if (y < 0 || y >= Main.height) {
                                out.println(Messages.GOT_OUT_OF_HEIGHT);
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
        }

        if (!Main.flag) {
            if (!Main.fieldCreated) {
                Main.generateMines(x, y);
                Main.fieldCreated = true;
            }

            // touch logic
            if (Main.touched[y][x]) {
                out.println(Messages.TOUCHED);
                new TouchingThread().start();
            } else {
                if (Main.resultMap[y][x].equals(Main.FLAG)) {
                    Main.flags--;
                }

                if (Main.mines[y][x]) {
                    Main.showMinesOnResultMap();
                    Main.resultMap[y][x] = Main.LAST;
                    Main.lose = true;
                    new GameOverThread().start();
                } else {
                    Main.touched[y][x] = true;
                    int around = Main.getCountAround(x, y);

                    if (around == 0) {
                        Main.resultMap[y][x] = Main.EMPTY;
                        Main.xEmptyCell.add(x);
                        Main.yEmptyCell.add(y);

                        while (!Main.xEmptyCell.isEmpty()) {
                            Main.openEmpty(Main.xEmptyCell.get(0), Main.yEmptyCell.get(0));
                        }
                    } else {
                        String color = switch (around) {
                            case 1 -> Main.C1;
                            case 2 -> Main.C2;
                            case 3 -> Main.C3;
                            case 4 -> Main.C4;
                            case 5 -> Main.C5;
                            case 6 -> Main.C6;
                            case 7 -> Main.C7;
                            default -> Main.C8;
                        };
                        Main.resultMap[y][x] = ' ' + color + around + Main.RESET + ' ';
                    }

                    if (--Main.touches == 0) {
                        Main.showMinesOnResultMap();
                        Main.lose = false;
                        new GameOverThread().start();
                    } else {
                        Main.drawResultMap();
                        new TouchingThread().start();
                    }
                }
            }
        }

        if (Main.flag) {
            out.println("Switched to Flag mode.\n");
            new FlagThread().start();
        }
    }
}