package org.tabbylauncher;

import java.util.HashMap;

import android.graphics.Color;

public class ColorUtils {

	public static HashMap<Integer,Integer> Colors = new HashMap<Integer,Integer>();
	protected static int[] color={
		Color.BLUE,
		Color.CYAN,
		Color.GREEN,
		Color.RED,
		Color.YELLOW
	};
	
	static{
		
		Colors.put(Color.BLUE,Color.CYAN);
		Colors.put(Color.CYAN,Color.GREEN);
		Colors.put(Color.GREEN,Color.MAGENTA);
		Colors.put(Color.MAGENTA,Color.RED);
		Colors.put(Color.RED,Color.WHITE);
		Colors.put(Color.WHITE,Color.YELLOW);
		Colors.put(Color.YELLOW,Color.LTGRAY);
		Colors.put(Color.LTGRAY,Color.GRAY);
		Colors.put(Color.GRAY,Color.DKGRAY);
		Colors.put(Color.DKGRAY,Color.BLACK);
		Colors.put(Color.BLACK,Color.BLUE);
	}

	public static int getNextColor(int color) {
		Integer res = Colors.get(color);
		if(res==null)
			return Color.BLUE;
		return res;
	}
	
}
