# 使用 breakpad 捕获 native crash

在以下环境测试通过

- CPU: Intel 10710U
- OS: macOS Catalina 10.15.7
- buildSDK: 31, NDK: 21
- kotlin 1.6.x, gradle 7.x, AGP 7.x
- Device: Redmi K40 & Android 11

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
## 捕获

1. 如何 CMake HelloWorld: [Google Code lab](https://codelabs.developers.google.com/codelabs/android-studio-cmake/), 事实上 Android Studio 自带有在项目中导入 C++ 支持的一键方式，很是方便
2. Android Studio 具有一键生成 JNI 函数，用起来还是很方便的： ![](https://cdn.jsdelivr.net/gh/zsqw123/cdn@master/picCDN/202112191909927.png)

3. 在 cpp 中初始化这俩玩意，然后引发 native crash 就好了：

   ```cpp
   MinidumpDescriptor descriptor(path); // 这个 path 填一个文件夹
   ExceptionHandler eh(descriptor, nullptr, DumpCallback, nullptr, true, -1);
   ```

## 分析

1. 拉取手机端在指定路径下的 dmp 文件

2. 找到编译后的 so 文件: 一般在 build 文件夹下，比如我在：`/app/build/intermediates/merged_native_libs/debug/out/lib/arm64-v8a/libbreakpadapp.so`，个人建议也可以在编译后的 apk 里面解压拿，这样比较方便

### 生成符号表

   1. 获取 `dump_syms` 的可执行文件，以下方法任选其一：

      - 找到对应的工具源码文件所在位置：如 macOS: `breakpad/src/tools/mac`，在 Xcode 中打开 dump_syms.xcodeproj 并编译即可得到 `dump_syms` 可执行文件
      - 若安装了 `Rust`，也可以使用另一个用 Rust 编写的 dump_syms 工具: [mozilla/dump_syms](https://github.com/mozilla/dump_syms), 运行 `cargo build` 来得到 `dump_syms` 可执行文件

   2. 生成 syms 文件：

      ```bash
      ./dump_syms libbreakpadapp.so > app.syms
      ```

   3. 制作指定的目录结构：

      ```bash
      head -n1 app.syms
      # MODULE Linux arm64 EB6D20434806296679496154A2365F410 libbreakpadapp.so
      ```

      通过输出，创建指定的文件夹，并将 syms 文件放入其中：

      ```bash
      mkdir -p ./syms/libbreakpadapp.so/EB...0/ # 根据上一步输出
      mv app.syms ./syms/libbreakpadapp.so/EB...0/ # 放入符号文件
      ```

### 分析 dump 文件:

```bash
./minidump_stackwalk 1.dmp ./syms
```

可以得到如下，其中标有 `(crashed)` 的即为崩溃线程

```bash
Operating system: Android
0.0.0 Linux 4.19.113-perf-gbdadb4f6dfbd #1 SMP PREEMPT Fri Sep 17 14:10:54 CST 2021 aarch64
CPU: arm64
8 CPUs

GPU: UNKNOWN

Crash reason:  SIGSEGV /SEGV_MAPERR
Crash address: 0x0
Process uptime: not available

Thread 0 (crashed) # crash 发生线程
0  libbreakpadapp.so + 0x28fa0 # crash 位置及寄存器信息
x0 = 0x0000000000000000    x1 = 0x0000000000000000
x2 = 0x00000000000000b4    x3 = 0xb400007a122c31e0
x4 = 0xb400007a122c31e0    x5 = 0x00000000000000a1
x6 = 0x00000000000000a1    x7 = 0x0000000000000074
x8 = 0x0000000000000000    x9 = 0x0000000000000002
x10 = 0x0000000000000001   x11 = 0x0000000000000000
x12 = 0x0000000000000001   x13 = 0xb4000079c40c1540
x14 = 0x0000000000000050   x15 = 0x0000000001000000
x16 = 0x0000007a19dcfbf0   x17 = 0x0000007a19d69f8c
x18 = 0x0000007ab6594000   x19 = 0xb400007a2f48bc00
x20 = 0x0000000000000000   x21 = 0xb400007a2f48bc00
x22 = 0x0000007ab570f000   x23 = 0xb400007a2f48bcb8
x24 = 0x0000007a2574a1f8   x25 = 0x0000007ab570f000
x26 = 0x0000000000000037   x27 = 0x0000000000000002
x28 = 0x0000007fffa091f0    fp = 0x0000007fffa091c0
lr = 0x0000007a19d6a11c    sp = 0x0000007fffa08ff0
pc = 0x0000007a19d69fa0
Found by: given as instruction pointer in context
1  libbreakpadapp.so + 0x29118
fp = 0x0000007fffa091f0    lr = 0x0000007a2e948ed8
sp = 0x0000007fffa091d0    pc = 0x0000007a19d6a11c
Found by: previous frame's frame pointer
```

我们还可以使用 ndk 中 (Android/sdk/ndk/21.4.7075529/toolchains/aarch64-linux-android-4.9/prebuilt/darwin-x86_64/bin) 提供的`addr2line` 来通过地址符号反解：  
![](https://cdn.jsdelivr.net/gh/zsqw123/cdn@master/picCDN/202112192223340.png)

```bash
➜  bin ./aarch64-linux-android-addr2line -f -C -e /Users/zsqw123/Desktop/libbreakpadapp.so 0x28fa0
# 输出: crash()
```

