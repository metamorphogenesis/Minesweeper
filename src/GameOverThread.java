import static java.lang.System.out;

class GameOverThread extends Thread{
    @Override
    public void run() {
        out.println(Messages.DOUBLE_LINE);
        Main.drawResultMap();

        if (Main.lose) {
            out.println(Messages.LOSE);
        } else {
            out.println(Messages.WON);
        }
        out.println( Messages.LINE + "\n" +
                Main.NUM + Messages.NUM_IS +
                Main.EMPTY + Messages.EMPTY_IS);

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
}