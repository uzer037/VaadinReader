package ru.ntlk.corpelib.bookloaders.loader;

import ru.ntlk.corpelib.bookloaders.book.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.ntlk.corpelib.bookloaders.book.Page;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EpubLoader extends BookLoader {
    private EpubReader epubReader = new EpubReader();
    Book parseSpineToBook(String html) {
        Document document = Jsoup.parse(html);
        Book currentBook = new Book();

        List<Document> pageElements = HtmlSlicer.splitHtmlByContentLength(document, maxCharPerPage);

        for(Document page : pageElements) {
            currentBook.addPage(
                    new Page(currentBook.getNextPageNumber(), page.text(), page.outerHtml())
                    );
        }
        return currentBook;
    }

    @Override
    public Book loadBook(InputStream bookStream) throws IOException {

        nl.siegmann.epublib.domain.Book epubBook = epubReader.readEpub(bookStream);
        List<SpineReference> references = epubBook.getSpine().getSpineReferences();

        StringBuilder stringBuilder;

        List<Book> bookParts = new ArrayList<>();

        for(SpineReference ref : references) {
            Resource res =  ref.getResource();
            if(res.getMediaType() == MediatypeService.XHTML) {
                stringBuilder = new StringBuilder();
                String line;

                BufferedReader bufferedReader = new BufferedReader(res.getReader());

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                bookParts.add(parseSpineToBook(stringBuilder.toString()));
            }
        }

        return Book.mergeBookParts(bookParts);
    }
}