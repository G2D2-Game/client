package com.lukeoldenburg.g2d2.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonFile {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

	public static <T> T read(Path path, Class<T> clazz) {
		try {
			String data = Files.readString(path);
			return GSON.fromJson(data, clazz);

		} catch (IOException e) {
			Client.getLogger().error("", e);
			return null;
		}
	}

	public static boolean write(Path path, Object data) {
		try {
			Files.write(path, GSON.toJson(data).getBytes());
			return true;

		} catch (IOException e) {
			Client.getLogger().error("", e);
			return false;
		}
	}
}