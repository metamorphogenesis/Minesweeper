import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Integer.parseInt;
import static java.lang.System.out;

class FlagThread extends Thread {
    private static int x, y;
    static final Termicol flag = new Termicol()
            .setFG(225, 225, 100).setEffect(Effect.FAINT);

    @Override
    public void run() {
        boolean valueAccepted = false;
        String line;
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        flag.setText(Messages.FLAG).printlnAndReset();

        // x-coordinate of flag
        while (!valueAccepted) {
            flag.setText("x: ").printAndReset();

            try {
                line = r.readLine();

                if (line == null || line.isEmpty()) {
                    valueAccepted = true;
                    Main.flag = false;
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

        // y-coordinate of flag
        if (Main.flag) {
            valueAccepted = false;

            while (!valueAccepted) {
                flag.setText("y: ").printAndReset();

                try {
                    line = r.readLine();

                    if (line == null || line.isEmpty()) {
                        valueAccepted = true;
                        Main.flag = false;
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

        if (Main.flag) {
            if (!Main.fieldCreated) {
                Main.generateMines();
                Main.fieldCreated = true;
            }

            // flag placement logic
            if (Main.touched[y][x]) {
                Main.notify.setText(Messages.TOUCHED).printlnAndReset();
            } else {
                if (Main.resultMap[y][x].equals(Main.FLAG)) {
                    Main.resultMap[y][x] = Main.UNTOUCHED;
                    Main.flags--;
                } else {
                    Main.resultMap[y][x] = Main.FLAG;
                    Main.flags++;
                }
                Main.drawResultMap();
            }
            new FlagThread().start();
        }

        if (!Main.flag) {
            out.println("\nSwitched to Touch mode.\n");
            new TouchingThread().start();
        }
    }
}