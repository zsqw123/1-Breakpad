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
## use

1. 如何 CMake HelloWorld: [Google Code lab](https://codelabs.developers.google.com/codelabs/android-studio-cmake/), 事实上 Android Studio 自带有在项目中导入 C++ 支持的一键方式，很是方便
2. Android Studio 具有一键生成 JNI 函数，用起来还是很方便的： ![](https://cdn.jsdelivr.net/gh/zsqw123/cdn@master/picCDN/202112191909927.png)

3. 在 cpp 中初始化这俩玩意，然后引发 native crash 就好了：

   ```cpp
   MinidumpDescriptor descriptor(path);
   ExceptionHandler eh(descriptor, nullptr, DumpCallback, nullptr, true, -1);
   ```

