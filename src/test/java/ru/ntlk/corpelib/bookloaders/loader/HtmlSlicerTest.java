package ru.ntlk.corpelib.bookloaders.loader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class HtmlSlicerTest {
    @Test
    void basicGetPrevElement() {
        String previousTagHtml = "<p>Goodby previous, world!</p>";

        Document document = Jsoup.parse("" +
                "<div class=\"Test\">" +
                    previousTagHtml +
                    "<p>Hello, new world!</p>" +
                "</div>"
        );
        Element element = document.select("p").get(1);

        assertThatCode(()->HtmlSlicer.getPreviousElement(element)).doesNotThrowAnyException();

        Element actualElement = HtmlSlicer.getPreviousElement(element);
        assertThat(actualElement.outerHtml()).isEqualTo(previousTagHtml);
    }

    @Test
    void getPrevElementOnDifferentLevel() {
        String previousTagHtml = "<p>Goodby previous, world!</p>";

        Document document = Jsoup.parse("" +
                "<div>" +
                    previousTagHtml +
                    "<div class=\"Test\">" +
                        "<p>Hello, new world!</p>" +
                    "</div>"+
                "</div>"
        );
        Element element = document.select("p").get(1);

        assertThatCode(()->HtmlSlicer.getPreviousElement(element)).doesNotThrowAnyException();

        Element actualElement = HtmlSlicer.getPreviousElement(element);
        assertThat(actualElement.outerHtml()).isEqualTo(previousTagHtml);
    }

    @Test
    void getPrevElementWhenNoParent() {
        String previousTagHtml = "<p>Goodby previous, world!</p>";

        Document document = Jsoup.parse("" +
                "<div>" +
                previousTagHtml +
                "<div class=\"Test\">" +
                "<p>Hello, new world!</p>" +
                "</div>"+
                "</div>"
        );
        Element element = document.select("p").get(1);

        assertThatCode(()->HtmlSlicer.getPreviousElement(element)).doesNotThrowAnyException();

        Element actualElement = HtmlSlicer.getPreviousElement(element);
        assertThat(actualElement.outerHtml()).isEqualTo(previousTagHtml);
    }

    @Test
    void getPrevElementIfThereNone() {
        Document document = Jsoup.parse("" +
                "<div>" +
                    "<div class=\"Test\">" +
                        "<p>Hello, new world!</p>" +
                    "</div>"+
                "</div>"
        );
        Element element = document.select("p").get(0);

        assertThatCode(()->HtmlSlicer.getPreviousElement(element)).doesNotThrowAnyException();

        Element actualElement = HtmlSlicer.getPreviousElement(element);
        assertThat(actualElement).isNull();
    }

    @Test
    void splitElement() {
        Document document = Jsoup.parse("" +
                "<div>" +
                    "<div class=\"Test\">" +
                        "<p id='1'>a</p>" +
                        "<p id='2' class=\"" + HtmlSlicer.SPLIT_CLASS_NAME + "\">b</p>" +
                        "<p id='3'>c</p>" +
                    "</div>"+
                "</div>");

        List<Document> parts = HtmlSlicer.splitHtmlByMarkedElement(document);
        Elements childrenPartOne = parts.get(0).select("p");
        Elements childrenPartTwo = parts.get(1).select("p");
        // right amounts in each part
        assertThat(childrenPartOne).size().isEqualTo(1);
        assertThat(childrenPartTwo).size().isEqualTo(2);

        assertThat(childrenPartOne.select("#1")).isNotEmpty();
        assertThat(childrenPartTwo.select("#1")).isEmpty();
    }

    @Test
    void splitElementIsAtRight() {
        Document document = Jsoup.parse("" +
                "<div>" +
                    "<div class=\"Test\">" +
                        "<p id='1'>a</p>" +
                        "<p id='2' class=\"" + HtmlSlicer.SPLIT_CLASS_NAME + "\">b</p>" +
                        "<p id='3'>c</p>" +
                    "</div>" +
                "</div>");

        List<Document> parts = HtmlSlicer.splitHtmlByMarkedElement(document);
        Elements childrenPartOne = parts.get(0).select("p");
        Elements childrenPartTwo = parts.get(1).select("p");

        assertThat(childrenPartOne.select("#2")).isEmpty();
        assertThat(childrenPartTwo.select("#2")).isNotEmpty();
    }

    @Test
    void splitByLength() {
        String sampleText = "Sample Text."; // 12 characters text
        int maxLength = 24;
        Document document = Jsoup.parse("" +
                "<div>" +
                    "<div class=\"Test\">" +
                        "<p class='class1'>" + sampleText + "</p>" +
                        "<p class='class2'>" + sampleText + "</p>" +
                        "<p class='class3'>" + sampleText + "</p>" +
                    "</div>" +
                    "<div class=\"Test\">" +
                        "<p class='class1'>" + sampleText + "</p>" +
                        "<p class='class2'>" + sampleText + "</p>" +
                        "<p class='class3'>" + sampleText + "</p>" +
                    "</div>" +
                    "<div class=\"Test\">" +
                        "<p class='class1'>" + sampleText + "</p>" +
                        "<p class='class2'>" + sampleText + "</p>" +
                        "<p class='class3'>" + sampleText + "</p>" +
                    "</div>" +
                "</div>");

        /*
            splitting by 24 characters, such that every 2 paragraphs will fall on the same page
            which will show how method handles slicing html structure
         */
        List<Document> parts = HtmlSlicer.splitHtmlByContentLength(document, maxLength);
        /*
            9 paragraphs grouped by 2 will give 5 groups, so there should be 5 parts
         */
        assertThat(parts).size().isEqualTo(5);

        // due to how Element.text() works, it's better to leave 5% tolerance for resulting string size
        int lengthWithTolerance = (int) Math.round(maxLength * 1.05);

        for (Document part: parts) {
            assertThat(part.text()).hasSizeLessThanOrEqualTo(lengthWithTolerance);
        }
    }
}