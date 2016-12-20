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
package bzh.plealog.bioinfo.ui.blast.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.StringTokenizer;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.feature.Qualifier;
import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.searchresult.SRHspScore;
import bzh.plealog.bioinfo.api.data.searchresult.SRIteration;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.SRRequestInfo;
import bzh.plealog.bioinfo.api.data.sequence.DAlphabet;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.data.searchresult.SRUtils;
import bzh.plealog.bioinfo.util.CoreUtil;

/**
 * This class can be used to export SROutput and component objects using text
 * format.
 * 
 * @author Patrick G. Durand
 */
public class TxtExportSROutput {
  /**
   * Width of the pairwise sequence alignment. Default is 60.
   */
  private int                        _msaWidth             = 60;
  /**
   * Width of the global text file. Default is 80.
   */
  private int                        _lineWidth            = 80;
  /**
   * Number of space characters to put of the left of the hit definition line.
   * Default is 4.
   */
  private int                        _defLineLeftDelta     = 4;
  /**
   * Number of characters to use to display the hit definition line in the hit
   * table. Default is 60.
   */
  private int                        _defLineWidth         = 60;
  /**
   * Number of characters to use to display the organism line in the hit table.
   * Default is 35.
   */
  private int                        _organismWidth        = 35;
  /**
   * Carriage return. Default is Unix CR code (\n).
   */
  private String                     CR_CHAR               = "\n";
  /**
   * Used internally to create once and store the default column model used to
   * display the hit table. This default model aims at displaying accession
   * number, definition line, bits score and E-value.
   */
  private DataColumn[]               _tableColumns;

  public static final String         NO_HITS_FOUND_TXT     = " ***** No hits found ******";

  private static final String        QUERY_FORMATTER       = "Query: %-{0}i  ";
  private static final String        HIT_FORMATTER         = "Sbjct: %-{0}i  ";
  private static final String        PLUS_FRAME            = "Plus";
  private static final String        MINUS_FRAME           = "Minus";
  private static final String        ITER_HEADER           = " Iteration: ";
  private static final String        ITER_HEADER2          = " Sequence: ";

  private static final String        FORMAT_SCORE_LINE_1   = " Score = %s bits (%s),  Expect = %s";
  private static final String        FORMAT_IDENT          = " Identities = %d/%d (%s)";
  private static final String        FORMAT_POSIT          = ", Positives = %d/%d (%s)";
  private static final String        FORMAT_GAPS           = ", Gaps = %d/%d (%s)";
  private static final String        FORMAT_NUC_FRAMES     = " Strand = %s / %s";
  private static final String        FORMAT_TRAD_FRAMES    = " Frame = %d / %d";

  private static final String        SCORE_FORMATTER_TXT   = "#####";
  private static final DecimalFormat SCORE_FORMATTER       = new DecimalFormat(
                                                               SCORE_FORMATTER_TXT,
                                                               new DecimalFormatSymbols(
                                                                   Locale.US));

  private static final String        EVALUE_FORMATTER1_TXT = "0E000";
  private static final DecimalFormat EVALUE_FORMATTER1     = new DecimalFormat(
                                                               EVALUE_FORMATTER1_TXT,
                                                               new DecimalFormatSymbols(
                                                                   Locale.US));

  private static final String        EVALUE_FORMATTER2_TXT = "##.##";
  private static final DecimalFormat EVALUE_FORMATTER2     = new DecimalFormat(
                                                               EVALUE_FORMATTER2_TXT,
                                                               new DecimalFormatSymbols(
                                                                   Locale.US));

  private static final String        PCT_FORMATTER_TXT     = "###.#";
  private static final DecimalFormat PCT_FORMATTER         = new DecimalFormat(
                                                               PCT_FORMATTER_TXT,
                                                               new DecimalFormatSymbols(
                                                                   Locale.US));

  private static final String        INT_FORMATTER_TXT     = "#########";
  private static final DecimalFormat INT_FORMATTER         = new DecimalFormat(
                                                               INT_FORMATTER_TXT,
                                                               new DecimalFormatSymbols(
                                                                   Locale.US));

  public static final int            HIT_NUM               = 0;
  public static final int            ACCESS_DEF            = 1;
  public static final int            LENGTH                = 2;
  public static final int            NBHSPS                = 3;
  public static final int            SCORE_BITS            = 4;
  public static final int            EVALUE                = 5;
  public static final int            SCORE                 = 6;
  public static final int            Q_FROM                = 7;
  public static final int            Q_TO                  = 8;
  public static final int            Q_GAPS                = 9;
  public static final int            Q_FRAME               = 10;
  public static final int            Q_COVERAGE            = 11;
  public static final int            H_FROM                = 12;
  public static final int            H_TO                  = 13;
  public static final int            H_GAP                 = 14;
  public static final int            H_FRAME               = 15;
  public static final int            H_COVERAGE            = 16;
  public static final int            IDENTITY              = 17;
  public static final int            POSITIVE              = 18;
  public static final int            GAPS                  = 19;
  public static final int            ALI_LEN               = 20;
  public static final int            ORGANISM              = 21;
  public static final int            TAXONOMY              = 22;
  public static final int            ACCESSION             = 23;
  public static final int            DEFINITION            = 24;
  public static final int            BIO_CLASSIF           = 25;
  public static final int            MISMATCHES            = 26;
  public static final int            T_GAPS                = 27;

