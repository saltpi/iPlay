package top.ourfor.app.iplay.common.model;

import top.ourfor.app.iplay.model.ImageModel;
import top.ourfor.app.iplay.common.type.MediaLayoutType;

public interface IMediaModel {
    String getId();
    String getName();
    ImageModel getImage();
    default String getOverview() {
        return "";
    }
    default MediaLayoutType getLayoutType() {
        return MediaLayoutType.None;
    }
}
