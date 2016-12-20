/* Copyright (C) 2003-2016 Patrick G. Durand
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.txt
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 */
package bzh.plealog.bioinfo.ui.blast.core;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import bzh.plealog.bioinfo.util.DAlphabetUtils;

/**
 * This class defines a Fasta sequence.
 * 
 * @author Patrick G. Durand
 */
public class FastaSequence implements Serializable {

  private static final long  serialVersionUID = 7062743465415292157L;

  public String              sequence;
  public String              name             = NO_NAME;
  private String             id               = NO_ID;
  private int                size             = -1;

  public static final String NO_NAME          = "No definition line";
  public static final String NO_ID            = "No_id";

  /**
   * Do not use. Only for XML serialization.
   */
  public FastaSequence() {
  }

  /**
   * Constructor.
   * 
   * Create a Fasta sequence given an input string. This input has to be Fasta
   * formatted otherwise this constructor throws a FastaSequenceException.
   */
  public FastaSequence(String input) {
    analyseInput(input);
  }

  /**
   * Constructor.
   * 
   * @param header
   *          Fasta header
   * @param letters
   *          sequence
   * @param forceStrictAlphabet
   *          true or false
   */
  public FastaSequence(String header, String seq, boolean forceStrictAlphabet) {
    setHeader(filterText(header, false, false).trim());
    sequence = filterText(seq, true, forceStrictAlphabet).trim();
  }

  /**
   * Constructor.
   * 
   * @param header
   *          Fasta header
   * @param seq
   *          sequence
   */
  public FastaSequence(String header, String seq) {
    setHeader(header);
    sequence = seq;
  }

  /**
   * Clone this object.
   * 
   * @return a FastaSequence object
   */
  public Object clone() {
    FastaSequence fs = new FastaSequence();
    fs.copy(this);
    return fs;
  }

  /**
   * Copy a FastaSequence.
   * 
   * @param src
   *          source sequence object
   */
  protected void copy(FastaSequence src) {
    this.sequence = src.sequence;
    this.id = src.id;
    this.name = src.name;
  }

  /**
   * Filter out data.
   * 
   * @param text
   *          the text to analyze
   * @param forceUpper
   *          true or false
   * @param checkAlphabet
   *          true or false
   */
  private String filterText(String text, boolean forceUpper,
      boolean checkAlphabet) {
    StringBuffer szBuf;
    int i, size;
    char ch, ch2;

    szBuf = new StringBuffer();
    size = text.length();
    for (i = 0; i < size; i++) {
      ch2 = text.charAt(i);
      if (forceUpper)
        ch = Character.toUpperCase(ch2);
      else
        ch = ch2;
      if (ch >= 32) {
        if (checkAlphabet) {
          if (ch == ' ')// skip space
            continue;
          if (!((ch >= 'A' && ch <= 'Z') || ch == '*'))
            throw new FastaSequenceException(
                "Fasta format error: Illegal character: " + ch);
        }
        szBuf.append(ch);
      }
    }
    return szBuf.toString();
  }

  /**
   * Analyze data.
   * 
   * @param input
   *          text to analyze
   */
  private void analyseInput(String input) {
    StringTokenizer tokenizer1;
    String na, seq;
    StringBuffer szBuf;

    // carriage return is used to get the elements
    tokenizer1 = new StringTokenizer(input, "\n");
    if (!tokenizer1.hasMoreTokens())
      throw new FastaSequenceException(
          "Fasta format error: no definition line found.");
    // first line: definition
    na = tokenizer1.nextToken().trim();
    if (na.charAt(0) != '>')
      throw new FastaSequenceException("Fasta format error: no '>' found.");
    // skip the first > and spaces
    na = na.substring(1, na.length()).trim();
    // next lines (at least one): the sequence
    if (!tokenizer1.hasMoreTokens())
      throw new FastaSequenceException("Fasta format error: no sequence found.");
    szBuf = new StringBuffer();
    while (tokenizer1.hasMoreTokens()) {
      seq = tokenizer1.nextToken();
      szBuf.append(seq);
    }
    setHeader(filterText(na, false, false).trim());
    sequence = filterText(szBuf.toString(), true, true).trim();
  }

  /**
   * Set the sequence identifier.
   */
  public void setId(String id) {
    if (id != null)
      this.id = id;
    else
      this.id = NO_ID;
  }

  /**
   * Return the sequence identifier.
   */
  public String getId() {
    return id;
  }

  /**
   * Return the sequence name.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the sequence name.
   */
  public void setName(String name) {
    if (name != null)
      this.name = name;
    else
      this.name = NO_NAME;
  }