  public static final int[]          DATA_COL_IDS          = { HIT_NUM,
      ACCESS_DEF, LENGTH, NBHSPS, SCORE_BITS, EVALUE, SCORE, Q_FROM, Q_TO,
      Q_GAPS, Q_FRAME, Q_COVERAGE, H_FROM, H_TO, H_GAP, H_FRAME, H_COVERAGE,
      IDENTITY, POSITIVE, GAPS, ALI_LEN, ORGANISM, TAXONOMY, ACCESSION,
      DEFINITION, BIO_CLASSIF, MISMATCHES, T_GAPS         };
  public static final String[]       DATA_COL_HEADERS      = {
      "#",
      // why '_' ? See DataColumn.setColName()
      // \" added to enable loading of text file within spreadsheet app.
      "\"Hit_Accession_and_Definition\"",// "Sequences_producing_significant_alignments:",
      "Hit length",
      "Nb. HSPs",
      "Score bits",
      // add a terminal space char to produce 1E-125 and 1E125
      // with 0E000 (see above), we cannot handle that
      "E Value_", "Score_", "Query from", "Query to", "Query gaps",
      "Query frame", "Query Coverage", "Hit from", "Hit to", "Hit gaps",
      "Hit frame", "Hit coverage", "Identity", "Positive", "GapsP",
      "Align. length", "Organism", "Taxonomy", "Hit Accession",
      "Hit Definition", "Biological classification", "Mismatches", "Gaps" };
  private static final boolean[]     DATA_COL_VISIBILITY   = { false, true,
      false, false, true, true, false, false, false, false, false, false,
      false, false, false, false, false, false, false, false, false, false,
      false, false, false, false, false, false            };
  private static final int[]         DATA_COL_SIZE         = {
      SCORE_FORMATTER_TXT.length(), 60, INT_FORMATTER_TXT.length(),
      SCORE_FORMATTER_TXT.length(), SCORE_FORMATTER_TXT.length(),
      EVALUE_FORMATTER1_TXT.length(), SCORE_FORMATTER_TXT.length(),
      INT_FORMATTER_TXT.length(), INT_FORMATTER_TXT.length(),
      INT_FORMATTER_TXT.length(), SCORE_FORMATTER_TXT.length(),
      PCT_FORMATTER_TXT.length(), INT_FORMATTER_TXT.length(),
      INT_FORMATTER_TXT.length(), INT_FORMATTER_TXT.length(),
      SCORE_FORMATTER_TXT.length(), PCT_FORMATTER_TXT.length(),
      PCT_FORMATTER_TXT.length(), PCT_FORMATTER_TXT.length(),
      PCT_FORMATTER_TXT.length(), INT_FORMATTER_TXT.length(), 25, 25, 25, 60,
      60, INT_FORMATTER_TXT.length(), INT_FORMATTER_TXT.length(), };

  private DataColumn[] getDefaultDataColumnModel(boolean full) {
    if (_tableColumns != null)
      return _tableColumns;
    _tableColumns = new DataColumn[DATA_COL_IDS.length];
    for (int i = 0; i < DATA_COL_IDS.length; i++) {
      _tableColumns[i] = new DataColumn(DATA_COL_IDS[i], DATA_COL_SIZE[i],
          DATA_COL_HEADERS[i], full ? true : DATA_COL_VISIBILITY[i]);
      if (_tableColumns[i].getColId() == ACCESS_DEF) {
        _tableColumns[i].setWidth(_defLineWidth);
      }
      if (_tableColumns[i].getColId() == ORGANISM
          || _tableColumns[i].getColId() == TAXONOMY) {
        _tableColumns[i].setWidth(_organismWidth);
      }
    }

    return _tableColumns;
  }

  private String formatDoubleToInt(double value) {
    return SCORE_FORMATTER.format(value);
  }

  private String formatDoubleToSci(double value) {
    if (value > 0 && value < 0.1)
      return EVALUE_FORMATTER1.format(value);
    else
      return EVALUE_FORMATTER2.format(value);
  }

  private static String formatPercent(double value, boolean addPctString) {
    if (addPctString)
      return PCT_FORMATTER.format(value) + "%";
    else
      return PCT_FORMATTER.format(value);
  }

  /**
   * Print out the scores of a SRHsp.
   */
  public void printScores(Writer writer, SRHsp hsp) throws IOException {
    DSequence dseq;
    SRHspScore score;
    int qInc, hInc, qGap, hGap;
    boolean isProteic, showFrame;

    isProteic = hsp.isProteic();
    dseq = hsp.getQuery().getSequence(hsp);
    if (dseq != null) {
      qGap = dseq.getGapContent();
    } else {
      qGap = 0;
    }
    qInc = SRUtils.getIncrement(hsp.getQuery().getFrom(), hsp.getQuery()
        .getTo(), hsp.getScores().getAlignLen() - qGap);
    dseq = hsp.getHit().getSequence(hsp);
    if (dseq != null) {
      hGap = dseq.getGapContent();
    } else {
      hGap = 0;
    }
    hInc = SRUtils.getIncrement(hsp.getHit().getFrom(), hsp.getHit().getTo(),
        hsp.getScores().getAlignLen() - hGap);
    if (isProteic && Math.abs(qInc) == 1 && Math.abs(hInc) == 1) {
      showFrame = false;// blastp
    } else {
      showFrame = true;// all others
    }
    score = hsp.getScores();
    writer.write(String.format(FORMAT_SCORE_LINE_1,
        formatDoubleToInt(score.getBitScore()),
        formatDoubleToInt(score.getScore()),
        formatDoubleToSci(score.getEvalue())));
    writer.write(CR_CHAR);
    writer.write(String.format(FORMAT_IDENT, score.getIdentity(),
        score.getAlignLen(), formatPercent(score.getIdentityP(), true)));
    if (isProteic) {
      writer.write(String.format(FORMAT_POSIT, score.getPositive(),
          score.getAlignLen(), formatPercent(score.getPositiveP(), true)));
    }
    if (score.getGaps() != 0) {
      writer.write(String.format(FORMAT_GAPS, score.getGaps(),
          score.getAlignLen(), formatPercent(score.getGapsP(), true)));
    }
    if (showFrame) {
      writer.write(CR_CHAR);
      if (!isProteic) {
        writer.write(String.format(FORMAT_NUC_FRAMES, (qInc > 0 ? PLUS_FRAME
            : MINUS_FRAME), (hInc > 0 ? PLUS_FRAME : MINUS_FRAME)));
      } else {
        qInc = hsp.getQuery().getFrame();
        if (qInc == 0)
          qInc = 1;
        hInc = hsp.getHit().getFrame();
        if (hInc == 0)
          hInc = 1;
        writer.write(String.format(FORMAT_TRAD_FRAMES, qInc, hInc));

      }
    }
    writer.write(CR_CHAR);
  }

