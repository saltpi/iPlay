package top.ourfor.app.iPlayClient;

import android.view.SurfaceHolder;

import top.ourfor.lib.mpv.MPV;

public class PlayerViewModel implements Player {
    public String url = null;
    private MPV mpv;
    public PlayerViewModel() {
        mpv = new MPV();
        mpv.create();
        mpv.setOptionString("profile", "fast");
        mpv.setOptionString("vo", "gpu-next");
        mpv.setOptionString("gpu-context", "android");
        mpv.setOptionString("opengl-es", "yes");
        mpv.setOptionString("hwdec", "auto");
        mpv.setOptionString("hwdec-codecs", "h264,hevc,mpeg4,mpeg2video,vp8,vp9,av1");
        mpv.setOptionString("ao", "audiotrack,opensles");
        mpv.init();
    }
    public void attach(SurfaceHolder holder) {
//        mpv.setDrawable(holder.getSurface());
        mpv.setDrawable(holder.getSurface());
    }

    @Override
    public void loadVideo(String url) {
//        mpv.command("loadfile", url);
        mpv.command("loadfile", url);
    }

    @Override
    public void resize(String newSize) {
//        mpv.setStringTypeProperty("android-surface-size", newSize);
    }

    @Override
    public boolean isPlaying() {
//        return mpv.getBoolTypeProperty("pause");
        return true;
    }

    @Override
    public void resume() {
//        mpv.setBoolTypeProperty("pause", false);
    }

    @Override
    public void pause() {
//        mpv.setBoolTypeProperty("pause", true);
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
//        mpv.destroy();
    }
}
