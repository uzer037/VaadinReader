package ru.ntlk.corpelib.bookloaders.loader;

import nl.siegmann.epublib.domain.Resources;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class EpubLoader extends BookLoader {
    private final EpubReader epubReader = new EpubReader();
    private Map<String, String> imageMap = new HashMap<>();

    List<Page> parseSpineToPages(String html) {
        Document document = Jsoup.parse(html);
        List<Page> pages = new ArrayList<>();

        // inserting base64 images
        Elements imgElements = document.select("img");
        for(Element img : imgElements) {
            /*
                removing relative path indicators
                e.g. "../path/to/file.png" -> "/path/to/file.png"
             */
            String src = extractFileName(img.attr("src"));
            System.out.println("Trying to find" + src);

            if(imageMap.containsKey(src)) {
                System.out.println("Replaced " + src + " with base64 image");
                img.attr("src", "data:image/png;base64, " + imageMap.get(src));
            }
        }

        List<Document> pageElements = HtmlSlicer.splitHtmlByContentLength(document, maxCharPerPage);

        int pageIndex = 1;
        for(Document page : pageElements) {
            pages.add(new Page(pageIndex, page.text(), page.outerHtml()));
            pageIndex++;
        }
        return pages;
    }

    /**
     * Extracting file name from resource path
     * (ex. "../images/section_1/cover.png" -> "cover.png"
     * @param path resource path
     * @return file name
     */
    String extractFileName(String path) {
        // TODO: test with different books, make sure that filenames cant overlap
        int pos = path.lastIndexOf("/") + 1;
        if (pos > 0) {
            return path.substring(pos);
        } else {
            return path;
        }
    }
    @Override
    public Book loadBook(InputStream bookStream) throws IOException {

        nl.siegmann.epublib.domain.Book epubBook = epubReader.readEpub(bookStream);
        List<SpineReference> references = epubBook.getSpine().getSpineReferences();
        Resources resources = epubBook.getResources();

        StringBuilder stringBuilder;

        imageMap = new HashMap<>();

        // first identifying all documents and loading images
        for(Resource res : resources.getAll()) {
            if (MediatypeService.isBitmapImage(res.getMediaType())) {
                System.out.println(res.getHref());

                imageMap.put(extractFileName(res.getHref()), Base64.encodeBase64String(res.getData()));
            }
        }

        // then, when all images are loaded, processing documents
        Book book = new Book();
        for(SpineReference ref : references) {
            Resource res = ref.getResource();
            if(res.getMediaType() == MediatypeService.XHTML) {


                stringBuilder = new StringBuilder();
                String line;

                BufferedReader bufferedReader = new BufferedReader(res.getReader());

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                book.addPages(parseSpineToPages(stringBuilder.toString()));
            }
        }

        return book;
    }
}