  private int writeSequence(Writer w, DSequence seq, int from, int to, int delta)
      throws IOException {
    DAlphabet alphabet;
    int i = 0, gaps = 0;
    char ch;

    // TODO: here, it'll be possible to use a special GraphicsFormatter object
    // to handle HTML coloured sequence. This object will have methods to return
    // Strings to use before and after letter to start/stop HTML tags. These
    // strings
    // will have to use the DGraphics associated to DSymbols
    alphabet = seq.getAlphabet();
    if (delta != 0) {
      for (i = 0; i < delta; i++) {
        w.write(" ");
      }
    }
    i = 0;
    while (from <= to) {
      ch = seq.getSymbol(from).getChar();
      if (ch == alphabet.getSymbol(DSymbol.GAP_SYMBOL_CODE).getChar()) {
        gaps++;
      }
      w.write(ch);
      from++;
      i++;
    }
    return gaps;
  }

  /**
   * Write a text by respecting the document width. It means that if the text is
   * wider that the document width, the text will be produced on several lines.
   * 
   * @param writer
   *          where to write
   * @param text
   *          the text to write
   * @param delimiter
   *          the character used to tokenize the text
   * @param delta
   *          the number of characters already produced on the current
   *          writer&apos;s line. If you start a new line, pass in zero.
   */
  private void dumpText(Writer writer, String text, char delimiter, int delta)
      throws IOException {
    StringTokenizer tokenizer;
    String token;
    int len, width, i;
    // loop to write text one word at a time
    tokenizer = new StringTokenizer(text, String.valueOf(delimiter));
    len = delta;
    while (tokenizer.hasMoreTokens()) {
      token = tokenizer.nextToken();
      width = token.length();
      if ((len + width) > _lineWidth) {
        // start a new line by adding space chars
        writer.write(CR_CHAR);
        for (i = 0; i < _defLineLeftDelta; i++) {
          writer.write(" ");
        }
        len = _defLineLeftDelta;
      }
      writer.write(token);
      writer.write(delimiter);
      len += (width + 1);
    }
  }

  /**
   * Print out the header of a sequence alignment. This header is made of the
   * accession number, definition line and hit sequence length.
   */
  public void printHitHeader(Writer writer, SRHit hit) throws IOException {
    String id;

    // write Id
    writer.write(">");
    id = hit.getHitId();
    writer.write(id);
    writer.write(" ");

    dumpText(writer, hit.getHitDef(), ' ', 1 + id.length() + 1/* > + id + space */);
    writer.write(CR_CHAR);
    writer.write("   Length: ");
    writer.write(String.valueOf(hit.getHitLen()));
    writer.write(CR_CHAR);
    if (hit.getSequenceInfo() != null) {
      if (hit.getSequenceInfo().getTaxonomy() != null) {
        writer.write("   Taxonomy : ");
        dumpText(writer, hit.getSequenceInfo().getTaxonomy(), ';', 14);
        writer.write(CR_CHAR);
      }
      if (hit.getSequenceInfo().getOrganism() != null) {
        writer.write("   Organism : ");
        dumpText(writer, hit.getSequenceInfo().getOrganism(), ';', 14);
        writer.write(CR_CHAR);
      }
    }
  }

