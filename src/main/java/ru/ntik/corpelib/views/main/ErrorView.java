package ru.ntik.corpelib.views.main;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.Optional;

@Route("error/:err")
public class ErrorView extends VerticalLayout implements BeforeEnterObserver {

    private final Span errorTextBlock = new Span("Error: no errors");
    public ErrorView() {
        add(errorTextBlock);
    }

    private void setErrorText(String text) {
        errorTextBlock.setText("Ошибка: " + text);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> errorCode = event.getRouteParameters().get("err");
        if(errorCode.isEmpty()) {
            setErrorText("Не задан код ошибки");
        } else {
            String code = errorCode.get();
            switch (code) {
                case "book-404":
                    setErrorText("Указанная книга не найдена");
                    break;
                // add more cases if needed
                default:
                    setErrorText("Неизвестный код ошибки");
                    break;
            }
        }
    }
}
