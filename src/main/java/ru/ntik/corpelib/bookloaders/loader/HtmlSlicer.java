package ru.ntik.corpelib.bookloaders.loader;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class HtmlSlicer {
    private HtmlSlicer() {}

    /**
     * Temporary html class name used to mark elements by which to split document
     * Should be somewhat long and uncommon to not interfere with documents own classes
     */
    static final String SPLIT_CLASS_NAME = "slicersplitpoint";
    /**
     * Returns previous body element
     * Might return null if there is no previous element in body or at all
     * @param element
     * @return
     */
    static Element getPreviousElement(Element element) {
        Element previous = element.previousElementSibling();
        while(previous == null && element.hasParent()) {
            element = element.parent();
            previous = element.previousElementSibling();
        }
        if(previous != null && previous.tagName().equals("head")) {
            return null;
        }
        return previous;
    }
    // TODO: Tests - splitHtmlByMarkedElement
    static List<Document> splitHtmlByMarkedElement(Document document) {
        Document preSplitDoc = document.clone();
        Document postSplitDoc = document;

        /*
            How split occurs:
            E - leaf element that does not fit to current page ("split element")
            t - other leaf tags
            -Doc#1- | -Doc#2-
            t t t t | E t t t
         */

        // removed all elements starting from splitElement
        Element preSplit = getPreviousElement(preSplitDoc.select("."+ SPLIT_CLASS_NAME).get(0));
        if (preSplit != null) {
            if (!preSplit.nextElementSiblings().isEmpty()) {
                preSplit.nextElementSiblings().remove();
            }
            while (preSplit.hasParent()) {
                preSplit = preSplit.parent();
                if (!preSplit.nextElementSiblings().isEmpty()) {
                    preSplit.nextElementSiblings().remove();
                }
            }
        }
        // removed all elements prior to splitElement (keeping splitElement)
        Element postSplit = postSplitDoc.select("."+ SPLIT_CLASS_NAME).get(0);
        // removing temporary class after using it
        postSplit.removeClass(SPLIT_CLASS_NAME);
        if(!postSplit.previousElementSiblings().isEmpty()) {
            postSplit.previousElementSiblings().remove();
        }
        while(postSplit.hasParent()) {
            postSplit = postSplit.parent();
            if(!postSplit.previousElementSiblings().isEmpty()) {
                postSplit.previousElementSiblings().remove();
            }
        }
        return List.of(preSplitDoc,postSplitDoc);
    }
    public static List<Document> splitHtmlByContentLength(Document document, int maxLength) {
        SplitPointMarker marker = new SplitPointMarker();
        document = marker.markSplitPoints(document, maxLength);

        // how much splits requiered
        int splitCount = document.select("."+SPLIT_CLASS_NAME).size();

        // splitting document by split elements
        List<Document> documentPages = new ArrayList<>(splitCount+1);
        Document unprocessedPart = document;

        for (int i = 0; i < splitCount; i++) {
            List<Document> parts = splitHtmlByMarkedElement(unprocessedPart);
            documentPages.add(parts.get(0));
            unprocessedPart = parts.get(1);
        }
        documentPages.add(unprocessedPart);
        return documentPages;
    }

    // dirty trick to make static class store some intermediate data during recursive calls
    // TODO: Tests - class SplitPointMarker
    static class SplitPointMarker {
        Document document;
        long currentPageLength;
        long maxLength;
        public Document markSplitPoints(Document document, long maxLength) {
            this.document = document;
            this.maxLength = maxLength;
            tryFitOnPage(this.document.root());
            return this.document;
        }
        private void tryFitOnPage(Element element) {
            // there is no point in look for head, style or script elements
            if (element.tagName().equals("head") || element.tagName().equals("style")
                    || element.tagName().equals("script")) {
                return;
            }

            long textLength = element.text().length();

            // if element fit on page - fit and return to parent
            if(currentPageLength + textLength < maxLength) {
                this.currentPageLength = this.currentPageLength + textLength;
            } else {
                // else - check if any of children fit
                if (element.childrenSize() != 0) {
                    for (Element child : element.children()) {
                        // for every child updating currentPageLength
                        tryFitOnPage(child);
                    }
                } else {
                    // if no children exist - mark as split element
                    element.addClass(SPLIT_CLASS_NAME);
                    // and reset page character count
                    this.currentPageLength = 0;
                }
            }
        }
    }
}