  /**
   * Print out the pairwise sequence alignment contained in a SRHsp.
   */
  public void printAlign(Writer writer, SRHsp hsp) throws IOException {
    PrintfFormat qFormatter, hFormatter;
    DSequence qs, ms, hs;
    String f;
    int start, stop, from, to, size, len, gaps, qInc, hInc, qFrom, hFrom, delta;

    // Gets the bigger sequence coordinate, and use it to figures out how many
    // characters it is needed to place seq location within the sequence
    // alignment
    size = Math.max(Math.max(hsp.getQuery().getFrom(), hsp.getQuery().getTo()),
        Math.max(hsp.getHit().getFrom(), hsp.getHit().getTo()));
    size = String.valueOf(size).length();
    f = MessageFormat.format(QUERY_FORMATTER, new Object[] { size });
    qFormatter = new PrintfFormat(f);
    f = MessageFormat.format(HIT_FORMATTER, new Object[] { size });
    hFormatter = new PrintfFormat(f);
    // decal will be use to align correctly midline seq between query and hit
    // seqs
    delta = 7 + size + 2;
    // Gets the data from the query
    qs = hsp.getQuery().getSequence(hsp);
    if (qs == null)
      return;
    qInc = SRUtils.getIncrement(hsp.getQuery().getFrom(), hsp.getQuery()
        .getTo(), hsp.getScores().getAlignLen() - qs.getGapContent());
    qFrom = hsp.getQuery().getFrom();
    // Gets the data from the midline
    if (hsp.getMidline() != null)
      ms = hsp.getMidline().getSequence(hsp);
    else
      ms = null;
    // Gets the data from the hit
    hs = hsp.getHit().getSequence(hsp);
    if (hs == null)
      return;
    hInc = SRUtils.getIncrement(hsp.getHit().getFrom(), hsp.getHit().getTo(),
        hsp.getScores().getAlignLen() - hs.getGapContent());
    hFrom = hsp.getHit().getFrom();

    // gets the size of the SeqAlign (use the query since all seq have the same
    // size)
    size = qs.size();
    start = 0;
    stop = size - 1;
    while (start <= stop) {
      from = start;
      to = Math.min(start + _msaWidth - 1, stop);
      len = to - from + 1;
      // dump query sequence
      writer.write(qFormatter.sprintf(qFrom));// starting coord
      gaps = writeSequence(writer, qs, from, to, 0);
      writer.write("  ");
      writer.write(String.valueOf(qFrom + ((len - 1 - gaps) * qInc)));// ending
                                                                      // coord
      qFrom = qFrom + ((len - gaps) * qInc);
      writer.write(CR_CHAR);
      // dump midline sequence
      if (ms != null) {
        writeSequence(writer, ms, from, to, delta);
        writer.write(CR_CHAR);
      }
      // dump hit sequence
      writer.write(hFormatter.sprintf(hFrom));// starting coord
      gaps = writeSequence(writer, hs, from, to, 0);
      writer.write("  ");
      writer.write(String.valueOf(hFrom + ((len - 1 - gaps) * hInc)));// ending
                                                                      // coord
      hFrom = hFrom + ((len - gaps) * hInc);
      writer.write(CR_CHAR);
      // done
      writer.write(CR_CHAR);
      start += _msaWidth;
    }
  }

  public static String getFormattedData(SRHit hit, SRHsp hsp, int colId,
      boolean escapeStringWithQuotes, boolean addPctString) {
    StringBuffer buf;
    String val = "?";
    double eval;

    switch (colId) {
      case HIT_NUM:
        val = SCORE_FORMATTER.format(hit.getHitNum());
        break;
      case ACCESS_DEF:
        buf = new StringBuffer();
        if (escapeStringWithQuotes) {
          buf.append("\"");
        }
        buf.append(hit.getHitAccession());
        buf.append("  ");
        buf.append(hit.getHitDef());
        if (escapeStringWithQuotes) {
          buf.append("\"");
        }
        val = buf.toString();
        break;
      case ACCESSION:
        buf = new StringBuffer();
        if (escapeStringWithQuotes) {
          buf.append("\"");
        }
        buf.append(hit.getHitAccession());
        if (escapeStringWithQuotes) {
          buf.append("\"");
        }
        val = buf.toString();
        break;
      case DEFINITION:
        buf = new StringBuffer();
        if (escapeStringWithQuotes) {
          buf.append("\"");
        }
        buf.append(hit.getHitDef());
        if (escapeStringWithQuotes) {
          buf.append("\"");
        }
        val = buf.toString();
        break;
      case LENGTH:
        val = INT_FORMATTER.format(hit.getHitLen());
        break;
      case SCORE_BITS:
        val = SCORE_FORMATTER.format(hsp.getScores().getBitScore());
        break;
      case EVALUE:
      case SCORE:
        eval = (colId == EVALUE ? hsp.getScores().getEvalue() : hsp.getScores()
            .getScore());
        if (eval > 0 && eval < 0.1)
          val = EVALUE_FORMATTER1.format(eval);
        else
          val = EVALUE_FORMATTER2.format(eval);
        break;
      case NBHSPS:
        val = SCORE_FORMATTER.format(hit.countHsp());
        break;
      case Q_FROM:
        val = INT_FORMATTER.format(hsp.getQuery().getFrom());
        break;
      case Q_TO:
        val = INT_FORMATTER.format(hsp.getQuery().getTo());
        break;
      case Q_GAPS:
        val = INT_FORMATTER.format(hsp.getQuery().getGaps());
        break;
      case H_FROM:
        val = INT_FORMATTER.format(hsp.getHit().getFrom());
        break;
      case H_TO:
        val = INT_FORMATTER.format(hsp.getHit().getTo());
        break;
      case H_GAP:
        val = INT_FORMATTER.format(hsp.getHit().getGaps());
        break;
      case Q_FRAME:
        val = SCORE_FORMATTER.format(hsp.getQuery().getFrame());
        break;
      case H_FRAME:
        val = SCORE_FORMATTER.format(hsp.getHit().getFrame());
        break;
      case IDENTITY:
        val = formatPercent(hsp.getScores().getIdentityP(), addPctString);
        break;
      case POSITIVE:
        val = formatPercent(hsp.getScores().getPositiveP(), addPctString);
        break;
      case GAPS:
        val = formatPercent(hsp.getScores().getGapsP(), addPctString);
        break;
      case T_GAPS:
        val = INT_FORMATTER.format(hsp.getScores().getGaps());
        break;
      case MISMATCHES:
        val = INT_FORMATTER.format(hsp.getScores().getMismatches());
        break;
      case ALI_LEN:
        val = INT_FORMATTER.format(hsp.getScores().getAlignLen());
        break;
      case Q_COVERAGE:
        val = formatPercent(hsp.getQueryCoverage(), addPctString);
        break;
      case H_COVERAGE:
        val = formatPercent(hsp.getHitCoverage(), addPctString);
        break;
      case ORGANISM:
        if (hit.getSequenceInfo() != null
            && hit.getSequenceInfo().getOrganism() != null) {
          if (escapeStringWithQuotes) {
            buf = new StringBuffer();
            buf.append("\"");
            buf.append(hit.getSequenceInfo().getOrganism());
            buf.append("\"");
            val = buf.toString();
          } else {
            val = hit.getSequenceInfo().getOrganism();
          }
        }
        break;
      case TAXONOMY:
        if (hit.getSequenceInfo() != null
            && hit.getSequenceInfo().getTaxonomy() != null) {
          // when possible create a simplified string with the first
          // and the "last-1" strings. The last string is not retrieved since
          // it is usually the species name, already available from Organism.
          // commented on user request ; providing simplified taxonomy was not a
          // good idea
          // code kept here just in case we restore it in a future release...
          /*
           * int size, counter; StringTokenizer tokenizer = new
           * StringTokenizer(hit.getSequenceInfo().getTaxonomy(),";");
           * 
           * size = tokenizer.countTokens(); if (size>=2){ counter=0; buf = new
           * StringBuffer(); String token; if
           * (escapeStringWithQuotes){buf.append("\"");}
           * while(tokenizer.hasMoreTokens()){ token = tokenizer.nextToken(); if
           * (counter==0){ buf.append(token); buf.append(";...;"); } if
           * (counter==(size-2)){ buf.append(token); } counter++; } if
           * (escapeStringWithQuotes){buf.append("\"");} val = buf.toString(); }
           * else{ if (escapeStringWithQuotes){ buf = new StringBuffer();
           * buf.append("\""); buf.append(val =
           * hit.getSequenceInfo().getTaxonomy()); buf.append("\""); val =
           * buf.toString(); } else{ val = hit.getSequenceInfo().getTaxonomy();
           * } }
           */
          if (escapeStringWithQuotes) {
            buf = new StringBuffer();
            buf.append("\"");
            buf.append(hit.getSequenceInfo().getTaxonomy());
            buf.append("\"");
            val = buf.toString();
          } else {
            val = hit.getSequenceInfo().getTaxonomy();
          }
        }
        break;
      case BIO_CLASSIF:
        val = "n/a";
        break;
    }
    return val;
  }

