package kr.sesac.wordcounter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class extractFiles
{
    static List<String> extract(Path file) throws IOException {
        String name = file.getFileName().toString().toLowerCase();
        String ext = name.substring(name.lastIndexOf('.') + 1);
        switch (ext) {
            case "txt": return extractTxt(file);
            case "tsv": return extractTsv(file);
            case "csv": return extractCsv(file);
            case "html", "htm": return extractHtml(file);
            default: throw new IOException("지원하지 않는 형식: " + ext);
        }
    }

    private static List<String> extractTxt(Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static List<String> extractTsv(Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        List<String> tsvColumns = List.of("document");

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("파일이 비어 있어 헤더를 읽을 수 없습니다.");
            }
            headerLine = headerLine.trim();
            String[] columnArray = headerLine.split("\t");
            List<String> headerList = Arrays.asList(columnArray);

            List<Integer> targetIndexes = new ArrayList<>(); //인덱스 저장용 리스트
            for (String col : tsvColumns) {
                int idx = headerList.indexOf(col);
                if (idx == -1) {
                    throw new IOException(col + " 열이 없습니다");
                }
                targetIndexes.add(idx);
            }

            String line;
            long recordNumber = 0;
            while ((line = reader.readLine()) != null) {
                recordNumber++;
                String[] columns = line.split("\t", -1);
                if (columns.length != columnArray.length) {
                    throw new IOException(recordNumber+"번 레코드의 셀 수가 헤더와 다릅니다.(기대: " + columnArray.length + "개, 실제: " + columns.length + "개)");
                }
                for (int idx : targetIndexes) {
                    String cell = columns[idx];
                    if (!cell.isBlank()) lines.add(cell);
                }
            }
        }
        return lines;
    }

    private static List<String> extractCsv(Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        List<String> csvColumns = List.of("text");
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            try (CSVParser parser = CSVParser.parse(reader, format)) {

                if (parser.getHeaderNames().isEmpty()) {
                    throw new IOException("파일이 비어 있어 헤더를 읽을 수 없습니다.");
                }

                for (String col : csvColumns) {
                    if (!parser.getHeaderNames().contains(col)) {
                        throw new IOException(col + " 열이 없습니다");
                    }
                }

                try {
                    for (CSVRecord record : parser) {
                        if (!record.isConsistent()) {
                            throw new IOException(record.getRecordNumber()+"번 레코드의 셀 수가 헤더와 다릅니다.(기대: " + parser.getHeaderNames().size() + "개, 실제: " + record.size() + "개)");
                        }
                        for (String col : csvColumns) {
                            String cell = record.get(col);
                            if (!cell.isBlank()) lines.add(cell);
                        }
                    }
                } catch (UncheckedIOException e) {
                    throw new IOException("CSV 형식 오류: " + e.getMessage());
                }
            }
        }
        return lines;
    }

    private static List<String> extractHtml(Path file) throws IOException {
        List<String> lines = new ArrayList<>();

        Document doc = Jsoup.parse(file.toFile(), "UTF-8");

        Elements elements = doc.select("#content");

        if (elements.size() != 1) {
            throw new IOException("#content 요소를 정확히 1개 찾지 못했습니다.");
        }

        Element content = elements.first().clone();
        content.select("script, style, nav, header, footer").remove();
        lines.add(content.text());

        return lines;
    }
}
