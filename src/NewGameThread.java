class NewGameThread extends Thread{
    static final Termicol green = new Termicol().setFG(0, 255, 100)
            .setEffect(Effect.FAINT);

    @Override
    public void run() {
        green.setText(Messages.NEW).printlnAndReset();
        Main.fieldCreated = false;
        Main.createField();
        Main.drawResultMap();
        new TouchingThread().start();
    }
}