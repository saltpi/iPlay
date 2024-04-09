//
//  PlayerMediaSelectView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/8.
//

#import <UIKit/UIKit.h>
#import "PlayerMediaSelectItemModel.h"

typedef void (^PlayerMediaSelectCallback)(PlayerMediaSelectItemModel *_Nullable);

NS_ASSUME_NONNULL_BEGIN

@interface PlayerMediaSelectView : UIView
@property (nonatomic, copy) NSArray<PlayerMediaSelectItemModel *> *datasource;
@property (nonatomic, copy) PlayerMediaSelectCallback onSelectCallback;

- (void)reloadData;

- (void)dismiss;
@end

NS_ASSUME_NONNULL_END
