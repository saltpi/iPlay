package top.ourfor.app.iplayx.page;

import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.page.album.AlbumPage;
import top.ourfor.app.iplayx.page.episode.EpisodePage;
import top.ourfor.app.iplayx.page.file.FilePage;
import top.ourfor.app.iplayx.page.home.HomePage;
import top.ourfor.app.iplayx.page.login.LoginPage;
import top.ourfor.app.iplayx.page.media.MediaPage;
import top.ourfor.app.iplayx.page.music.MusicPage;
import top.ourfor.app.iplayx.page.player.MoviePlayerPage;
import top.ourfor.app.iplayx.page.player.MusicPlayerPage;
import top.ourfor.app.iplayx.page.search.SearchPage;
import top.ourfor.app.iplayx.page.setting.SettingPage;
import top.ourfor.app.iplayx.page.setting.about.AboutPage;
import top.ourfor.app.iplayx.page.setting.audio.AudioPage;
import top.ourfor.app.iplayx.page.setting.cache.CachePage;
import top.ourfor.app.iplayx.page.setting.cloud.CloudPage;
import top.ourfor.app.iplayx.page.setting.picture.PicturePage;
import top.ourfor.app.iplayx.page.setting.site.SitePage;
import top.ourfor.app.iplayx.page.setting.theme.ThemePage;
import top.ourfor.app.iplayx.page.setting.video.VideoPage;
import top.ourfor.app.iplayx.page.star.StarPage;
import top.ourfor.app.iplayx.page.web.WebPage;

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
