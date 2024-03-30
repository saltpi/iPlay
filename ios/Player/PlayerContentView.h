//
//  MPVView.h
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/25.
//

#import <UIKit/UIKit.h>
#import "PlayerViewModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface PlayerContentView : UIView
@property (nonatomic, strong) PlayerViewModel *viewModel;
@end

NS_ASSUME_NONNULL_END
