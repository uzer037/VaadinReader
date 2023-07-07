package ru.ntlk.corpelib.bookloaders.book;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PageTest {

    /*
        Page page1 = new Page(1, "Hello, World!", "<p>Hello, World!</p>");
        Page page2 = new Page(2, "Goodbye, World!", "<p>Goodbye, World!</p>");
     */
    @Test
    void getPageNumber() {
        Page page = new Page(1, "Hello, World!", "<p>Hello, World!</p>");
        assertThat(page.getPageNumber()).isEqualTo(1);
    }

    @Test
    void updatePageNumber() {
        Page page = new Page(1, "Hello, World!", "<p>Hello, World!</p>");
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThatCode(()->page.updatePageNumber(15)).doesNotThrowAnyException();
        assertThat(page.getPageNumber()).isEqualTo(15);
    }

    @Test
    void size() {
        String pageText = "Hello, World!";
        Page page = new Page(1, pageText, "<p>Hello, World!</p>");
        assertThat(page.size()).isEqualTo(pageText.length());
    }

    @Test
    void getText() {
        String pageText = "Hello, World!";
        Page page = new Page(1, pageText, "<p>Hello, World!</p>");
        assertThat(page.getText()).isEqualTo(pageText);
    }

    @Test
    void getHtml() {
        String pageHtml = "<p>Hello, World!</p>";
        Page page = new Page(1, "Hello, World!", pageHtml);
        assertThat(page.getHtml()).isEqualTo(pageHtml);
    }

    @Test
    void testToString() {
        String pageText = "Hello, World!";
        String pageHtml = "<p>Hello, World!</p>";
        int pageNumber = 1;
        Page page = new Page(pageNumber, pageText, pageHtml);
        assertThat(page).hasToString("{number: " + pageNumber +
                ", text: \"" + pageText + "\", html: \"" + pageHtml + "\"}");
    }
}