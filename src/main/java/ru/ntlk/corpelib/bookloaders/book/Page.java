package ru.ntlk.corpelib.bookloaders.book;

public class Page {
    private final String text;
    private final String html;
    private int pageNumber;

    public Page(int pageNumber, String text, String html) {
        this.pageNumber = pageNumber;
        this.text = text;
        this.html = html;
    }
    public int getPageNumber() {
        return pageNumber;
    }

    public void updatePageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     *
     * @return page length as it will be displayed (not including tags at text)
     */
    public int size() {
        return text.length();
    }
    public String getText() {
        return text;
    }
    public String getHtml() { return html; }

    @Override
    public String toString() {
        return "{number: " + pageNumber + ", text: \"" + text + "\", html: \"" + html + "\"}";
    }
}
