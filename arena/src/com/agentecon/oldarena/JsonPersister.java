package com.agentecon.oldarena;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Random;

import com.agentecon.metric.series.Chart;
import com.google.gson.Gson;

public class JsonPersister {

	private Random rand;
	private Path baseDir;

	public JsonPersister() {
		this(FileSystems.getDefault().getPath("data"));
	}

	public JsonPersister(Path dir) {
		this.baseDir = dir;
		this.rand = new Random();
	}

	public IPersistable load(Class<?> type, int id) throws IOException {
		Path file = getFile(type.getSimpleName(), id);
		String content = new String(Files.readAllBytes(file), "UTF-8");
		Gson gson = new Gson();
		IPersistable persistable = (IPersistable) gson.fromJson(content, type);
		assert persistable.getId() == id;
		return persistable;
	}

	private Path getFile(String type, int id) {
		return baseDir.resolve(type + "-" + id + ".json");
	}

	public void save(IPersistable o) throws IOException {
		Gson gson = new Gson();
		String content = gson.toJson(o);
		String type = o.getClass().getSimpleName();
		int id = o.getId();
		if (id == 0) {
			while (true) {
				do {
					id = Math.abs(rand.nextInt());
				} while (id == 0);
				Path path = getFile(type, id);
				if (Files.notExists(path)) {
					o.setId(id);
					Files.write(path, content.getBytes("UTF-8"));
					return;
				}
			}
		} else {
			Files.write(getFile(type, id), content.getBytes("UTF-8"));
		}
	}

	public static void main(String[] args) throws IOException {
		JsonPersister persister = new JsonPersister();
		Chart chart = new Chart("1313", "TestChart", "Subtitle", Collections.EMPTY_LIST);
		persister.save(chart);
		System.out.println("save chart under id " + chart.getId());
	}

	public void delete(Class<?> class1, int del) throws IOException {
		Files.deleteIfExists(getFile(class1.getSimpleName(), del));
	}

}
