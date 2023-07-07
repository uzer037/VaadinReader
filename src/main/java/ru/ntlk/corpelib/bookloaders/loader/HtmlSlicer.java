package ru.ntlk.corpelib.bookloaders.loader;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        // getting all leaf-elements (elements without child elements)
        Elements leaves = document.select(":not(:has(*))");
        // list of css selectors
        int pageSymbolCount = 0;
        int leafCharCount = 0;

        // how much splits requiered
        int splitCount = 0;

        // getting split elements
        for(Element leaf : leaves) {
            leafCharCount = leaf.text().length();
            if(pageSymbolCount + leafCharCount > maxLength) {
                // marking leaf as split element
                leaf.addClass(SPLIT_CLASS_NAME);
                splitCount++;
                pageSymbolCount = 0;
            }
            pageSymbolCount += leafCharCount;
        }

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
}
