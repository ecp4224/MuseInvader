package com.eddiep.muse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.eddiep.muse.logic.Handler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;

public class MuseFun {
	public static final AssetManager ASSETS = new AssetManager();

	private static MuseClient INSTANCE;
	private static Handler DEFAULT = new BlankHandler();

	public static void setDefaultHandler(Handler handler) {
		DEFAULT = handler;
	}

	@NotNull
	public static MuseClient getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MuseClient(DEFAULT);
		}

		return INSTANCE;
	}

	private static boolean loaded;
	public static void loadGameAssets(AssetManager manager) {
		if (loaded)
			return;

		//Load all sprites
		FileHandle[] sprites = Gdx.files.internal("sprites").list(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith("png") ||
						pathname.getName().endsWith("PNG") ||
						pathname.getName().endsWith("jpg") ||
						pathname.getName().endsWith("JPG");
			}
		});

		for (FileHandle file: sprites) {
			manager.load(file.path(), Texture.class);
		}

		FileHandle[] sounds = Gdx.files.internal("sounds").list(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith("mp3") ||
						pathname.getName().endsWith("wav") ||
						pathname.getName().endsWith("ogg");
			}
		});

		for (FileHandle file: sounds) {
			manager.load(file.path(), Sound.class);
		}


		//TODO Load other shit

		loaded = true;
	}

	private static class BlankHandler implements Handler {
		@Override
		public void start() {

		}

		@Override
		public void tick() {

		}
	}
}
