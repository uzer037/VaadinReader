package ru.ntik.corpelib.bookloaders.loader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class BookLoaderTest {

    @Test
    void isWorking() {
        assertThatCode(()->BookLoader.isFitsOnPage(15)).doesNotThrowAnyException();
    }

    @Test
    void bigNumbers() {
        assertThat(BookLoader.isFitsOnPage(BookLoader.maxCharPerPage/10)).isTrue();
        assertThat(BookLoader.isFitsOnPage(BookLoader.maxCharPerPage*10)).isFalse();
    }

    @Test
    void edgeCaseFits() {
        assertThat(BookLoader.isFitsOnPage(BookLoader.maxCharPerPage)).isTrue();
    }

    @Test
    void edgeCaseBigger() {
        assertThat(BookLoader.isFitsOnPage(BookLoader.maxCharPerPage + 1)).isFalse();
    }
}