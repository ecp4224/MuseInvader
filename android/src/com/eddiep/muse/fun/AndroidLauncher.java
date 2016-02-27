package com.eddiep.muse.fun;


import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.eddiep.muse.MuseFun;
import com.eddiep.muse.handlers.MuseHandler;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.resolutionStrategy = new FillResolutionStrategy();
		config.useWakelock = true;
		MuseFun.setDefaultHandler(new MuseHandler());
		initialize(MuseFun.getInstance(), config);
	}
}
