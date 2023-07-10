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
    private final Html viewerFrame = new Html("<div>Loading your book...</div>");
    private final Paragraph pageIndicator = new Paragraph();
    private final EpubLoader parser = new EpubLoader();

    private Book book = null;
    private int pageNumber = 1;

    public DetailView() throws IllegalAccessException {
        // functional stuff
        loadBook();
        redrawPage();
        add(viewerFrame);

        // UI stuff
        Button prevPageButton = new Button("<");
        Button nextPageButton = new Button(">");

        HorizontalLayout controlsLayout = new HorizontalLayout();
        prevPageButton.addSingleClickListener(e -> {
            selectPrevPage();
            redrawPage();
        });
        controlsLayout.add(prevPageButton);

        pageIndicator.setText(String.valueOf(pageNumber));
        controlsLayout.add(pageIndicator);

        nextPageButton.addSingleClickListener(e -> selectNextPage());
        controlsLayout.add(nextPageButton);
        add(controlsLayout);
    }

    private void loadBook() throws IllegalAccessException {
        long start = System.currentTimeMillis();
        try (InputStream bookStream = getClass().getClassLoader().getResourceAsStream("Spring REST 2022.epub")) {
            book = parser.loadBook(bookStream);


        } catch (IOException e) {
            throw new IllegalAccessException("Wrong book");
        }
    }

    private void redrawPage() {
        viewerFrame.setHtmlContent("<div class='book-content'>" + book.getPage(pageNumber).getHtml() + "</div>");
        pageIndicator.setText(String.valueOf(pageNumber));

    }

    private void selectPrevPage() {
        if (pageNumber != 1) {
            pageNumber--;
        }
    }

    private void selectNextPage() {
        if (pageNumber != book.getNextPageNumber() - 1) {
            pageNumber++;
        }
    }
}
