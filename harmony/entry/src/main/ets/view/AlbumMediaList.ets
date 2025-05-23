import { MediaModel } from "../../../api/iPlayDataSource"
import { router } from "../../../module/Router"
import { MediaCellView } from "./MediaCellView"


export class AlbumMediasModel {
  id: string|null = null
  type: string|null = null
  title: string = ""
  image: string = ""
  medias: MediaModel[] = []
}


@Component
export struct AlbumMediaList {
  @Prop model: AlbumMediasModel

  build() {
    Column() {
      Row() {
        Text(this.model?.title)
          .fontWeight(18)
          .textAlign(TextAlign.Start)
          .fontWeight(FontWeight.Bold)
          .flexGrow(1)
          .margin({top: 5, left: 15, bottom: 10})
        Text($r("app.string.album_view_more"))
          .fontWeight(16)
          .textAlign(TextAlign.End)
          .fontWeight(FontWeight.Normal)
          .fontColor($r("app.color.home_album_view_more"))
          .margin({top: 5, right: 8, bottom: 10})
          .visibility(this.model.type == "resume" ? Visibility.Hidden : Visibility.Visible)
          .onClick(() => {
            router.pushPage("pages/AlbumPage", {
              albumId: this.model?.id
            })
          })
      }
      .height(this.model.type == "albums" ? 0 : "auto")
      .visibility(this.model.type == "albums" ? Visibility.Hidden : Visibility.Visible)
      .width("100%")
      List({ space: 5, initialIndex: 0 }) {
        ForEach(this.model?.medias, (item: MediaModel) => {
          ListItem() {
            MediaCellView({
              model: item
            })
          }
        }, (item: MediaModel) => item.id)
      }
      .listDirection(Axis.Horizontal)
      .scrollBar(BarState.Off)
      .friction(0.6)
      .divider({ strokeWidth: 2, color: 0xFFFFFF, startMargin: 2, endMargin: 2 }) // 每行之间的分界线
      .edgeEffect(EdgeEffect.Spring)
      .onScrollIndex((firstIndex: number, lastIndex: number, centerIndex: number) => {
        console.info('first' + firstIndex)
        console.info('last' + lastIndex)
        console.info('center' + centerIndex)
      })
      .onScrollVisibleContentChange((start: VisibleListContentInfo, end: VisibleListContentInfo) => {
        console.log(' start index: ' + start.index +
          ' start item group area: ' + start.itemGroupArea +
          ' start index in group: ' + start.itemIndexInGroup)
        console.log(' end index: ' + end.index +
          ' end item group area: ' + end.itemGroupArea +
          ' end index in group: ' + end.itemIndexInGroup)
      })
      .onDidScroll((scrollOffset: number, scrollState: ScrollState) => {
        console.info(`onScroll scrollState = ScrollState` + scrollState + `, scrollOffset = ` + scrollOffset)
      })
      .width('100%')
    }
    .width('100%')
    .height(this.cellHeight())
    .padding({ top: 0 })
  }

  cellHeight() {
    let type = this.model.type
    let height = 230;
    if (type == "albums") {
      height = 100;
    } else if (type == "resume") {
      height = 175
    }
    return height
  }

}