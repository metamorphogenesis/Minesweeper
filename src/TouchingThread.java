import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Integer.parseInt;
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

                if (line == null || line.isEmpty()) {
                    valueAccepted = true;
                    Main.flag = true;
                } else if (Main.EXIT.contains(line.toLowerCase())) {
                    Main.exit();
                } else {
                    try {
                        x = parseInt(line) - 1;

                        if (x < 0 || x >= Main.width) {
                            Main.error.setText(Messages.GOT_OUT_OF_WIDTH).printlnAndReset();
                        } else {
                            valueAccepted = true;
                        }
                    } catch (NumberFormatException e) {
                        Main.error.setText(Messages.ERROR).printlnAndReset();
                    }
                }
            } catch (IOException e) {
                Main.error.setText(Messages.ERROR).printlnAndReset();
            }
        }

        // y-coordinate of touching cell
        if (!Main.flag) {
            valueAccepted = false;

            while (!valueAccepted) {
                out.print("y: ");

                try {
                    line = r.readLine();

                    if (line == null || line.isEmpty()) {
                        valueAccepted = true;
                        Main.flag = true;
                    } else if (Main.EXIT.contains(line.toLowerCase())) {
                        Main.exit();
                    } else {
                        try {
                            y = parseInt(line) - 1;

                            if (y < 0 || y >= Main.height) {
                                Main.error.setText(Messages.GOT_OUT_OF_HEIGHT).printlnAndReset();
                            } else {
                                valueAccepted = true;
                            }
                        } catch (NumberFormatException e) {
                            Main.error.setText(Messages.ERROR).printlnAndReset();
                        }
                    }
                } catch (IOException e) {
                    Main.error.setText(Messages.ERROR).printlnAndReset();
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
                Main.notify.setText(Messages.TOUCHED).printlnAndReset();
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
                        String color;
                        switch (around) {
                            case 1:
                                color = Main.C1;
                                break;
                            case 2:
                                color = Main.C2;
                                break;
                            case 3:
                                color = Main.C3;
                                break;
                            case 4:
                                color = Main.C4;
                                break;
                            case 5:
                                color = Main.C5;
                                break;
                            case 6:
                                color = Main.C6;
                                break;
                            case 7:
                                color = Main.C7;
                                break;
                            default:
                                color = Main.C8;
                                break;
                        }
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
            FlagThread.flag.setText("\nSwitched to Flag mode.\n").printlnAndReset();
            new FlagThread().start();
        }
    }
}