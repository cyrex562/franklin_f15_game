package edu.franklin.practicum.f15.strategygame;

import com.badlogic.gdx.Gdx;

public class Logger {
	public static String logBuffer = "";

	public static void logMsg(String msg) {
		logBuffer += msg + '\n';
		Gdx.app.log(Defines.TAG, msg);
	}
}
