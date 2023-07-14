package ru.ntik.corpelib.bookloaders.book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Book {
    private final List<Page> pages = new ArrayList<>();

    public static Book mergeBookParts(List<Book> parts) {
        Book book = new Book();
        for(Book part : parts) {
            book.addPages(part.getPages());
        }
        return book;
    }
    // for images 25 px of height approximately equals to 1 row of characters (60 chars)
    public void addPage(Page page) {
        page.updatePageNumber(getNextPageNumber());
        pages.add(page);
    }
    public void addPages(Collection<Page> pages) {
        int lastPageNumber = getNextPageNumber() - 1; // -1 for array offset
        this.pages.addAll(pages);
        for(int i = lastPageNumber; i < getNextPageNumber() - 1; i++) {
            this.pages.get(i).updatePageNumber(i+1);
        }
    }

    public List<Page> getPages() {
        return pages;
    }

    /**
     * ru.ntlk.corpelib.bookparser.Book.Page numeration goes from 1
     * @return Next page number available
     */
    public int getNextPageNumber() {
        return pages.size() + 1;
    }

    public Page getPage(int number) throws IndexOutOfBoundsException{
        if(pages.size() >= number) {
            return pages.get(number - 1);
        }
        else {
            throw new IndexOutOfBoundsException("ru.ntlk.corpelib.bookparser.Book.Page #" + number + " does not exists");
        }
    }
}
