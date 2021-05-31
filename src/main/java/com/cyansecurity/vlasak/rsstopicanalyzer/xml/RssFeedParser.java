package com.cyansecurity.vlasak.rsstopicanalyzer.xml;

import com.cyansecurity.vlasak.rsstopicanalyzer.beans.Feed;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RssFeedParser {

    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String ITEM_SELECTOR = "//" + ITEM;

    public RssFeedParser() {
    }

    @Nonnull
    public List<Feed> parse(@Nonnull final String uri) throws RssFeedParserException {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final Document doc = factory.newDocumentBuilder().parse(uri);
            final Element root = doc.getDocumentElement();
            // normalize xml
            root.normalize();
            final XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expression = xPath.compile(ITEM_SELECTOR);
            final NodeList nl = (NodeList) expression.evaluate(root, XPathConstants.NODESET);

            final int itemsCount = nl.getLength();
            if (itemsCount == 0) {
                handleRssFormatError(TITLE, uri);
            }
            final List<Feed> parseResult = new ArrayList<>(itemsCount);
            for (int index = 0; index < itemsCount; index++) {
                final Node node = nl.item(index);
                expression = xPath.compile(TITLE);
                final Node title = (Node) expression.evaluate(node, XPathConstants.NODE);
                expression = xPath.compile(LINK);
                final Node link = (Node) expression.evaluate(node, XPathConstants.NODE);
                checkPresence(title, link, uri);
                parseResult.add(new Feed(title.getTextContent(), link.getTextContent()));
            }
            return parseResult;
        } catch (SAXException |
                XPathExpressionException |
                ParserConfigurationException |
                IOException e) {
            throw new RssFeedParserException(
                    String.format("Some problem with reading/parsing XML from URI: %s", uri), e);
        }
    }

    private void checkPresence(final Node title, final Node link, final String uri) {
        if (null == title) {
            handleRssFormatError(TITLE, uri);
        }
        if (null == link) {
            handleRssFormatError(LINK, uri);
        }
    }

    private void handleRssFormatError(final String tag, final String uri) {
        throw new RssFeedParserException(
                String.format("Incorrect RSS format - missing %s tag by XML from URI: %s", tag, uri));
    }
}
