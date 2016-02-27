package com.eddiep.muse.game;

import com.interaxon.libmuse.*;

public class MuseHandler extends MuseDataListener {
    private Muse muse;
    private double beta;
    private boolean isConnected;
    private Runnable onBlink;

    public MuseHandler(Muse muse) {
        this.muse = muse;
    }

    public void start() {
        try {
            muse.runAsynchronously();
        } catch (Exception e) {
            e.printStackTrace();
        }
        muse.registerDataListener(this, MuseDataPacketType.BETA_SCORE);
        muse.registerDataListener(this, MuseDataPacketType.ARTIFACTS);
        muse.setPreset(MusePreset.PRESET_14);
        muse.enableDataTransmission(true);
    }

    @Override
    public void receiveMuseDataPacket(MuseDataPacket p0) {
        if (p0 == null)
            return;

        if (p0.getPacketType() == MuseDataPacketType.BETA_SCORE) {
            double sum = 0;
            for (double d : p0.getValues()) {
                sum += d;
            }
            sum /= (double)(p0.getValues().size());

            beta = sum;
        }
    }

    @Override
    public void receiveMuseArtifactPacket(MuseArtifactPacket p0) {
        if (p0 == null)
            return;

        if (onBlink != null && p0.getHeadbandOn() && p0.getBlink()) {
            onBlink.run();
        }

        isConnected = p0.getHeadbandOn();
    }

    public Muse getMuse() {
        return muse;
    }

    public double getBeta() {
        return beta;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setOnBlink(Runnable onBlink) {
        this.onBlink = onBlink;
    }
}
