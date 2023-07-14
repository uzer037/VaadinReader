package ru.ntik.corpelib.bookloaders.loader;

import org.junit.jupiter.api.Test;
import ru.ntik.corpelib.bookloaders.book.Book;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void zipLists() {
        List<Integer> a = List.of(1,2,3,4);
        List<String> b = List.of("a","b","c","d");

        Map<Integer, String> expectedMap = new HashMap<>(){{
            put(1,"a");
            put(2,"b");
            put(3,"c");
            put(4,"d");
        }};
        List<Map.Entry<Integer, String>> resultList = EpubLoader.zipLists(a,b);
        Map<Integer,String> resultMap = resultList.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertThat(resultMap).containsAllEntriesOf(expectedMap);
    }
}
