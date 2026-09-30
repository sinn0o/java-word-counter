# 프로젝트 진행 기록

이 파일을 채워 10월 1일 발표용 PPT를 준비합니다. 구현하지 않은 기능은 '미구현'으로 표시하고, 심화 항목은 진행한 경우에만 작성하세요. 발표는 분석·조회·저장 시연을 포함해 5~10분입니다. 10월 1일 전후로 코드와 이 파일을 본인의 GitHub 저장소에 업로드하고, 저장소 링크를 강사에게 전달해 리뷰를 받습니다. 자세한 안내는 [진행 기록과 발표](docs/project-guide.md)에 있습니다.

## 1. 실행 방법

- JDK: 21
- IntelliJ에서 실행할 클래스: `kr.sesac.wordcounter.Main`
- 작업 디렉터리(`pom.xml`이 있는 폴더): 저장소 루트 폴더(`java-word-counter-main`, `pom.xml`이 직접 들어 있는 폴더)
- 설정 위치와 현재 값: CSV 열 / TSV 열 / HTML 본문 선택자
    - 열 이름과 HTML 선택자는 **변수값을 하드코딩하여 설정**
    - CSV 열: `src/main/java/kr/sesac/wordcounter/extractFiles.java`의 `extractCsv` 메서드 안 `csvColumns` 변수, 현재 값 `text`
    - TSV 열: 같은 파일 `extractTsv` 메서드 안 `tsvColumns` 변수, 현재 값 `document`
    - HTML 본문 선택자: 같은 파일 `extractHtml` 메서드 안 `doc.select("#content")`, 현재 값 `#content`

## 2. 구현한 기능