  public static String getFormattedData(SRHit hit, SRHsp hsp, int colId,
      boolean escapeStringWithQuotes) {
    return getFormattedData(hit, hsp, colId, escapeStringWithQuotes, true);
  }

  public static String getFormattedData(SRHit hit, SRHsp hsp, int colId) {
    return TxtExportSROutput.getFormattedData(hit, hsp, colId, true, true);
  }

  private String getData(SRHit hit, SRHsp hsp, DataColumn dc) {
    return getFormattedData(hit, hsp, dc.getColId());
  }

  private void fillBuf(StringBuffer buf, String data, int len) {
    int size, j, i;

    buf.setLength(len);
    size = data.length();
    j = 0;
    for (i = 0; i < len; i++) {
      if (j < size) {
        buf.setCharAt(i, data.charAt(j));
        j++;
      } else {
        buf.setCharAt(i, ' ');
      }
    }
    if (size > len) {// truncate large string
      if (buf.charAt(0) == '"') {// String with escaped char ?
        buf.setCharAt(len - 1, '"');
        buf.setCharAt(len - 2, '.');
        buf.setCharAt(len - 3, '.');
        buf.setCharAt(len - 4, '.');
      } else {
        buf.setCharAt(len - 1, '.');
        buf.setCharAt(len - 2, '.');
        buf.setCharAt(len - 3, '.');

      }
    }
  }

  /**
   * Print out the header of the hit table.
   */
  private void printHitTableHeader(Writer w, DataColumn[] tcs)
      throws IOException {
    DataColumn tc;
    String data;
    StringBuffer buf;
    int k, nTC, j, nLines = 0;

    nTC = tcs.length;
    for (j = 0; j < nTC; j++) {
      tc = tcs[j];
      if (tc.isVisible()) {
        k = tc.getNLines();
        if (k > nLines)
          nLines = k;
      }
    }
    buf = new StringBuffer();
    for (k = nLines; k > 0; k--) {
      for (j = 0; j < nTC; j++) {
        tc = tcs[j];
        if (tc.isVisible()) {
          data = tc.getColName(k);
          fillBuf(buf, data, tc.getWidth());
          buf.setLength(tc.getWidth());
          w.write(buf.toString());
          w.write("  ");
        }
      }
      w.write(CR_CHAR);
    }
    w.write(CR_CHAR);
  }

  private DataColumn[] prepareUserDefineColumns(DataColumn[] defTcs,
      int[] dataColIDS) {
    DataColumn[] tcs;
    DataColumn tc;
    int i, j, nDCs, nIDs, id;

    nIDs = dataColIDS.length;
    tcs = new DataColumn[nIDs];
    nDCs = defTcs.length;
    for (i = 0; i < nIDs; i++) {
      id = dataColIDS[i];
      for (j = 0; j < nDCs; j++) {
        tc = defTcs[j];
        if (tc.getColId() == id) {
          tc.visible = true;
          tcs[i] = tc;
          break;
        }
      }
    }
    return tcs;
  }

