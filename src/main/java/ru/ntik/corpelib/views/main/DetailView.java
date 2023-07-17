package ru.ntik.corpelib.views.main;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import ru.ntik.corpelib.views.bookreader.BookReaderView;
import ru.ntik.corpelib.views.bookreader.EpubReaderView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@PageTitle("Книга")
@Route(value = "detail/:book/:page")
public class DetailView extends VerticalLayout implements BeforeEnterObserver {
    // elements
    BookReaderView bookReaderView = new EpubReaderView();
    String bookName;

    public DetailView() throws IOException {
        // UI stuff
        add(new Button("< К каталогу",
                        buttonClickEvent -> UI.getCurrent().navigate(MainView.class)));

        // functional stuff
        bookReaderView.addReaderPageListener(pageNumber -> {
            // from Vaadin docs: https://vaadin.com/docs/latest/routing/updating-url-parameters
            RouteParameters parameters = new RouteParameters(
                    new RouteParam("book", bookName),
                    new RouteParam("page", Integer.toString(pageNumber))
            );
            String deepLinkingUrl = RouteConfiguration.forSessionScope()
                    .getUrl(DetailView.class, parameters);
            UI.getCurrent().getPage().getHistory().replaceState(null, deepLinkingUrl);
        });
        add(bookReaderView);
    }
    private void loadBook(String name) throws FileNotFoundException {
        this.bookName = name;
        try {
            bookReaderView.loadBook(name);
        } catch (IOException e) {
            throw new FileNotFoundException("Book can't be loaded. Details: " + e);
        }
    }

    public void gotoPage(int number) {
        try {
            bookReaderView.gotoPage(number);
        } catch (IndexOutOfBoundsException e) {
            Notification.show("Некорректный номер страницы")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            bookReaderView.gotoPage(1);
        }
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
                gotoPage(1);
            } else {
                int number = Integer.parseInt(pageParam.get());
                gotoPage(number);
            }
        } catch (NumberFormatException e) {
            Notification.show("Некорректный номер страницы")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            gotoPage(1);
        }
    }
}
