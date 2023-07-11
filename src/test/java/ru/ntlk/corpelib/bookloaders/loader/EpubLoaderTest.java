package ru.ntlk.corpelib.bookloaders.loader;

import org.junit.jupiter.api.Test;
import ru.ntlk.corpelib.bookloaders.book.Book;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class EpubLoaderTest {
    private static String getFormattedDuration(Duration duration) {
        return getFormattedDuration(duration, false);
    }
    private static String getFormattedDuration(Duration duration, boolean useNanoseconds) {
        StringBuilder stringBuilder = new StringBuilder();

        long value = duration.toDays();
        if(value > 0) {
            stringBuilder.append(value + " Days ");
        }
        value = duration.toHours() % 24;
        if(value > 0) {
            stringBuilder.append(value + " Hours ");
        }
        value = duration.toMinutes() % 60;
        if(value > 0) {
            stringBuilder.append(value + " Minutes ");
        }
        value = duration.toSeconds() % 60;
        if(value > 0) {
            stringBuilder.append(value + " Seconds ");
        }
        value = duration.toMillis() % 1000;
        if(value > 0) {
            stringBuilder.append(value + " Milliseconds ");
        }
        if(useNanoseconds) {
            value = duration.toNanos() % 1000000L;
            if (value > 0) {
                stringBuilder.append(value + " Nanoseconds ");
            }
        }
        // removing last space
        stringBuilder.deleteCharAt(stringBuilder.length()-1);

        return stringBuilder.toString();
    }

    @Test
    void parseTest() throws IOException{
        EpubLoader parser = new EpubLoader();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("spine_example.xhtml");
        final String spineXHTML = stream.readAllBytes().toString();

        assertThatCode(()->parser.parseSpineToPages(spineXHTML)).doesNotThrowAnyException();
    }
    @Test
    void loadAndParseTest() throws IOException{
        EpubLoader parser = new EpubLoader();
        Book book = null;

        InputStream bookStream = getClass().getClassLoader().getResourceAsStream("treasure-island.epub");

        assertThatCode(()->parser.loadBook(bookStream)).doesNotThrowAnyException();
    }
    void speedTest() {
        EpubLoader parser = new EpubLoader();
        Book book = null;

        long startTime = System.nanoTime();
        int booksCount = 10;
        for(int i = 0; i < booksCount; i++) {
            try {
                InputStream bookStream = getClass().getClassLoader().getResourceAsStream("treasure-island.epub");
                book = parser.loadBook(bookStream);
            } catch (IOException e) {
                System.err.println("File provided does not exists or inaccessible: " + e);
            }
        }
        long endTime = System.nanoTime();
        Duration duration = Duration.ofNanos(endTime-startTime);
        Duration avgDuration = Duration.ofNanos((endTime-startTime)/booksCount);
        String formattedElapsedTime = getFormattedDuration(duration);
        String formattedAvgElapsedTime = getFormattedDuration(avgDuration);

        System.out.println("Processed " + booksCount + " books in " + formattedElapsedTime);
        System.out.println("(" + formattedAvgElapsedTime + " per book on average)");
    }
}
