package com.understandai.understandai.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import org.apache.poi.xslf.usermodel.*;
import com.understandai.understandai.model.PowerpointSlide;

@Service
public class PowerpointService {

    public byte[] createPresentationFromAnalysis(String aiResponse) throws IOException {
        List<PowerpointSlide> slideContents = parseSlides(aiResponse);
        return createPowerpoint(slideContents);
    }

    public static List<PowerpointSlide> parseSlides(String aiResponse) {
        List<PowerpointSlide> slides = new ArrayList<>();

        String[] slideParts = aiResponse.split("\\n\\n");
        List<String> slidePartsList = new ArrayList<>();

        for (String part : slideParts) {
            slidePartsList.add(part.trim());
        }

        for (int i = 0; i < slidePartsList.size(); i++) {
            String part = slidePartsList.get(i);
            if (part.isEmpty()) {
                slidePartsList.remove(i);
            }
        }


        for (String part : slidePartsList) {
            String title = "";
            String subtitle = "";
            String content = "";

            if (part.contains("Title:")) {
                title = extractLine(part, "Title:");
            }
            if (part.contains("Subtitle:")) {
                subtitle = extractLine(part, "Subtitle:");
            }
            if (part.contains("Content:")) {
                content = extractLine(part, "Content:");
            }
            if (!title.isEmpty()) {
                PowerpointSlide slide = new PowerpointSlide(title, content, subtitle);
                slides.add(slide);
            }
        }

        return slides;
    }

    public byte[] createPowerpoint(List<PowerpointSlide> slides) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow();

        // Get default layout master
        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);

        for (int i = 0; i < slides.size(); i++) {
            PowerpointSlide slideData = slides.get(i);
            XSLFSlide slide = ppt.createSlide(layout);
            slide.getBackground().setFillColor(new Color(172, 102, 234)); // White background

            XSLFTextShape titleShape = slide.getPlaceholder(0);
            if (slideData.getTitle() != null) {
                titleShape.clearText(); // Clean default text
                titleShape.setText(slideData.getTitle());
            }

            XSLFTextShape contentShape = slide.getPlaceholder(1);
            contentShape.clearText();

            if (i == 0 && slideData.getSubtitle() != null) {
                // For title slide, add subtitle as second line in title or custom text box
                XSLFTextParagraph para = contentShape.addNewTextParagraph();
                XSLFTextRun run = para.addNewTextRun();
                run.setText(slideData.getSubtitle());
                run.setFontSize(36.0);
                run.setFontColor(Color.BLACK); // Black
            } else if (slideData.getContent() != null) {
                // Split content into bullet points if it's long
                String[] bulletPoints = slideData.getContent().split("\\.\\s+");
                for (String point : bulletPoints) {
                    XSLFTextParagraph para = contentShape.addNewTextParagraph();
                    para.setBullet(true);
                    XSLFTextRun run = para.addNewTextRun();
                    run.setText(point.trim() + ".");
                    run.setFontSize(24.0);
                }
            }
        }

        // Write to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ppt.write(out);
        ppt.close();

        return out.toByteArray();
    }

    private static String extractLine(String part, String key) {
        if (key.equals("Content:")) {
            // For content, we want everything after the key
            int startIndex = part.indexOf(key);
            if (startIndex != -1) {
                return part.substring(startIndex + key.length()).trim();
            }
            return "";
        }
        for (String line : part.split("\n")) {
            if (line.trim().startsWith(key)) {
                return line.replaceFirst(key, "").trim();
            }
        }
        return "";
    }
}
