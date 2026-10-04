package kr.sesac.wordcounter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static kr.sesac.wordcounter.extractFiles.extract;

public class AnalysisForPR {
    private HashMap<String, Long> wordCount = null;
    private AnalysisSummary lastSummary = null;
    private long totalNum = 0;

    public Map<String, Long> getWordCount() {
        HashMap<String, Long> temp = new HashMap<>();
        if (wordCount == null) {
            return null;
        }
        temp.putAll(wordCount);
        return temp;
    }

    public AnalysisSummary getLastSummary() {
        return lastSummary;
    }

    public void printSummary() {
        PrintSummary(lastSummary);
    }

    public void checkFiles (Scanner scanner){
        String inputPath;
        List<Path> files;
        int skippedFiles;

        while (true) {
            //입력, 존재 여부 및 확장자 체크
            inputPath = Main.readText(scanner, "파일 또는 폴더 경로 (이전으로 돌아갈 경우 0 입력)> ");
            Path path;
            try {
                int num = Integer.parseInt(inputPath);
                if (num == 0) {
                    return;
                }
            } catch (NumberFormatException e) {
            }
            try {
                path = Path.of(inputPath);
            } catch (InvalidPathException e) {
                System.out.println("경로를 찾을 수 없습니다: " + inputPath);
                continue;
            }
            if (!Files.exists(path)) {
                System.out.println("경로를 찾을 수 없습니다: " + inputPath);
                continue;
            }

            files = new ArrayList<>();
            skippedFiles = 0;
            Set<String> validExtension = Set.of("txt", "csv", "tsv", "html", "htm");

            if (Files.isDirectory(path)) {
                try (Stream<Path> stream = Files.list(path)) {
                    for (Path p : stream.filter(Files::isRegularFile).sorted().toList()) {
                        String name = p.getFileName().toString().toLowerCase();
                        int index = name.lastIndexOf(".");
                        if (index > 0 && validExtension.contains(name.substring(index + 1))) {
                            files.add(p);
                        } else {
                            skippedFiles++;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("폴더를 읽을 수 없습니다: " + inputPath + " (" + e.getMessage() + ")");
                    continue;
                }
            } else {
                String name = path.getFileName().toString().toLowerCase();
                int index = name.lastIndexOf(".");
                if (index > 0 && validExtension.contains(name.substring(index + 1))) {
                    files.add(path);
                }
                else {
                    System.out.println("유효하지 않은 확장자입니다. 지원 확장자: .txt .csv .tsv .html .htm");
                    continue;
                }
            }

            if (files.isEmpty()) {
                System.out.println("분석할 수 있는 지원 파일이 없습니다: " + inputPath);
                continue;
            }
            break;
        }
        analyzeFiles(files,inputPath,skippedFiles);
    }

    public void analyzeFiles(List<Path> files, String inputPath, int skippedFiles){
        // 파일 읽고 시간재고
        HashMap<String, Long> tempWordCount = new HashMap<>();
        long tempTotalNum = 0;
        int success = 0, fail = 0;
        long startTime = System.nanoTime();

        for (Path file : files) {
            HashMap<String, Long> fileCount = new HashMap<>();
            long fileTotal = 0;

            try {
                List<String> texts = extract(file);
                for (String text : texts) {
                    for (String word : Main.tokenize(text)) {
                        fileCount.put(word, fileCount.getOrDefault(word, 0L) + 1);
                        fileTotal++;
                    }
                }
                for (Map.Entry<String, Long> e : fileCount.entrySet()) {
                    tempWordCount.merge(e.getKey(), e.getValue(), Long::sum);
                }
                tempTotalNum += fileTotal;
                success++;
            } catch (IOException e) {
                fail++;
                System.out.println("파일 처리 실패: " + file + " (" + e.getMessage() + ")");
            }
        }
        long endTime = System.nanoTime();

        // 결과 저장 요약 출력해줌
        if (success==0){
            System.out.println("분석 실패: 모든 파일을 처리하지 못했습니다");
            wordCount = null;
            totalNum = 0;
            lastSummary = new AnalysisSummary(inputPath,files.size(),success,fail,skippedFiles,tempTotalNum,tempWordCount.size(),(endTime-startTime));
            PrintSummary(lastSummary);
        }
        else {
            wordCount = tempWordCount;
            totalNum = tempTotalNum;

            System.out.println("분석 완료");

            lastSummary = new AnalysisSummary(inputPath,files.size(),success,fail,skippedFiles,totalNum,wordCount.size(),(endTime-startTime));
            PrintSummary(lastSummary);
        }
    }


    private record AnalysisSummary(String inputPath,
                                   int fileSize,
                                   int success,
                                   int fail,
                                   int skippedFiles,
                                   long totalNum,
                                   int wordCountSize,
                                   long time

    ){} //결과 저장용 레코드

    private static void PrintSummary(AnalysisSummary s){ //1이랑 5에서 사용
        System.out.println("입력: " + s.inputPath);
        System.out.println("파일: 시도 " + s.fileSize + "개 / 성공 " + s.success
                + "개 / 실패 " + s.fail + "개 / 지원하지 않아 건너뜀 " + s.skippedFiles + "개");
        System.out.println("전체 단어: " + s.totalNum + "개 / 서로 다른 단어: " + s.wordCountSize + "개");
        System.out.println("처리 시간: " + s.time / 1000000.0 + "ms");
    }
}
