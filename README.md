# 使用breakpad捕获 nativeCrash

## 安装

1. clone breakpad

```bash
git clone git@github.com:google/breakpad.git
// or
// git clone https://chromium.googlesource.com/breakpad/breakpad
```

2. 由于 Breakpad 依赖于 LSS，因此需要下载 [LSS](https://chromium.googlesource.com/linux-syscall-support), 并将 `linux_syscall_support.h` 文件放至 `breakpad/src/third_party/lss/`
3. 编译 breakpad, 我们就可以看到 libbreakpad.a 文件

```bash
cd breakpad
./configure && make
make install
```

