package top.ourfor.app.iplay.page;

import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.page.album.AlbumPage;
import top.ourfor.app.iplay.page.episode.EpisodePage;
import top.ourfor.app.iplay.page.file.FilePage;
import top.ourfor.app.iplay.page.home.HomePage;
import top.ourfor.app.iplay.page.login.LoginPage;
import top.ourfor.app.iplay.page.media.MediaPage;
import top.ourfor.app.iplay.page.music.MusicPage;
import top.ourfor.app.iplay.page.player.MoviePlayerPage;
import top.ourfor.app.iplay.page.player.MusicPlayerPage;
import top.ourfor.app.iplay.page.search.SearchPage;
import top.ourfor.app.iplay.page.setting.SettingPage;
import top.ourfor.app.iplay.page.setting.about.AboutPage;
import top.ourfor.app.iplay.page.setting.audio.AudioPage;
import top.ourfor.app.iplay.page.setting.cache.CachePage;
import top.ourfor.app.iplay.page.setting.cloud.CloudPage;
import top.ourfor.app.iplay.page.setting.picture.PicturePage;
import top.ourfor.app.iplay.page.setting.site.SitePage;
import top.ourfor.app.iplay.page.setting.theme.ThemePage;
import top.ourfor.app.iplay.page.setting.video.VideoPage;
import top.ourfor.app.iplay.page.star.StarPage;
import top.ourfor.app.iplay.page.web.WebPage;

public class PageMaker {
    static public Page makePage(int itemId) {
        if (itemId == R.id.homePage) {
            return new HomePage();
        } else if (itemId == R.id.filePage) {
            return new FilePage();
        } else if (itemId == R.id.starPage) {
            return new StarPage();
        } else if (itemId == R.id.settingPage) {
            return new SettingPage();
        } else if (itemId == R.id.searchPage) {
            return new SearchPage();
        } else if (itemId == R.id.mediaPage) {
            return new MediaPage();
        } else if (itemId == R.id.albumPage) {
            return new AlbumPage();
        } else if (itemId == R.id.episodePage) {
            return new EpisodePage();
        } else if (itemId == R.id.playerPage) {
            return new MoviePlayerPage();
        } else if (itemId == R.id.cloudPage) {
            return new CloudPage();
        } else if (itemId == R.id.themePage) {
            return new ThemePage();
        } else if (itemId == R.id.sitePage) {
            return new SitePage();
        } else if (itemId == R.id.picturePage) {
            return new PicturePage();
        } else if (itemId == R.id.videoPage) {
            return new VideoPage();
        } else if (itemId == R.id.audioPage) {
            return new AudioPage();
        } else if (itemId == R.id.cachePage) {
            return new CachePage();
        } else if (itemId == R.id.aboutPage) {
            return new AboutPage();
        } else if (itemId == R.id.loginPage) {
            return new LoginPage();
        } else if (itemId == R.id.musicPage) {
            return new MusicPage();
        } else if (itemId == R.id.musicPlayerPage) {
            return new MusicPlayerPage();
        } else if (itemId == R.id.webPage) {
            return new WebPage();
        }
        return null;
    }

}
