package ru.ntlk.corpelib.bookloaders.loader;
import ru.ntlk.corpelib.bookloaders.book.Book;

import java.io.IOException;
import java.io.InputStream;

public abstract class BookLoader {
    static int maxCharPerPage = 1800;
    static int maxCharPerLine = 80;
    // Planning Epub, fb2 and pdf support, else is optional
    /**
     * Method that parses book's text file to java object
     * @param stream Path to book in filesystem
     * @return Returns book object
     */
    public abstract Book loadBook(InputStream stream) throws IOException;
    public static boolean isFitsOnPage(int paragraphLength) {
        return paragraphLength <= maxCharPerPage;
    }
    /**
     * tolerable deviation from the maximum number of characters:
     * paragraph will fit to current page if
     * maxCharPerPage - charDelta <= paragraphCharacterCount <= maxCharPerPage + charDelta
     */
}
