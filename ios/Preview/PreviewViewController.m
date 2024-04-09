//
//  PreviewViewController.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/8.
//

#import "PreviewViewController.h"
#import "PlayerMediaSelectView.h"

@implementation PreviewViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    PlayerMediaSelectView *view = [PlayerMediaSelectView new];
    [self.view addSubview:view];
    @weakify(self);
    [view remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.center.equalTo(self.view);
        make.width.equalTo(self.view);
        make.height.equalTo(@420);
    }];
}

@end
