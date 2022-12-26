package io;

import core.OrderBook;

import java.io.*;
import java.util.*;

public class RWFile {
    public static void readFile(String filename) {
        List<OrderBook> listBid = new ArrayList<>();
        List<OrderBook> listAsk = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int index = 0;

            while ((line = reader.readLine()) != null) {
                OrderBook orderBook = new OrderBook();
                Scanner scanner = new Scanner(line);
                scanner.useDelimiter(",");
                if (line.charAt(0) == 'u') {
                    while (scanner.hasNext()) {
                        String data = scanner.next();
                        if (index == 1) {
                            orderBook.setPrice(Integer.parseInt(data));
                        } else if (index == 2) {
                            orderBook.setSize(Integer.parseInt(data));
                        }
                        index++;
                    }
                    if (line.endsWith("bid")) listBid.add(orderBook);
                    else if (line.endsWith("ask")) listAsk.add(orderBook);
                } else if (line.charAt(0) == 'q') {
                    if (line.endsWith("best_bid")) {
                        int size = listBid.size();
                        if (size >= 1) {
                            Collections.sort(listBid);
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                                writer.write(String.valueOf(listBid.get(0)));
                            }
                        }
                    } else if (line.endsWith("best_ask")) {
                        int size = listAsk.size();
                        if (size >= 1) {
                            Collections.sort(listAsk);
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                                writer.write(String.valueOf(listAsk.get(0)));
                            }
                        }
                    } else if (line.contains("size")) {
                        while (scanner.hasNext()) {
                            String data = scanner.next();
                            if (index == 2) {
                                int specifiedPrice = Integer.parseInt(data);
                                int temp;
                                temp = RWFile.specifiedPrice(listAsk, listBid, specifiedPrice);
                                try(BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                                    writer.write(temp + "\n");
                                }
                            }
                            index++;
                        }
                    }
                } else if (line.charAt(0) == 'o') {
                    while (scanner.hasNext()) {
                        String data = scanner.next();
                        if (index == 2 && line.contains("buy")) {
                            int currentSize = Integer.parseInt(data);
                            int minPrice = RWFile.minValue(currentSize, listAsk);
                            listAsk.removeIf((OrderBook o) -> o.getSize() == currentSize && o.getPrice() == minPrice);
                        } else if (index == 2 && line.contains("sell")) {
                            int currentSize = Integer.parseInt(data);
                            int maxPrice = RWFile.maxValue(currentSize, listBid);
                            listBid.removeIf((OrderBook o) -> o.getSize() == currentSize && o.getPrice() == maxPrice);
                        }
                        index++;
                    }
                }
                index = 0;
            }

        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
    }

    private static int minValue(int currentSize, List<OrderBook> list) {
        int minValue = Integer.MAX_VALUE;
        for (OrderBook orderBook : list) {
            if (orderBook.getSize() == currentSize && minValue > orderBook.getPrice()) {
                minValue = orderBook.getPrice();
            }
        }
        return minValue;
    }

    private static int maxValue(int currentSize, List<OrderBook> list) {
        int maxValue = Integer.MIN_VALUE;
        for (OrderBook orderBook : list) {
            if (orderBook.getSize() == currentSize && maxValue < orderBook.getPrice()) {
                maxValue = orderBook.getPrice();
            }
        }
        return maxValue;
    }

    private static int specifiedPrice(List<OrderBook> list1, List<OrderBook> list2, int price) {
        int i = 0;
        for (OrderBook orderBook : list1) {
            if (orderBook.getPrice() == price) {
                i++;
            }
        }
        for (OrderBook orderBook : list2) {
            if (orderBook.getPrice() == price) {
                i++;
            }
        }
        return i;
    }
}
