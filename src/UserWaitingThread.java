import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class UserWaitingThread extends Thread{
    @Override
    public void run() {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(Messages.QUESTION.toString() + Messages.ANSWERS);
        System.out.print(Messages.SO);
        String answer;

        try {
            answer = r.readLine().toLowerCase();

            if (answer.equalsIgnoreCase(Messages.YES.toString())) {
                new NewGameThread().start();
            } else {
                new Termicol(Messages.THANKS).setFG(255, 200, 0).setEffect(Effect.BOLD).println();
            }
        } catch (IOException e) {
            new Termicol(Messages.THANKS).setFG(255, 200, 0).setEffect(Effect.BOLD).println();
        }
    }
}