  /**
   * Clean the current name to be displayed on screen. If null or NO_NAME, then
   * return an empty string. If contains DBXref data, then return only the
   * description.
   * 
   * @return a string never null
   */
  public String getCleanedName() {
    if ((StringUtils.isNotBlank(name)) && (!name.equals(FastaSequence.NO_NAME))) {
      int indexXRef = name.indexOf("[[");
      if (indexXRef != -1) {
        return name.substring(0, indexXRef);
      } else {
        return name;
      }
    }
    return "";
  }

  /**
   * Set the sequence header. Such a header is supposed to contain ID and
   * name/description.
   */
  public void setHeader(String n) {
    if (n == null) {
      this.name = NO_NAME;
      this.id = NO_ID;
    } else {
      n = n.trim();
      // analyse Fasta header
      int idx = n.indexOf(' ');
      if (idx != -1) {
        id = n.substring(0, idx).trim();
        name = n.substring(idx + 1).trim();
      } else {
        id = n;
        name = NO_NAME;
      }
    }
  }

  /**
   * Return the sequence as a standard Java String.
   * 
   * Note: to handle compact sequences, use DSequence Framework.
   */
  public String getSequence() {
    return sequence;
  }

  /**
   * Return a Fasta formatted string from this object.
   */
  public String getFastaSequence() {
    if (sequence != null) {
      StringBuffer szBuf = new StringBuffer(">");
      szBuf.append(id);
      if (!NO_NAME.equals(name)) {
        szBuf.append(" ");
        szBuf.append(name);
      }
      szBuf.append("\n");
      szBuf.append(sequence);
      szBuf.append("\n");
      return szBuf.toString();
    } else
      return (">undefined sequence\n");
  }

  /**
   * Return a Fasta formatted string from this object. Sequence is formatted
   * with 80 letters per line.
   */
  public String getFastaSequence80Col() {
    StringBuffer szBuf;
    int i, size;

    szBuf = new StringBuffer(">");
    szBuf.append(id);
    if (!NO_NAME.equals(name)) {
      szBuf.append(" ");
      szBuf.append(name);
    }
    szBuf.append("\n");
    if (sequence == null) {
      szBuf.append("null\n");
      return szBuf.toString();
    }
    sequence = sequence.replace("\n", "");
    size = sequence.length();
    for (i = 0; i < size; i++) {
      szBuf.append(sequence.charAt(i));
      // in the following line, I use 79 since I want to add a carriage
      // return every 80 letters and I'm using a zero-based counter.
      if (i != 0 && (i + 1) % 80 == 0)
        szBuf.append("\n");
    }
    // add a terminal carriage return only if sequence size is not a multiple
    // of 80
    if (size % 80 != 0)
      szBuf.append("\n");
    return szBuf.toString();
  }

  /**
   * Set the sequence.
   */
  public void setSequence(String seq) {
    // if (seq != null)
    // this.sequence = seq.replace("\n", "");
    // else
    this.sequence = seq;
  }

  /**
   * Wrap a call to getFastaSequence method.
   * */
  public String toString() {
    return getFastaSequence();
  }

  /**
   * Return the sequence size.
   */
  public int getSize() {
    if (size != -1)
      return size;
    if (sequence != null)
      size = sequence.length();
    return size;
  }

  /**
   * Set the sequence size.
   */
  public void setSize(int size) {
    this.size = size;
  }

  /**
   * Basic method that checks whether or not the sequence is a protein. Return
   * true if this sequence contains a letter that differs from
   * DViewerSystem.getIUPAC_DNA_Alphabet().
   */
  public boolean isProteic() {
    String nucSymbols = DAlphabetUtils.getIUPAC_DNA_Alphabet().getSymbolsList();
    int i, size;
    char ch;

    size = sequence.length();
    for (i = 0; i < size; i++) {
      ch = sequence.charAt(i);
      if (Character.isLetter(ch) == false)// only consider letters (bypass * if
                                          // any)
        continue;
      if (nucSymbols.indexOf(ch) == -1)
        return true;
    }
    return false;
  }

  /**
   * Serialize this object.
   */
  public void write(OutputStream out) throws IOException {
    PrintStream printer;
    printer = new PrintStream(new BufferedOutputStream(out));
    printer.print(getFastaSequence80Col());
    printer.flush();
  }

  /**
   * Deserialize this object.
   */
  public void read(InputStream is) throws IOException, FastaSequenceException {
    BufferedReader reader;
    StringBuffer buf;
    String line;

    buf = new StringBuffer();
    reader = new BufferedReader(new InputStreamReader(is));
    while ((line = reader.readLine()) != null) {
      buf.append(line);
      buf.append("\n");
    }
    analyseInput(buf.toString());
  }
}
