package ru.ntik.corpelib.views.bookreader;

import ru.ntik.corpelib.bookloaders.book.Book;

import java.io.IOException;

public interface BookReader {
    public void gotoPage(int number);

    /** Flips to the next page. If on last page - does nothing */
    public void nextPage();

    /** Flips to the previous page. If on first page - does nothing */
    public void prevPage();

    public void loadBook(Book book);
    public void loadBook(String name) throws IOException;
}
