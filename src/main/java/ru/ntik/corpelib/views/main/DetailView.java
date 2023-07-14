package ru.ntik.corpelib.views.main;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import ru.ntik.corpelib.bookloaders.loader.EpubLoader;
import ru.ntik.corpelib.bookloaders.book.Book;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@PageTitle("Книга")
@Route(value = "detail/:book/:page")
public class DetailView extends VerticalLayout implements BeforeEnterObserver {
    // elements
    private final Html viewerFrame = new Html("<div>Loading your book...</div>");
    private final Paragraph pageIndicator = new Paragraph();
    private final EpubLoader parser = new EpubLoader();
    private Book book = null;
    private String bookName;
    private int maxPageNumber = 1;
    private int pageNumber = 1;

    public DetailView() throws IOException {
        // UI stuff
        add(new Button("< К каталогу",
                        buttonClickEvent -> UI.getCurrent().navigate(MainView.class)));

        // functional stuff
        add(viewerFrame);

        Button prevPageButton = new Button("<");
        Button nextPageButton = new Button(">");

        HorizontalLayout controlsLayout = new HorizontalLayout();
        prevPageButton.addSingleClickListener(e -> {
            if (pageNumber > 1)
                redirectToPage(pageNumber-1);
        });
        controlsLayout.add(prevPageButton);

        pageIndicator.setText(String.valueOf(pageNumber));
        controlsLayout.add(pageIndicator);

        nextPageButton.addSingleClickListener(e -> {
            if (pageNumber < maxPageNumber)
                redirectToPage(pageNumber+1);
        });
        controlsLayout.add(nextPageButton);
        add(controlsLayout);
    }

    /**
     * Makes sure that pageNumber is in range
     * from a = 1 to b = [last book page]
     * @param pageNumber
     */
    private int clampPageNumber(int pageNumber) {
        int a = 1;
        int b = maxPageNumber;
        return Math.min(Math.max(a, pageNumber), b);
    }

    private void loadBook(String name) throws FileNotFoundException {
        this.bookName = name;
        try (InputStream bookStream = getClass().getClassLoader().getResourceAsStream(name)) {
            book = parser.loadBook(bookStream);
            maxPageNumber = book.getNextPageNumber()-1;
        } catch (IOException e) {
            throw new FileNotFoundException("Book can't be loaded. Details: " + e);
        }
    }

    private void redrawPage() {
        viewerFrame.setHtmlContent("<div class='book-content'>" + book.getPage(pageNumber).getHtml() + "</div>");
        pageIndicator.setText(pageNumber + " / " + (maxPageNumber));
    }

    public void redirectToPage(int pageNumber) {
        final String page = Integer.toString(pageNumber);
        System.out.println("Redirecting to page " + page);
        if(this.getUI().isEmpty()) {
            System.out.println("How tf UI is not present... Just how...");
        }
        this.getUI().ifPresent(
                ui->ui.navigate(this.getClass(),
                        new RouteParam("book", bookName),
                        new RouteParam("page", String.valueOf(pageNumber))
                )
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        RouteParameters parameters = beforeEnterEvent.getRouteParameters();

        // selecting book
        try {
            Optional<String> bookParam = parameters.get("book");
            if(bookParam.isPresent()) {
                loadBook(bookParam.get());
            } else {
                UI.getCurrent().navigate(
                        ErrorView.class,
                        new RouteParam("err","book-404")
                );
            }
        } catch (IOException e) {
            System.err.println("Book not found. Details: " + e);
            UI.getCurrent().navigate(
                    ErrorView.class,
                    new RouteParam("err","book-404")
            );
        }

        try {
            Optional<String> pageParam = parameters.get("page");
            if(pageParam.isEmpty()) {
                redirectToPage(1);
            } else {
                int number = Integer.parseInt(pageParam.get());
                this.pageNumber = clampPageNumber(number);
                if (this.pageNumber == number) {
                    redrawPage();
                } else {
                    this.redirectToPage(this.pageNumber);
                    this.redrawPage();
                }
            }
        } catch (NumberFormatException e) {
            Notification.show("Некорректный номер страницы")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            redirectToPage(1);
        }
    }
}
