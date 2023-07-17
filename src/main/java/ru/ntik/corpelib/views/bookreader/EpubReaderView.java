package ru.ntik.corpelib.views.bookreader;

import ru.ntik.corpelib.bookloaders.loader.EpubLoader;

public class EpubReaderView extends BookReaderView{
    public EpubReaderView(){
        super();
        bookLoader = new EpubLoader();
    }
}
