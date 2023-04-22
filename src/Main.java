import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static ArrayBlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static int sizeText = 10_000;
    public static int lengthText = 100_000;


    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < sizeText; i++) {
                String text = generateText("abc", lengthText);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();

        Thread threadA = new Thread(() -> {
            char letter = 'a';
            long maxLength;
            try {
                maxLength = findLetter(queueA, letter);
            } catch (InterruptedException e) {
                System.out.printf("Thread %s interrupted \n" + Thread.currentThread().getName());
                maxLength = -1;
            }
            System.out.printf("Максимальное количество символов %s в тексте %d \n", letter, maxLength);
        });

        Thread threadB = new Thread(() -> {
            char letter = 'b';
            long maxLength;
            try {
                maxLength = findLetter(queueB, letter);
            } catch (InterruptedException e) {
                System.out.printf("Thread %s interrupted \n" + Thread.currentThread().getName());
                maxLength = -1;
            }
            System.out.printf("Максимальное количество символов %s в тексте %d \n", letter, maxLength);
        });

        Thread threadC = new Thread(() -> {
            char letter = 'c';
            long maxLength;
            try {
                maxLength = findLetter(queueC, letter);
            } catch (InterruptedException e) {
                System.out.printf("Thread %s interrupted \n" + Thread.currentThread().getName());
                maxLength = -1;
            }
            System.out.printf("Максимальное количество символов %s в тексте %d \n", letter, maxLength);
        });

        threadA.start();
        threadB.start();
        threadC.start();

        threadA.join();
        threadB.join();
        threadC.join();
    }

    public static long findLetter(ArrayBlockingQueue<String> queue, char letter) throws InterruptedException {
        long maxLength = 0;
        String text;
        long count;
        for (int i = 0; i < sizeText; i++) {
            text = queue.take();
            count = text.chars().filter(ch -> ch == letter).count();
            if (count > maxLength) {
                maxLength = count;
            }
        }
        return maxLength;
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