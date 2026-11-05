package labs;

import java.nio.file.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.*;

public class AverageCorrector {
  public static void correct(Path input, Path output) throws Exception {
    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
    f.setValidating(true);
    f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    f.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    f.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    DocumentBuilder b = f.newDocumentBuilder();
    b.setEntityResolver(
        (publicId, systemId) -> {
          if (systemId != null && systemId.endsWith("/student.dtd")) {
            return new InputSource(AverageCorrector.class.getResourceAsStream("/student.dtd"));
          }
          throw new SAXException("Only bundled student.dtd is allowed");
        });
    b.setErrorHandler(
        new org.xml.sax.helpers.DefaultHandler() {
          public void error(SAXParseException e) throws SAXException {
            throw e;
          }

          public void fatalError(SAXParseException e) throws SAXException {
            throw e;
          }
        });
    Document doc = b.parse(input.toFile());
    if (doc.getDoctype() == null || doc.getDoctype().getInternalSubset() != null)
      throw new SAXException("Expected external student.dtd only");
    NodeList subjects = doc.getElementsByTagName("subject");
    NodeList averages = doc.getElementsByTagName("average");
    Element root = doc.getDocumentElement();
    if (subjects.getLength() == 0) {
      if (averages.getLength() > 0) root.removeChild(averages.item(0));
    } else {
      int sum = 0;
      for (int i = 0; i < subjects.getLength(); i++)
        sum += Integer.parseInt(((Element) subjects.item(i)).getAttribute("mark"));
      String mean =
          java.math.BigDecimal.valueOf(sum)
              .divide(
                  java.math.BigDecimal.valueOf(subjects.getLength()),
                  10,
                  java.math.RoundingMode.HALF_UP)
              .stripTrailingZeros()
              .toPlainString();
      Element average =
          averages.getLength() == 0 ? doc.createElement("average") : (Element) averages.item(0);
      average.setTextContent(mean);
      root.appendChild(average);
    }
    Path parent = output.toAbsolutePath().getParent();
    Files.createDirectories(parent);
    try (var in = AverageCorrector.class.getResourceAsStream("/student.dtd")) {
      Path dtd = parent.resolve("student.dtd");
      if (!Files.exists(dtd)) Files.copy(in, dtd);
    }
    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    Transformer t = tf.newTransformer();
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "student.dtd");
    t.transform(new DOMSource(doc), new StreamResult(output.toFile()));
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 2) throw new IllegalArgumentException("Usage: input.xml output.xml");
    correct(Path.of(args[0]), Path.of(args[1]));
    System.out.println("Corrected: " + args[1]);
  }
}
