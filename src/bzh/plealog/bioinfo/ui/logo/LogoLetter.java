/* Copyright (C) 2003-2017 Patrick G. Durand
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
package bzh.plealog.bioinfo.ui.logo;

import java.awt.Color;

/**
 * A logo letter.
 * 
 * @author Patrick G. Durand
 * 
 * */
public class LogoLetter {
	private double  value_ = 0.0;
	private String  symbol_ = "?";
	private Color   barColor_ = Color.YELLOW;
	private Color   symbBkColor_ = Color.GRAY;
	private Color   symbFgColor_ = Color.BLACK;
	
	/**
	 * Constructor.
	 */
	public LogoLetter(){}
	
	/**
	 * Constructor.
	 * 
	 * @param symbol letter symbolized by this LogoLetter.
	 * @param frequency frequency of this letter
	 */
	public LogoLetter(String symbol, double frequency){
		this();
	  setSymbol(symbol);
	  setFrequency(frequency);
	}

	/**
   * Constructor.
   * 
   * @param symbol letter symbolized by this LogoLetter.
   * @param frequency frequency of this letter
   * @param barColor bar color
   * @param symbBkColor symbol background color
   * @param symbFgColor symbol foreground color
   * 
   */
	public LogoLetter(String symbol, double frequency, 
			Color barColor, Color symbBkColor, Color symbFgColor){
		setSymbol(symbol);
		setFrequency(frequency);
		setBarColor(barColor);
		setSymbBkColor(symbBkColor);
		setSymbFgColor(symbFgColor);
	}

	public double getFrequency() {
		return value_;
	}


	public void setFrequency(double value) {
		value_ = value;
	}


	public String getSymbol() {
		return symbol_;
	}


	public void setSymbol(String symbol) {
		symbol_ = symbol;
	}

	public Color getBarColor() {
		return barColor_;
	}

	public void setBarColor(Color barColor) {
		barColor_ = barColor;
	}

	public Color getSymbBkColor() {
		return symbBkColor_;
	}

	public void setSymbBkColor(Color symbBkColor) {
		symbBkColor_ = symbBkColor;
	}

	public Color getSymbFgColor() {
		return symbFgColor_;
	}

	public void setSymbFgColor(Color symbFgColor) {
		symbFgColor_ = symbFgColor;
	}
	
}
