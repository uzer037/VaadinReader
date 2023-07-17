package ru.ntik.corpelib.bookloaders.book;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void mergeBookParts() {
        Book bookPart1 = new Book();
        Page page1 = new Page(1, "Hello, World!", "<p>Hello, World!</p>");
        bookPart1.addPage(page1);

        Book bookPart2 = new Book();
        Page page2 = new Page(2, "Goodbye, World!", "<p>Goodbye, World!</p>");
        bookPart2.addPage(page2);

        assertThatCode(()->Book.mergeBookParts(List.of(bookPart1,bookPart2))).doesNotThrowAnyException();

        Book book = Book.mergeBookParts(List.of(bookPart1,bookPart2));
        assertThat(book.getPages()).isEqualTo(List.of(page1,page2));
    }

    @Test
    void addPage() {
        Book book = new Book();
        Page page1 = new Page(1, "Hello, World!", "<p>Hello, World!</p>");
        Page page2 = new Page(2, "Goodbye, World!", "<p>Goodbye, World!</p>");

        assertThat(book.getPages()).size().isEqualTo(0);

        book.addPage(page1);
        assertThat(book.getPages()).size().isEqualTo(1);
        assertThat(book.getPages()).element(0).isEqualTo(page1);

        book.addPage(page2);
        assertThat(book.getPages()).element(1).isEqualTo(page2);
    }

    @Test
    void addPages() {
        Book book = new Book();
        Page page1 = new Page(1, "Hello, World!", "<p>Hello, World!</p>");
        Page page2 = new Page(2, "Goodbye, World!", "<p>Goodbye, World!</p>");
        List<Page> pages = List.of(page1,page2);

        assertThat(book.getPages()).size().isEqualTo(0);

        book.addPages(pages);
        assertThat(book.getPages()).size().isEqualTo(2);
        assertThat(book.getPages()).isEqualTo(pages);
    }

    @Test
    void getPages() {
        Book book = new Book();
        Page page1 = new Page(1, "Hello, World!", "<p>Hello, World!</p>");
        Page page2 = new Page(2, "Goodbye, World!", "<p>Goodbye, World!</p>");
        List<Page> pages = List.of(page1,page2);

        book.addPages(pages);
        assertThat(book.getPages()).isEqualTo(pages);
    }

    @Test
    void getNextPageNumber() {
        Book book = new Book();
        Page page1 = new Page(1, "Hello, World!", "<p>Hello, World!</p>");
        Page page2 = new Page(2, "Goodbye, World!", "<p>Goodbye, World!</p>");
        List<Page> pages = List.of(page1,page2);

        assertThat(book.getLastPageNumber()).isEqualTo(0);

        book.addPage(page1);
        assertThat(book.getLastPageNumber()).isEqualTo(1);

        book.addPages(pages);
        assertThat(book.getLastPageNumber()).isEqualTo(3);
    }

    @Test
    void getPage() {
        Book book = new Book();
        Page page1 = new Page(1, "Hello, World!", "<p>Hello, World!</p>");

        assertThat(book.getPages()).size().isEqualTo(0);

        book.addPage(page1);
        assertThat(book.getPage(1)).isEqualTo(page1);
        assertThrows(IndexOutOfBoundsException.class, ()->book.getPage(0));
        assertThrows(IndexOutOfBoundsException.class, ()->book.getPage(-1));
        assertThrows(IndexOutOfBoundsException.class, ()->book.getPage(2));
    }
}