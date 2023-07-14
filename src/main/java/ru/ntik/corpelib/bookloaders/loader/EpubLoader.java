package ru.ntik.corpelib.bookloaders.loader;

import nl.siegmann.epublib.domain.Resources;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.ntik.corpelib.bookloaders.book.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.ntik.corpelib.bookloaders.book.Page;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EpubLoader extends BookLoader {
    private final EpubReader epubReader = new EpubReader();
    private Map<String, String> imageMap = new HashMap<>();

    // thanks to https://stackoverflow.com/a/31964093
    // TODO: zipLists - extract to some utility class
    public static <A, B> List<Map.Entry<A, B>> zipLists(List<A> as, List<B> bs) {
        return IntStream.range(0, Math.min(as.size(), bs.size()))
                .mapToObj(i -> Map.entry(as.get(i), bs.get(i)))
                .collect(Collectors.toList());
    }

    List<Page> parseSpineToPages(String html) {
        Document document = Jsoup.parse(html);

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
        int pagesCount = pageElements.size();

        // thanks to https://stackoverflow.com/a/22829036
        List<Integer> pageIndexes = IntStream.rangeClosed(0, pagesCount-1).boxed().toList();

        List<Map.Entry<Integer, Document>> pagesZip = zipLists(pageIndexes, pageElements);

        // initializing with nulls
        final List<Page> pages = new ArrayList<>(Collections.nCopies(pagesCount, null));

        pagesZip.parallelStream().forEach(zip->{
            int index = zip.getKey();
            Document page = zip.getValue();
            pages.set(index,
                    new Page(index+1, page.text(), page.html()));
        });

        return pages;
    }

    /**
     * Extracting file name from resource path
     * (ex. "../images/section_1/cover.png" -> "cover.png"
     * @param path resource path
     * @return file name
     */
    // TODO: Tests - extractFileName
    // TODO: extractFileName - test with different books, make sure that filenames cant overlap
    String extractFileName(String path) {
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