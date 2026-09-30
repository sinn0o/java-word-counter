package kr.sesac.wordcounter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Analysis analysis = new Analysis();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            while (running) {
                printMenu();
                int menu = readInt(scanner, "선택: ");

                switch (menu) {
                    case 1: {
                        analysis.checkFiles(scanner);
                        break;
                    }

                    case 2: {
                        if (analysis.getWordCount() == null) {
                            System.out.println("분석이 되지 않았습니다. 분석을 먼저 진행하세요.");
                            break;
                        }
                        findNthWords(scanner, analysis.getWordCount());

                        break;
                    }

                    case 3: {
                        if (analysis.getWordCount() == null) {
                            System.out.println("분석이 되지 않았습니다. 분석을 먼저 진행하세요.");
                            break;
                        }
                        wordSearch(scanner, analysis.getWordCount());
                        break;
                    }

                    case 4: {
                        if (analysis.getWordCount() == null) {
                            System.out.println("분석이 되지 않았습니다. 분석을 먼저 진행하세요.");
                            break;
                        }
                        makeOutFile(analysis.getWordCount());

                        break;
                    }

                    case 5:
                        if (analysis.getWordCount()==null) {
                            System.out.println("분석이 되지 않았습니다. 분석을 먼저 진행하세요.");
                            break;
                        }
                        analysis.printSummary();

                        break;

                    case 0:
                        running = false;
                        System.out.println("종료합니다.");
                        break;
                    default:
                        System.out.println("메뉴에 있는 번호를 선택하세요.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. 새 분석 시작");
        System.out.println("2. 상위 N개 단어 보기");
        System.out.println("3. 특정 단어 횟수 찾기");
        System.out.println("4. 전체 결과 저장");
        System.out.println("5. 최근 분석 요약 보기");
        System.out.println("0. 종료");
    }

    static String readText(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            String text = readText(scanner, prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("정수로 입력하세요.");
            }
        }
    }

    private static List<String> sortWords (Map<String, Long> wordCount){
        List<String> keySet = new ArrayList<>(wordCount.keySet()); //value 순서대로 나오도록 하는법
        keySet.sort((o1, o2) -> {
            int result = wordCount.get(o2).compareTo(wordCount.get(o1)); //횟수 내림차순
            if (result != 0) {
                return result;
            }
            return o1.compareTo(o2); //단어 오름차순
        }); //여기까지 람다식
        return keySet;
    }

    static List<String> tokenize(String line) {
        String[] words = line.split("[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");
        List<String> result = new ArrayList<>();
        for (String word : words) {
            String lower = word.toLowerCase();
            if (lower.isEmpty() || lower.matches("\\d+")) {
                continue;
            }
            result.add(lower);
        }
        return result;
    }

    private static void findNthWords (Scanner scanner, Map<String, Long> wordCount){ //case 2
        int count = 0;
        while (true) {
            String searchCount = readText(scanner, "몇 개를 볼까요? (기본 10) > ");
            if (searchCount.isEmpty()) {
                count = 10;
                break;
            }
            try {
                count = Integer.parseInt(searchCount);
                if (count < 1) {
                    System.out.println("1 이상의 정수를 입력하세요.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("1 이상의 정수를 입력하세요.");
            }
        }

        List<String> keySet = sortWords(wordCount);
        int iteration = Math.min(count, wordCount.size());
        for (int i = 0; i < iteration; i++) {
            System.out.println(i + 1 + ". " + keySet.get(i) + " : " + wordCount.get(keySet.get(i)) + "회");
        }
    }

    private static void wordSearch(Scanner scanner, Map<String, Long> wordCount){ //case3
        while (true) {
            String searchWord = readText(scanner, "찾을 단어 > ");
            List<String> tokens = tokenize(searchWord);
            if (tokens.size() != 1) {
                System.out.println("단어 하나를 입력하세요.");
                continue;
            }
            String key = tokens.get(0); //찾을 단어 리스트에서 string으로 변환해주는거임
            System.out.println(key + " : " + wordCount.getOrDefault(key, 0L) + "회");
            break;
        }
    }

    private static void makeOutFile (Map<String, Long> wordCount){ //case4
        List<String> keySet = sortWords(wordCount);
        Path output = Path.of("out/counts.tsv");

        try {
            Files.createDirectories(output.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
                writer.write("word" + "\t" + "count");
                writer.newLine(); //처음 헤더
                for (String word : keySet) {
                    writer.write(word + "\t" + wordCount.get(word));
                    writer.newLine(); //단어들 넣음
                }
            }
            System.out.println("전체 결과 " + keySet.size() + "개 단어를 " + output + "에 저장했습니다.");
        }catch (IOException e) {
            System.out.println("파일 처리 실패: " + e.getMessage());
        }
    }
}
