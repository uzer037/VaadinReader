package ru.ntik.corpelib.views.bookreader;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import ru.ntik.corpelib.bookloaders.book.Book;
import ru.ntik.corpelib.bookloaders.loader.BookLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class BookReaderView extends Div implements BookReader {
    private int currentPage = 1;
    private Html viewerFrame = new Html("<div class=ReaderViewFrame'>Loading your book...</div>");
    private Span pageIndicator = new Span();
    private Book book;
    protected BookLoader bookLoader;
    protected BookReaderView() {
        // functional stuff
        add(viewerFrame);

        Button prevPageButton = new Button("<");
        Button nextPageButton = new Button(">");

        HorizontalLayout controlsLayout = new HorizontalLayout();
        prevPageButton.addSingleClickListener(e -> prevPage());
        controlsLayout.add(prevPageButton);

        pageIndicator.setText(String.valueOf("1 / 1"));
        controlsLayout.add(pageIndicator);

        nextPageButton.addSingleClickListener(e -> nextPage());
        controlsLayout.add(nextPageButton);
        add(controlsLayout);
    }

    public interface ReaderPageListener {
        public void onPageFlipped(int pageNumber);
    }
    private List<ReaderPageListener> pageListeners = new ArrayList<>();

    public void addReaderPageListener(ReaderPageListener listener) {
        pageListeners.add(listener);
    }

    protected void redrawPage() {
        viewerFrame.setHtmlContent("<div class=ReaderViewFrame'>" + book.getPage(currentPage).getHtml() + "</div>");
        pageIndicator.setText(String.valueOf(currentPage +  " / " + book.getLastPageNumber()));
    }
    @Override
    public void gotoPage(int number) throws IndexOutOfBoundsException{
        if(number <= 0 || book.getLastPageNumber() < number) {
            throw new IndexOutOfBoundsException("Page with number " + number + " does not exist.\n" +
                    "Can only go to (1 .. " + book.getLastPageNumber() + ")");
        } else {
            currentPage = number;
            for(ReaderPageListener listener : pageListeners) {
                listener.onPageFlipped(number);
            }

            redrawPage();
        }
    }

    @Override
    public void nextPage() {
        if(currentPage < book.getLastPageNumber()) {
            gotoPage(currentPage + 1);
        }
    }

    @Override
    public void prevPage() {
        if(currentPage > 1) {
            gotoPage(currentPage - 1);
        }
    }

    @Override
    public void loadBook(Book book) {
        this.book = book;
        currentPage = 1;
        redrawPage();
    }

    @Override
    public void loadBook(String name) throws IOException {
        InputStream bookStream = getClass().getClassLoader().getResourceAsStream(name);
        this.loadBook(bookLoader.loadBook(bookStream));
    }
}
