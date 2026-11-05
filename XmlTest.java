package labs;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

class XmlTest {
  @TempDir Path dir;

  void write(String body) throws Exception {
    Files.writeString(
        dir.resolve("in.xml"),
        "<?xml version=\"1.0\"?><!DOCTYPE student SYSTEM \"student.dtd\">" + body);
  }

  @Test
  void fixesAverage() throws Exception {
    write(
        "<student lastname=\"Test\"><subject title=\"A\" mark=\"5\"/><subject title=\"B\""
            + " mark=\"3\"/><average>1</average></student>");
    AverageCorrector.correct(dir.resolve("in.xml"), dir.resolve("out.xml"));
    assertTrue(Files.readString(dir.resolve("out.xml")).contains("<average>4</average>"));
    AverageCorrector.correct(dir.resolve("out.xml"), dir.resolve("again.xml"));
  }

  @Test
  void emptyAndMissing() throws Exception {
    write("<student lastname=\"Test\"><average>NaN</average></student>");
    AverageCorrector.correct(dir.resolve("in.xml"), dir.resolve("out.xml"));
    assertFalse(Files.readString(dir.resolve("out.xml")).contains("<average>"));
    write("<student lastname=\"Test\"><subject title=\"A\" mark=\"5\"/></student>");
    AverageCorrector.correct(dir.resolve("in.xml"), dir.resolve("out.xml"));
    assertTrue(Files.readString(dir.resolve("out.xml")).contains("<average>5</average>"));
  }

  @Test
  void invalidMark() throws Exception {
    write("<student lastname=\"Test\"><subject title=\"A\" mark=\"6\"/></student>");
    assertThrows(
        Exception.class,
        () -> AverageCorrector.correct(dir.resolve("in.xml"), dir.resolve("out.xml")));
  }
}
