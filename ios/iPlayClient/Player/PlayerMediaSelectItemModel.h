//
//  PlayerMediaSelectItemModel.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/8.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface PlayerMediaSelectItemModel<T> : NSObject
@property (nonatomic) BOOL isSelected;
@property (nonatomic) T item;
@end

NS_ASSUME_NONNULL_END
