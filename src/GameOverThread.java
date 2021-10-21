import static java.lang.System.out;

class GameOverThread extends Thread{
    @Override
    public void run() {

        if (Main.lose) {
            Main.error.setText("\n" + repeat("=")).printlnAndReset();
            Main.drawResultMap();
            Main.error.setText(Messages.LOSE + "\n" + repeat("-")).printlnAndReset();
        } else {
            NewGameThread.green.setText("\n" + repeat("=")).printlnAndReset();
            Main.drawResultMap();
            NewGameThread.green.setText(Messages.WON + "\n" + repeat("-")).printlnAndReset();
        }
        out.println(Main.NUM + Messages.NUM_IS + Main.EMPTY + Messages.EMPTY_IS);

        if (Main.flags != 0) {
            out.println(Main.FLAG + Messages.FLAG_IS);
        }
        out.println(Main.MINE + Messages.MINE_IS);

        if (Main.lose) {
            out.println(Main.UNTOUCHED + Messages.UNTOUCHED_IS);
            out.println(Main.LAST + Messages.LAST_IS);
        }
        new UserWaitingThread().start();
    }

    private static String repeat(String pattern) {
        StringBuilder sb = new StringBuilder(pattern);
        for (int i = 0; i < Math.max((3 + Main.width * 3 + (Main.height < 10 ? 0 : 2)), 9); i++) {
            sb.append(pattern);
        }
        return sb.toString();
    }
}