package com.awadm.inspirationalquotes;

import java.util.Random;

import android.content.res.Resources;

public class Utils {
	final static int NO_OF_BGS = 6;

	public static int[] randomGradient(Resources res) {
		Random rnd = new Random();
		int index = rnd.nextInt(NO_OF_BGS - 1);

		switch (index) {
		case 0:
			return res.getIntArray(R.array.color_1);
		case 1:
			return res.getIntArray(R.array.color_2);
		case 2:
			return res.getIntArray(R.array.color_3);
		case 3:
			return res.getIntArray(R.array.color_4);
		case 4:
			return res.getIntArray(R.array.color_5);
		case 5:
			return res.getIntArray(R.array.color_6);
		default:
			return res.getIntArray(R.array.color_1);
		}
	}

}
