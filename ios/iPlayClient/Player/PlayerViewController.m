//
//  PlayerViewController.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/3/21.
//

#import "PlayerViewController.h"

@interface PlayerViewController ()

@end

@implementation PlayerViewController

- (void)layoutPlayerView {
    [self.view addSubview:self.contentView];
    [self.view addSubview:self.controlView];
    [self.view addSubview:self.eventsView];
    
    @weakify(self);
    [self.contentView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.edges.equalTo(self.view);
    }];

    [self.controlView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.edges.equalTo(self.view);
    }];

    [self.eventsView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.left.equalTo(self.view);
        make.right.equalTo(self.view);
        make.top.equalTo(self.view.top).with.offset(100);
        make.bottom.equalTo(self.view.bottom).with.offset(-100);
    }];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.modalPresentationCapturesStatusBarAppearance = YES;
}

- (BOOL)prefersStatusBarHidden {
    return YES;
}

- (BOOL)prefersHomeIndicatorAutoHidden {
    return YES;
}

- (BOOL)shouldAutorotate {
    return YES;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskLandscape;
}


@end