  private DataColumn[] prepareDataColumnModel(int[] dataColIDS) {
    DataColumn[] tcs;
    int i;

    if (dataColIDS == null)
      tcs = getDefaultDataColumnModel(false);
    else {
      tcs = prepareUserDefineColumns(getDefaultDataColumnModel(false),
          dataColIDS);
    }
    if (tcs != null) {
      for (i = 0; i < tcs.length; i++) {
        if (tcs[i] == null)
          return null;
      }
    } else {
      return null;
    }
    return tcs;
  }

  /**
   * Print out the hit data as a table row.
   */
  public void printHitTableData(Writer w, SRHit hit) throws IOException {
    printHitTableData(w, hit, null);
  }

  /**
   * Print out the hit data as a table row.
   */
  public void printHitTableData(Writer w, SRHit hit, int[] dataColIDS)
      throws IOException {
    DataColumn[] tcs;
    DataColumn tc;
    StringBuffer buf;
    String data;
    int nTC, j;

    tcs = prepareDataColumnModel(dataColIDS);
    nTC = tcs.length;
    // printHitTableHeader(w, tcs);
    buf = new StringBuffer();
    for (j = 0; j < nTC; j++) {
      tc = tcs[j];
      if (tc.isVisible()) {
        data = getData(hit, hit.getHsp(0), tc);
        fillBuf(buf, data, tc.getWidth());
        buf.setLength(tc.getWidth());
        w.write(buf.toString());
        w.write("  ");
      }
    }
    w.write(CR_CHAR);
  }

  /**
   * Print out only the table header used to display the Hit Table.
   */
  public void printHitTableHeader(Writer w, int[] dataColIDS)
      throws IOException {
    DataColumn[] tcs;

    tcs = prepareDataColumnModel(dataColIDS);
    printHitTableHeader(w, tcs);
  }

  /**
   * Print out only the table header used to display the Hit Table.
   */
  public void printHitTableHeader(Writer w) throws IOException {
    printHitTableHeader(w, (int[]) null);
  }

  /**
   * Print out only the hit table of a SRIteration. Use the standard data model
   * made of accession number, definition line, bits score and E-value.
   */
  public void printHitTable(Writer w, SRIteration iter) throws IOException {
    printHitTable(w, iter, null);
  }

  /**
   * Print out the hit table of a SRIteration. This method can be used to
   * provided a user defined data model to be used to display the needed columns
   * in the table. For that purpose uses the parameter dataColIDS that must be
   * made of column identifiers defined in this class.
   */
  public void printHitTable(Writer w, SRIteration iter, int[] dataColIDS)
      throws IOException {
    DataColumn[] tcs;
    DataColumn tc;
    SRHit hit;
    StringBuffer buf;
    String data;
    int nhit, i, nTC, j;

    tcs = prepareDataColumnModel(dataColIDS);
    nTC = tcs.length;
    printHitTableHeader(w, tcs);
    buf = new StringBuffer();
    nhit = iter.countHit();
    for (i = 0; i < nhit; i++) {
      hit = iter.getHit(i);
      for (j = 0; j < nTC; j++) {
        tc = tcs[j];
        if (tc.isVisible()) {
          data = getData(hit, hit.getHsp(0), tc);
          fillBuf(buf, data, tc.getWidth());
          buf.setLength(tc.getWidth());
          w.write(buf.toString());
          w.write("  ");
        }
      }
      w.write(CR_CHAR);
    }
    w.write(CR_CHAR);
  }

  /**
   * Print out the hit table of a SROutput. This method can be used to provided
   * a user defined data model to be used to display the needed columns in the
   * table. For that purpose uses the parameter dataColIDS that must be made of
   * column identifiers defined in this class.
   */
  public void printHitTable(Writer w, SROutput bo, int[] dataColIDS)
      throws IOException {
    DataColumn[] tcs;
    DataColumn tc;
    SRIteration iter;
    SRHit hit;
    StringBuffer buf;
    String data;
    int niter, nhit, i, k, nTC, j;
    boolean dumpExtendedIterHeader;

    if (bo == null)
      return;
    tcs = prepareDataColumnModel(dataColIDS);
    nTC = tcs.length;
    printHitTableHeader(w, tcs);
    niter = bo.countIteration();
    dumpExtendedIterHeader = (bo.getBlastType() != SROutput.PSIBLAST);
    buf = new StringBuffer();
    for (k = 0; k < niter; k++) {
      iter = bo.getIteration(k);
      if (niter > 1) {
        w.write(CR_CHAR);
        if (dumpExtendedIterHeader) {
          w.write(ITER_HEADER2);
        } else {
          w.write(ITER_HEADER);
        }
        w.write(String.valueOf(k + 1));
        if (dumpExtendedIterHeader) {
          w.write(": ");
          w.write(iter.getIterationQueryDesc());
          w.write(" (");
          w.write(String.valueOf(iter.getIterationQueryLength()));
          w.write(" letters)");
        }
        w.write(CR_CHAR);
        w.write(CR_CHAR);
      }
      nhit = iter.countHit();
      if (nhit == 0) {
        w.write("    ");
        w.write(NO_HITS_FOUND_TXT);
        w.write(CR_CHAR);
      }
      for (i = 0; i < nhit; i++) {
        hit = iter.getHit(i);
        for (j = 0; j < nTC; j++) {
          tc = tcs[j];
          if (tc.isVisible()) {
            data = getData(hit, hit.getHsp(0), tc);
            fillBuf(buf, data, tc.getWidth());
            buf.setLength(tc.getWidth());
            w.write(buf.toString());
            w.write("  ");
          }
        }
        w.write(CR_CHAR);
      }
    }
    w.write(CR_CHAR);
  }

