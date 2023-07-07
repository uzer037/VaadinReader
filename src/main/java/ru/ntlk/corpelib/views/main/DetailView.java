package ru.ntlk.corpelib.views.main;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.ntlk.corpelib.bookloaders.book.Book;
import ru.ntlk.corpelib.bookloaders.loader.EpubLoader;

import java.io.IOException;
import java.io.InputStream;

@PageTitle("Книга")
@Route(value = "detail")
public class DetailView extends VerticalLayout {
    // elements
    Html viewerFrame = new Html("<div>Loading your book...</div>");
    Paragraph pageIndicator = new Paragraph();
    Button prevPageButton = new Button("<");
    Button nextPageButton = new Button(">");
    EpubLoader parser = new EpubLoader();
    Book book = null;
    InputStream bookStream = getClass().getClassLoader().getResourceAsStream("Spring REST 2022.epub");
    int pageNumber = 1;

    private void loadBook() {
        try {
            book = parser.loadBook(bookStream);
        } catch (IOException e) {
            System.err.println("File provided does not exists or inaccessible: " + e);
        }
    }

    private void redrawPage() {
        if(book != null) {
            viewerFrame.setHtmlContent("<div class=\"book-content\">" + book.getPage(pageNumber).getHtml() + "</div>");
        }
    }
    public DetailView() {
        // functional stuff
        loadBook();
        redrawPage();
        add(viewerFrame);

        // UI stuff
        HorizontalLayout controlsLayout = new HorizontalLayout();
        prevPageButton.addSingleClickListener(e->{selectPrevPage(); redrawPage();});
        controlsLayout.add(prevPageButton);

        pageIndicator.setText(String.valueOf(pageNumber));
        controlsLayout.add(pageIndicator);

        nextPageButton.addSingleClickListener(e->selectNextPage());
        controlsLayout.add(nextPageButton);
        add(controlsLayout);
    }

    private void selectPrevPage() {
        --pageNumber;
        pageNumber = Math.max(pageNumber, 1);
        pageIndicator.setText(String.valueOf(pageNumber));
        redrawPage();
    }
    private void selectNextPage() {
        ++pageNumber;
        pageNumber = Math.min(pageNumber, book.getNextPageNumber()-1);
        pageIndicator.setText(String.valueOf(pageNumber));
        redrawPage();
    }
}
