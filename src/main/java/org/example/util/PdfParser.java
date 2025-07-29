package org.example.util;

import org.example.model.GrantData;
import org.example.service.DataService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class PdfParser {

    private static final Pattern SPECIALTY_PATTERN = Pattern.compile("^(6B\\d{2}|[BV]\\d{3})\\s-\\s.*");
    private static final Pattern DATA_ROW_START_PATTERN = Pattern.compile("^\\d+\\s+\\d{9}");

    public static Map<String, List<GrantData>> parseGrantsPdf(File file, DataService dataService) {
        Map<String, List<GrantData>> groupedData = new LinkedHashMap<>();
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            String[] lines = text.split("\\r?\\n");

            String currentSpecialtyName = "Не определена";
            List<String> currentBlock = new ArrayList<>();

            for (String line : lines) {
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty() || trimmedLine.matches("^\\d{1,4}$") || trimmedLine.contains("Проходные параметры")) {
                    continue;
                }

                boolean isNewDataRow = DATA_ROW_START_PATTERN.matcher(trimmedLine).find();
                boolean isSpecialty = SPECIALTY_PATTERN.matcher(trimmedLine).matches();

                if ((isNewDataRow || isSpecialty) && !currentBlock.isEmpty()) {
                    processBlock(currentBlock, currentSpecialtyName, groupedData, dataService);
                    currentBlock.clear();
                }

                if (isSpecialty) {
                    currentSpecialtyName = trimmedLine;
                    groupedData.putIfAbsent(currentSpecialtyName, new ArrayList<>());
                } else {
                    currentBlock.add(trimmedLine);
                }
            }
            if (!currentBlock.isEmpty()) {
                processBlock(currentBlock, currentSpecialtyName, groupedData, dataService);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return groupedData;
    }

    private static void processBlock(List<String> block, String specialtyName, Map<String, List<GrantData>> groupedData, DataService dataService) {
        String fullBlockText = String.join(" ", block);
        GrantData grantData = parseFullText(fullBlockText, specialtyName, dataService);
        if (grantData != null) {
            groupedData.computeIfAbsent(specialtyName, k -> new ArrayList<>()).add(grantData);
        }
    }

    private static GrantData parseFullText(String text, String specialtyName, DataService dataService) {
        try {
            String[] parts = text.split("\\s+");
            if (parts.length < 4) return null;

            int number = Integer.parseInt(parts[0]);
            String iin = parts[1];

            List<String> remainingParts = new ArrayList<>(List.of(parts).subList(2, parts.length));

            String universityCode = "N/A";
            String quota = "-";
            int score = 0;

            for (int i = remainingParts.size() - 1; i >= 0; i--) {
                String currentPart = remainingParts.get(i);
                if (currentPart.equalsIgnoreCase("сельская") || currentPart.equalsIgnoreCase("сирота") || currentPart.equalsIgnoreCase("ЧС")) {
                    quota = remainingParts.remove(i);
                } else if (isNumeric(currentPart) && currentPart.length() == 3 && universityCode.equals("N/A")) {
                    universityCode = remainingParts.remove(i);
                }
            }

            for (int i = remainingParts.size() - 1; i >= 0; i--) {
                String currentPart = remainingParts.get(i);
                if (isNumeric(currentPart) && currentPart.length() >= 2 && currentPart.length() <= 3) {
                    score = Integer.parseInt(remainingParts.remove(i));
                    break;
                }
            }

            String fullName = String.join(" ", remainingParts);
            String universityName = dataService.getUniversityNameByCode(universityCode);

            return new GrantData(number, iin, fullName, score, universityCode, universityName, quota, specialtyName);
        } catch (Exception e) {
            System.err.println("Ошибка парсинга блока: " + text);
            return null;
        }
    }

    private static boolean isNumeric(String str) {
        if (str == null) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
