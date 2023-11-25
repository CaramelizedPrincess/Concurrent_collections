import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    public static final int wordsAmount = 10_000;

    public static void main(String[] args) throws InterruptedException {

        String[] texts = new String[wordsAmount];

        Thread queue = new Thread(() -> {
            for (int i = 0; i < texts.length; i++) {
                texts[i] = generateText("abc", 100000);
                try {
                    queueA.put(texts[i]);
                    queueB.put(texts[i]);
                    queueC.put(texts[i]);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        queue.start();

        Thread thrdA = new Thread(() -> countLetters('a', queueA));
        Thread thrdB = new Thread(() -> countLetters('b', queueB));
        Thread thrdC = new Thread(() -> countLetters('c', queueC));

        thrdA.start();
        thrdB.start();
        thrdC.start();
        thrdA.join();
        thrdB.join();
        thrdC.join();
    }


    public static void countLetters(char letter, BlockingQueue<String> textsQueue) {
        int counter = 0;
        int counterMax = 0;
        for (int i = 0; i < wordsAmount; i++) {
            try {
                String word = textsQueue.take();
                for (int j = 0; j < word.length(); j++) {
                    if (word.charAt(j) == letter) {
                        counter++;
                    }
                }
                if (counter > counterMax) {

                    counterMax = counter;
                }
                counter = 0;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Наибольшее количество символов " + letter + " : " + counterMax);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}