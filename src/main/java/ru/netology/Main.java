package ru.netology;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

public class Main {
    public static BlockingQueue<String> aQueue = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> bQueue = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> cQueue = new ArrayBlockingQueue<>(100);
      final static int COUNT = 10_000;

    public static void main(String[] args) throws InterruptedException {

        Thread fillThread = new Thread(() -> {
            for (int i = 0; i < COUNT; i++) {
                String text = generateText("abc", 100_000);
                try {
                    aQueue.put(text);
                    bQueue.put(text);
                    cQueue.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        fillThread.start();

        Thread aThread = new Thread(() -> {
            System.out.println("Наибольшее количество буквы a равно "
                    + collectLetters(COUNT, 'a', aQueue));
        });
        aThread.start();

        Thread bThread = new Thread(() -> {
            System.out.println("Наибольшее количество буквы b равно "
                    + collectLetters(COUNT, 'b', bQueue));
        });
        bThread.start();

        Thread cThread = new Thread(() -> {
            System.out.println("Наибольшее количество буквы c равно "
                    + collectLetters(COUNT, 'c', cQueue));
        });
        cThread.start();


        fillThread.join();
        aThread.join();
        bThread.join();
        cThread.join();



    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int collectLetters(int count, char letter, BlockingQueue<String> queue) {
        int r = 0;
        for (int i = 0; i < count; i++) {
            try {
                String text = queue.take();
                int aFound = (int) IntStream
                        .range(0, text.length())
                        .filter((c) -> {
                            return letter == text.charAt(c);
                        }).count();

                if (r < aFound) {
                    r = aFound;
                }
            } catch (InterruptedException e) {
                return 0;
            }
        }
        return r;
    }
}