//
//  PlayerMediaSelectItemView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/8.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface PlayerMediaSelectItemView : UITableViewCell
@property (nonatomic) BOOL isSelected;
@property (nonatomic) NSString *title;
@end

NS_ASSUME_NONNULL_END