  /**
   * Print out only the hit table of a SROutput. Use the standard data model
   * made of accession number, definition line, bits score and E-value.
   */
  public void printHitTable(Writer w, SROutput bo) throws IOException {
    printHitTable(w, bo, null);
  }

  public void printFeatureTable(Writer w, FeatureTable ft) throws IOException {
    Enumeration<Feature> enumFeat;
    Enumeration<Qualifier> enumQual;
    Feature feature;
    Qualifier qualifier;
    if (ft == null)
      return;
    enumFeat = ft.enumFeatures();
    if (!enumFeat.hasMoreElements())
      return;
    printSpacer(w, 1);
    w.write("Feature Table: ");
    w.write(CR_CHAR);
    while (enumFeat.hasMoreElements()) {
      feature = (Feature) enumFeat.nextElement();
      printSpacer(w, 4);
      w.write(feature.getKey());
      printSpacer(w, 4);
      w.write(String.valueOf(feature.getFrom()));
      w.write("..");
      w.write(String.valueOf(feature.getTo()));
      w.write(" (");
      w.write(String.valueOf(feature.getStrand()));
      w.write(")");
      w.write(CR_CHAR);
      enumQual = feature.enumQualifiers();
      while (enumQual.hasMoreElements()) {
        qualifier = (Qualifier) enumQual.nextElement();
        printSpacer(w, 8);
        w.write("/");
        w.write(qualifier.getName());
        w.write("=");
        w.write(qualifier.getValue());
        w.write(CR_CHAR);
      }
    }
    w.write(CR_CHAR);
  }

  /**
   * Print the content of a SRHsp. Print the scores and the pairwise sequence
   * alignment.
   */
  public void printHsp(Writer w, SRHsp hsp) throws IOException {
    printScores(w, hsp);
    printFeatureTable(w, hsp.getHit().getFeatures());
    w.write(CR_CHAR);
    printAlign(w, hsp);
  }

  /**
   * Print the content of a SRHit. Print the hit header and all the SRHsps
   * contained in the hit.
   */
  public void printHit(Writer w, SRHit hit) throws IOException {
    SRHsp hsp;
    int nhsp, k;

    printHitHeader(w, hit);
    nhsp = hit.countHsp();
    w.write(CR_CHAR);
    for (k = 0; k < nhsp; k++) {
      hsp = hit.getHsp(k);
      printHsp(w, hsp);
    }
  }

  /**
   * Print the content of a SRIteration. Print all the hits and hsps contained
   * in the iteration.
   */
  public void printIteration(Writer w, SRIteration iter) throws IOException {
    SRHit hit;
    int nhit, k;

    if (iter.countHit() == 0) {
      w.write(NO_HITS_FOUND_TXT);
      w.write(CR_CHAR);
    } else {
      nhit = iter.countHit();
      for (k = 0; k < nhit; k++) {
        hit = iter.getHit(k);
        printHit(w, hit);
      }
    }
  }

  /**
   * Print the output header. It is made of the program, database and query
   * names.
   */
  public void printOutputHeaderPrgmDb(Writer w, SROutput bo) throws IOException {
    SRRequestInfo info;
    Object o;

    if (bo == null)
      return;
    info = bo.getRequestInfo();
    // Program name and version
    o = info.getValue(SRRequestInfo.PRGM_VERSION_DESCRIPTOR_KEY);
    if (o != null) {
      w.write(o.toString());
    }
    w.write(CR_CHAR);
    w.write(CR_CHAR);
    // Program reference
    o = info.getValue(SRRequestInfo.PRGM_REFERENCE_DESCRIPTOR_KEY);
    if (o != null) {
      w.write("Reference: ");
      w.write(CR_CHAR);
      dumpText(w, o.toString(), ' ', 0);
      w.write(o.toString());
      w.write(CR_CHAR);
    }
    // DB reference
    w.write("Database: ");
    o = info.getValue(SRRequestInfo.DATABASE_DESCRIPTOR_KEY);
    if (o != null) {
      w.write(o.toString());
    } else {
      w.write("unknown.");
    }
    w.write(CR_CHAR);
  }

  /**
   * Print the output header. It is made of the program, database and query
   * names.
   */
  public void printOutputHeaderQueryName(Writer w, SROutput bo)
      throws IOException {
    SRRequestInfo info;
    Object o;
    boolean isPlast = false;

    if (bo == null)
      return;
    info = bo.getRequestInfo();
    // check program name
    o = info.getValue(SRRequestInfo.PRGM_VERSION_DESCRIPTOR_KEY);
    if (o != null
        && (o.toString().toLowerCase().indexOf("klast") != -1 || o.toString()
            .toLowerCase().indexOf("plast") != -1)) {
      isPlast = true;
    }
    // Query reference
    w.write("Query= ");
    if (isPlast) {
      o = info.getValue(SRRequestInfo.QUERY_ID_DESCRIPTOR_KEY);
      if (o == null) {
        o = info.getValue(SRRequestInfo.QUERY_DEF_DESCRIPTOR_KEY);
      }
    } else {
      o = info.getValue(SRRequestInfo.QUERY_DEF_DESCRIPTOR_KEY);
    }
    if (o != null) {
      dumpText(w, o.toString(), ' ', "Query= ".length() + 1);
      w.write("");
      o = info.getValue(SRRequestInfo.QUERY_LENGTH_DESCRIPTOR_KEY);
      if (o != null) {
        w.write(CR_CHAR);
        w.write("      (");
        w.write(o.toString());
        w.write(" letters)");
      }
    } else {
      w.write("unknown.");
    }
    w.write(CR_CHAR);

  }

