package ru.ntlk.corpelib.views.main;

import ru.ntlk.corpelib.Category;
import ru.ntlk.corpelib.views.categories.CategoriesView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;

@PageTitle("Список кник")
@Route(value = "")
public class MainView extends VerticalLayout {
    private TreeDataProvider<Category> categoryProvider;
    public MainView() {
        categoryProvider = new TreeDataProvider<>(getCategories());
        add(new CategoriesView(categoryProvider));
    }

    private TreeData<Category> getCategories() {
        TreeData<Category> categories = new TreeData<>();
        categories.addRootItems(
                new Category("Разработка"),
                new Category("Менеджмент"),
                new Category("Саморазвитие")
        );
        Optional<Category> codeRootCategory = categories.getRootItems().stream()
                .filter(c->c.getName().equals("Разработка")).findFirst();
        if(codeRootCategory.isPresent())
        {
            categories.addItems(codeRootCategory.get(),
                new Category("Фреймворки"),
                new Category("Качество кода"),
                new Category("Тестирование")
            );
        }
        Optional<Category> managementRootCategory = categories.getRootItems().stream()
                .filter(c->c.getName().equals("Менеджмент")).findFirst();
        if(managementRootCategory.isPresent()) {
            categories.addItems(managementRootCategory.get(),
                    new Category("Инструменты и подходы управления"),
                    new Category("Организация команд")
            );
        }
        return categories;
    }
}