| 기능 | 상태(완료·진행 중·미구현) | 확인한 입력과 결과 |
|---|---|---|
| TXT 카운팅 | 완료 | 파일 또는 폴더 경로 > samples/equivalent/basic.txt<br/><br/>분석 완료<br/>입력: samples/equivalent/basic.txt<br/>파일: 시도 1개 / 성공 1개 / 실패 0개 / 지원하지 않아 건너뜀 0개<br/>전체 단어: 9개 / 서로 다른 단어: 6개<br/>처리 시간: 1.0558ms |
| CSV·TSV·HTML 처리 | 완료 | 파일 또는 폴더 경로 > samples/equivalent/basic.tsv<br/><br/>분석 완료<br/>입력: samples/equivalent/basic.tsv<br/>파일: 시도 1개 / 성공 1개 / 실패 0개 / 지원하지 않아 건너뜀 0개<br/>전체 단어: 9개 / 서로 다른 단어: 6개<br/>처리 시간: 0.9587ms<br/><br/>파일 또는 폴더 경로 > samples/equivalent/basic.csv<br/><br/>분석 완료<br/>입력: samples/equivalent/basic.csv<br/>파일: 시도 1개 / 성공 1개 / 실패 0개 / 지원하지 않아 건너뜀 0개<br/>전체 단어: 9개 / 서로 다른 단어: 6개<br/>처리 시간: 0.9782ms<br/><br/>파일 또는 폴더 경로 > samples/equivalent/basic.html<br/>분석 완료<br/>입력: samples/equivalent/basic.html<br/>파일: 시도 1개 / 성공 1개 / 실패 0개 / 지원하지 않아 건너뜀 0개<br/>전체 단어: 9개 / 서로 다른 단어: 6개<br/>처리 시간: 113.8963ms |
| 여러 파일 순차 처리 | 완료 | 파일 또는 폴더 경로 > samples/equivalent/<br/><br/>분석 완료<br/>입력: samples/equivalent/<br/>파일: 시도 4개 / 성공 4개 / 실패 0개 / 지원하지 않아 건너뜀 0개<br/>전체 단어: 36개 / 서로 다른 단어: 6개<br/>처리 시간: 4.1396ms |
| 상위 단어·특정 단어 조회 | 완료 | (basic.txt 기준)<br/>선택: 2<br/>몇 개를 볼까요? (기본 10) > 3<br/><br/>1. java : 3회<br/>2. 자료구조 : 2회<br/>3. java17 : 1회 |
| 전체 결과 저장 | 완료 | 선택: 4<br/>전체 결과 6개 단어를 out\counts.tsv에 저장했습니다.<br/><br/>`word count`<br/>`java 3`<br/>`자료구조 2`<br/>`java17 1`<br/>`ㅋㅋ 1`<br/>`공부했다 1`<br/>`자바를 1` |
| 잘못된 입력·실패 파일·빈 파일 처리 | 완료 | &lt;잘못된 입력&gt;<br/>선택: 1<br/>파일 또는 폴더 경로 > ddd<br/>경로를 찾을 수 없습니다: ddd<br/><br/>&lt;실패 파일&gt;<br/>파일 또는 폴더 경로 > samples/invalid<br/>파일 처리 실패: samples\invalid\broken-quote.csv (CSV 형식 오류: org.apache.commons.csv.CSVException: (startline 3) EOF reached before encapsulated token finished)<br/>파일 처리 실패: samples\invalid\missing-column.csv (text 열이 없습니다)<br/>파일 처리 실패: samples\invalid\missing-content.html (#content 요소를 정확히 1개 찾지 못했습니다.)<br/>파일 처리 실패: samples\invalid\wrong-width.tsv (레코드의 셀 수가 헤더와 다릅니다.)<br/>분석 실패: 모든 파일을 처리하지 못했습니다<br/>입력: samples/invalid<br/>파일: 시도 4개 / 성공 0개 / 실패 4개 / 지원하지 않아 건너뜀 0개<br/>전체 단어: 0개 / 서로 다른 단어: 0개<br/>처리 시간: 3.1522ms<br/><br/>&lt;빈 파일 처리&gt;<br/>*temp라는 빈 폴더를 만들었음<br/>파일 또는 폴더 경로 > samples/temp<br/>분석할 수 있는 지원 파일이 없습니다: samples/temp |

## 3. 정확성 확인과 처리 시간

- 작은 기본 샘플의 전체 결과를 정답과 비교한 방법:
    - `samples/equivalent/basic.txt`분석 후 `out/counts.tsv`로 저장
    - `expected/basic-counts.tsv`와 비교
    - 결과: 9개 · 6종(같은 결과) // 단어 · 횟수 · 정렬 순서 일치
- CSV 따옴표·줄바꿈을 확인한 결과:
    - `samples/edge/quoted-lines.csv`를 `text` 열 설정으로 분석 후 `out/counts.tsv`로 저장
    - `expected/quoted-lines-counts.tsv`와 비교
    - 결과: 5개 · 3종 (같은 결과) // 줄바꿈 때문에 레코드가 쪼개지지 않음을 확인
- 일부 파일이 실패했을 때 확인한 결과:
    - `data/local/error-demo` 폴더에 `samples/equivalent/basic.txt`와 `samples/invalid/broken-quote.csv`를 넣고 함께 분석
    - `expected/basic-counts.tsv` 와 비교
    - 결과: 시도 2개 / 성공 1개 / 실패 1개 // 깨진 파일의 내용이 집계에 섞이지 않음을 확인
- 결과 저장 파일 위치: `out/counts.tsv`

CSV의 분석 열은 `text`입니다. 아래 입력은 각각 따로 실행합니다. 정답은 필수 요구사항의 큰 데이터 처리를 참고하세요.

| 입력 | 데이터 건수 / 파일 수 | 전체 단어 수 | 종류 수 | 처리 시간 | 완료·오류 |
| --- | --- | --- | --- | --- | --- |
| `data/klue-ynat/news-1000.csv` | 1,000 / 1 | 6,991 | 5,052 | 158.2725ms | 완료 |
| `data/klue-ynat/news-10000.csv` | 10,000 / 1 | 70,374 | 28,871 | 170.3535ms | 완료 |
| `data/klue-ynat/news-full.csv` | 45,678 / 1 | 321,084 | 78,309 | 419.7431ms | 완료 |
| `data/klue-ynat/many` | 45,678 / 16, 순차 처리 | 321,084 | 78,309 | 388.3376ms | 완료 |
- 전체 파일 하나와 16개 파일의 **모든 단어별 횟수**를 비교한 방법과 결과:
    - `news-full.csv` 분석 후 저장한 `out/counts.tsv`를 별도 파일로 복사
    - `many` 폴더(16개 파일)를 분석해 다시 저장한 `out/counts.tsv`와 비교
    - 결과: 파일의 값이 동일했으며, `expected/news-full-counts.tsv`와도 차이가 없었다.

## 4. 구현 중 해결한 문제

- 문제: jsoup과 Apache Commons CSV의 사용
- 원인: 어떤 기능이 있는지 몰라서 구현 자체를 시작하는 것이 어려웠다.
- 해결하거나 시도한 방법:
  - 구글에 “jsoup html”, “자바 jsoup”, ‘csv 파일 자바로” 등의 키워드로 검색하기
  - Calude에게 jsoup과 Apache Commons CSV으로 확장자별 파일을 탐색하는 방법 묻기
- 확인한 입력과 결과: samples의 파일을 읽어오는 데에 성공했다.


- 문제: Case 1의 구현
- 원인: 필요할 때마다 매서드를 만드는 방식으로 접근했더니 불필요한 매서드가 많아져 오히려 가독성이 떨어졌다.
- 해결하거나 시도한 방법: 매서드를 전부 해체하고 우선 case 1에 전부 써둔 뒤 기능에 따라 매서드로 분리하는 방식을 사용했다.
- 확인한 입력과 결과: 예외 처리를 한 뒤, 파일을 불러오고, 확장자를 확인한 다음 파일을 분석해 결과를 저장하는 기능을 구현할 수 있었다.
- +다만 아직도 매서드 하나에 너무 많은 기능이 들어가 있어서 100% 해결된 문제라고 보긴 어렵다. 하나의 변수가 여러 곳에 쓰이다보니 기능을 분리하는 과정에서 어떤 식으로 변수를 전달해야 할지 공부가 더 필요할 것 같다.

## 5. 심화(진행한 경우만)

- 한 파일 처리 개선: 바꾼 부분, 전후 시간, 결과 동일 여부
- 여러 파일 병렬 처리: 입력 폴더, 스레드 수 1·2·4, 전후 시간, 결과 동일 여부
- 중단 후 재개·데이터 수집·기타: 사용법과 확인한 결과

성능을 비교했다면 측정 기기·JDK, 예열·반복 횟수, 전체 작업의 중앙값을 적습니다. 표 양식은 [심화 요구사항](docs/advanced.md)에 있습니다.

## 6. AI 대화 또는 참고 자료

- 웹 대화에서 물어본 개념·힌트·오류 설명: tsv, csv, html 파일을 읽고 처리하는 방법 (특히, jsoup과 Apache Commons CSV의 사용법과 api 등) +이 코드에서 처리하지 못하는, 들어왔을 때 오류가 날 법한 파일 예시와 그로 인해 발생 가능한 오류들의 힌트 등
- 도움을 바탕으로 직접 구현한 내용: `extractFiles.extractTsv` , `extractFiles.extractCsv` , `extractFiles.extractHtml` 모듈에서 jsoup 코드와 Apache Commons CSV를 사용한 부분들, 예외 처리 부분들
- 직접 확인한 입력과 결과: samples의 파일을 읽어오는 데에 성공했다.
- 참고 링크: https://spatiumwdev.tistory.com/36  https://offbyone.tistory.com/116#google_vignette


- 웹 대화에서 물어본 개념·힌트·오류 설명: return을 하나밖에 할 수 없는 자바 매서드에서 파일 분석 결과를 저장하는 방법에 관한 힌트
- 도움을 바탕으로 직접 구현한 내용: record를 사용할 것을 추천받아  `Analysis.AnalysisSummary`  record 구현
- 직접 확인한 입력과 결과: case 5에서 저장된 분석값을 불러올 수 있었다.
- 참고 링크: https://s7won.tistory.com/2


- 그외: 구현에 누락은 없는지 체크리스트 형식으로 보여주기

## 7. 발표할 내용

- 구현한 기능과 전체 처리 흐름
- 시연할 파일·폴더와 정답
- 분석 → 조회 → 저장 시연
- 해결한 문제 또는 성능 실험에서 알게 된 점
- 남은 문제와 더 개선하고 싶은 부분