  /**
   * Print the content of a SROutput. Print all the iterations, hits and hsps
   * contained in the output. This method can be used to provided a user defined
   * data model to be used to display the needed columns in the hit table. For
   * that purpose uses the parameter dataColIDS that must be made of column
   * identifiers defined in this class.
   */
  public void printOutput(Writer w, SROutput bo,
      TxtExportSROutputOptions options) throws IOException {
    SRIteration iter;
    int niter, k;
    boolean dumpExtendedIterHeader;

    if (options != null && options.isExportHeaderPrgmDb()) {
      printOutputHeaderPrgmDb(w, bo);
      w.write(CR_CHAR);
    }
    if (options != null && options.isExportHeaderQueryName()) {
      printOutputHeaderQueryName(w, bo);
      w.write(CR_CHAR);
    }
    if (bo == null || bo.isEmpty()) {
      w.write(NO_HITS_FOUND_TXT);
      w.write(CR_CHAR);
    } else {
      dumpExtendedIterHeader = (bo.getBlastType() != SROutput.PSIBLAST);
      if (options != null && options.isExportTable()) {
        printHitTable(w, bo, options.getColIds());
      }
      // printTable(w, bo, new int[]{ACCESS_DEF, HIT_NUM, SCORE, EVALUE});
      if (options != null && options.isExportAlignments()) {
        niter = bo.countIteration();
        for (k = 0; k < niter; k++) {
          iter = bo.getIteration(k);
          if (niter > 1) {
            w.write(CR_CHAR);
            if (dumpExtendedIterHeader) {
              w.write(ITER_HEADER2);
            } else {
              w.write(ITER_HEADER);
            }
            w.write(String.valueOf(k + 1));
            if (dumpExtendedIterHeader) {
              w.write(": ");
              w.write(iter.getIterationQueryDesc());
              w.write(" (");
              w.write(String.valueOf(iter.getIterationQueryLength()));
              w.write(" letters)");
            }
            w.write(CR_CHAR);
            w.write(CR_CHAR);
          }
          printIteration(w, iter);
          if ((k + 1) < niter) {
            w.write(CR_CHAR);
          }
        }
      }
    }
    w.flush();
  }

  /**
   * Print the content of a SROutput. Print all the iterations, hits and hsps
   * contained in the output.
   */
  public void printOutput(Writer w, SROutput bo) throws IOException {
    printOutput(w, bo, null);
  }

  /**
   * Print the content of a SROutput. Print all the iterations, hits and hsps
   * contained in the output.
   */
  public void printOutput(OutputStream os, SROutput bo) throws IOException {
    PrintWriter writer;

    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
    printOutput(writer, bo, null);
  }

  /**
   * Print the content of a SROutput. Print all the iterations, hits and hsps
   * contained in the output.
   */
  public void printOutput(OutputStream os, SROutput bo,
      TxtExportSROutputOptions options) throws IOException {
    PrintWriter writer;

    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
    printOutput(writer, bo, options);
  }

  /**
   * Print out a carriage return.
   */
  public void printCR(Writer w) throws IOException {
    w.write(CR_CHAR);
  }

  /**
   * Print out a spacer of size 'size'.
   */
  public void printSpacer(Writer w, int size) throws IOException {
    for (int i = 0; i < size; i++)
      w.write(" ");
  }

  /**
   * Print out a string.
   */
  public void printString(Writer w, String str) throws IOException {
    w.write(str);
  }

  /**
   * Print out a line separator.
   */
  public void printLineSeparator(Writer w) throws IOException {
    for (int i = 0; i < _lineWidth; i++)
      w.write("-");
  }

  private class DataColumn {
    private int      colId;   // id of the column
    private int      width;   // nb od char needed to display data
    private String   colName; // string to put on the header
    private boolean  visible; // display or not that column
    private String[] colNames; // internal use

    public DataColumn(int colId, int width, String colName, boolean visible) {
      super();
      this.colId = colId;
      this.width = width;
      this.colName = colName;
      this.visible = visible;
      initColNames();
    }

    public int getColId() {
      return colId;
    }

    public int getWidth() {
      return width;
    }

    public int getNLines() {
      return colNames.length;
    }

    public void setWidth(int w) {
      width = w;
      initColNames();
    }

    private void initColNames() {
      StringTokenizer tokenizer;
      int i = 0, w;

      // col header made of serveral words will be produced on several lines
      tokenizer = new StringTokenizer(colName, " ");
      colNames = new String[tokenizer.countTokens()];
      while (tokenizer.hasMoreTokens()) {
        // trick: if you do not want to produce a multi-words header on several
        // line, use _ instead of a space: it'll be replaced here.
        colNames[i] = CoreUtil.replaceAll(tokenizer.nextToken(), "_", " ");
        w = colNames[i].length();
        // adjust column width with the wider name, if needed
        if (w > this.width) {
          this.width = w;
        }
        i++;
      }
    }

    public boolean isVisible() {
      return visible;
    }

    public String getColName(int line) {
      int idx = colNames.length - line;
      if (idx < 0 || idx >= colNames.length) {
        return "";
      } else {
        return colNames[idx];
      }
    }
  }
}
