import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Integer.parseInt;
import static java.lang.System.out;

class FlagThread extends Thread {
    private static int x, y;

    @Override
    public void run() {
        boolean valueAccepted = false;
        String line;
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        out.println(Messages.FLAG);

        // x-coordinate of flag
        while (!valueAccepted) {
            out.print("x: ");

            try {
                line = r.readLine();

                if (line == null || line.length() == 0) {
                    out.println();
                    valueAccepted = true;
                    Main.flag = false;
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

        // y-coordinate of flag
        if (Main.flag) {
            valueAccepted = false;

            while (!valueAccepted) {
                out.print("y: ");

                try {
                    line = r.readLine();

                    if (line == null || line.length() == 0) {
                        out.println();
                        new TouchingThread().start();
                        out.println("flag thread interrupt");
                        currentThread().interrupt();
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

        if (Main.flag) {
            if (!Main.fieldCreated) {
                Main.generateMines();
                Main.fieldCreated = true;
            }

            // flag placement logic
            if (Main.touched[y][x]) {
                out.println(Messages.TOUCHED);
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
            out.println("Switched to Touch mode.\n");
            new TouchingThread().start();
        }
    }
}