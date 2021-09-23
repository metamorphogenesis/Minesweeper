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
                System.out.println(Messages.THANKS);
            }
        } catch (IOException e) {
            System.out.println(Messages.THANKS);
        }
    }
}