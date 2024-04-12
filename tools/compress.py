#!/usr/bin/env python

import shutil
import os

def create_zip(src_dir, dest_file):
    shutil.make_archive(dest_file, 'zip', src_dir)

cwd = os.getcwd()

create_zip(os.path.join(cwd, 'windows', 'AppPackages'), 'iPlay.all')