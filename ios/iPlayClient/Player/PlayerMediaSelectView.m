//
//  PlayerMediaSelectView.m
//  iPlayClient
//
//  Created by 赫拉 on 2024/4/8.
//

#import "PlayerMediaSelectView.h"
#import "PlayerMediaSelectItemView.h"
#import "PlayerMediaSelectItemModel.h"


@interface PlayerMediaSelectView ()<UITableViewDelegate, UITableViewDataSource>
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) UIImageView *closeButton;
@end

@implementation PlayerMediaSelectView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self _setupUI];
        [self _layout];
        [self _bind];
    }
    return self;
}


- (void)_setupUI {
    self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.4];
    self.layer.cornerRadius = 6;
    self.layer.masksToBounds = YES;
    self.clipsToBounds = YES;
    [self addSubview:self.closeButton];
    [self addSubview:self.tableView];
}

- (void)_layout {
    @weakify(self);
    [self.tableView remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.top.equalTo(self).with.offset(28);
        make.left.equalTo(self);
        make.right.equalTo(self);
        make.bottom.equalTo(self);
    }];
    
    [self.closeButton remakeConstraints:^(MASConstraintMaker *make) {
        @strongify(self);
        make.top.equalTo(self).with.offset(4);
        make.right.equalTo(self).with.offset(-4);
        make.size.equalTo(@20);
    }];
}

- (void)_bind {
    NSMutableArray<PlayerMediaSelectItemModel *> *models = @[].mutableCopy;
    for (int i = 0; i < 100; i++) {
        PlayerMediaSelectItemModel *model = [PlayerMediaSelectItemModel new];
        model.isSelected = NO;
        model.item = [NSString stringWithFormat:@"abc %d", i];
        [models addObject:model];
    }
    self.datasource = models;
    Class clazz = PlayerMediaSelectItemView.class;
    [self.tableView registerClass:clazz forCellReuseIdentifier:NSStringFromClass(clazz)];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismiss)];
    [self.closeButton addGestureRecognizer:tap];
}

- (void)reloadData {
    [self.tableView reloadData];
}

- (void)dismiss {
    [self removeFromSuperview];
}

#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.datasource.count;
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSMutableArray<NSIndexPath *> *indices = [NSMutableArray new];
    for (int i = 0; i < self.datasource.count; i++) {
        PlayerMediaSelectItemModel *model = self.datasource[i];
        if (model.isSelected) {
            model.isSelected = NO;
            NSIndexPath *indexPath = [NSIndexPath indexPathForItem:i inSection:0];
            [indices addObject:indexPath];
        }
    }
    PlayerMediaSelectItemModel *model = self.datasource[indexPath.item];
    model.isSelected = !model.isSelected;
    BLOCK_INVOKE(self.onSelectCallback, model);
    [tableView reloadData];
}

- (void)tableView:(UITableView *)tableView didDeselectRowAtIndexPath:(NSIndexPath *)indexPath {
    PlayerMediaSelectItemModel *model = self.datasource[indexPath.item];
    model.isSelected = !model.isSelected;
    [tableView reloadData];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    PlayerMediaSelectItemView *cell = [tableView dequeueReusableCellWithIdentifier:NSStringFromClass(PlayerMediaSelectItemView.class)];
    PlayerMediaSelectItemModel *model = self.datasource[indexPath.item];
    cell.isSelected = model.isSelected;
    cell.title = [model.item description];
    return cell;
}


#pragma mark - Getter
- (UITableView *)tableView {
    if (!_tableView) {
        UITableView *tableView = [[UITableView alloc] init];
        tableView.backgroundColor = UIColor.clearColor;
        tableView.allowsSelection = YES;
        tableView.allowsMultipleSelection = NO;
        tableView.showsVerticalScrollIndicator = NO;
        tableView.userInteractionEnabled = YES;
        _tableView = tableView;
    }
    return _tableView;
}

- (UIImageView *)closeButton {
    if (!_closeButton) {
        UIImage *image = [UIImage imageNamed:@"player/close"];
        UIImageView *imageView = [[UIImageView alloc] init];
        imageView.image = [image imageWithTintColor:UIColor.whiteColor];
        imageView.contentMode = UIViewContentModeScaleAspectFit;
        imageView.userInteractionEnabled = YES;
        _closeButton = imageView;
    }
    return _closeButton;
}

@end
