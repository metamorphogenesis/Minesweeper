class NewGameThread extends Thread{
    @Override
    public void run() {
        System.out.println(Messages.NEW);
        Main.fieldCreated = false;
        Main.createField();
        Main.drawResultMap();
        new TouchingThread().start();
    }
}